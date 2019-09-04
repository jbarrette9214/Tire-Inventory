package com.barrette.tireinventory.DesktopApp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.barrette.tireinventory.DesktopApp.models.Tire;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

public class DAO {
	
	Connection conn;
	
	static final String USER = "sa";
	static final String PASS = "";
	
	private List<Tire> tires = new ArrayList<Tire>();
	
	public DAO() {
		try {
			
			String userHome = System.getProperty("user.home") + "/tire_inventory.mv.db";
			
			if(!Paths.get(userHome).toFile().exists()) {
				Alert alert = new Alert(AlertType.INFORMATION, "Database doesn't exist, load from backup",
						ButtonType.YES, ButtonType.NO);
				
				Optional<ButtonType> result = alert.showAndWait();
				
				if(result == null) {
					System.exit(0);
				}else if(result.get() == ButtonType.YES) {
					//show dialog to pick backup file
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Open Backup Database");
					
					//set the directory for the chooser to dart with
					String userDir = System.getProperty("user.home");
					File startDir = new File(userDir);
					
					fileChooser.setInitialDirectory(startDir);

					
					File file = fileChooser.showOpenDialog(null);
					
					if(file != null) {
						try {
							String userhome = System.getProperty("user.home") + "/tire_inventory.mv.db";

							File newCopy = new File(userhome);
							Files.copy(file.toPath(), newCopy.toPath());
							
							App.dao = null;
							App.dao = new DAO();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						System.exit(0);
					}
					
				} else if(result.get() == ButtonType.NO) {
					Alert alert2 = new Alert(AlertType.CONFIRMATION, "Create new database", ButtonType.YES,
											ButtonType.NO);
					
					result = alert.showAndWait();
					if(result.get() == ButtonType.NO) {
						System.exit(0);
					}
					createDB();
					
				}
				
			} 
			conn = DriverManager.getConnection("jdbc:h2:~/tire_inventory", USER, PASS);
		} catch (SQLException  e) {
			
			Alert alert = new Alert(AlertType.ERROR, "Unable to connect to database, may be open already", ButtonType.OK);
			e.printStackTrace();

			alert.showAndWait();
			System.exit(0);
		}
		
	}
	
	public void closeConnection() {
		
		try {
			conn.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * gets all the tires from the database
	 * @return
	 */
	public List<Tire> getAllTires() {
		tires.clear();
		
		String query = "select * from tire_inventory;";
		
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);
			
			//convert result set to a list
			tires = convertResultsToList(rs);
			
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!tires.isEmpty()) {
			return tires;
		} else {
			return null;
		}
	}
	
	public List<Tire> getEntireInventory() {
		tires.clear();
		
		String sql = "select * from tire_inventory where quantity>0;";
		
		try {
			
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql);
			
			tires = convertResultsToList(rs);
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(!tires.isEmpty() ) {
			return tires;
		} else {
			return null;
		}
	}
	
	/**
	 * get all the tires stored in the database of a specific size
	 * @param width
	 * @param aspect
	 * @param rim
	 * @return
	 */
	public List<Tire> getTiresBySize(int width, int aspect, int rim) {
		
		String query = "select * from tire_inventory where WIDTH=" + width + "and aspect_ratio=" + aspect +
						"and rim_size=" + rim + ";";
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);
			
			tires = convertResultsToList(rs);
			
			stmt.close();
		} catch (SQLException e) {
			
		}
		
		
		return tires;
	}
	
	/**
	 * get a tire by the id
	 * @param id
	 * @return
	 */
	public Tire getTireById(int id) {
		Tire tire = new Tire();
		
		String query = "select * from tire_inventory where id=" + id + ";";
		
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				tire = new Tire();
				
				tire.setId(rs.getInt("id"));
				tire.setBrand(rs.getString("brand"));
				tire.setTireModel(rs.getString("tire_model"));
				tire.setWidth(rs.getInt("width"));
				tire.setAspectRatio(rs.getInt("aspect_ratio"));
				tire.setRimSize(rs.getInt("rim_size"));
				tire.setType(rs.getString("tire_type"));
				tire.setIsNew(rs.getBoolean("new"));
				tire.setQuantity(rs.getInt("quantity"));
				
			}
			
			stmt.close();
		} catch (SQLException e) {
			
		}
		
		
		if(tire.getBrand() != null) {
			return tire;
		} else {
			return null;
		}
	}
	
	
	
	/**
	 * adds tire to the database, checks if there already is an instance that matches, if so increments the quantity
	 * if not in database it creates new instance
	 * @param tire
	 */
	public void addTireToDatabase(Tire tire) {
		int isThere = checkForTire(tire);
		
		//if the tire is there increment the quantity
		//if not create the new instance
		
		if(isThere != -1) {
			incrementQuantity(tire, tire.getQuantity());
		} else {
			String insert = "insert into tire_inventory (brand, tire_model, width, aspect_ratio, rim_size, tire_type, new, quantity)" +
							" values ('" + tire.getBrand() + "','" + tire.getTireModel() + "'," + tire.getWidth() + "," +
							tire.getAspectRatio() + "," + tire.getRimSize() + ",'" + tire.getType() + "'," +
							tire.getIsNew() + "," + tire.getQuantity() + ");";
			
			try {
				Statement stmt = conn.createStatement();
				
				stmt.executeUpdate(insert);
				
				stmt.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
		
	}
	
	
	
	public void incrementQuantity(Tire tire, int amountIncrement) {
		//retrieve the tire count for this tire
		String query = "select quantity from tire_inventory where id=" + tire.getId() + ";";
		
		int quantity = -1;
		
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);

			while(rs.next()) {
				quantity = rs.getInt("quantity");
			}

			if(quantity != -1) {
				quantity = tire.getQuantity() + amountIncrement;
			}
			
			String update = "update tire_inventory set quantity=" + quantity + " where id=" + tire.getId() +";";

			stmt.executeUpdate(update);
			
			stmt.close();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void decrementQuantity(Tire tire, int amountDecrement) {
		String query = "select quantity from tire_inventory where id=" + tire.getId() + ";";
		
		int quantity = -1;
		
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);

			while(rs.next()) {
				quantity = rs.getInt("quantity");
			}

			if(quantity != -1 && tire.getQuantity() >= amountDecrement) {
				quantity = tire.getQuantity() - amountDecrement;
			}
			
			String update = "update tire_inventory set quantity=" + quantity + " where id=" + tire.getId() +";";

			stmt.executeUpdate(update);
			
			stmt.close();
		
		} catch (SQLException e) {
			
		}
	}
	
	
	/**
	 * returns true if the tire matching that description is already in the database
	 * returns false if not in the database
	 * @param tire
	 * @return
	 */
	public int checkForTire(Tire tire) {
		
		String query = "select id from tire_inventory where brand =" + tire.getBrand() + ", width=" + tire.getWidth() +
						", aspect_ratio=" + tire.getAspectRatio() + ", rim_size=" + tire.getRimSize() + ";";
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				return rs.getInt("id");
			}
			
			stmt.close();
		} catch (SQLException e) {
			
		}
		
		return -1;
	}
	
	
	/**
	 * used to convert the result set into a list
	 * @param rs
	 * @return
	 */
	private List<Tire> convertResultsToList(ResultSet rs) {
		//clear the list
		tires.clear();
		
		try {
			while(rs.next()) {
				Tire tire = new Tire();
				
				tire.setId(rs.getInt("id"));
				tire.setBrand(rs.getString("brand"));
				tire.setTireModel(rs.getString("tire_model"));
				tire.setWidth(rs.getInt("width"));
				tire.setAspectRatio(rs.getInt("aspect_ratio"));
				tire.setRimSize(rs.getInt("rim_size"));
				tire.setType(rs.getString("tire_type"));
				tire.setIsNew(rs.getBoolean("new"));
				tire.setQuantity(rs.getInt("quantity"));
				
				tires.add(tire);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tires;
	}
	
	/**
	 * creates the database if it is called
	 */
	private void createDB() {
		
		try {
			conn = DriverManager.getConnection("jdbc:h2:~/tire_inventory", USER, PASS);
			
			Statement stmt = conn.createStatement();
			
			String sql = "CREATE TABLE tire_inventory (id IDENTITY NOT NULL PRIMARY KEY, brand  VARCHAR NOT NULL," +
					"tire_model VARCHAR NOT NULL, width INT NOT NULL, aspect_ratio int NOT NULL, rim_size int NOT NULL," +
					"tire_type VARCHAR NOT NULL, new BOOLEAN NOT NULL, quantity INT NOT NULL)";
		
			stmt.executeUpdate(sql);
		
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		
	}
	
}
