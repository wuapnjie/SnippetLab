package com.flying.xiaopo.lab.action;

import com.flying.xiaopo.lab.LabManager;
import com.flying.xiaopo.lab.kits.DialogKit;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.text.StringUtil;

/**
 * @author wupanjie on 2017/10/27.
 */
public class ConfigureStorageAction extends AnAction implements DumbAware {

  public ConfigureStorageAction() {
    super("Configure", "configure storage", AllIcons.General.GearPlain);
  }

  @Override public void actionPerformed(AnActionEvent anActionEvent) {
    LabManager.State state = LabManager.getInstance().getState();
    if (state == null) return;

    final String currentStoragePath = state.labStoragePath;

    final String newStoragePath = DialogKit.showChooseFolderPathDialog(
        "选择一个新的Lab路径",
        "Choose",
        null,
        currentStoragePath
    );

    if (StringUtil.equals(currentStoragePath, newStoragePath) ||
        StringUtil.isEmpty(newStoragePath)) {
      return;
    }

    state.labStoragePath = newStoragePath;
    LabManager.getInstance().loadState(state);
  }
}
