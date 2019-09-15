package com.barrette.tireinventory.DesktopApp.controllers;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

import com.barrette.tireinventory.DesktopApp.models.Tire;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class AllTireViewController {

	@FXML ScrollPane scrollPane;
	
	@FXML public void initialize() {
		
		
	}
	
	public ScrollPane updateView(List<Tire> tiresRetrieved) {
		VBox tirePane = new VBox();
		
		//set the width of the vbox so that it takes up the width of the screen, was only going halfway
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int newwidth = (int)screen.getWidth() - 150;

		tirePane.setStyle("-fx-pref-width:" + newwidth + ";");
		
		Label something = new Label();
		something.setText("something");
		
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

		scrollPane.setContent(tirePane);
		
		return scrollPane;
	}
	
}
