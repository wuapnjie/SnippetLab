package com.flying.xiaopo.lab.kits;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * @author wupanjie on 2017/10/25.
 */
public abstract class TypedListCellRenderer<T> extends DefaultListCellRenderer {

  @SuppressWarnings("unchecked") @Override
  public Component getListCellRendererComponent(JList<?> list, Object value, int index,
      boolean isSelected, boolean cellHasFocus) {
    return getTypedListCellRendererComponent(list, (T) value, index, isSelected, cellHasFocus);
  }

  public abstract Component getTypedListCellRendererComponent(JList<?> list, T value, int index,
      boolean isSelected, boolean cellHasFocus);
}
