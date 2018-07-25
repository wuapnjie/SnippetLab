package com.flying.xiaopo.lab;

import com.flying.xiaopo.lab.model.Category;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.ContentManager;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wupanjie on 2017/10/29.
 */
class LabContentManagerImpl extends LabContentManager {
  private Project project;
  private ContentManager contentManager;
  private HashMap<String, LabContent> namedLabContents = new HashMap<>();

  @Override public void install(@NotNull Project project, @NotNull final ContentManager
      contentManager) {
    this.project = project;
    this.contentManager = contentManager;
  }

  @Nullable @Override public LabContent getCurrentLabContent() {
    if (contentManager == null || contentManager.getSelectedContent() == null) {
      return null;
    }
    return namedLabContents.get(contentManager.getSelectedContent().getTabName());
  }

  @Nullable @Override public LabContent getLabContent(String name) {
    return namedLabContents.get(name);
  }

  @NotNull @Override public LabContent getLabContentAll() {
    return namedLabContents.get(Category.ALL.name);
  }

  @Override public void addLabContent(LabContent labContent) {
    namedLabContents.put(labContent.getName(), labContent);
  }

  @Override public void selectLabContent(LabContent labContent) {
    contentManager.setSelectedContent(labContent.getContent());
  }

  @Override public LabContent createNewLabContent(Category category) {
    return new LabToolWindowContent(project, contentManager, category);
  }

  @Nullable @Override public Category getCurrentShowCategory() {
    LabContent current = getCurrentLabContent();
    return current == null ? null : current.getCategory();
  }

  @Override public void renameCurrentLabContent(String newName) {
    LabContent current = getCurrentLabContent();
    if (current == null) {
      return;
    }
    namedLabContents.remove(current.getName());
    current.getContent().setTabName(newName);
    current.getContent().setDisplayName(newName);
    current.getLabUI().refreshList(newName);

    namedLabContents.put(newName, current);
  }

  @Override public void removeLabContent(String name) {
    LabContent content = getLabContent(name);
    if (content == null) {
      return;
    }

    namedLabContents.remove(name);
    contentManager.removeContent(content.getContent(), true);
  }
}
