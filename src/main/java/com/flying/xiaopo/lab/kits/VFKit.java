package com.flying.xiaopo.lab.kits;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.intellij.openapi.fileEditor.impl.IdeDocumentHistoryImpl;
import com.intellij.openapi.fileEditor.impl.NonProjectFileWritingAccessExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectEx;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NotNullLazyKey;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.ex.temp.TempFileSystem;
import com.intellij.project.ProjectKt;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;

/**
 * @author wupanjie on 2017/10/31.
 */
public class VFKit {
  private static final Key<Boolean> ENABLE_IN_TESTS =
      Key.create("NON_PROJECT_FILE_ACCESS_ENABLE_IN_TESTS");
  private static final NotNullLazyKey<AtomicInteger, UserDataHolder> ACCESS_ALLOWED
      = NotNullLazyKey.create("NON_PROJECT_FILE_ACCESS", holder -> new AtomicInteger());

  private VFKit() {
    //no instance
  }

  public static boolean isWriteAccessAllowed(@NotNull VirtualFile file, @NotNull Project project) {
    if (isAllAccessAllowed()) return true;
    if (file.isDirectory()) return true;

    if (!(file.getFileSystem() instanceof LocalFileSystem)) {
      return true; // do not block e.g., HttpFileSystem, LightFileSystem etc.
    }
    if (file.getFileSystem() instanceof TempFileSystem) return true;

    IdeDocumentHistoryImpl documentHistory =
        (IdeDocumentHistoryImpl) IdeDocumentHistory.getInstance(project);

    if (documentHistory.isRecentlyChanged(file)) return true;

    if (!getApp().isUnitTestMode()
        && FileUtil.isAncestor(new File(FileUtil.getTempDirectory()),
        VfsUtilCore.virtualToIoFile(file), true)) {
      return true;
    }

    VirtualFile each = file;
    while (each != null) {
      if (ACCESS_ALLOWED.getValue(each).get() > 0) return true;
      each = each.getParent();
    }

    return isProjectFile(file, project);
  }

  private static boolean isAllAccessAllowed() {
    Application app = getApp();

    // disable checks in tests, if not asked
    if (app.isUnitTestMode() && app.getUserData(ENABLE_IN_TESTS) != Boolean.TRUE) {
      return true;
    }
    return ACCESS_ALLOWED.getValue(app).get() > 0;
  }

  private static Application getApp() {
    return ApplicationManager.getApplication();
  }

  private static boolean isProjectFile(@NotNull VirtualFile file, @NotNull Project project) {
    for (NonProjectFileWritingAccessExtension each : Extensions.getExtensions(
        NonProjectFileWritingAccessExtension.EP_NAME, project)) {
      if (each.isWritable(file)) return true;
      if (each.isNotWritable(file)) return false;
    }

    ProjectFileIndex fileIndex = ProjectFileIndex.SERVICE.getInstance(project);
    if (fileIndex.isInContent(file)) return true;
    if (!Registry.is("ide.hide.excluded.files")
        && fileIndex.isExcluded(file)
        && !fileIndex.isUnderIgnored(file)) {
      return true;
    }

    if (project instanceof ProjectEx && !project.isDefault()) {
      if (ProjectKt.getStateStore(project).isProjectFile(file)) {
        return true;
      }

      String filePath = file.getPath();
      for (Module module : ModuleManager.getInstance(project).getModules()) {
        if (FileUtil.namesEqual(filePath, module.getModuleFilePath())) {
          return true;
        }
      }
    }
    return false;
  }
}
