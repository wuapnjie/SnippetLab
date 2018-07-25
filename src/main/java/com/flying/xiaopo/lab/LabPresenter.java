package com.flying.xiaopo.lab;

import com.flying.xiaopo.lab.model.Category;
import com.flying.xiaopo.lab.model.SnippetFile;
import com.flying.xiaopo.lab.ui.LabUI;
import com.intellij.ide.dnd.FileCopyPasteUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.ui.TextTransferable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import org.jetbrains.annotations.NotNull;

/**
 * @author wupanjie on 2017/10/26.
 */
public class LabPresenter {
  private final Project project;
  private final Category category;
  private LabUI labUI;

  public LabPresenter(Project project, Category category) {
    this.project = project;
    this.category = category;
  }

  public void setLabUI(LabUI labUI) {
    this.labUI = labUI;
  }

  public void openSnippetFile(@NotNull VirtualFile virtualFile) {
    PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
    if (psiFile != null) {
      psiFile.navigate(true);
    }
  }

  public SnippetFile saveSnippetToLab(final PsiFile psiFile, final SnippetFile snippetInfo) {
    final LabManager.State state = LabManager.getInstance().getState();
    if (state == null) return null;
    final String storagePath = state.labStoragePath;
    final File storageDir = new File(storagePath);
    if (!storageDir.exists()) {
      FileUtil.createDirectory(storageDir);
    }

    final File categorizedStorageDir = new File(storagePath, snippetInfo.getCategoryName());
    if (!categorizedStorageDir.exists()) {
      FileUtil.createDirectory(categorizedStorageDir);
    }

    final VirtualFile virtualFile = psiFile.getVirtualFile();

    return WriteAction.compute(() -> {
      try {
        LocalDate now = LocalDate.now();
        InputStream stream = virtualFile.getInputStream();
        File toFile = new File(categorizedStorageDir,
            virtualFile.getNameWithoutExtension()
                + System.currentTimeMillis()
                + "."
                + virtualFile.getExtension());
        FileUtil.createIfDoesntExist(toFile);
        FileUtil.copy(stream, new FileOutputStream(toFile));

        String createDate = now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth();
        SnippetFile snippetFile = new SnippetFile(toFile.getAbsolutePath(), createDate);
        snippetFile.setName(snippetInfo.getName());
        snippetFile.setCategoryName(snippetInfo.getCategoryName());
        snippetFile.setDescription(snippetInfo.getDescription());

        LabManager.getInstance().addNewSnippet(snippetFile, snippetInfo.getCategoryName());

        // insert new cell to categorized content
        labUI.insertNewSnippetFile(snippetFile);

        // insert new cell to content all
        LabContentManager.getInstance()
            .getLabContentAll()
            .getLabUI()
            .insertNewSnippetFile(snippetFile);

        return snippetFile;
      } catch (IOException e) {
        e.printStackTrace();
      }

      return null;
    });
  }

  public void deleteTargetSnippet() {
    SnippetFile targetSnippet = labUI.getTargetSnippet();
    if (targetSnippet == null) {
      return;
    }

    final LabManager labManager = LabManager.getInstance();
    labManager.deleteSnippet(targetSnippet);
    FileUtil.delete(new File(targetSnippet.getPath()));

    labUI.removeSnippet(targetSnippet);

    LabContent labContent = category.isAll() ?
        LabContentManager.getInstance().getLabContent(targetSnippet.getCategoryName()) :
        LabContentManager.getInstance().getLabContentAll();

    if (labContent != null) {
      labContent.getLabUI().removeSnippet(targetSnippet);
    }
  }

  public void renameTargetSnippet(String newName) {
    SnippetFile targetSnippet = labUI.getTargetSnippet();
    if (targetSnippet == null || StringUtil.equals(newName, targetSnippet.getName())) {
      return;
    }

    SnippetFile tempInfo = new SnippetFile(targetSnippet);

    LabManager.getInstance().renameSnippet(targetSnippet, newName);

    labUI.renameSnippet(targetSnippet, newName);

    LabContent labContent = category.isAll() ?
        LabContentManager.getInstance().getLabContent(targetSnippet.getCategoryName()) :
        LabContentManager.getInstance().getLabContentAll();

    if (labContent != null) {
      labContent.getLabUI().renameSnippet(tempInfo, newName);
    }
  }

  public void moveTargetSnippetTo(Category category) {
    SnippetFile targetSnippet = labUI.getTargetSnippet();
    if (targetSnippet == null || targetSnippet.getCategoryName().equals(category.name)) {
      return;
    }

    LabManager.getInstance()
        .moveSnippet(targetSnippet, category);
  }

  public void copyTargetSnippet() {
    SnippetFile targetSnippet = labUI.getTargetSnippet();
    if (targetSnippet == null) {
      return;
    }

    targetSnippet.getVirtualFile()
        .map(virtualFile -> {
          try {
            return FileUtil.loadFile(new File(virtualFile.getPath()));
          } catch (IOException e) {
            e.printStackTrace();
          }
          return null;
        }).ifPresent(s ->
        CopyPasteManager.getInstance()
            .setContents(new TextTransferable(s))
    );
  }
}
