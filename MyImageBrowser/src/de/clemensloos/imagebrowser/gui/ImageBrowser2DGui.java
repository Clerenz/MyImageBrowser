package de.clemensloos.imagebrowser.gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.clemensloos.imagebrowser.ImageBrowser;
import de.clemensloos.imagebrowser.MyProperties;
import de.clemensloos.imagebrowser.database.MyTransferHandler;
import de.clemensloos.imagebrowser.types.Date;
import de.clemensloos.imagebrowser.types.DateTreeHelper;
import de.clemensloos.imagebrowser.types.Event;
import de.clemensloos.imagebrowser.types.Group;
import de.clemensloos.imagebrowser.types.Image;
import de.clemensloos.imagebrowser.types.Person;
import de.clemensloos.imagebrowser.types.Tag;


public class ImageBrowser2DGui implements ImageBrowserGui {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(com.jgoodies.looks.windows.WindowsLookAndFeel.class.getCanonicalName());
		} catch (Exception e) {
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
	DefaultListModel<Person> personListModel;
	JList<Person> personList;
	JScrollPane personScrollPane;
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
	DefaultTreeModel eventTreeModel;
	DefaultMutableTreeNode eventRootNode;

	JScrollPane dateScrollPane;
	JTree dateTree;
	DefaultTreeModel dateTreeModel;
	DateTreeHelper dateRootNode;

	JSplitPane splitPaneVerti;
	JSplitPane splitPaneHoriz;

	JFrame frame;


	private void initGui() {

		// BUILD MAIN AREA ===================================================

		listModel = new DefaultListModel<Image>();
		list = new JList<Image>(listModel);
		scrollPane = new JScrollPane(list);
		scrollPane.setTransferHandler(new MyTransferHandler(this));

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
		tagList.setSelectionModel(new DefaultListSelectionModel() {
			@Override
		    public void setSelectionInterval(int index0, int index1)  {
		        if(tagList.isSelectedIndex(index0)) {
		        	tagList.removeSelectionInterval(index0, index1);
		        }
		        else {
		        	tagList.addSelectionInterval(index0, index1);
		        }
		    }
		});
		tagList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				imageBrowser.setTags(tagList.getSelectedValuesList());
				loadImages();
			}
		});
		tagScrollPane = new JScrollPane(tagList);

		personListModel = new DefaultListModel<Person>();
		personList = new JList<Person>(personListModel);
		personScrollPane = new JScrollPane(personList);

		groupListModel = new DefaultListModel<Group>();
		groupList = new JList<Group>(groupListModel);
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupScrollPane = new JScrollPane(groupList);

		bottomFilterPanel = new JPanel();
		// bottomFilterPanel = new FormDebugPanel();

		String columnSpecs = "3dlu, pref:GROW, 3dlu, pref:GROW, 3dlu, pref:GROW, 3dlu";
		String rowSpecs = "3dlu, 150px:GROW, 1dlu, 3dlu";
		FormLayout layout = new FormLayout(columnSpecs, rowSpecs);
		layout.setColumnGroups(new int[][] { { 2, 4, 6 } });
		bottomFilterPanel.setLayout(layout);

		PanelBuilder builder = new PanelBuilder(layout, bottomFilterPanel);
		CellConstraints cc = new CellConstraints();

		builder.add(tagScrollPane, cc.xywh(2, 2, 1, 2));
		builder.add(personScrollPane, cc.xywh(4, 2, 1, 2));
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
		eventTreeModel = new DefaultTreeModel(eventRootNode, true);
		eventTree = new JTree(eventTreeModel);
		eventTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		eventTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if(e.getPath().getPathCount() == 3) {
					imageBrowser.setEvent(e.getPath().getLastPathComponent().toString());
				}
				else {
					imageBrowser.clearEvent();
				}
				loadImages();
			}
		});
		eventScrollPane = new JScrollPane(eventTree);

		dateRootNode = new DateTreeHelper("Dates");
		dateTreeModel = new DefaultTreeModel(dateRootNode, true);
		dateTree = new JTree(dateTreeModel);
		dateScrollPane = new JScrollPane(dateTree);

		leftTabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		leftTabbedPane.addTab("Events", eventScrollPane);
		leftTabbedPane.addTab("Datum", dateScrollPane);

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
		frame.pack();
		frame.setMinimumSize(new Dimension(500, 500));

		Point defaultSize = new Point(750, 600);
		Point size = guiProperties.getPropPoint("gui_frame_size", defaultSize);

		Point defaultPos = new Point(-10000, -10000);
		Point pos = guiProperties.getPropPoint("gui_frame_position", defaultPos);
		if (isWindowInScreenBounds(pos, size)) {
			frame.setSize(size.x, size.y);
			frame.setLocation(pos);
		}
		else {
			frame.setSize(defaultSize.x, defaultSize.y);
			frame.setLocationByPlatform(true);
		}

		frame.setVisible(true);
		// frame.setExtendedState(Frame.MAXIMIZED_BOTH);

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

		listModel.removeAllElements();

		List<Image> images = imageBrowser.getImages();
		if (images != null) {
			for (Image im : images) {
				listModel.addElement(im);
			}
		}
	}


	public void buildEventTree() {

		eventRootNode.removeAllChildren();

		List<Event> events = imageBrowser.getEvents();
		if (events == null) {
			return;
		}

		for (Event e : events) {
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

		List<Date> dates = imageBrowser.getDates();
		for (Date d : dates) {
			dateRootNode.add(d);
		}

		dateTreeModel.reload();

	}


	public void loadTags() {

		tagListModel.clear();

		List<Tag> tags = imageBrowser.getTags();
		if (tags == null) {
			return;
		}
		for (Tag t : tags) {
			tagListModel.addElement(t);
		}
	}


	public void loadPersons() {

		personListModel.clear();

		List<Person> persons = imageBrowser.getPersons();
		if (persons == null) {
			return;
		}
		for (Person p : persons) {
			personListModel.addElement(p);
		}
	}


	public void loadGroups() {

		groupListModel.clear();

		List<Group> groups = imageBrowser.getGroups();
		if (groups == null) {
			return;
		}
		for (Group g : groups) {
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

	}


	private static boolean isWindowInScreenBounds(Point location, Point size) {

		Point myLocation = new Point(location);

		if (!isLocationInScreenBounds(myLocation)) {
			return false;
		}

		myLocation.translate(size.x, 0);
		if (!isLocationInScreenBounds(myLocation)) {
			return false;
		}

		myLocation.translate(0, size.y);
		if (!isLocationInScreenBounds(myLocation)) {
			return false;
		}

		myLocation.translate(-size.x, 0);
		if (!isLocationInScreenBounds(myLocation)) {
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
