package com.flying.xiaopo.lab.action;

import com.flying.xiaopo.lab.LabContent;
import com.flying.xiaopo.lab.LabContentManager;
import com.flying.xiaopo.lab.LabManager;
import com.flying.xiaopo.lab.kits.DialogKit;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.util.IconUtil;

/**
 * @author wupanjie on 2017/10/31.
 */
public class DeleteCategoryAction extends DumbAwareAction {
  public DeleteCategoryAction() {
    super("Delete This Category", "delete this category", IconUtil.getRemoveIcon());
  }

  @Override public void actionPerformed(AnActionEvent anActionEvent) {
    LabContent labContent = LabContentManager.getInstance()
        .getCurrentLabContent();

    if (labContent == null) return;

    boolean sureToDelete = DialogKit.showDeleteCategoryConfirmDialog();

    if (sureToDelete) {
      LabManager.getInstance().deleteCurrentShowCategory();
    }
  }
}
