package com.flying.xiaopo.lab;

import com.flying.xiaopo.lab.model.Category;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowContentUiType;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.impl.content.ToolWindowContentUi;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author wupanjie on 2017/10/26.
 */
public class LabToolWindowFactory implements ToolWindowFactory {
  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    LabProject.getInstance().install(project);
    if (LabManager.getInstance().getState() == null) {
      LabManager.State state = new LabManager.State();
      state.labStoragePath = "/Users/wupanjie/SnippetLab/";
      state.labCategories.add(Category.UNCATEGORIZED);
      LabManager.getInstance().loadState(state);
    }

    toolWindow.setDefaultContentUiType(ToolWindowContentUiType.COMBO);
    toolWindow.getComponent().putClientProperty(ToolWindowContentUi.HIDE_ID_LABEL, "true");
    final ContentManager contentManager = toolWindow.getContentManager();
    LabContentManager.getInstance().install(project, contentManager);

    // add all content
    LabContent labContentAll = new LabToolWindowContent(project, contentManager, Category.ALL);
    LabContentManager.getInstance().addLabContent(labContentAll);

    // add category content
    LabManager.getInstance().getState().labCategories.forEach(category -> {
      LabContent labContent = new LabToolWindowContent(project, contentManager, category);
      LabContentManager.getInstance().addLabContent(labContent);
    });
  }
}
