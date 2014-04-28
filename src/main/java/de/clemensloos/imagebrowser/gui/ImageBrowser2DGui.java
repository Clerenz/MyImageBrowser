package de.clemensloos.imagebrowser.gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.clemensloos.imagebrowser.ImageBrowser;
import de.clemensloos.imagebrowser.types.ImgDate;
import de.clemensloos.imagebrowser.types.DateTreeHelper;
import de.clemensloos.imagebrowser.types.ImgEvent;
import de.clemensloos.imagebrowser.types.ImgGroup;
import de.clemensloos.imagebrowser.types.Image;
import de.clemensloos.imagebrowser.types.ImgPerson;
import de.clemensloos.imagebrowser.types.ImgTag;
import de.clemensloos.imagebrowser.utils.MyProperties;


public class ImageBrowser2DGui implements ImageBrowserGui {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// UIManager.setLookAndFeel(com.jgoodies.looks.windows.WindowsLookAndFeel.class.getCanonicalName());
			// UIManager.setLookAndFeel(com.jtattoo.plaf.hifi.HiFiLookAndFeel.class.getCanonicalName());
			UIManager.setLookAndFeel(com.jtattoo.plaf.noire.NoireLookAndFeel.class.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ImageBrowser2DGui();
			}
		});
	}


	ImageBrowser imageBrowser;


	ImageBrowser2DGui() {

		imageBrowser = new ImageBrowser(this);

		loadProperties();

		initGui();

		refreshGuiComponents();
	}


	private MyProperties guiProperties;


	private void loadProperties() {

		String propFile = imageBrowser.getProperty("gui_properties_file");
		guiProperties = new MyProperties(imageBrowser, propFile, true);

	}


	// MAIN AREA
	JPanel mainPanel;
	JScrollPane scrollPane;

	// LOWER AREA
	JPanel bottomPanel;

	JPanel hideBottomPanel;
	JButton ratingDown;
	JButton ratingNorm;
	JButton ratingUp;
	JLabel hideBottomLabel;
	boolean bottomPanelVisible = true;

	DefaultListModel<ImgTag> tagListModel;
	JList<ImgTag> tagList;
	JScrollPane tagScrollPane;
	DefaultListModel<ImgPerson> personListModel;
	JList<ImgPerson> personList;
	JScrollPane personScrollPane;
	DefaultListModel<ImgGroup> groupListModel;
	JList<ImgGroup> groupList;
	JScrollPane groupScrollPane;

	// LEFT AREA
	JPanel leftPanel;
	JLabel hideLeftPanel;
	boolean leftPanelVisible = true;
	JTabbedPane leftTabbedPane;

	JScrollPane eventScrollPane;
	JTree eventTree;
	DefaultTreeModel eventTreeModel;
	DefaultMutableTreeNode eventRootNode;

	JScrollPane dateScrollPane;
	JTree dateTree;
	DefaultTreeModel dateTreeModel;
	DateTreeHelper dateRootNode;

	// GENERAL
	JSplitPane splitPaneVerti;
	JSplitPane splitPaneHoriz;

	JFrame frame;

	// GLASS PANE
	MyGlassPane glassPane;


	private void initGui() {

		// BUILD MAIN AREA ===================================================

		// listModel = new DefaultListModel<Image>();
		// list = new JList<Image>(listModel);
		mainPanel = new JPanel(true);
		mainPanel.setLayout(null);
		scrollPane = new JScrollPane(mainPanel);
		scrollPane.setTransferHandler(new MyTransferHandler(this));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);

		// BUILD LOWER AREA ==================================================

		ratingDown = new RatingButton(this, -1);
		ratingNorm = new RatingButton(this, 0);
		ratingUp = new RatingButton(this, 1);

		hideBottomLabel = new JLabel("\\/");
		hideBottomLabel.setHorizontalAlignment(SwingConstants.CENTER);
		hideBottomLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent event) {
				if (bottomPanelVisible) {
					splitPaneVerti.setLastDividerLocation(splitPaneVerti.getDividerLocation());
					splitPaneVerti.setDividerLocation(splitPaneVerti.getHeight() - ratingDown.getHeight()
							- splitPaneVerti.getDividerSize());
					hideBottomLabel.setText("/\\");
					bottomPanelVisible = false;
				} else {
					splitPaneVerti.setDividerLocation(splitPaneVerti.getLastDividerLocation());
					hideBottomLabel.setText("\\/");
					bottomPanelVisible = true;
				}
			}
		});
		hideBottomPanel = new JPanel();
		// hideBottomPanel = new FormDebugPanel();

		String columnSpecs = "3dlu, pref, 3dlu, pref, 3dlu, pref, pref:GROW";
		String rowSpecs = "pref";
		FormLayout layout = new FormLayout(columnSpecs, rowSpecs);
		layout.setColumnGroups(new int[][] { { 2, 4, 6 } });
		hideBottomPanel.setLayout(layout);

		PanelBuilder builder = new PanelBuilder(layout, hideBottomPanel);
		CellConstraints cc = new CellConstraints();

		builder.add(ratingDown, cc.xy(2, 1));
		builder.add(ratingNorm, cc.xy(4, 1));
		builder.add(ratingUp, cc.xy(6, 1));
		builder.add(hideBottomLabel, cc.xy(7, 1));

		tagListModel = new DefaultListModel<ImgTag>();
		tagList = new JList<ImgTag>(tagListModel);
		tagList.setSelectionModel(new DefaultListSelectionModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;


			@Override
			public void setSelectionInterval(int index0, int index1) {
				if (tagList.isSelectedIndex(index0)) {
					tagList.removeSelectionInterval(index0, index1);
				} else {
					tagList.addSelectionInterval(index0, index1);
				}
				imageBrowser.setTags(tagList.getSelectedValuesList());
				loadImages();
			}
		});
		tagList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {
					// imageBrowser.createTag(new Tag("test")); XXX
					refreshGuiComponents();
				}
			}
		});
		tagScrollPane = new JScrollPane(tagList);

		personListModel = new DefaultListModel<ImgPerson>();
		personList = new JList<ImgPerson>(personListModel);
		personList.setSelectionModel(new DefaultListSelectionModel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;


			@Override
			public void setSelectionInterval(int index0, int index1) {
				if (personList.isSelectedIndex(index0)) {
					personList.removeSelectionInterval(index0, index1);
				} else {
					personList.addSelectionInterval(index0, index1);
				}
				imageBrowser.setPersons(personList.getSelectedValuesList());
				loadImages();
			}

		});
		personScrollPane = new JScrollPane(personList);

		groupListModel = new DefaultListModel<ImgGroup>();
		groupList = new JList<ImgGroup>(groupListModel);
		groupList.setSelectionModel(new DefaultListSelectionModel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;


			@Override
			public void setSelectionInterval(int index0, int index1) {
				if (groupList.isSelectedIndex(index0)) {
					groupList.removeSelectionInterval(index0, index1);
					imageBrowser.clearGroup();
				} else {
					super.setSelectionInterval(index0, index1);
					imageBrowser.setGroup(groupList.getSelectedValue(), true);
				}
				loadImages();
			}

		});
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupScrollPane = new JScrollPane(groupList);

		bottomPanel = new JPanel();
		// bottomFilterPanel = new FormDebugPanel();

		columnSpecs = "3dlu, pref:GROW, 3dlu, pref:GROW, 3dlu, pref:GROW, 3dlu";
		rowSpecs = "pref, 3dlu, 150px:GROW, 1dlu, 3dlu";
		layout = new FormLayout(columnSpecs, rowSpecs);
		layout.setColumnGroups(new int[][] { { 2, 4, 6 } });
		bottomPanel.setLayout(layout);

		builder = new PanelBuilder(layout, bottomPanel);
		cc = new CellConstraints();

		builder.add(hideBottomPanel, cc.xywh(1, 1, 7, 1));
		builder.add(tagScrollPane, cc.xywh(2, 3, 1, 2));
		builder.add(personScrollPane, cc.xywh(4, 3, 1, 2));
		builder.add(groupScrollPane, cc.xywh(6, 3, 1, 2));

		// BUILD VERTICAL SPLIT PANE =========================================

		splitPaneVerti = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, bottomPanel);
		splitPaneVerti.setResizeWeight(1);
		splitPaneVerti.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!bottomPanelVisible && (int) evt.getNewValue() < (int) evt.getOldValue()) {
					hideBottomLabel.setText("\\/");
					bottomPanelVisible = true;
				}
			}
		});

		// BUILD LEFT AREA ===================================================

		eventRootNode = new DefaultMutableTreeNode("Events");
		eventTreeModel = new DefaultTreeModel(eventRootNode, true);
		eventTree = new JTree(eventTreeModel);
		eventTree.setSelectionModel(new EventTreeSelectionModel(this));
		eventScrollPane = new JScrollPane(eventTree);

		dateRootNode = new DateTreeHelper("Dates");
		dateTreeModel = new DefaultTreeModel(dateRootNode, true);
		dateTree = new JTree(dateTreeModel);
		dateTree.setSelectionModel(new DateTreeSelectionModel(this));
		dateScrollPane = new JScrollPane(dateTree);

		leftTabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		leftTabbedPane.addTab("Events", eventScrollPane);
		leftTabbedPane.addTab("Datum", dateScrollPane);
		leftTabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (leftTabbedPane.getSelectedComponent().equals(eventScrollPane)) {
					dateTree.setSelectionInterval(-10, -1);
					imageBrowser.clearDates();
				} else if (leftTabbedPane.getSelectedComponent().equals(dateScrollPane)) {
					eventTree.setSelectionInterval(-10, -1);
					imageBrowser.clearEvent();
				}
				loadImages();
			}
		});

		hideLeftPanel = new JLabel(" < ");
		hideLeftPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent event) {
				if (leftPanelVisible) {
					splitPaneHoriz.setLastDividerLocation(splitPaneHoriz.getDividerLocation());
					splitPaneHoriz.setDividerLocation(hideLeftPanel.getWidth());
					hideLeftPanel.setText(" > ");
					leftPanelVisible = false;
				} else {
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
		splitPaneHoriz.setDividerLocation(250);
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
		frame.pack();
		frame.setMinimumSize(new Dimension(500, 500));

		glassPane = new MyGlassPane();
		frame.setGlassPane(glassPane);

		Point defaultSize = new Point(750, 600);
		Point size = guiProperties.getPropPoint("gui_frame_size", defaultSize);

		Point defaultPos = new Point(-10000, -10000);
		Point pos = guiProperties.getPropPoint("gui_frame_position", defaultPos);

		if (isWindowInScreenBounds(pos, size)) {
			frame.setSize(size.x, size.y);
			frame.setLocation(pos);
		} else {
			frame.setSize(defaultSize.x, defaultSize.y);
			frame.setLocationByPlatform(true);
		}
		
		int extendedState = guiProperties.getPropInt("gui_frame_maximized", 0);
		if (extendedState > 0) {
			frame.setExtendedState(extendedState);
		}

		frame.setVisible(true);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyKeyEventDispatcher(this));
		
		refreshGuiComponents();

	}


	void hideAndShowToolbars(boolean leftOnly) {

		boolean hide = bottomPanelVisible || leftPanelVisible;

		MouseEvent me;
		if (leftOnly || hide == leftPanelVisible) {
			me = new MouseEvent(hideLeftPanel, 0, 0, 0, 1, 1, 1, false);
			for (MouseListener ml : hideLeftPanel.getMouseListeners()) {
				ml.mouseReleased(me);
			}
		}
		if (!leftOnly && hide == bottomPanelVisible) {
			me = new MouseEvent(hideBottomLabel, 0, 0, 0, 1, 1, 1, false);
			for (MouseListener ml : hideBottomLabel.getMouseListeners()) {
				ml.mouseReleased(me);
			}
		}

	}


	@Override
	public void dropImages(List<File> files) {

		for (File f : files) {
			imageBrowser.addImage(f);
		}

		refreshGuiComponents();

	}


	public void refreshGuiComponents() {

		loadImages();

		buildEventTree();
		buildDateTree();

		loadTags();
		loadPersons();
		loadGroups();

	}


	public void loadImages() {

		mainPanel.removeAll();
		int i = 0;
		int row = 0;
		int columns = 4;
		int width = mainPanel.getWidth();
		int gap = 4;
		int diameter = (width - ((columns + 1) * gap)) / columns;

		List<Image> images = imageBrowser.getImages();
		if (images.size() > 0) {
			for (row = 0; row < Integer.MAX_VALUE; row++) {
				int y = gap + (row * (diameter + gap));
				for (int col = 0; col < columns; col++) {
					int x = gap + (col * (diameter + gap));
					mainPanel.add(new ImagePanel(images.get(i), x, y, diameter, i));
					i++;
					if (i == images.size()) {
						break;
					}
				}
				if (i == images.size()) {
					break;
				}
			}
			Dimension d = new Dimension(width, (row + 1) * (diameter + gap) + gap);
			mainPanel.setPreferredSize(d);
		}

		scrollPane.validate();
		scrollPane.repaint();
	}


	public void buildEventTree() {

		eventRootNode.removeAllChildren();

		List<ImgEvent> events = imageBrowser.getEvents();
		if (events == null) {
			return;
		}

		for (ImgEvent e : events) {
			Calendar c = new GregorianCalendar();
			c.setTimeInMillis(e.eventstart);
			int year = c.get(Calendar.YEAR);

			DefaultMutableTreeNode node = null;

			for (int i = 0; i < eventRootNode.getChildCount(); i++) {
				Object o = eventRootNode.getChildAt(i);
				if (o instanceof DefaultMutableTreeNode && o.toString().equals("" + year)) {
					node = (DefaultMutableTreeNode) o;
					break;
				}
			}

			if (node == null) {
				node = new DefaultMutableTreeNode("" + year);
				eventRootNode.add(node);
			}

			node.add(new DefaultMutableTreeNode(e, false));
		}

		eventTreeModel.reload();
	}


	public void buildDateTree() {

		dateRootNode.removeAllChildren();

		List<ImgDate> dates = imageBrowser.getDates();
		for (ImgDate d : dates) {
			dateRootNode.add(d);
		}

		dateTreeModel.reload();

	}


	public void loadTags() {

		tagListModel.clear();

		List<ImgTag> tags = imageBrowser.getTags();
		if (tags == null) {
			return;
		}
		for (ImgTag t : tags) {
			tagListModel.addElement(t);
		}
	}


	public void loadPersons() {

		personListModel.clear();

		List<ImgPerson> persons = imageBrowser.getPersons();
		if (persons == null) {
			return;
		}
		for (ImgPerson p : persons) {
			personListModel.addElement(p);
		}
	}


	public void loadGroups() {

		groupListModel.clear();

		List<ImgGroup> groups = imageBrowser.getGroups();
		if (groups == null) {
			return;
		}
		for (ImgGroup g : groups) {
			groupListModel.addElement(g);
		}
	}


	@Override
	public void log(String message) {
		System.err.println(message);
	}


	@Override
	public void shutDown() {

		guiProperties.setProp("gui_frame_size", new Point(frame.getWidth(), frame.getHeight()));
		guiProperties.setProp("gui_frame_position", frame.getLocation());
		guiProperties.setProp("gui_frame_maximized", frame.getExtendedState());

	}


	public void showUserMessage(String message) {
		glassPane.show(message);
	}


	private static boolean isWindowInScreenBounds(Point location, Point size) {

		Point myLocation = new Point(location);

		if (!isLocationInScreenBounds(myLocation)) {
			System.out.println(myLocation.toString());
			return false;
		}

		myLocation.translate(size.x - 1, 0);
		if (!isLocationInScreenBounds(myLocation)) {
			System.out.println(myLocation.toString());
			return false;
		}

		myLocation.translate(0, size.y - 1);
		if (!isLocationInScreenBounds(myLocation)) {
			System.out.println(myLocation.toString());
			return false;
		}

		myLocation.translate(-(size.x - 1), 0);
		if (!isLocationInScreenBounds(myLocation)) {
			System.out.println(myLocation.toString());
			return false;
		}

		return true;

	}


	/**
	 * Check if the location is in the bounds of one of the graphics devices.
	 * 
	 * @param location
	 * @return
	 */
	private static boolean isLocationInScreenBounds(Point location) {

		GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		for (int j = 0; j < graphicsDevices.length; j++) {

			if (graphicsDevices[j].getDefaultConfiguration().getBounds().contains(location)) {

				return true;
			}
		}
		return false;
	}

}
