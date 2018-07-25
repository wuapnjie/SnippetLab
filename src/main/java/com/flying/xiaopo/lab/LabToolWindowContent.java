package com.flying.xiaopo.lab;

import com.flying.xiaopo.lab.model.Category;
import com.flying.xiaopo.lab.ui.LabPanel;
import com.flying.xiaopo.lab.ui.LabUI;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author wupanjie on 2017/10/26.
 */
public class LabToolWindowContent implements LabContent {
  private final LabUI ui;
  private final Content content;
  private final Category category;
  private final LabPresenter presenter;

  public LabToolWindowContent(@NotNull Project project, @NotNull ContentManager contentManager,
      @NotNull Category category) {
    this.presenter = new LabPresenter(project, category);
    this.ui = new LabPanel(presenter, category);
    this.presenter.setLabUI(ui);
    this.category = category;

    content =
        contentManager.getFactory().createContent(ui.getComponent(), category.name, false);
    content.setTabName(category.name);

    contentManager.addContent(content);
  }

  @Override public LabUI getLabUI() {
    return ui;
  }

  @Override public LabPresenter getLabPresenter() {
    return presenter;
  }

  @Override public String getName() {
    return content.getTabName();
  }

  @Override public Category getCategory() {
    return category;
  }

  @Override public Content getContent() {
    return content;
  }
}
