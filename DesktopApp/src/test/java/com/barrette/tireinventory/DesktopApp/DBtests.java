package com.barrette.tireinventory.DesktopApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import com.barrette.tireinventory.DesktopApp.models.Tire;

public class DBtests {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//DAO dao = new DAO();
		
		
		/*
		//populate the database with some stuff
		Tire tire1 = new Tire();
		tire1.setBrand("Pirelli");
		tire1.setTireModel("Scorpion");
		tire1.setWidth(235);
		tire1.setAspectRatio(45);
		tire1.setRimSize(18);
		tire1.setType("all season");
		tire1.setIsNew(true);
		tire1.setQuantity(4);
		
		dao.addTireToDatabase(tire1);
		
		
		Tire tire2 = new Tire();
		tire2.setBrand("Cooper");
		tire2.setTireModel("SRX");
		tire2.setWidth(205);
		tire2.setAspectRatio(60);
		tire2.setRimSize(15);
		tire2.setType("all season");
		tire2.setIsNew(true);
		tire2.setQuantity(8);
		
		dao.addTireToDatabase(tire2);
		
		Tire tire3 = new Tire();
		tire3.setBrand("Cooper");
		tire3.setTireModel("RS5");
		tire3.setWidth(205);
		tire3.setAspectRatio(60);
		tire3.setRimSize(15);
		tire3.setType("all season");
		tire3.setIsNew(true);
		tire3.setQuantity(8);
		
		dao.addTireToDatabase(tire3);
		*/
		
		//run this try block to add the table for sales to the database
		try {
			Connection conn = DriverManager.getConnection("jdbc:h2:~/test_tire_inventory", "SA", "");
			
			Statement stmt = conn.createStatement();
			
			//create the main table to hold the tire inventory
			String sql = "CREATE TABLE tire_inventory (id IDENTITY NOT NULL PRIMARY KEY, brand  VARCHAR NOT NULL," +
					"tire_model VARCHAR NOT NULL, width INT NOT NULL, aspect_ratio int NOT NULL, rim_size int NOT NULL," +
					"tire_type VARCHAR NOT NULL, new BOOLEAN NOT NULL, quantity INT NOT NULL)";
		
			//stmt.executeUpdate(sql);
		
			//get the current year
			Date today = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);
			
			int currentYear = cal.get(Calendar.YEAR);
			String tableName = "sales_" + currentYear;
			
			//create the table to hold sales
			sql = "CREATE TABLE " + tableName + " (id IDENTITY NOT NULL PRIMARY KEY, january INT, february INT" +
					", march INT, april INT, may INT, june INT, july INT, august INT, september INT" +
					", october INT, november INT, december INT);";
			
			stmt.executeUpdate(sql);
			
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
