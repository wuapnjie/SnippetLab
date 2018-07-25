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
public class RenameCategoryAction extends DumbAwareAction {

  public RenameCategoryAction() {
    super("Rename", "Rename this category", IconUtil.getEditIcon());
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    LabContent labContent = LabContentManager.getInstance()
        .getCurrentLabContent();
    if (labContent == null) return;

    String newName = DialogKit.showRenameCategoryDialog();

    if (DialogKit.isValidCategory.checkInput(newName)) {
      LabManager.getInstance().renameCurrentShowCategory(newName);
    }
  }
}
