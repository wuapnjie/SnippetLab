package com.flying.xiaopo.lab.model;

import com.flying.xiaopo.lab.LabManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.Serializable;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * @author wupanjie on 2017/10/26.
 */
public class SnippetFile implements Serializable {

  @NotNull private String name = "";
  @NotNull private String path = "";
  @NotNull private String createDate = "";
  @NotNull private String categoryName = Category.UNCATEGORIZED.name;
  private String description = "";

  public SnippetFile() {

  }

  public SnippetFile(@NotNull String path, @NotNull String createDate) {
    this.path = path;
    this.createDate = createDate;
  }

  public SnippetFile(SnippetFile src) {
    this.name = src.name;
    this.path = src.path;
    this.createDate = src.createDate;
    this.categoryName = src.categoryName;
    this.description = src.description;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Optional<VirtualFile> getVirtualFile() {
    return Optional.ofNullable(LocalFileSystem.getInstance().refreshAndFindFileByPath(path));
  }

  @NotNull public String getPath() {
    return path;
  }

  public void setPath(@NotNull String path) {
    this.path = path;
  }

  @NotNull public String getCreateDate() {
    return createDate;
  }

  public void setCreateDate(@NotNull String createDate) {
    this.createDate = createDate;
  }

  public Optional<Category> getCategory() {
    return LabManager.getInstance()
        .getCategoryOrNull(categoryName);
  }

  @NotNull public String getCategoryName() {
    return categoryName;
  }

  public void setCategory(Category category) {
    this.categoryName = category.name;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  @NotNull public String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof SnippetFile) {
      SnippetFile other = (SnippetFile) obj;
      return this.path.equals(other.path) &&
          this.name.equals(other.name) &&
          this.categoryName.equals(other.categoryName) &&
          this.description.equals(other.description) &&
          this.createDate.equals(other.createDate);
    }
    return false;
  }

  @Override public int hashCode() {
    int result = 17;
    result = 31 * result + name.hashCode();
    result = 31 * result + path.hashCode();
    result = 31 * result + createDate.hashCode();
    result = 31 * result + categoryName.hashCode();
    result = 31 * result + description.hashCode();
    return result;
  }

  public boolean isUncategorized() {
    return StringUtil.equals(categoryName, Category.UNCATEGORIZED.name);
  }

}
