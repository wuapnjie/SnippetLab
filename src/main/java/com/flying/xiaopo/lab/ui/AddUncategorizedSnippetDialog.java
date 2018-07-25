package com.flying.xiaopo.lab.ui;

import com.flying.xiaopo.lab.model.Category;
import com.flying.xiaopo.lab.model.SnippetFile;
import com.intellij.ide.actions.TemplateKindCombo;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.util.PlatformIcons;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jetbrains.annotations.NotNull;

/**
 * @author wupanjie on 2017/10/29.
 */
public class AddUncategorizedSnippetDialog extends AddSnippetDialog {

  public AddUncategorizedSnippetDialog(String initialName,
      InputValidator validator) {
    super(initialName, Category.UNCATEGORIZED, validator);
  }

  @NotNull @Override protected JPanel createMessagePanel() {
    JPanel messagePanel = new JPanel(new GridLayout(3, 1));

    if (myMessage != null) {
      JComponent textComponent = createTextComponent();
      messagePanel.add(textComponent, BorderLayout.NORTH);
    }

    nameTextField = new JTextField(30);
    categoryKindCombo = new TemplateKindCombo();
    descriptionTextField = new JTextField(30);

    messagePanel.add(createInputPanel(nameTextField, "Name:", PlatformIcons.UP_DOWN_ARROWS));
    messagePanel.add(createInputPanel(descriptionTextField, "Description:", null));

    myField = nameTextField;
    return messagePanel;
  }

  public SnippetFile getSnippetFileWithoutPath() {
    if (getExitCode() == 0) {
      SnippetFile snippetFile = new SnippetFile();
      snippetFile.setDescription(descriptionTextField.getText());
      snippetFile.setName(nameTextField.getText());
      return snippetFile;
    }
    return null;
  }
}
