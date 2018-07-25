package com.flying.xiaopo.lab.ui;

import com.intellij.ui.SpeedSearchBase;
import javax.swing.JList;
import org.jetbrains.annotations.Nullable;

/**
 * @author wupanjie on 2017/11/3.
 */
public class SnippetSpeedSearch extends SpeedSearchBase<JList> {

  public SnippetSpeedSearch(JList list) {
    super(list);
  }

  @Override protected int getSelectedIndex() {
    return 0;
  }

  @Override protected Object[] getAllElements() {
    return new Object[0];
  }

  @Nullable @Override protected String getElementText(Object o) {
    return null;
  }

  @Override protected void selectElement(Object o, String s) {

  }
}
