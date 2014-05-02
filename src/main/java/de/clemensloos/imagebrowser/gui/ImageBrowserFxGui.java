package de.clemensloos.imagebrowser.gui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.RegionBuilder;
import javafx.stage.Stage;
import de.clemensloos.imagebrowser.ImageBrowser;
import de.clemensloos.imagebrowser.types.ImgEvent;
import de.clemensloos.imagebrowser.utils.MyProperties;

public class ImageBrowserFxGui extends Application implements ImageBrowserGui {
	
	
	ImageBrowser imageBrowser;
	private MyProperties guiProperties;
	
	Stage stage;
	
	boolean leftPaneVisible = true;
	SplitPane verticalSplitPane;
	double verticalSplitPaneDivPos = 0;
	SplitPane horizontalSplitPane;
	double horizontalSplitPaneDivPos = 0;
	Label hideLeftPane;
	
	public static void main(String[] args0) {
		
		launch(args0);
		
	}

	@Override
	public void log(String message) {
		// TODO Auto-generated method stub

	}


	@Override
	public void dropImages(List<File> files) {
		// TODO Auto-generated method stub

	}


	@Override
	public void shutDown() {

		guiProperties.setProp("gui_frame_size", new Point((int)stage.getWidth(), (int)stage.getHeight()));
		guiProperties.setProp("gui_frame_position", new Point((int)stage.getX(), (int)stage.getY()));

	}


	@Override
	public void start(Stage primaryStage) throws Exception {

		
		imageBrowser = new ImageBrowser(this);
		
		loadProperties();
		
		initGui(primaryStage);

	}
	
	
	private void loadProperties() {

		String propFile = imageBrowser.getProperty("gui_properties_file");
		guiProperties = new MyProperties(imageBrowser, propFile, true);

	}
	
	
	private void initGui(Stage primaryStage) {
		
		this.stage = primaryStage;
		
		

//		splitPaneHoriz = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, splitPaneVerti);
//		splitPaneHoriz.setResizeWeight(0);
//		splitPaneHoriz.setDividerLocation(250);
//		splitPaneHoriz.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
//			@Override
//			public void propertyChange(PropertyChangeEvent evt) {
//				if (!leftPanelVisible && (int) evt.getNewValue() > (int) evt.getOldValue()) {
//					hideLeftPanel.setText(" < ");
//					leftPanelVisible = true;
//				}
//			}
//		});
		
		
		
		// BUILD VERTICAL SPLIT PANE =========================================
		
		verticalSplitPane = new SplitPane();
		verticalSplitPane.setOrientation(Orientation.VERTICAL);
		verticalSplitPane.getItems().addAll(
				RegionBuilder.create().build(),
				RegionBuilder.create().build());
		verticalSplitPane.setDividerPositions(0.2);
		
		// BUILD LEFT AREA ===================================================

		TreeItem<ImgEvent> eventRootNode = new TreeItem<ImgEvent>(new ImgEvent("", 0, 0));
		TreeView<ImgEvent> eventTree = new TreeView<ImgEvent>();
		eventTree.setShowRoot(false);
		eventTree.setRoot(eventRootNode);
		for (int i=0; i<40; i++) {
			TreeItem<ImgEvent> item = new TreeItem<ImgEvent>(new ImgEvent("Element " + i, 0, 0));
			eventRootNode.getChildren().add(item);
			TreeItem<ImgEvent> child = new TreeItem<ImgEvent>(new ImgEvent("Child " + i, 0, 0));
			item.getChildren().add(child);
		}
		Tab eventTab = new Tab("Events");
		eventTab.setContent(eventTree);
		
		TreeItem<String> dateRootNode = new TreeItem<String>("Dates");
		TreeView<String> dateTree = new TreeView<String>();
		dateTree.setShowRoot(false);
		dateTree.setRoot(dateRootNode);
		for (int i=0; i<40; i++) {
			TreeItem<String> item = new TreeItem<String>("Element " + i);
			dateRootNode.getChildren().add(item);
			TreeItem<String> child = new TreeItem<String>("Child " + i);
			item.getChildren().add(child);
		}
		Tab dateTab = new Tab("Dates");
		dateTab.setContent(dateTree);
		
		TabPane leftTabPane = new TabPane();
		leftTabPane.setSide(Side.TOP);
		leftTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		leftTabPane.getTabs().addAll(dateTab, eventTab);
//		leftTabbedPane.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				if (leftTabbedPane.getSelectedComponent().equals(eventScrollPane)) {
//					dateTree.setSelectionInterval(-10, -1);
//					imageBrowser.clearDates();
//				}
//				else if (leftTabbedPane.getSelectedComponent().equals(dateScrollPane)) {
//					eventTree.setSelectionInterval(-10, -1);
//					imageBrowser.clearEvent();
//				}
//				loadImages();
//			}
//		});
		
		BorderPane leftPane = new BorderPane();
		leftPane.setCenter(leftTabPane);
		
		hideLeftPane = new Label(" < ");
		BorderPane.setAlignment(hideLeftPane, Pos.CENTER);
		hideLeftPane.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				if (leftPaneVisible) {
					horizontalSplitPaneDivPos = horizontalSplitPane.getDividerPositions()[0];
//					;
					horizontalSplitPane.setDividerPosition(0, (hideLeftPane.getWidth() +6) / stage.getWidth());
//					horizontalSplitPane.set
					hideLeftPane.setText(" > ");
					leftPaneVisible = false;
				}
				else {
					horizontalSplitPane.setDividerPosition(0, horizontalSplitPaneDivPos);
					hideLeftPane.setText(" < ");
					leftPaneVisible = true;
				}
			}
		});

		leftPane.setRight(hideLeftPane);
		

		// BUILD HORIZONTAL SPLIT PANE =======================================
		
		horizontalSplitPane = new SplitPane();
		horizontalSplitPane.setOrientation(Orientation.HORIZONTAL);
		horizontalSplitPane.getItems().addAll(
				leftPane,
				verticalSplitPane);
		horizontalSplitPane.setDividerPositions(0.2);
		
		SplitPane.setResizableWithParent(verticalSplitPane, false);
		
		// BUILD THE FRAME ===================================================

		stage.setScene(new Scene(horizontalSplitPane));

		Point defaultSize = new Point(750, 600);
		Point size = guiProperties.getPropPoint("gui_frame_size", defaultSize);

		Point defaultPos = new Point(-10000, -10000);
		Point pos = guiProperties.getPropPoint("gui_frame_position", defaultPos);
		if (isWindowInScreenBounds(pos, size)) {
			stage.setWidth(size.x);
			stage.setHeight(size.y);
			stage.setX(pos.x);
			stage.setY(pos.y);
		}
		else {
			stage.setWidth(defaultSize.x);
			stage.setHeight(defaultSize.y);
		}
		
		stage.setMinWidth(500);
		stage.setMinHeight(500);
		
		stage.setTitle("Image Browser");
		stage.show();
		
		
		
	}
	
	
	@Override
	public void stop() {
		imageBrowser.shutDown();
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

	@Override
	public void deselectAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshGuiComponents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showUserMessage(String message) {
		// TODO Auto-generated method stub
		
	}
	
}
