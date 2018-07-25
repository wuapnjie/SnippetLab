package com.flying.xiaopo.lab.action.add;

import com.flying.xiaopo.lab.LabContent;
import com.flying.xiaopo.lab.LabContentManager;
import com.flying.xiaopo.lab.kits.ActionKit;
import com.flying.xiaopo.lab.kits.DialogKit;
import com.flying.xiaopo.lab.kits.LabLogger;
import com.flying.xiaopo.lab.model.Category;
import com.flying.xiaopo.lab.model.SnippetFile;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiFile;
import com.intellij.util.IconUtil;
import java.util.Optional;

/**
 * @author wupanjie on 2017/10/26.
 */
public class AddSnippetToLabAction extends AnAction implements DumbAware {

  private Category category;

  public AddSnippetToLabAction(Category category) {
    super(category.name, "Add current file to lab", null);
    this.category = category;
  }

  public AddSnippetToLabAction() {
    super("Add", "add current file to lab", IconUtil.getAddIcon());
  }

  @Override public void actionPerformed(AnActionEvent anActionEvent) {
    PsiFile currentEditFile = ActionKit.getCurrentEditFile(anActionEvent);

    if (currentEditFile == null) {
      LabLogger.warn("No file is editing now");
    }

    Optional.ofNullable(currentEditFile)
        .ifPresent(file -> {
          SnippetFile snippetInfo = DialogKit.showAddSnippetDialog(file.getName(), category);
          if (snippetInfo != null) {
            LabContent labContent = LabContentManager.getInstance()
                .getLabContent(snippetInfo.getCategoryName());
            if (labContent != null) {
              LabContentManager.getInstance().selectLabContent(labContent);
              labContent.getLabPresenter().saveSnippetToLab(file, snippetInfo);
            }
          }
        });
  }
}
