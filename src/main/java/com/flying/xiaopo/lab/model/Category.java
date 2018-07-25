package com.flying.xiaopo.lab.model;

import com.intellij.openapi.util.text.StringUtil;
import java.util.ArrayList;

/**
 * @author wupanjie on 2017/10/28.
 */
public class Category {
  public String name = "";
  public ArrayList<SnippetFile> snippets = new ArrayList<>();

  public static Category ALL = new Category("All Snippets");
  public static Category UNCATEGORIZED = new Category("Uncategorized");

  public Category() {

  }

  public Category(String name) {
    this.name = name;
  }

  public boolean isUncategorized() {
    return UNCATEGORIZED.equals(this);
  }

  public boolean isAll() {
    return ALL.equals(this);
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof Category) {
      Category other = (Category) obj;
      return StringUtil.equals(this.name, other.name);
    }
    return false;
  }

  @Override public int hashCode() {
    int result = 17;
    result = 31 * result + (name == null ? 0 : name.hashCode());
    return result;
  }
}
