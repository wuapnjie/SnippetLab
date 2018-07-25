package com.flying.xiaopo.lab.action.move;

import com.flying.xiaopo.lab.LabManager;
import com.flying.xiaopo.lab.model.Category;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wupanjie on 2017/10/30.
 */
public class MoveToSnippetActionGroup extends ActionGroup {

  public MoveToSnippetActionGroup() {
    super("Move To", "move to another category", null);
    setPopup(true);
  }

  @NotNull @Override public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {

    final LabManager.State state = LabManager.getInstance().getState();
    if (state != null) {
      final int categorySize = state.labCategories.size();
      AnAction[] actions = new AnAction[categorySize + 1];
      int index = 0;

      for (Category labCategory : state.labCategories) {
        if (labCategory.isUncategorized()) continue;
        actions[index++] = new MoveSnippetAction(labCategory);
      }

      actions[index++] = Separator.getInstance();
      actions[index] = new MoveSnippetAction(Category.UNCATEGORIZED);

      return actions;
    }

    return new AnAction[] {new MoveSnippetAction(Category.UNCATEGORIZED)};
  }
}
