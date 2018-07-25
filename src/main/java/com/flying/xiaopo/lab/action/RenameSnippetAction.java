package com.flying.xiaopo.lab.action;

import com.flying.xiaopo.lab.LabContent;
import com.flying.xiaopo.lab.LabContentManager;
import com.flying.xiaopo.lab.kits.DialogKit;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

/**
 * @author wupanjie on 2017/10/30.
 */
public class RenameSnippetAction extends DumbAwareAction {

  public RenameSnippetAction() {
    super("Rename", "Rename this snippet", null);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    LabContent labContent = LabContentManager.getInstance()
        .getCurrentLabContent();
    if (labContent == null) return;

    String newName = DialogKit.showRenameSnippetDialog();

    if (DialogKit.isValidName.checkInput(newName)) {
      labContent.getLabPresenter().renameTargetSnippet(newName);
    }
  }
}
