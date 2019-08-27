/**
 * class to hold all the information about a tire that is obtained from the database
	need to use snake case for all the variables so they match up with the json data that is from the
	database, mysql uses snake case
 */

//


package com.barrette.tireinventory.DesktopApp.models;

import com.barrette.tireinventory.DesktopApp.controllers.*;
import com.barrette.tireinventory.DesktopApp.App;


import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class Tire {
	private int id;
	private String brand;
	private String tire_model;
	private int width;
	private int aspect_ratio;
	private int rim_size;
	private String tire_type;
	private int quantity;
	private boolean isNew;
	
	//constructor
	public Tire() {
		//set all the default values
		id=-1;
		brand="none";
		tire_model="none";
		width = 0;
		aspect_ratio=0;
		rim_size=0;
		tire_type="all season";
		quantity = -1;
		isNew = true;
	}

	public void setId(int idIn) {
		id = idIn;
	}
	
	public int getId() {
		return id;
	}
		
	public String getBrand() {
		return brand.toUpperCase();
	}
	
	public void setBrand(String brnd) {
		brand = brnd;
	}
	
	public String getTireModel() {
		return tire_model.toUpperCase();
	}
	
	public void setTireModel(String model) {
		tire_model = model;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int wide) {
		width = wide;
	}
	
	public int getAspectRatio() {
		return aspect_ratio;
	}
	
	public void setAspectRatio(int ratio) {
		aspect_ratio = ratio;
	}
	
	public int getRimSize() {
		return rim_size;
	}
	
	public void setRimSize(int rim) {
		rim_size = rim;
	}
	
	public String getType() {
		return tire_type;
	}
	
	public void setType(String strType) {
		tire_type = strType;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quant) {
		quantity = quant;
	}
	
	
	public boolean getIsNew() {
		return isNew;
	}
	
	public void setIsNew(boolean newIn) {
		isNew = newIn;
	}
	
	
	public HBox getTireView() {
		HBox pane = new HBox();
		TireViewController controller = null;
		try {
			//create a FXMLLoader instance so that you can get the controller
			
			FXMLLoader loader = new FXMLLoader(App.class.getResource("resources/TireViewModel.fxml"));
			
			pane = loader.load();
			
			controller =(TireViewController)loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//make sure controller was loaded
		if(controller != null) {
			controller.updateView(this);
		}
		
		return pane;
	}
	
	@Override
	public String toString() {
		String str = this.brand + " " + this.tire_model + "    " + this.width + "/" + this.aspect_ratio + "R" + 
				this.rim_size + "    " + this.tire_type;
		
		return str;
	}
	

}
