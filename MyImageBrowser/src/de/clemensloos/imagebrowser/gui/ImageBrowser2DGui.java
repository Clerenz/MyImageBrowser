package de.clemensloos.imagebrowser.gui;


import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.clemensloos.imagebrowser.ImageBrowser;
import de.clemensloos.imagebrowser.types.Event;
import de.clemensloos.imagebrowser.types.Group;
import de.clemensloos.imagebrowser.types.Image;
import de.clemensloos.imagebrowser.types.Tag;
import de.clemensloos.imagebrowser.types.User;


public class ImageBrowser2DGui implements ImageBrowserGui {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// UIManager.setLookAndFeel(com.jgoodies.looks.plastic.Plastic3DLookAndFeel.class.getCanonicalName());
			UIManager.setLookAndFeel(com.jgoodies.looks.windows.WindowsLookAndFeel.class.getCanonicalName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			@SuppressWarnings("unused")
			@Override
			public void run() {
				new ImageBrowser2DGui();
			}
		});
	}


	ImageBrowser imageBrowser;


	ImageBrowser2DGui() {

		initGui();

		// console.append("Application started");

		// log("What a nice day.");
		// log("Let's sort some pictures...");

		imageBrowser = new ImageBrowser(this);

//		 File f = new File("C:\\Users\\clemens.loos\\Desktop\\tmp\\Beispiel Bilder\\a.jpg");
//		 imageBrowser.addImage(f);
//		
//		 f = new File("C:\\Users\\clemens.loos\\Desktop\\tmp\\Beispiel Bilder\\b.jpg");
//		 imageBrowser.addImage(f);
//		
//		 f = new File("C:\\Users\\clemens.loos\\Desktop\\tmp\\Beispiel Bilder\\c.jpg");
//		 imageBrowser.addImage(f);
//		
//		 f = new File("C:\\Users\\clemens.loos\\Desktop\\tmp\\Beispiel Bilder\\d.jpg");
//		 imageBrowser.addImage(f);

		List<Image> images = imageBrowser.getImages();
		if (images != null) {
			for (Image im : images) {
				listModel.addElement(im);
			}
		}
	}


	// MAIN AREA
	DefaultListModel<Image> listModel;
	JList<Image> list;
	JScrollPane scrollPane;
	
	// LOWER AREA
	JPanel bottomPanel;
	JLabel hideBottomPanel;
	boolean bottomPanelVisible = true;
	
	JPanel bottomFilterPanel;
	DefaultListModel<Tag> tagListModel;
	JList<Tag> tagList;
	JScrollPane tagScrollPane;
	DefaultListModel<User> userListModel;
	JList<User> userList;
	JScrollPane userScrollPane;
	DefaultListModel<Group> groupListModel;
	JList<Group> groupList;
	JScrollPane groupScrollPane;
	
	// LEFT AREA
	JPanel leftPanel;
	JLabel hideLeftPanel;
	boolean leftPanelVisible = true;
	JTabbedPane leftTabbedPane;
	
	JScrollPane eventScrollPane;
	JTree eventTree;
	DefaultMutableTreeNode eventRootNode;
	
	JScrollPane dateScrollPane;
	
	
	JLabel leftComponent;
	JLabel leftComponent2;

	JSplitPane splitPaneVerti;
	JSplitPane splitPaneHoriz;

	JFrame frame;


//	JPanel mainPanel;


	// JTextArea console;

	private void initGui() {

		// BUILD MAIN AREA ===================================================

		listModel = new DefaultListModel<Image>();
		list = new JList<Image>(listModel);
		scrollPane = new JScrollPane(list);

		// BUILD LOWER AREA ==================================================

		hideBottomPanel = new JLabel("\\/");
		hideBottomPanel.setHorizontalAlignment(SwingConstants.CENTER);
		hideBottomPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent event) {
				if (bottomPanelVisible) {
					splitPaneVerti.setLastDividerLocation(splitPaneVerti.getDividerLocation());
					splitPaneVerti.setDividerLocation(splitPaneVerti.getHeight() - hideBottomPanel.getHeight()
							- splitPaneVerti.getDividerSize());
					hideBottomPanel.setText("/\\");
					bottomPanelVisible = false;
				}
				else {
					splitPaneVerti.setDividerLocation(splitPaneVerti.getLastDividerLocation());
					hideBottomPanel.setText("\\/");
					bottomPanelVisible = true;
				}
			}
		});
		
		tagListModel = new DefaultListModel<Tag>();
		tagList = new JList<Tag>(tagListModel);
		tagScrollPane = new JScrollPane(tagList);
		// XXX
		for (int i=0; i<10; i++) {
			tagListModel.addElement(new Tag("tagged " + i));			
		}
		
		userListModel = new DefaultListModel<User>();
		userList = new JList<User>(userListModel);
		userScrollPane = new JScrollPane(userList);
		// XXX
		for (int i=0; i<10; i++) {
			userListModel.addElement(new User("user name " + i));			
		}
		
		groupListModel = new DefaultListModel<Group>();
		groupList = new JList<Group>(groupListModel);
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupScrollPane = new JScrollPane(groupList);
		// XXX
		for (int i=0; i<10; i++) {
			groupListModel.addElement(new Group("group name " + i));			
		}

		bottomFilterPanel = new JPanel();
		bottomFilterPanel = new FormDebugPanel();

		String columnSpecs = "3dlu, pref:GROW, 3dlu, pref:GROW, 3dlu, pref:GROW, 3dlu";
		String rowSpecs = "3dlu, 150px:GROW, 1dlu, 3dlu";
		FormLayout layout = new FormLayout(columnSpecs, rowSpecs);
		layout.setColumnGroups(new int[][]{{2, 4, 6}});
		bottomFilterPanel.setLayout(layout);

		PanelBuilder builder = new PanelBuilder(layout, bottomFilterPanel);
		CellConstraints cc = new CellConstraints();

		builder.add(tagScrollPane, cc.xywh(2, 2, 1, 2));
		builder.add(userScrollPane, cc.xywh(4, 2, 1, 2));
		builder.add(groupScrollPane, cc.xywh(6, 2, 1, 2));
		// builder.add(console, cc.xy(2, 5));

		bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(bottomFilterPanel);
		bottomPanel.add(hideBottomPanel, BorderLayout.NORTH);

		// BUILD VERTICAL SPLIT PANE =========================================

		splitPaneVerti = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, bottomPanel);
		splitPaneVerti.setResizeWeight(1);
		splitPaneVerti.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!bottomPanelVisible && (int) evt.getNewValue() < (int) evt.getOldValue()) {
					hideBottomPanel.setText("\\/");
					bottomPanelVisible = true;
				}
			}
		});

		// BUILD LEFT AREA ===================================================
		
		eventRootNode = new DefaultMutableTreeNode("Events");
		buildEventTree();
		TreeModel treeModel = new DefaultTreeModel(eventRootNode, true);
		eventTree = new JTree(treeModel);
		eventScrollPane = new JScrollPane(eventTree);
		
		leftComponent = new JLabel("left component");
		leftComponent2 = new JLabel("left component 2");

		leftTabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		leftTabbedPane.addTab("Events", eventScrollPane);
		leftTabbedPane.addTab("Datum", leftComponent2);

		hideLeftPanel = new JLabel(" < ");
		hideLeftPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent event) {
				if (leftPanelVisible) {
					splitPaneHoriz.setLastDividerLocation(splitPaneHoriz.getDividerLocation());
					splitPaneHoriz.setDividerLocation(hideLeftPanel.getWidth());
					hideLeftPanel.setText(" > ");
					leftPanelVisible = false;
				}
				else {
					splitPaneHoriz.setDividerLocation(splitPaneHoriz.getLastDividerLocation());
					hideLeftPanel.setText(" < ");
					leftPanelVisible = true;
				}
			}
		});
		leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(leftTabbedPane);
		leftPanel.add(hideLeftPanel, BorderLayout.EAST);

		// BUILD HORIZONTAL SPLIT PANE =======================================

		splitPaneHoriz = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, splitPaneVerti);
		splitPaneHoriz.setResizeWeight(0);
		splitPaneHoriz.setDividerLocation(150);
		splitPaneHoriz.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!leftPanelVisible && (int) evt.getNewValue() > (int) evt.getOldValue()) {
					hideLeftPanel.setText(" < ");
					leftPanelVisible = true;
				}
			}
		});

		// BUILD THE FRAME ===================================================

		frame = new JFrame("Image Browser");
		// frame.setUndecorated(true);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				imageBrowser.shutDown();
				System.exit(0);
			}
		});

		frame.add(splitPaneHoriz);
		frame.setResizable(true);
		frame.setLocationByPlatform(true);
		frame.pack();
		frame.setMinimumSize(frame.getSize());
		frame.setSize(750, 500);
		frame.setVisible(true);
		// frame.setExtendedState(Frame.MAXIMIZED_BOTH);

	}
	
	
	public void buildEventTree() {
		
		eventRootNode.removeAllChildren();
		
		Event evt11 = new Event("Event 11", 0l, 0l);
		Event evt12 = new Event("Event 12", 0l, 0l);
		Event evt13 = new Event("Event 13", 0l, 0l);
		Event evt21 = new Event("Event 21", 0l, 0l);
		Event evt22 = new Event("Event 22", 0l, 0l);
		Event evt23 = new Event("Event 23", 0l, 0l);
		
		DefaultMutableTreeNode node2013 = new DefaultMutableTreeNode("2013");
		DefaultMutableTreeNode node2014 = new DefaultMutableTreeNode("2014");
		
		DefaultMutableTreeNode mevt11 = new DefaultMutableTreeNode(evt11, false);
		node2013.add(mevt11);
		DefaultMutableTreeNode mevt12 = new DefaultMutableTreeNode(evt12, false);
		node2013.add(mevt12);
		DefaultMutableTreeNode mevt13 = new DefaultMutableTreeNode(evt13, false);
		node2013.add(mevt13);
		
		DefaultMutableTreeNode mevt21 = new DefaultMutableTreeNode(evt21, false);
		node2014.add(mevt21);
		DefaultMutableTreeNode mevt22 = new DefaultMutableTreeNode(evt22, false);
		node2014.add(mevt22);
		DefaultMutableTreeNode mevt23 = new DefaultMutableTreeNode(evt23, false);
		node2014.add(mevt23);
		
		eventRootNode.add(node2013);
		eventRootNode.add(node2014);
		
	}


	@Override
	public void log(String message) {
		// if(message != null) {
		// console.append("\n" + message);
		// }
	}
}
