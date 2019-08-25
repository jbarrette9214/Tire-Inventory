package com.barrette.tireinventory.DesktopApp.models;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SetupData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private char[] password;
	private String serverAddress;
	
	
	public SetupData() {
		String currentDir = System.getProperty("user.dir");
		File setup = new File(currentDir + "/setup.dat");
		if(setup.exists()) {
			//load all information
			readFromFile();
		} else {
			//doesn't exist
		}
	}
	
	
	public void setUserName(String user) {
		userName = user;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setPassword(char[] pass) {
		password = pass;
	}
	
	public char[] getPassword() {
		return password;
	}
	
	public void setServerAddress(String server) {
		serverAddress = server;
	}
	
	public String getServerAddress() {
		return serverAddress;
	}
	
	/**
	 * saves the setup information from the file
	 */
	public void saveToFile() {
		try {
			String currentDir = System.getProperty("user.dir");
			FileOutputStream fileOut = new FileOutputStream(new File(currentDir + "/setup.dat"));
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			
			objOut.writeObject(this);
			
			objOut.close();
			fileOut.close();
		} catch(FileNotFoundException e) {
			System.out.println("File not found.");
		} catch(IOException e) {
			System.out.println("Error initializing stream");
		} 
	}
	
	/**
	 * reads the setup information from the file
	 */
	private void readFromFile() {
		try {
			String currentDir = System.getProperty("user.dir");
			FileInputStream fileIn = new FileInputStream(new File(currentDir + "/setup.dat"));
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			
			
			SetupData temp = (SetupData)objIn.readObject();

			this.setUserName(temp.getUserName());
			this.setPassword(temp.getPassword());
			this.setServerAddress(temp.getServerAddress());
			
			objIn.close();
			fileIn.close();

			
			temp = null;
		}catch(EOFException e) {
			//do nothing end of file was reached		
		}catch(FileNotFoundException e) {
			System.out.println("File not found.");
		} catch(IOException e) {
			System.out.println("Couldn't initialzie input stream");
		} catch(ClassNotFoundException e) {
			System.out.println("Couldn't find class");
		}
	}
	
}
