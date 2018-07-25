package com.flying.xiaopo.lab;

import com.flying.xiaopo.lab.model.Category;
import com.flying.xiaopo.lab.ui.LabUI;
import com.intellij.ui.content.Content;

/**
 * @author wupanjie on 2017/10/29.
 */
public interface LabContent {

  Category getCategory();

  LabUI getLabUI();

  LabPresenter getLabPresenter();

  String getName();

  Content getContent();
}
