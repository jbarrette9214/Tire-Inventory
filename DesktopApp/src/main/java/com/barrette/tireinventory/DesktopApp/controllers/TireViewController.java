package com.barrette.tireinventory.DesktopApp.controllers;

import com.barrette.tireinventory.DesktopApp.App;
import com.barrette.tireinventory.DesktopApp.models.Tire;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class TireViewController {

	@FXML private Label brand_model;
	@FXML private Label tireSize;
	@FXML private Label type;
	@FXML private Label quantity;
	@FXML private ComboBox<String> qty;

	private Tire thisTire;
	
	/**
	 * <p>called to update all the values in the TireViewModel with the values from the Tire passed in</p>
	 * @param tire
	 */
	public void updateView(Tire tire) {
		thisTire = tire;
		
		brand_model.setText(tire.getBrand() + " " + tire.getTireModel());
		String size = tire.getWidth() + "/" + tire.getAspectRatio() + "R" + tire.getRimSize();
		tireSize.setText(size);
		type.setText(tire.getType());
		quantity.setText("Quantity on hand: " + tire.getQuantity());
		
		for(int i = 1; i <= tire.getQuantity(); ++i) {
			qty.getItems().add(Integer.toString(i));
		}
		
	}
	
	/**
	 * calls the RestServices updateTire function to update the database with the new quantity
	 * of tires depending on response from message
	 * @param ae
	 */
	@FXML protected void removeHandler(ActionEvent ae) {
		String message = "";
		
		int remove = Integer.parseInt(qty.getValue());
		App.dao.decrementQuantity(thisTire, remove);
	/*	
		if((remove != 0) && (remove <= thisTire.getQuantity())) {
			int newQty = thisTire.getQuantity() - remove;
			message = rest.removeTires(thisTire, newQty);
		}
		
		Alert alert = new Alert(AlertType.NONE, message, ButtonType.OK);
		alert.showAndWait();
	*/	
		updateView(thisTire);
	}
}
