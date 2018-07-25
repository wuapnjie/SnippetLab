package com.flying.xiaopo.lab.action;

import com.flying.xiaopo.lab.LabContent;
import com.flying.xiaopo.lab.LabContentManager;
import com.flying.xiaopo.lab.LabManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

/**
 * @author wupanjie on 2017/11/7.
 */
public class CopySnippetAction extends DumbAwareAction {
  public CopySnippetAction() {
    super("Copy", "Copy this snippet", null);
  }

  @Override public void actionPerformed(AnActionEvent anActionEvent) {
    LabContent labContent = LabContentManager.getInstance()
        .getCurrentLabContent();

    if (labContent == null) return;

    labContent.getLabPresenter()
        .copyTargetSnippet();
  }
}
