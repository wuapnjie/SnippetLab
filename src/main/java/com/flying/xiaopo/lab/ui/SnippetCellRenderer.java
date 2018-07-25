package com.flying.xiaopo.lab.ui;

import com.flying.xiaopo.lab.kits.TypedListCellRenderer;
import com.flying.xiaopo.lab.model.SnippetFile;
import java.awt.Component;
import javax.swing.JList;

/**
 * @author wupanjie on 2017/10/26.
 */
public class SnippetCellRenderer extends TypedListCellRenderer<SnippetFile> {
  @Override
  public Component getTypedListCellRendererComponent(JList<?> list, SnippetFile value, int index,
      boolean isSelected, boolean cellHasFocus) {

    SnippetCell snippetCell = new SnippetCell();

    snippetCell.bind(value, isSelected);

    return snippetCell.rootPanel;
  }
}
