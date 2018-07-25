package com.flying.xiaopo.lab;

import com.flying.xiaopo.lab.model.Category;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wupanjie on 2017/10/29.
 */
public abstract class LabContentManager {

  public abstract void install(@NotNull Project project, @NotNull final ContentManager
      contentManager);

  public static LabContentManager getInstance() {
    return LazyLoad.lazy;
  }

  @Nullable public abstract LabContent getCurrentLabContent();

  @Nullable public abstract LabContent getLabContent(String name);

  @NotNull public abstract LabContent getLabContentAll();

  public abstract void addLabContent(LabContent labContent);

  public abstract void selectLabContent(LabContent labContent);

  public abstract LabContent createNewLabContent(Category category);

  @Nullable public abstract Category getCurrentShowCategory();

  public abstract void renameCurrentLabContent(String newName);

  public abstract void removeLabContent(String name);

  static class LazyLoad {
    static LabContentManager lazy = new LabContentManagerImpl();
  }
}
