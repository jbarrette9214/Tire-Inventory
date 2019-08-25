package com.barrette.tireinventory.DesktopApp.controllers;

import com.barrette.tireinventory.Models.*;

import java.util.Comparator;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

public class AddExistingController {

	@FXML private ComboBox<String> qtyCombo;
	@FXML private ListView<String> listView;
	
	private List<Tire> retrieved;
	private RestServices rest;
	
	/**
	 * set the form up 
	 */
	@FXML public void initialize() {
		//fill the quantity box
		String[] qty = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
		qtyCombo.getItems().setAll(qty);
		
				
		rest = new RestServices();
		
		retrieved = rest.getAllTires();
		
		retrieved.sort(Comparator.comparing(Tire::getBrand));
		
		listView.getItems().clear();
		for(Tire t : retrieved) {
			listView.getItems().add(t.toString());
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
			int newQty = Integer.parseInt(qtyCombo.getSelectionModel().getSelectedItem()) + temp.getQuantity();
			String message = rest.addTiresToInventory(temp, newQty);

			//show message
			Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.OK);
			alert.showAndWait();
			
			
		}
		
	}
	
	/**
	 * resets the form
	 * @param ae
	 */
	@FXML protected void cancelButton(ActionEvent ae) {
		reset();
	}
	
	/***
	 * resets the fields, used when the focus has moved away and back
	 */
	public void reset() {
		initialize();
	}
	
}
