package com.barrette.tireinventory.DesktopApp.controllers;


import com.barrette.tireinventory.DesktopApp.models.*;
import com.barrette.tireinventory.DesktopApp.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;



public class mainController {
	@FXML private HBox nav;
	@FXML private ComboBox<String> tireWidth;
	@FXML private ComboBox<String> aspectRatio;
	@FXML private ComboBox<String> rimSize;
	@FXML private TextField sizeTextField;
	@FXML private Label message;
	@FXML private VBox mainPane;
	
	@FXML private ScrollPane scroll;
	
	@FXML private static Button queryButton;
	
	private static Tab lastTab, currentTab, analysisTab;
	private static SingleSelectionModel<Tab> tabSelection;
	private Tab addNewTab, tireViewTab, existingTab;
	private TabPane tabPane;
	
	private AddTireViewController addController;
	private AddExistingController existingController;
	private AllTireViewController scrollController;
	
	/**
	 * <p> initializes all the comboboxes of the nav panel, and sets the message to ready in the message panel</o>
	 */
	@FXML public void initialize() {
		
//		String[] widths = {"<175","185", "195", "205", "215", "225", "235", "245", "255", "265", "275", "285", ">295"};
		
//		tireWidth.getItems().setAll(widths);
		List<Integer> temp = new ArrayList<Integer>();
		temp = App.dao.getWidths();
		for(Integer t : temp) {
			tireWidth.getItems().add(t.toString());
		}
		
		//String[] aspect = {"40", "45", "50", "55", "60", "65", "70", "75", "80", "85" };
		//aspectRatio.getItems().setAll(aspect);
		
		temp.clear();
		temp = App.dao.getAspects();
		for(Integer t : temp) {
			aspectRatio.getItems().add(t.toString());
		}
		
		
		
		//String[] rim = {"13", "14", "15", "16", "17", "18", "19", "20", "21", "22"};
		//rimSize.getItems().setAll(rim);
		
		temp.clear();
		temp = App.dao.getRimSizes();
		for(Integer t : temp) {
			rimSize.getItems().add(t.toString());
		}
		
		//load all the panes that need to be viewed into a tab pane with the tab part removed in CSS
		//trying to mimic a card layout
		BorderPane addPane = null;
		FXMLLoader loader = new FXMLLoader(App.class.getResource("resources/AddTireViewModel.fxml"));
		try {
			addPane = loader.load();
			addController = (AddTireViewController)loader.getController();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("can't load addtireviewmodel.fxml");
		}
		addNewTab = new Tab();
		addNewTab.setContent(addPane);
		
		
		
		
		
		FXMLLoader loader2 = new FXMLLoader(App.class.getResource("resources/AllTireView.fxml"));
		BorderPane scrollPane = null;
		try {
			scrollPane = loader2.load();
			scrollController = (AllTireViewController)loader2.getController();
		} catch(IOException e) {
			e.printStackTrace();
		}
		tireViewTab = new Tab();
		tireViewTab.setContent(scrollPane);
		
		
		
		
		
		FXMLLoader loader3 = new FXMLLoader(App.class.getResource("resources/AddExistingModel.fxml"));
		BorderPane existingPane = null;
		try {
			existingPane = loader3.load();
			existingController = (AddExistingController)loader3.getController();
			
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("can't load addexisting.fxml");
		}
		existingTab = new Tab();
		existingTab.setContent(existingPane);
		
		tireViewTab = new Tab();
		
		FXMLLoader loader4 = new FXMLLoader(App.class.getResource("resources/AnalysisModel.fxml"));
		BorderPane analysisPane = null;
		try {
			analysisPane = loader4.load();
			
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("Can't load analysisModel.fxml");
		}
		analysisTab = new Tab();
		analysisTab.setContent(analysisPane);
		
		
		tabPane = new TabPane();
//		tabPane.setMaxHeight(900.0);
		tabPane.getTabs().add(addNewTab);
		tabPane.getTabs().add(tireViewTab);
		tabPane.getTabs().add(existingTab);
		tabPane.getTabs().add(analysisTab);

		tabSelection = tabPane.getSelectionModel();
		
		tabSelection.select(existingTab);

		//scroll.setContent(tabPane);
		mainPane.getChildren().add(tabPane);
		
		lastTab = tabSelection.getSelectedItem();
		currentTab = tabSelection.getSelectedItem();
		
		//set the message in the message pane to ready
		message.setText("Ready");
		
	}

	/**
	 * <p>fires when the add button is clicked
	 * will change the mainPane to a pane where tires can be added </p>
	 * @param ae
	 */
	@FXML protected void addButton(ActionEvent ae) {
		
		lastTab = currentTab;
		currentTab = addNewTab;
		
		addController.reset();
		
		tabSelection.select(addNewTab);
		
	}
	
	
	/**
	 * used to add tires to the inventory that already exists in the database
	 * @param ae
	 */
	@FXML protected void addExistingButton(ActionEvent ae) {
		lastTab = currentTab;
		currentTab = existingTab;
		
		existingController.reset();
		
		tabSelection.select(existingTab);
		
	}
	
	
	/**
	 * loads all the tires in the database into the scroll pane for viewing
	 * 
	 * @param ae
	 */
	@FXML protected void showAllButton(ActionEvent ae) {
		message.setText("Querying database");
		
		lastTab = currentTab;
		currentTab = tireViewTab;
		
	
		List<Tire> tiresRetrieved = App.dao.getAllTires();
		
/*		VBox tirePane = new VBox();
		if(tiresRetrieved != null) {
			for(Tire t : tiresRetrieved) {
				tirePane.getChildren().add(t.getTireView());
			}
		} else {
			Label nothing = new Label();
			nothing.setStyle("-fx-font-size:40;");
			nothing.setText("Database is empty");
			
			tirePane.getChildren().add(nothing);
		}

		
		
		tireViewTab.setContent(null);
		tireViewTab.setContent(tirePane);
*/
		
		ScrollPane scroll = scrollController.updateView(tiresRetrieved);
		scroll.setPrefWidth(2000);

		tireViewTab.setContent(null);
		tireViewTab.setContent(scroll);
		
		tabSelection.select(tireViewTab);
	
		
		
		
		message.setText("Ready");
	}
	
	
	/**
	 * <p>fires when the query button clicked, checks to see if something is selected in the 3 comboboxes
	 * then it will take that info and load all the tires that size into the main pane. </>
	 * @param ae
	 */
	@FXML protected void queryButton(ActionEvent ae) {
		message.setText("Querying database");
		
		lastTab = currentTab;
		currentTab = tireViewTab;
		
		List<Tire> tiresRetrieved = null;
		if(sizeTextField.getText().length() == 7) {
			tiresRetrieved = App.dao.getTiresBySize(Integer.parseInt(sizeTextField.getText().substring(0, 3)), 
					Integer.parseInt(sizeTextField.getText().substring(3, 5)), 
					Integer.parseInt(sizeTextField.getText().substring(5,7)));
		} else if (sizeTextField.getText().length() == 9) {
			tiresRetrieved = App.dao.getTiresBySize(Integer.parseInt(sizeTextField.getText().substring(0,3)),
					Integer.parseInt(sizeTextField.getText().substring(4,6)), 
					Integer.parseInt(sizeTextField.getText().substring(7,9)));
		} else {
		
			tiresRetrieved = App.dao.getTiresBySize(Integer.parseInt(tireWidth.getValue()), 
				Integer.parseInt(aspectRatio.getValue()), Integer.parseInt(rimSize.getValue()));
		}
		
		sizeTextField.setText("");

		
		VBox tirePane = new VBox();
		
		
		if(tiresRetrieved != null && !tiresRetrieved.isEmpty()) {
			for(Tire t : tiresRetrieved) {
				tirePane.getChildren().add(t.getTireView());
			}
		} else {
			Label nothingThere = new Label("No tires found for that size");
			nothingThere.setStyle("-fx-font: 30 arial;");
			tirePane.getChildren().add(nothingThere);
		}
		
		
		tireViewTab.setContent(tirePane);
		
		tabSelection.select(tireViewTab);

		
		message.setText("Ready");
	}
	

	public static void triggerQueryButton() {
		queryButton.fire();
	}
	
	public static void backTab() {
		Tab temp;
		
		tabSelection.select(lastTab);
		
		temp = lastTab;
		lastTab = currentTab;
		currentTab = temp;
		
		temp = null;
	}
	
	public static void gotoAnalysisTab() {
		tabSelection.select(analysisTab);
	}
	
}
