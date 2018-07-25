package com.flying.xiaopo.lab.kits;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;

/**
 * @author wupanjie on 2017/10/29.
 */
public class ActionKit {
  private ActionKit() {
    //no instance
  }

  public static PsiFile getCurrentEditFile(AnActionEvent anActionEvent) {
    PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
    if (psiFile != null) {
      return psiFile;
    }

    Project project = anActionEvent.getProject();
    Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);

    if (editor == null || project == null) {
      LabLogger.warn("editor or project is null");
      return null;
    }

    return PsiUtilBase.getPsiFileInEditor(editor, project);
  }
}
