package com.flying.xiaopo.lab.action;

import com.flying.xiaopo.lab.LabManager;
import com.flying.xiaopo.lab.kits.DialogKit;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.IconUtil;

/**
 * @author wupanjie on 2017/10/29.
 */
public class CreateNewCategoryAction extends DumbAwareAction {
  public CreateNewCategoryAction() {
    super("Create New Category", "create new category", IconUtil.getAddIcon());
  }

  @Override public void actionPerformed(AnActionEvent anActionEvent) {
    String categoryName = DialogKit.showCreateNewCategoryDialog();
    if (StringUtil.isEmpty(categoryName)) {
      return;
    }

    boolean success = LabManager.getInstance().createNewCategoryAndShow(categoryName);
    if (!success) {
      Messages.showMessageDialog("Create new category failed.", "Failed", null);
    }
  }
}
