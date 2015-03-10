package de.clemensloos.imagebrowser.gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import java.util.HashMap;
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
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.clemensloos.imagebrowser.ImageBrowser;
import de.clemensloos.imagebrowser.gui.dialog.EventDialog;
import de.clemensloos.imagebrowser.gui.dialog.GroupDialog;
import de.clemensloos.imagebrowser.gui.dialog.PersonDialog;
import de.clemensloos.imagebrowser.gui.dialog.TagDialog;
import de.clemensloos.imagebrowser.types.DateTreeHelper;
import de.clemensloos.imagebrowser.types.Image;
import de.clemensloos.imagebrowser.types.ImgDate;
import de.clemensloos.imagebrowser.types.ImgEvent;
import de.clemensloos.imagebrowser.types.ImgGroup;
import de.clemensloos.imagebrowser.types.ImgGroupHelper;
import de.clemensloos.imagebrowser.types.ImgPerson;
import de.clemensloos.imagebrowser.types.ImgPersonHelper;
import de.clemensloos.imagebrowser.types.ImgTag;
import de.clemensloos.imagebrowser.types.ImgTagHelper;
import de.clemensloos.imagebrowser.utils.MyProperties;


public class ImageBrowser2DGui implements ImageBrowserGui, ActionListener {

	public static final int VIEW_MODE_MULTI = 0;
	public static final int VIEW_MODE_SINGLE = 1;


	// public static final int VIEW_MODE_COMPARE = 2;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// UIManager.setLookAndFeel(com.jgoodies.looks.windows.WindowsLookAndFeel.class.getCanonicalName());
			UIManager.setLookAndFeel(com.jtattoo.plaf.hifi.HiFiLookAndFeel.class.getCanonicalName());
			// UIManager.setLookAndFeel(com.jtattoo.plaf.noire.NoireLookAndFeel.class.getCanonicalName());
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
	public MyKeyEventDispatcher keyEventDispatcher;
	
	public volatile float alpha = 0f;
	public volatile float delta = 0.2f;
	Timer timer;

//	List<ImagePanel> loadedImages = new ArrayList<ImagePanel>();
	HashMap<Integer, ImagePanel> loadedImages = new HashMap<Integer, ImagePanel>();
	int leadSelection = -1;
	

	ImageBrowser2DGui() {

		imageBrowser = new ImageBrowser(this);

		loadProperties();

		initGui();

		refreshGuiComponents();
		
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().equals(timer)) {
			
			alpha += delta;
			if (alpha >= 1f) {
				alpha = 1f;
				delta *= -1.f;
				loadImages();
			}
			else if (alpha <= 0f) {
				alpha = 0f;
				delta *= -1.f;
				timer.stop();
			}
			
			mainPanel.repaint();
		}
	}
	
	
	public void switchView() {
		
		if(mainPanelViewMode == VIEW_MODE_MULTI) {
			mainPanelViewMode = VIEW_MODE_SINGLE;
		}
		else {
			mainPanelViewMode = VIEW_MODE_MULTI;
		}
		
		timer = new Timer(50, this);
		timer.start();
	}
	
	

	private MyProperties guiProperties;


	private void loadProperties() {

		String propFile = imageBrowser.getProperty("gui_properties_file");
		guiProperties = new MyProperties(imageBrowser, propFile, true);

	}
	
	// MAIN AREA
	JPanel mainPanel;
	public int mainPanelViewMode = 0; // TODO: persist
	int columnsOfImages = 4; // TODO: persist
	JScrollPane scrollPane;

	// LOWER AREA
	JPanel bottomPanel;

	JPanel hideBottomPanel;
	JButton ratingDown;
	JButton ratingNorm;
	JButton ratingUp;
	JLabel hideBottomLabel;
	JButton zoomThumbsOut;
	JButton zoomThumbsIn;
	boolean bottomPanelVisible = true;

	DefaultListModel<ImgTag> tagListModel;
	JList<ImgTag> tagList;
	JScrollPane tagScrollPane;
	ImgTagHelper newTagLabel = new ImgTagHelper();
	DefaultListModel<ImgPerson> personListModel;
	JList<ImgPerson> personList;
	JScrollPane personScrollPane;
	ImgPersonHelper newPersonLabel = new ImgPersonHelper();
	DefaultListModel<ImgGroup> groupListModel;
	JList<ImgGroup> groupList;
	JScrollPane groupScrollPane;
	ImgGroupHelper newGroupLabel = new ImgGroupHelper();

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

	public JFrame frame;

	// GLASS PANE
	MyGlassPane glassPane;


	private void initGui() {

		// BUILD MAIN AREA ===================================================

		// listModel = new DefaultListModel<Image>();
		// list = new JList<Image>(listModel);
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		scrollPane = new JScrollPane(mainPanel);
		scrollPane.setTransferHandler(new MyTransferHandler(this));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		// scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				loadImages();
			}
		});

		// BUILD LOWER AREA ==================================================

		ratingDown = new RatingButton(this, -1);
		ratingNorm = new RatingButton(this, 0);
		ratingUp = new RatingButton(this, 1);

		zoomThumbsOut = new JButton("-");
		zoomThumbsOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (columnsOfImages < 10) {
					columnsOfImages++;
				}
				if (columnsOfImages >= 10) {
					zoomThumbsOut.setEnabled(false);
				}
				if (columnsOfImages > 2) {
					zoomThumbsIn.setEnabled(true);
				}
				loadImages();
			}
		});
		zoomThumbsIn = new JButton("+");
		zoomThumbsIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (columnsOfImages > 2) {
					columnsOfImages--;
				}
				if (columnsOfImages <= 2) {
					zoomThumbsIn.setEnabled(false);
				}
				if (columnsOfImages < 10) {
					zoomThumbsOut.setEnabled(true);
				}
				loadImages();
			}
		});

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

		String columnSpecs = "3dlu, pref, 3dlu, pref, 3dlu, pref, pref:GROW, pref, 3dlu, pref, 3dlu";
		String rowSpecs = "pref";
		FormLayout layout = new FormLayout(columnSpecs, rowSpecs);
		layout.setColumnGroups(new int[][] { { 2, 4, 6, 8, 10 } });
		hideBottomPanel.setLayout(layout);

		PanelBuilder builder = new PanelBuilder(layout, hideBottomPanel);
		CellConstraints cc = new CellConstraints();

		builder.add(ratingDown, cc.xy(2, 1));
		builder.add(ratingNorm, cc.xy(4, 1));
		builder.add(ratingUp, cc.xy(6, 1));
		builder.add(hideBottomLabel, cc.xy(7, 1));
		builder.add(zoomThumbsOut, cc.xy(8, 1));
		builder.add(zoomThumbsIn, cc.xy(10, 1));

		tagListModel = new DefaultListModel<ImgTag>();
		tagList = new JList<ImgTag>(tagListModel);
		tagList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tagList.setSelectionModel(new DefaultListSelectionModel() {

			private static final long serialVersionUID = 1L;


			@Override
			public void setSelectionInterval(int index0, int index1) {
				if (index1 == tagListModel.getSize() - 1) {
					index1 = index1 - 1;
					if (index1 < index0) {
						return;
					}
				}
				super.setSelectionInterval(index0, index1);
				loadTags();
			}


			@Override
			public void addSelectionInterval(int index0, int index1) {
				if (index1 == tagListModel.getSize() - 1) {
					index1 = index1 - 1;
					if (index1 < index0) {
						return;
					}
				}
				super.addSelectionInterval(index0, index1);
				loadTags();
			}


			@Override
			public void removeSelectionInterval(int index0, int index1) {
				super.removeSelectionInterval(index0, index1);
				loadTags();
			}


			private void loadTags() {
				imageBrowser.setTags(tagList.getSelectedValuesList());
				loadImages();

			}

		});
		tagList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					if (tagListModel.get(tagList.locationToIndex(e.getPoint())) instanceof ImgTagHelper) {
						keyEventDispatcher.setActive(false);
						TagDialog td = new TagDialog(frame);
						keyEventDispatcher.setActive(true);
						ImgTag it = td.getImgTag();
						if (it != null) {
							imageBrowser.createTag(it);
							loadTags();
						}
					}
				}
			}
		});
		tagScrollPane = new JScrollPane(tagList);

		personListModel = new DefaultListModel<ImgPerson>();
		personList = new JList<ImgPerson>(personListModel);
		personList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		personList.setSelectionModel(new DefaultListSelectionModel() {

			private static final long serialVersionUID = 1L;

			
			@Override
			public void setSelectionInterval(int index0, int index1) {
				if (index1 == personListModel.getSize() - 1) {
					index1 = index1 - 1;
					if (index1 < index0) {
						return;
					}
				}
				super.setSelectionInterval(index0, index1);
				loadPersons();
			}


			@Override
			public void addSelectionInterval(int index0, int index1) {
				if (index1 == personListModel.getSize() - 1) {
					index1 = index1 - 1;
					if (index1 < index0) {
						return;
					}
				}
				super.addSelectionInterval(index0, index1);
				loadPersons();
			}


			@Override
			public void removeSelectionInterval(int index0, int index1) {
				super.removeSelectionInterval(index0, index1);
				loadPersons();
			}


			private void loadPersons() {
				imageBrowser.setPersons(personList.getSelectedValuesList());
				loadImages();

			}

		});
		personList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					if (personListModel.get(personList.locationToIndex(e.getPoint())) instanceof ImgPersonHelper) {
						keyEventDispatcher.setActive(false);
						PersonDialog pd = new PersonDialog(frame);
						keyEventDispatcher.setActive(true);
						ImgPerson ip = pd.getImgPerson();
						if (ip != null) {
							imageBrowser.createPerson(ip);
							loadPersons();
						}
					}
				}
			}
		});
		personScrollPane = new JScrollPane(personList);

		groupListModel = new DefaultListModel<ImgGroup>();
		groupList = new JList<ImgGroup>(groupListModel);
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupList.setSelectionModel(new DefaultListSelectionModel() {

			private static final long serialVersionUID = 1L;

			
			@Override
			public void setSelectionInterval(int index0, int index1) {
				if (index1 == groupListModel.getSize() - 1) {
					index1 = index1 - 1;
					if (index1 < index0) {
						return;
					}
				}
				super.setSelectionInterval(index0, index1);
				loadGroup();
			}


			@Override
			public void addSelectionInterval(int index0, int index1) {
				if (index1 == groupListModel.getSize() - 1) {
					index1 = index1 - 1;
					if (index1 < index0) {
						return;
					}
				}
				super.addSelectionInterval(index0, index1);
				loadGroup();
			}


			@Override
			public void removeSelectionInterval(int index0, int index1) {
				super.removeSelectionInterval(index0, index1);
				loadGroup();
			}


			private void loadGroup() {
				imageBrowser.setPersons(personList.getSelectedValuesList());
				imageBrowser.setGroup(groupList.getSelectedValue(), true); // FIXME: make group selection editable
				loadImages();

			}

		});
		groupList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					if (groupListModel.get(groupList.locationToIndex(e.getPoint())) instanceof ImgGroupHelper) {
						keyEventDispatcher.setActive(false);
						GroupDialog gd = new GroupDialog(frame);
						keyEventDispatcher.setActive(true);
						ImgGroup group = gd.getImgGroup();
						if (group != null) {
							imageBrowser.createGroup(group);
							loadGroups();
						}
					}
				}
			}
		});
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

		eventRootNode = new DefaultMutableTreeNode("Add Event ...");
		// eventRootNode.
		eventTreeModel = new DefaultTreeModel(eventRootNode, true);
		eventTree = new JTree(eventTreeModel);
		eventTree.setSelectionModel(new EventTreeSelectionModel(this));
		eventTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					TreePath selPath = eventTree.getPathForLocation(e.getX(), e.getY());
					if (selPath != null && selPath.getLastPathComponent().equals(eventRootNode)) {
						eventTree.expandPath(new TreePath(eventRootNode.getPath()));
						keyEventDispatcher.setActive(false);
						EventDialog ed = new EventDialog(frame);
						keyEventDispatcher.setActive(true);
						ImgEvent ie = ed.getImgEvent();
						if (ie != null) {
							imageBrowser.createEvent(ie);
							buildEventTree();
						}
					}
				}
			}
		});
		eventScrollPane = new JScrollPane(eventTree);

		dateRootNode = new DateTreeHelper("Dates");
		dateTreeModel = new DefaultTreeModel(dateRootNode, true);
		dateTree = new JTree(dateTreeModel);
		// dateTree.setRootVisible(false);
		dateTree.setSelectionModel(new DateTreeSelectionModel(this));
		// dateTree.setSelectionModel(new DefaultTreeSelectionModel());
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

		keyEventDispatcher = new MyKeyEventDispatcher(this);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);

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


	@Override
	public void refreshGuiComponents() {

		loadImages();

		buildEventTree();
		buildDateTree();

		loadTags();
		loadPersons();
		loadGroups();

	}


	public void loadImages() {

		if (mainPanelViewMode == VIEW_MODE_MULTI) {

			mainPanel.removeAll();
			int i = 0;
			int row = 0;
			int width = scrollPane.getWidth() - 17;
			int gap = 4;
			int diameter = (width - ((columnsOfImages + 1) * gap)) / columnsOfImages;

			List<Image> images = imageBrowser.getImages();
//			List<ImagePanel> newList = new ArrayList<ImagePanel>();
			HashMap<Integer, ImagePanel> newList2 = new HashMap<Integer, ImagePanel>();
			ImagePanel panel = null;
			if (images.size() > 0) {
				for (row = 0; row < Integer.MAX_VALUE; row++) {
					int y = gap + (row * (diameter + gap));
					for (int col = 0; col < columnsOfImages; col++) {
						int x = gap + (col * (diameter + gap));

						panel = loadedImages.remove(images.get(i).image_id);
						if (panel != null) {
//							panel = loadedImages.remove(loadedImages.indexOf(images.get(i)));
							panel.refresh(images.get(i), x, y, diameter, diameter);
						}
						else {
							panel = new ImagePanel(this, images.get(i), x, y, diameter, diameter);
						}
						newList2.put(images.get(i).image_id, panel);

						mainPanel.add(panel);
						i++;
						if (i == images.size()) {
							break;
						}
					}
					if (i == images.size()) {
						break;
					}
				}
			}

			Dimension d = new Dimension(width, (row + 1) * (diameter + gap) + gap);
			mainPanel.setPreferredSize(d);

			loadedImages.clear();
			loadedImages.putAll(newList2);

			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			scrollPane.getVerticalScrollBar().setUnitIncrement(diameter);
			scrollPane.getVerticalScrollBar().setBlockIncrement(diameter);
			scrollPane.validate();
			scrollPane.repaint();
			
			if (leadSelection != -1) { // TODO: lead selection panel
				ImagePanel ip = loadedImages.get(leadSelection);
				if (ip != null) {
					ip.scrollRectToVisible(ip.getBounds());
				}
			}
			
		}
		else if (mainPanelViewMode == VIEW_MODE_SINGLE) {

			mainPanel.removeAll();
			int width = scrollPane.getWidth();
			int height = scrollPane.getHeight();

			List<Image> images = imageBrowser.getImages();
//			List<ImagePanel> newList = new ArrayList<ImagePanel>();
			HashMap<Integer, ImagePanel> newList2 = new HashMap<Integer, ImagePanel>();
			
//			ImagePanel panel = null;
			
			for(Image i : images) {
				
				int key = i.image_id;
				ImagePanel ip = loadedImages.remove(key);
				
				if (ip == null) {
					ip = new ImagePanel(this, i, 0, 0, 10, 10);
				}
				else {
//					ip.refresh(i, 0, 0, 10, 10);
				}
				
				if (key == leadSelection) {
					ip.refresh(i, 0, 0, width, height);
				}
				newList2.put(key, ip);
				
			}
			
			ImagePanel panel = newList2.get(leadSelection);
			if (panel == null) {
				panel = new ImagePanel(this, images.get(0), 0, 0, width, height);
			}

//			newList2.put(panel.image.image_id, panel);

			mainPanel.add(panel);

			Dimension d = new Dimension(width, height);
			mainPanel.setPreferredSize(d);

//			loadedImages2.clear();
//			loadedImages2.putAll(newList2);

			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

			scrollPane.validate();
			scrollPane.repaint();

		}

	}
	


	@Override
	public void selectAll() {
		
		for(ImagePanel ip: loadedImages.values()) {
			ip.select();
		}

//		for (ImagePanel ip : loadedImages) {
//			ip.select();
//		}
	}


	@Override
	public void deselectAll() {
		
		for(ImagePanel ip: loadedImages.values()) {
			ip.deselect();
		}
		
//		for (ImagePanel ip : loadedImages) {
//			ip.deselect();
//		}
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

		tagListModel.addElement(newTagLabel);

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
		
		personListModel.addElement(newPersonLabel);
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
		
		groupListModel.addElement(newGroupLabel);
	}


	@Override
	public void log(String message) {
		System.err.println(message);
	}


	@Override
	public void shutDown() {

		if (frame.getExtendedState() != Frame.MAXIMIZED_BOTH) {
			guiProperties.setProp("gui_frame_size", new Point(frame.getWidth(), frame.getHeight()));
			guiProperties.setProp("gui_frame_position", frame.getLocation());
		}
		guiProperties.setProp("gui_frame_maximized", frame.getExtendedState());

	}


	public void showUserMessage(String message) {
		glassPane.show(message);
	}


	private static boolean isWindowInScreenBounds(Point location, Point size) {

		Point myLocation = new Point(location);

		if (!isLocationInScreenBounds(myLocation)) {
			return false;
		}

		myLocation.translate(size.x - 1, 0);
		if (!isLocationInScreenBounds(myLocation)) {
			return false;
		}

		myLocation.translate(0, size.y - 1);
		if (!isLocationInScreenBounds(myLocation)) {
			return false;
		}

		myLocation.translate(-(size.x - 1), 0);
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
