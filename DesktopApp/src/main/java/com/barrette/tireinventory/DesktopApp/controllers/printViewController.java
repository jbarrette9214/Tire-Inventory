package com.barrette.tireinventory.DesktopApp.controllers;

import com.barrette.tireinventory.DesktopApp.models.Tire;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class printViewController {

	@FXML Label brandModel, tireSize, newOrUsed, qtyLabel;
	
	/**
	 * returns a view of the current tire that was selected for printing
	 * @param tire
	 */
	public void updateView(Tire tire) {
		brandModel.setText(tire.getBrand() + " " + tire.getTireModel());
		String size = tire.getWidth() + "/" + tire.getAspectRatio() + "R" + tire.getRimSize();
		tireSize.setText(size);
		qtyLabel.setText("Quantity on hand: " + tire.getQuantity());

		if(tire.getIsNew()) {
			newOrUsed.setText("New");
		} else {
			newOrUsed.setText("Used");
		}
		
/*		for(int i = 1; i <= tire.getQuantity(); ++i) {
			qty.getItems().add(Integer.toString(i));
		}
*/		
	}
	
}
