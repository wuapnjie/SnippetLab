package com.flying.xiaopo.lab;

import com.flying.xiaopo.lab.model.Category;
import com.flying.xiaopo.lab.model.SnippetFile;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wupanjie on 2017/10/27.
 */
@State(name = "SnippetLabManager", storages = @Storage("snippet_lab_manager.xml"))
public class LabManager implements PersistentStateComponent<LabManager.State> {

  public static class State {
    public String labStoragePath;
    public ArrayList<SnippetFile> labSnippets = new ArrayList<>();
    public ArrayList<Category> labCategories = new ArrayList<>();

    @Override public boolean equals(Object obj) {
      if (obj instanceof State) {
        State other = (State) obj;
        return StringUtil.equals(this.labStoragePath, other.labStoragePath) &&
            this.labSnippets.equals(other.labSnippets) &&
            this.labCategories.equals(other.labCategories);
      }
      return false;
    }

    @Override public int hashCode() {
      int result = 17;
      result = 31 * result + (labStoragePath == null ? 0 : labStoragePath.hashCode());
      result = 31 * result + (labSnippets == null ? 0 : labSnippets.hashCode());
      return result;
    }
  }

  private State state;
  private final LabContentManager labContentManager = LabContentManager.getInstance();

  public static LabManager getInstance() {
    return ServiceManager.getService(LabManager.class);
  }

  @Nullable @Override public State getState() {
    return this.state;
  }

  @NotNull public Optional<State> getStateOrNull() {
    return Optional.ofNullable(this.state);
  }

  @Override public void loadState(State state) {
    this.state = state;
  }

  public boolean containsCategory(final String name) {
    return state.labCategories
        .stream()
        .filter(category -> StringUtil.equals(category.name, name))
        .count() > 0;
  }

  public boolean createNewCategoryAndShow(String categoryName) {
    if (containsCategory(categoryName)) {
      return false;
    }

    Category newCategory = new Category(categoryName);
    File categoryFolder = new File(state.labStoragePath, categoryName);
    if (!categoryFolder.exists()) {
      FileUtil.createDirectory(categoryFolder);
    }

    state.labCategories.add(newCategory);
    loadState(state);

    LabContent newLabContent = labContentManager.createNewLabContent(newCategory);
    labContentManager.addLabContent(newLabContent);
    labContentManager.selectLabContent(newLabContent);
    return true;
  }

  public Category createNewCategory(String categoryName) {
    if (containsCategory(categoryName)) {
      return null;
    }

    Category newCategory = new Category(categoryName);
    File categoryFolder = new File(state.labStoragePath, categoryName);
    if (!categoryFolder.exists()) {
      FileUtil.createDirectory(categoryFolder);
    }

    state.labCategories.add(newCategory);
    loadState(state);

    LabContent newLabContent = labContentManager.createNewLabContent(newCategory);
    labContentManager.addLabContent(newLabContent);
    return newCategory;
  }

  public Optional<Category> getCategoryOrNull(String name) {
    for (Category labCategory : state.labCategories) {
      if (StringUtil.equals(labCategory.name, name)) {
        return Optional.of(labCategory);
      }
    }

    return Optional.empty();
  }

  public void deleteCurrentShowCategory() {
    final LabContent currentContent = labContentManager.getCurrentLabContent();
    final Category currentCategory = labContentManager.getCurrentShowCategory();
    if (currentContent == null || currentCategory == null) {
      return;
    }

    getCategoryOrNull(currentCategory.name)
        .ifPresent(category -> {
          state.labCategories.remove(category);
          state.labSnippets.removeAll(category.snippets);

          File categoryFolder = new File(state.labStoragePath, category.name);
          FileUtil.delete(categoryFolder);

          loadState(state);
          labContentManager.removeLabContent(category.name);
          labContentManager.getLabContentAll().getLabUI().refreshList(state.labSnippets);
        });
  }

  public void renameCurrentShowCategory(final String newName) {
    final LabContent currentContent = labContentManager.getCurrentLabContent();
    final Category currentCategory = labContentManager.getCurrentShowCategory();
    if (currentContent == null || currentCategory == null) {
      return;
    }

    getCategoryOrNull(currentCategory.name)
        .ifPresent(category -> {
          final String oldName = category.name;
          category.name = newName;
          currentCategory.name = newName;

          loadState(state);
          labContentManager.renameCurrentLabContent(newName);

          try {
            File oldFolder = new File(state.labStoragePath, oldName);
            FileUtil.rename(oldFolder, newName);
            File newFolder = new File(state.labStoragePath, newName);

            category.snippets
                .forEach(snippetFile -> {
                  File newPath = new File(newFolder, snippetFile.getName());
                  snippetFile.setPath(newPath.getAbsolutePath());
                });
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }

  public void addNewSnippet(final SnippetFile snippetFile, String categoryName) {
    // add to all
    state.labSnippets.add(new SnippetFile(snippetFile));

    // add to categorized lab
    if (categoryName != null && !categoryName.equals(Category.UNCATEGORIZED.name)) {
      getCategoryOrNull(categoryName)
          .ifPresent(c -> c.snippets.add(new SnippetFile(snippetFile)));
    }

    loadState(state);
  }

  public void deleteSnippet(final SnippetFile snippetFile) {
    if (snippetFile == null) {
      return;
    }

    state.labSnippets.remove(snippetFile);

    getCategoryOrNull(snippetFile.getCategoryName())
        .ifPresent(category -> category.snippets.remove(snippetFile));

    loadState(state);
  }

  public void moveSnippet(final SnippetFile snippetFile, Category dstCategory) {
    if (snippetFile == null || snippetFile.getCategoryName().equals(dstCategory.name)) {
      return;
    }

    Category[] srcDst = new Category[2];
    state.labCategories
        .forEach(category -> {
          if (StringUtil.equals(category.name, snippetFile.getCategoryName())) {
            srcDst[0] = category;
          } else if (dstCategory.equals(category)) {
            srcDst[1] = category;
          }
        });

    File fromDir = new File(state.labStoragePath, srcDst[0].name);
    if (!fromDir.exists()) {
      FileUtil.createDirectory(fromDir);
    }
    File toDir = new File(state.labStoragePath, srcDst[1].name);
    if (!toDir.exists()) {
      FileUtil.createDirectory(toDir);
    }
    File fromFile = new File(snippetFile.getPath());
    File toFile = new File(toDir, fromFile.getName());
    fromFile.renameTo(toFile);

    srcDst[0].snippets
        .removeIf(snippetFile::equals);
    final LabContent srcContent = labContentManager.getLabContent(srcDst[0].name);
    if (srcContent != null) {
      srcContent.getLabUI()
          .removeSnippet(snippetFile);
    }

    SnippetFile added = new SnippetFile(snippetFile);
    added.setCategory(srcDst[1]);
    added.setPath(toFile.getAbsolutePath());
    srcDst[1].snippets
        .add(added);
    final LabContent dstContent = labContentManager.getLabContent(srcDst[1].name);
    if (dstContent != null) {
      dstContent.getLabUI()
          .insertNewSnippetFile(added);
    }

    labContentManager.getLabContentAll()
        .getLabUI()
        .refreshSnippet(snippetFile, added);

    loadState(state);
  }

  public void renameSnippet(final SnippetFile targetSnippet, final String newName) {
    if (targetSnippet == null) {
      return;
    }

    final SnippetFile tempInfo = new SnippetFile(targetSnippet);

    state.labSnippets
        .stream()
        .filter(tempInfo::equals)
        .findFirst()
        .ifPresent(findSnippet -> findSnippet.setName(tempInfo.getName()));

    getCategoryOrNull(targetSnippet.getCategoryName())
        .ifPresent(category -> category.snippets
            .stream()
            .filter(tempInfo::equals)
            .findFirst()
            .ifPresent(findSnippet -> {
              findSnippet.setName(tempInfo.getName());
            }));

    loadState(state);
  }

  @NotNull public ArrayList<SnippetFile> getSnippetFiles(Category category) {
    if (category.isAll()) {
      //ArrayList<SnippetFile> result = new ArrayList<>();
      //state.labSnippets.clear();
      //for (Category labCategory : state.labCategories) {
      //  result.addAll(labCategory.snippets);
      //  state.labSnippets.addAll(labCategory.snippets);
      //}

      return new ArrayList<>(state.labSnippets);
    } else if (category.isUncategorized()) {
      ArrayList<SnippetFile> result = new ArrayList<>(state.labSnippets);
      for (Category labCategory : state.labCategories) {
        result.removeAll(labCategory.snippets);
      }

      Category.UNCATEGORIZED.snippets.clear();
      Category.UNCATEGORIZED.snippets.addAll(result);

      return result;
    }

    for (Category labCategory : state.labCategories) {
      if (StringUtil.equals(labCategory.name, category.name)) {
        return new ArrayList<>(labCategory.snippets);
      }
    }

    return new ArrayList<>();
  }
}
