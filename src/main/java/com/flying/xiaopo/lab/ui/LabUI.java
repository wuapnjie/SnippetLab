package com.flying.xiaopo.lab.ui;

import com.flying.xiaopo.lab.model.SnippetFile;
import com.intellij.openapi.Disposable;
import java.util.List;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @author wupanjie on 2017/10/26.
 */
public interface LabUI extends Disposable{

  JComponent getComponent();

  void insertNewSnippetFile(@NotNull SnippetFile snippetInfo);

  SnippetFile getTargetSnippet();

  int getTargetSnippetPosition();

  void refreshList(String newName);

  void refreshSnippet(SnippetFile snippetFile, SnippetFile info);

  void refreshList(List<SnippetFile> snippetFiles);

  void removeSnippet(SnippetFile snippetFile);

  void renameSnippet(SnippetFile snippetFile, String newName);

  @Override default void dispose() {

  }
}
