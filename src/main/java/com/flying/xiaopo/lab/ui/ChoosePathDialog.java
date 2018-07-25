package com.flying.xiaopo.lab.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import java.awt.BorderLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wupanjie on 2017/10/27.
 */
public class ChoosePathDialog extends Messages.InputDialog {

  public ChoosePathDialog(String message, @Nls String title, @Nullable Icon icon,
      @Nullable String initialValue, InputValidator validator) {
    super(message, title, icon, initialValue, validator);
  }

  @NotNull @Override protected JPanel createMessagePanel() {
    JPanel messagePanel = new JPanel(new BorderLayout());
    if (myMessage != null) {
      JComponent textComponent = createTextComponent();
      messagePanel.add(textComponent, BorderLayout.NORTH);
    }
    FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();

    TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();
    textFieldWithBrowseButton.getTextField().setColumns(30);
    textFieldWithBrowseButton.addBrowseFolderListener(new TextBrowseFolderListener(descriptor));
    myField = textFieldWithBrowseButton.getTextField();

    messagePanel.add(textFieldWithBrowseButton, BorderLayout.SOUTH);

    return messagePanel;
  }
}
