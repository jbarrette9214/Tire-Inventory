package com.barrette.tireinventory.DesktopApp.controllers;


import com.barrette.tireinventory.DesktopApp.models.*;
import com.barrette.tireinventory.DesktopApp.App;

import java.io.IOException;
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
	@FXML private Label message;
	@FXML private VBox mainPane;
	
	@FXML private ScrollPane scroll;
	
	@FXML private static Button queryButton;
	
	private static Tab lastTab, currentTab;
	private static SingleSelectionModel<Tab> tabSelection;
	private Tab addNewTab, tireViewTab, existingTab;
	private TabPane tabPane;
	
	private AddTireViewController addController;
	private AddExistingController existingController;
	
	/**
	 * <p> initializes all the comboboxes of the nav panel, and sets the message to ready in the message panel</o>
	 */
	@FXML public void initialize() {
		
		String[] widths = {"<175","185", "195", "205", "215", "225", "235", "245", "255", "265", "275", "285", ">295"};
		
		tireWidth.getItems().setAll(widths);
		
		String[] aspect = {"40", "45", "50", "55", "60", "65", "70", "75", "80", "85" };
		aspectRatio.getItems().setAll(aspect);
		
		String[] rim = {"13", "14", "15", "16", "17", "18", "19", "20", "21", "22"};
		rimSize.getItems().setAll(rim);
		

		
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
		
		
		tabPane = new TabPane();
		tabPane.getTabs().add(addNewTab);
		tabPane.getTabs().add(tireViewTab);
		tabPane.getTabs().add(existingTab);

		tabSelection = tabPane.getSelectionModel();
		
		tabSelection.select(existingTab);
		
		scroll.setContent(tabPane);
		
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
		
		VBox tirePane = new VBox();
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
				
		
		
		List<Tire> tiresRetrieved = App.dao.getTiresBySize(Integer.parseInt(tireWidth.getValue()), 
				Integer.parseInt(aspectRatio.getValue()), Integer.parseInt(rimSize.getValue()));
		
		
		VBox tirePane = new VBox();
		
		if(tiresRetrieved != null) {
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
	
	
}
