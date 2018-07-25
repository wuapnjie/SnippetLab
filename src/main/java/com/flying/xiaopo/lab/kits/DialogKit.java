package com.flying.xiaopo.lab.kits;

import com.flying.xiaopo.lab.LabManager;
import com.flying.xiaopo.lab.model.Category;
import com.flying.xiaopo.lab.model.SnippetFile;
import com.flying.xiaopo.lab.ui.AddSnippetDialog;
import com.flying.xiaopo.lab.ui.AddUncategorizedSnippetDialog;
import com.flying.xiaopo.lab.ui.ChoosePathDialog;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import java.io.File;
import javax.swing.Icon;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * @author wupanjie on 2017/10/27.
 */
public class DialogKit {
  private DialogKit() {
    //no instance
  }

  public final static InputValidator isFolderPath = new InputValidator() {
    @Override public boolean checkInput(String input) {
      if (StringUtil.isEmpty(input)) return false;

      File file = new File(input);
      return file.exists() && file.isDirectory();
    }

    @Override public boolean canClose(String input) {
      return checkInput(input);
    }
  };

  public final static InputValidator notEmpty = new InputValidator() {
    @Override public boolean checkInput(String input) {
      return !StringUtil.isEmpty(input);
    }

    @Override public boolean canClose(String input) {
      return checkInput(input);
    }
  };

  public final static InputValidator isValidCategory = new InputValidatorEx() {
    @Nullable @Override public String getErrorText(String input) {
      if (StringUtil.isEmpty(input)) {
        return "输入不能为空";
      } else if (LabManager.getInstance().containsCategory(input)) {
        return "Category已经存在";
      }
      return null;
    }

    @Override public boolean checkInput(String input) {
      return !StringUtil.isEmpty(input) &&
          !LabManager.getInstance().containsCategory(input) &&
          !StringUtil.equals(input, Category.ALL.name) &&
          !StringUtil.equals(input, Category.UNCATEGORIZED.name);
    }

    @Override public boolean canClose(String input) {
      return checkInput(input);
    }
  };

  public final static InputValidator isValidName = new InputValidatorEx() {
    @Nullable @Override public String getErrorText(String input) {
      if (StringUtil.isEmpty(input)) {
        return "输入不能为空";
      } else if (StringUtil.length(input) > 50) {
        return "Name过长";
      }
      return null;
    }

    @Override public boolean checkInput(String input) {
      return !StringUtil.isEmpty(input) && StringUtil.length(input) <= 50;
    }

    @Override public boolean canClose(String input) {
      return checkInput(input);
    }
  };

  @Nullable public static String showChooseFolderPathDialog(String
      message,
      @Nls(capitalization = Nls.Capitalization.Title) String title, @Nullable
      Icon icon, @Nullable String initialValue) {
    ChoosePathDialog dialog =
        new ChoosePathDialog(message, title, icon, initialValue, isFolderPath);
    dialog.show();
    return dialog.getInputString();
  }

  @Nullable public static SnippetFile showAddSnippetDialog(String initialName, Category
      initialCategory) {
    AddSnippetDialog dialog = initialCategory.isUncategorized() ?
        new AddUncategorizedSnippetDialog(initialName, notEmpty) :
        new AddSnippetDialog(initialName, initialCategory, notEmpty);
    dialog.show();
    return dialog.getSnippetFileWithoutPath();
  }

  public static String showCreateNewCategoryDialog() {
    return Messages.showInputDialog("Input category name ", "Create New Category", null,
        null, isValidCategory);
  }

  public static String showRenameSnippetDialog() {
    return Messages.showInputDialog("Input new snippet name ", "Rename Snippet", null,
        "", isValidName);
  }

  public static String showRenameCategoryDialog() {
    return Messages.showInputDialog("Input new category name ", "Rename Category", null,
        null, isValidCategory);
  }

  @SuppressWarnings("DialogTitleCapitalization")
  public static boolean showDeleteSnippetConfirmDialog() {
    return Messages.showOkCancelDialog(
        "You can't undo this action.",
        "Are you sure you want to delete this snippet?",
        "Delete",
        "Cancel",
        null) == Messages.OK;
  }

  @SuppressWarnings("DialogTitleCapitalization")
  public static boolean showDeleteCategoryConfirmDialog() {
    return Messages.showOkCancelDialog(
        "You can't undo this action.",
        "Are you sure you want to delete this category?",
        "Delete",
        "Cancel",
        null) == Messages.OK;
  }
}
