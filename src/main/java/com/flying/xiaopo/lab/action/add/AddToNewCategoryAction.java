package com.flying.xiaopo.lab.action.add;

import com.flying.xiaopo.lab.LabContent;
import com.flying.xiaopo.lab.LabContentManager;
import com.flying.xiaopo.lab.LabManager;
import com.flying.xiaopo.lab.kits.ActionKit;
import com.flying.xiaopo.lab.kits.DialogKit;
import com.flying.xiaopo.lab.model.Category;
import com.flying.xiaopo.lab.model.SnippetFile;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;

/**
 * @author wupanjie on 2017/10/29.
 */
public class AddToNewCategoryAction extends AnAction {

  public AddToNewCategoryAction() {
    super("Add To New Category Lab", "add to new category lab", AllIcons.General.Add);
  }

  @Override public void actionPerformed(AnActionEvent anActionEvent) {
    PsiFile currentEditFile = ActionKit.getCurrentEditFile(anActionEvent);
    if (currentEditFile == null) return;

    String categoryName = DialogKit.showCreateNewCategoryDialog();
    if (StringUtil.isEmpty(categoryName)) {
      return;
    }

    Category newCategory = LabManager.getInstance().createNewCategory(categoryName);

    if (newCategory == null) {
      return;
    }

    SnippetFile snippetInfo =
        DialogKit.showAddSnippetDialog(currentEditFile.getName(), newCategory);

    if (snippetInfo != null) {
      LabContent labContent = LabContentManager.getInstance()
          .getLabContent(snippetInfo.getCategoryName());
      if (labContent != null) {
        LabContentManager.getInstance().selectLabContent(labContent);
        labContent.getLabPresenter().saveSnippetToLab(currentEditFile, snippetInfo);
      }
    }
  }
}
