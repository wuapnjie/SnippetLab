package com.flying.xiaopo.lab.action.add;

import com.flying.xiaopo.lab.LabManager;
import com.flying.xiaopo.lab.kits.VFKit;
import com.flying.xiaopo.lab.model.Category;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wupanjie on 2017/10/29.
 */
public class AddToLabActionGroup extends ActionGroup implements DumbAware {
  @NotNull @Override public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
    if (anActionEvent == null) return AnAction.EMPTY_ARRAY;

    final Project project = anActionEvent.getProject();
    if (project == null) {
      return AnAction.EMPTY_ARRAY;
    }

    final PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
    if (psiFile == null ||
        psiFile.isDirectory() ||
        !VFKit.isWriteAccessAllowed(psiFile.getVirtualFile(), project)) {
      return AnAction.EMPTY_ARRAY;
    }

    LabManager.State state = LabManager.getInstance().getState();
    if (state != null) {
      final int categorySize = state.labCategories.size();
      AnAction[] actions = new AnAction[categorySize + 2];
      int index = 0;

      for (Category labCategory : state.labCategories) {
        actions[index++] = new AddSnippetToLabAction(labCategory);
      }

      actions[index++] = Separator.getInstance();
      actions[index] = new AddToNewCategoryAction();

      return actions;
    }

    return new AnAction[] {new AddToNewCategoryAction()};
  }
}
