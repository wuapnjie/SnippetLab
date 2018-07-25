package com.flying.xiaopo.lab.ui;

import com.flying.xiaopo.lab.LabContent;
import com.flying.xiaopo.lab.LabContentManager;
import com.flying.xiaopo.lab.LabManager;
import com.flying.xiaopo.lab.model.Category;
import com.flying.xiaopo.lab.model.SnippetFile;
import com.intellij.ide.actions.TemplateKindCombo;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.PlatformIcons;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jetbrains.annotations.NotNull;

/**
 * @author wupanjie on 2017/10/28.
 */
public class AddSnippetDialog extends Messages.InputDialog {

  private String snippetName;

  protected JTextField nameTextField;
  protected TemplateKindCombo categoryKindCombo;
  protected JTextField descriptionTextField;

  public AddSnippetDialog(String initialName, Category initialCategory, InputValidator validator) {
    super("输入Snippet文件名字", "Add", null, null, validator);
    this.snippetName = initialName;
    nameTextField.setText(snippetName);

    // configure category combo
    if (initialCategory != null) {
      categoryKindCombo.setSelectedName(initialCategory.name);
    } else {
      LabContent currentContent = LabContentManager.getInstance().getCurrentLabContent();
      if (currentContent != null && !currentContent.getCategory().isUncategorized()) {
        categoryKindCombo.setSelectedName(currentContent.getCategory().name);
      }
    }
  }

  @NotNull @Override protected JPanel createMessagePanel() {
    JPanel messagePanel = new JPanel(new GridLayout(4, 1));

    if (myMessage != null) {
      JComponent textComponent = createTextComponent();
      messagePanel.add(textComponent, BorderLayout.NORTH);
    }

    nameTextField = new JTextField(30);
    categoryKindCombo = new TemplateKindCombo();
    descriptionTextField = new JTextField(30);

    messagePanel.add(createInputPanel(nameTextField, "Name:", PlatformIcons.UP_DOWN_ARROWS));
    messagePanel.add(createCategoryPanel());
    messagePanel.add(createInputPanel(descriptionTextField, "Description:", null));

    myField = nameTextField;
    return messagePanel;
  }

  @Override public JComponent getPreferredFocusedComponent() {
    return categoryKindCombo;
  }

  protected JPanel createInputPanel(JComponent rightComponent, String leftHint, Icon icon) {
    JPanel panel = new JPanel(new BorderLayout());
    JLabel label = new JLabel(leftHint);
    Dimension dimension = label.getPreferredSize();
    dimension.width = 100;
    label.setPreferredSize(dimension);
    panel.add(label, BorderLayout.WEST);
    panel.add(rightComponent, BorderLayout.CENTER);
    if (icon != null) {
      JLabel upDownHint = new JLabel();
      upDownHint.setIcon(icon);
      upDownHint.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
      panel.add(upDownHint, BorderLayout.EAST);
    }
    return panel;
  }

  protected JPanel createCategoryPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    JLabel labelCategory = new JLabel("Category:");
    Dimension dimension = labelCategory.getPreferredSize();
    dimension.width = 100;
    labelCategory.setPreferredSize(dimension);
    categoryKindCombo = new TemplateKindCombo();
    labelCategory.setLabelFor(categoryKindCombo);

    inflateCategoryKind();

    panel.add(labelCategory, BorderLayout.WEST);
    panel.add(categoryKindCombo, BorderLayout.CENTER);
    return panel;
  }

  private void inflateCategoryKind() {
    LabManager.getInstance()
        .getStateOrNull()
        .ifPresent(state -> state.labCategories.forEach(this::addCategoryKind));
  }

  private void addCategoryKind(Category category) {
    categoryKindCombo.addItem(category.name, null, category.name);
  }

  public SnippetFile getSnippetFileWithoutPath() {
    if (getExitCode() == 0) {
      SnippetFile snippetFile = new SnippetFile();
      snippetFile.setCategory(new Category(categoryKindCombo.getSelectedName()));
      snippetFile.setDescription(descriptionTextField.getText());
      snippetFile.setName(nameTextField.getText());
      return snippetFile;
    }
    return null;
  }
}
