package com.barrette.tireinventory.DesktopApp;

import java.awt.Dimension;
import java.awt.Toolkit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public class App extends Application
{

	public static DAO dao;
	
	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage primary) throws Exception {
/*		//check to see if DB is accessible, and exit if it isn't (server not started)
		if(rest.getAllTires() == null) {
			Alert alert = new Alert(AlertType.ERROR, "Database cannot be reached, make sure server is started", 
					ButtonType.OK);
			alert.showAndWait();
			System.exit(0);
		}
	*/	
		dao = new DAO();

		
		Parent root = FXMLLoader.load(App.class.getResource("resources/fxml_main.fxml"));
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		
		Scene mainScene = new Scene(root, screen.getWidth() - 100, screen.getHeight() - 100);
		
		primary.setTitle("Tire Inventory");
		primary.setScene(mainScene);
		primary.setResizable(false);
		
		primary.show();

	}
}
