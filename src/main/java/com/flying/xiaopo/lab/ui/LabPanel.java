package com.flying.xiaopo.lab.ui;

import com.flying.xiaopo.lab.LabManager;
import com.flying.xiaopo.lab.LabPresenter;
import com.flying.xiaopo.lab.LabProject;
import com.flying.xiaopo.lab.action.ConfigureStorageAction;
import com.flying.xiaopo.lab.action.CreateNewCategoryAction;
import com.flying.xiaopo.lab.action.DeleteCategoryAction;
import com.flying.xiaopo.lab.action.RenameCategoryAction;
import com.flying.xiaopo.lab.model.Category;
import com.flying.xiaopo.lab.model.SnippetFile;
import com.intellij.ide.dnd.DnDActionInfo;
import com.intellij.ide.dnd.DnDDragStartBean;
import com.intellij.ide.dnd.DnDImage;
import com.intellij.ide.dnd.DnDSupport;
import com.intellij.ide.dnd.TransferableWrapper;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.speedSearch.SpeedSearchSupply;
import com.intellij.util.EditSourceOnDoubleClickHandler;
import com.intellij.util.Function;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wupanjie on 2017/10/26.
 */
public class LabPanel extends SimpleToolWindowPanel implements LabUI {
  private JList<SnippetFile> snippetFileList;
  private DefaultListModel<SnippetFile> snippetFileListModel;
  private SnippetCellRenderer snippetCellRenderer;
  private DefaultListModel<SnippetFile> selectedSnippetListModel = new DefaultListModel<>();

  private final LabPresenter labPresenter;
  private final LabManager labManager;
  private final Category category;

  private final Point rightClickPosition = new Point();
  private SnippetFile targetSnippet;
  private int targetSnippetPosition;

  private JPopupMenu snippetMenu;

  public LabPanel(LabPresenter labPresenter, Category category) {
    super(true);
    this.labPresenter = labPresenter;
    this.labManager = LabManager.getInstance();
    this.category = category;

    setBorder(JBUI.Borders.empty());

    initContent();
    initListener();
    initSearch();
    initDnDAction();
  }

  private void initDnDAction() {
    DnDSupport.createBuilder(snippetFileList)
        .setBeanProvider(this::provideDnDBean)
        .setImageProvider(this::provideDnDImage)
        .setDisposableParent(this)
        .install();
  }

  private DnDDragStartBean provideDnDBean(DnDActionInfo dnDActionInfo) {
    SnippetFile snippetFile = getSelectSnippet();
    if (snippetFile != null) {
      return new DnDDragStartBean(new TransferableWrapper() {
        @Nullable @Override public TreeNode[] getTreeNodes() {
          return new TreeNode[0];
        }

        @Nullable @Override public PsiElement[] getPsiElements() {
          Optional<VirtualFile> optional = snippetFile.getVirtualFile();
          if (optional.isPresent()) {
            VirtualFile virtualFile = optional.get();
            PsiElement psiElement =
                PsiManager.getInstance(LabProject.getInstance().getProject()).findFile
                    (virtualFile);
            return new PsiElement[] {psiElement};
          }
          return new PsiElement[0];
        }

        @Nullable @Override public List<File> asFileList() {
          return Arrays.asList(new File(snippetFile.getPath()));
        }
      });
    }
    return null;
  }

  private DnDImage provideDnDImage(DnDActionInfo dnDActionInfo) {
    SnippetFile snippetFile = getSelectSnippet();
    if (snippetFile != null) {
      JLabel label = new JLabel(snippetFile.getName());
      label.setOpaque(true);
      label.setSize(label.getPreferredSize());

      BufferedImage image = UIUtil.createImage(label.getWidth(), label.getHeight(), BufferedImage
          .TYPE_INT_ARGB);

      Graphics2D graphics2D = image.createGraphics();
      graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
      label.paint(graphics2D);
      graphics2D.dispose();

      return new DnDImage(image, new Point(-image.getWidth(), -image.getHeight()));
    }

    return null;
  }

  private void initSearch() {
    new ListSpeedSearch<SnippetFile>(snippetFileList,
        (Function<SnippetFile, String>) SnippetFile::getName) {
      @Override protected void selectElement(Object o, String query) {
        selectedSnippetListModel.clear();
        findAllFilteredElements(query);
        snippetFileList.setModel(selectedSnippetListModel);

        if (StringUtil.isEmpty(query)) {
          snippetFileList.setModel(snippetFileListModel);
        }
      }

      private void findAllFilteredElements(String s) {
        String _s = s.trim();

        Object[] elements = getAllListElements();
        for (int i = 0; i < elements.length; i++) {
          final Object element = elements[i];
          if (isMatchingElement(element, _s)) {
            selectedSnippetListModel.addElement(snippetFileListModel.getElementAt(i));
          }
        }
      }

      Object[] getAllListElements() {
        return snippetFileListModel.toArray();
      }

      @Override protected Object[] getAllElements() {
        return snippetFileListModel.toArray();
      }
    }.addChangeListener(evt -> {
      if (evt.getPropertyName().equals(SpeedSearchSupply.ENTERED_PREFIX_PROPERTY_NAME) &&
          evt.getNewValue() == null) {
        snippetFileList.setModel(snippetFileListModel);
      }
    });
  }

  private void openSourceFile() {
    SnippetFile snippetFile = getSelectSnippet();
    if (snippetFile != null) {
      snippetFile.getVirtualFile()
          .ifPresent(labPresenter::openSnippetFile);
    }
  }

  private JComponent createToolbarPanel() {
    final DefaultActionGroup group = new DefaultActionGroup();

    group.add(new CreateNewCategoryAction());

    if (!category.isAll() && !category.isUncategorized()) {
      group.add(new RenameCategoryAction());
      group.add(new DeleteCategoryAction());
    }

    group.add(new ConfigureStorageAction());

    final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar(
        "SnippetBottomToolbar", group, true);

    JPanel toolbarPanel = JBUI.Panels.simplePanel(actionToolBar.getComponent());
    toolbarPanel.setBorder(JBUI.Borders.empty());
    return toolbarPanel;
  }

  private void initContent() {
    initListModel();
    snippetCellRenderer = new SnippetCellRenderer();

    snippetFileList = new JBList<>(snippetFileListModel);
    snippetFileList.setCellRenderer(snippetCellRenderer);

    JPanel contentPanel = new JPanel(new BorderLayout());
    JScrollPane listPanel = ScrollPaneFactory.createScrollPane(snippetFileList);
    listPanel.setBorder(JBUI.Borders.empty());
    contentPanel.add(listPanel, BorderLayout.CENTER);
    contentPanel.add(createToolbarPanel(), BorderLayout.SOUTH);

    setContent(contentPanel);
  }

  private SnippetFile getSelectSnippet() {
    final int selectedIndex = snippetFileList.getSelectedIndex();
    if (selectedIndex == -1 || selectedIndex >= snippetFileListModel.size()) return null;
    return snippetFileListModel.getElementAt(selectedIndex);
  }

  private void initListener() {
    // double click
    EditSourceOnDoubleClickHandler.install(snippetFileList, this::openSourceFile);

    // right click
    ActionGroup group =
        (ActionGroup) ActionManager.getInstance().getAction("SnippetLab.SnippetPopupMenu");
    ActionPopupMenu popupMenu =
        ActionManager.getInstance().createActionPopupMenu("SnippetPopupMenu", group);
    snippetMenu = popupMenu.getComponent();

    PopupHandler popupHandler = new PopupHandler() {
      public void invokePopup(Component comp, int x, int y) {
        rightClickPosition.setLocation(x, y);
        targetSnippetPosition = snippetFileList.locationToIndex(rightClickPosition);
        if (targetSnippetPosition < 0 ||
            targetSnippetPosition >= snippetFileListModel.size() ||
            !snippetFileList.getCellBounds(targetSnippetPosition, targetSnippetPosition)
                .contains(rightClickPosition)) {
          targetSnippetPosition = -1;
          targetSnippet = null;
          return;
        }

        targetSnippet = snippetFileListModel.getElementAt(targetSnippetPosition);
        snippetMenu.show(comp, x, y);
      }
    };

    snippetFileList.addMouseListener(popupHandler);
  }

  @Override public SnippetFile getTargetSnippet() {
    return targetSnippet;
  }

  @Override public int getTargetSnippetPosition() {
    return targetSnippetPosition;
  }

  private void initListModel() {
    snippetFileListModel = new DefaultListModel<>();

    labManager.getSnippetFiles(category)
        .forEach(snippetFileListModel::addElement);
  }

  @Override public void insertNewSnippetFile(@NotNull SnippetFile snippetInfo) {
    snippetFileListModel.addElement(snippetInfo);
  }

  @Override public void refreshSnippet(SnippetFile snippetFile, SnippetFile info) {
    final int index = snippetFileListModel.indexOf(snippetFile);
    if (index != -1) {
      snippetFileListModel.set(index, new SnippetFile(info));
    }
  }

  @Override public void refreshList(String newName) {
    final int size = snippetFileListModel.size();
    for (int i = 0; i < size; i++) {
      snippetFileListModel.getElementAt(i).setCategoryName(newName);
    }
  }

  @Override public void refreshList(List<SnippetFile> snippetFiles) {
    snippetFileListModel.clear();
    snippetFiles.forEach(snippetFileListModel::addElement);
  }

  @Override public void removeSnippet(SnippetFile snippetFile) {
    snippetFileListModel.removeElement(snippetFile);
  }

  @Override public void renameSnippet(SnippetFile snippetFile, String newName) {
    final int index = snippetFileListModel.indexOf(snippetFile);
    if (index != -1) {
      snippetFileListModel.getElementAt(index).setName(newName);
    }
  }
}
