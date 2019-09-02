package com.barrette.tireinventory.DesktopApp.controllers;

import com.barrette.tireinventory.DesktopApp.App;
import com.barrette.tireinventory.DesktopApp.models.*;

import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddExistingController {

	@FXML private ComboBox<String> qtyCombo;
	@FXML private ListView<String> listView;
	
	private List<Tire> retrieved;
	
	/**
	 * set the form up 
	 */
	@FXML public void initialize() {
		//fill the quantity box
		String[] qty = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
		qtyCombo.getItems().setAll(qty);
		
				
		
		retrieved = App.dao.getAllTires();
		
//		retrieved.sort(Comparator.comparing(Tire::brand));
		
		listView.getItems().clear();
		if(retrieved != null) {
			for(Tire t : retrieved) {
				listView.getItems().add(t.toString());
			}
		}
	}
	
	/**
	 * adds the tire to the inventory
	 * @param ae
	 */
	@FXML protected void addButton(ActionEvent ae) {
		
		//match the string that is in the list view with the object it comes from
		Tire temp = null;
		for(Tire t : retrieved) {
			if(t.toString().equals(listView.getSelectionModel().getSelectedItem())) {
				temp = t;
				break;
			}
		}
		
		//make sure a quantity was selected
		if(qtyCombo.getSelectionModel().getSelectedItem() != null  && temp != null) {
			App.dao.incrementQuantity(temp, Integer.parseInt(qtyCombo.getValue()));
			
			
			
		}
		
	}
	
	/**
	 * resets the form
	 * @param ae
	 */
	@FXML protected void cancelButton(ActionEvent ae) {
		reset();
	}
	
	/**
	 * creates a view that will be printed so that the inventory in the database can be checked
	 * against the actual inventory
	 * @param ae
	 */
	@FXML protected void printButton(ActionEvent ae) {
		
		VBox pane = new VBox();
		
		Label title = new Label();
		title.setText("Inventory");
		title.setStyle("-fx-font: 24 arial;");
		
		List<Tire> inventory = App.dao.getEntireInventory();
		
		
		for(Tire t : inventory) {
			HBox toAdd = t.getPrintView();
			pane.getChildren().add(toAdd);
		}
		

		Printer printer = Printer.getDefaultPrinter();
		PrinterJob job = PrinterJob.createPrinterJob();

		PageLayout pageLayout = printer.createPageLayout(Paper.A4,  PageOrientation.PORTRAIT, 
				Printer.MarginType.DEFAULT);
				
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to print inventory", 
								ButtonType.YES, ButtonType.NO);
		Optional<ButtonType> result = alert.showAndWait();
		
		if(job != null && result.get() == ButtonType.YES ) {
		
			boolean success = job.printPage(pageLayout, pane);
			if(success) {
				job.endJob();
			}
			
		
		} 
		
	}
	
	
	/***
	 * resets the fields, used when the focus has moved away and back
	 */
	public void reset() {
		initialize();
	}
	
}
