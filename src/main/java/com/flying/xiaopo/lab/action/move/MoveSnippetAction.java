package com.flying.xiaopo.lab.action.move;

import com.flying.xiaopo.lab.LabContent;
import com.flying.xiaopo.lab.LabContentManager;
import com.flying.xiaopo.lab.model.Category;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;

/**
 * @author wupanjie on 2017/10/26.
 */
public class MoveSnippetAction extends AnAction implements DumbAware {

  private final Category category;

  public MoveSnippetAction(Category category) {
    super(category.name, "move snippet to " + category.name, null);
    this.category = category;
  }

  @Override public void actionPerformed(AnActionEvent anActionEvent) {
    LabContent labContent = LabContentManager.getInstance()
        .getCurrentLabContent();

    if (labContent == null) return;

    labContent.getLabPresenter().moveTargetSnippetTo(category);
  }
}
