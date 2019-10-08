package com.barrette.tireinventory.DesktopApp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.barrette.tireinventory.DesktopApp.AnalysisDAO;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.Series;

public class AnalysisController {
	
	AnalysisDAO analysisDAO;
	int[] monthlySales = new int[12];
	
	@FXML HBox chartHBox;
	@FXML VBox statBox;
	@FXML ComboBox<String> yearCombo, monthCombo, chartCombo;

	@FXML public void initialize() {
		analysisDAO = new AnalysisDAO();
		List<String> temp = analysisDAO.getAllSalesYears();

		for(String t: temp) {
			yearCombo.getItems().add(t);
		}
		yearCombo.getSelectionModel().selectFirst();
		
		String[] months = {"All", "January", "February", "March", "April", "May", "June", "July", "August",
						"September", "October", "November", "December"};
		
		monthCombo.getItems().addAll(months);
		monthCombo.getSelectionModel().selectFirst();

		
		String[] charts = {"Sales by Year", "Monthly"};
		chartCombo.getItems().addAll(charts);
		
		
		createMonthlyChart("August", "2016");
	}
	
	/***
	 * creates the line chart to analyze a year, takes the year as a parameter
	 * @param year
	 */
	private void createYearChart(String year) {
		chartHBox.getChildren().clear();
		statBox.getChildren().clear();
		
		//get the info
		monthlySales = null;
		monthlySales = analysisDAO.getAllMonthTotals(year);
		
		//setup the chart
		NumberAxis monthAxis = new NumberAxis();
		monthAxis.setLabel("Months");
		monthAxis.setAutoRanging(false);
		monthAxis.setUpperBound(12);
		monthAxis.setTickLabelsVisible(false);
		
		NumberAxis salesAxis = new NumberAxis();
		salesAxis.setLabel("Sales");
		salesAxis.setAutoRanging(false);
		salesAxis.setUpperBound(0);
		
		Series<Number, Number> series = new XYChart.Series<Number, Number>();
		series.getData().add(new XYChart.Data<Number, Number>(0,0));
		
		for(int i = 0; i < monthlySales.length; ++i) {
			//reset the upper bound if the value in array is higher
			if(monthlySales[i] > salesAxis.getUpperBound()) {
				int tempInt = ((monthlySales[i] + 10)/10) * 10;
				salesAxis.setUpperBound(tempInt);
			}
			series.getData().add(new XYChart.Data<Number, Number>(i + 1, monthlySales[i]));
		}
		
		LineChart<Number, Number> chart = new LineChart<Number, Number>(monthAxis, salesAxis);
		chart.setTitle("Monthly Sales for " + year);
		chart.getData().add(series);
		chart.setPrefWidth(1000);
		chart.setMaxHeight(500);
		chart.setLegendVisible(false);
		
		chartHBox.getChildren().add(chart);
		
		
		//fill in the data box with stats
		
		Label statsLabel = new Label();
		statsLabel.setText(year + " Statistics");
		statsLabel.setStyle("-fx-font-size:2em");
		statBox.getChildren().add(statsLabel);
		
		Label space = new Label(" ");
		statBox.getChildren().add(space);
		
		Label yearTotalLabel = new Label();
		yearTotalLabel.setText("Total Sales: " + analysisDAO.getYearsTotalSales(year));
		yearTotalLabel.setStyle("-fx-font-size:2em");
		statBox.getChildren().add(yearTotalLabel);
		
		Label bestSellingLabel = new Label();
		bestSellingLabel.setText("Best Selling Tire:");
		bestSellingLabel.setStyle("-fx-font-size:2em");
		statBox.getChildren().add(bestSellingLabel);
		
		Label bestSellingLabel2 = new Label();
		ResultSet rs = analysisDAO.getBestSellerByYear("2016");
		String labelString = "";
		try {
			while(rs.next()) {
				labelString = "     " + rs.getString("brand") + " " + rs.getString("tire_model") + " " + 
						String.valueOf(rs.getString("width")) + "/" + String.valueOf(rs.getString("aspect_ratio")) + "R" +
						String.valueOf(rs.getInt("rim_size")) + "    " + String.valueOf(rs.getInt("total")) + " sold";
				
				break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bestSellingLabel2.setText(labelString);
		bestSellingLabel2.setStyle("-fx-font-size:1.5em");
		statBox.getChildren().add(bestSellingLabel2);
		
		Label bestSellingBrand = new Label();
		bestSellingBrand.setText("Best Selling Brand:");
		bestSellingBrand.setStyle("-fx-font-size:2em");
		statBox.getChildren().add(bestSellingBrand);
		
		Label bestSellingBrand2 = new Label();
	
		rs = analysisDAO.getBestSellingBrandByYear(year);
		try {
			while(rs.next()) {
				bestSellingBrand2.setText("     " + rs.getString("brand") + "    " + String.valueOf(rs.getInt("total")) + " sold");
				break;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		bestSellingBrand2.setStyle("-fx-font-size:1.5em");
		statBox.getChildren().add(bestSellingBrand2);
		
		Label bestSellingModel = new Label();
		bestSellingModel.setText("Best Selling Tire Model");
		bestSellingModel.setStyle("-fx-font-size:2em");
		statBox.getChildren().add(bestSellingModel);
		
		Label bestSellingModel2 = new Label();
		
		rs = analysisDAO.getBestSellingModelByYear(year);
		try {
			while(rs.next()) {
				bestSellingModel2.setText("     " + rs.getString("brand") + " " + rs.getString("tire_model") + "     " +
						String.valueOf(rs.getInt("total")) + " Sold");
				break;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		bestSellingModel2.setStyle("-fx-font-size:1.5em");
		statBox.getChildren().add(bestSellingModel2);
		
		
		Label bestSellingSize = new Label();
		bestSellingSize.setText("Best Selling Tire Size:");
		bestSellingSize.setStyle("-fx-font-size:2em");
		statBox.getChildren().add(bestSellingSize);
		
		Label bestSellingSize2 = new Label();
		
		rs = analysisDAO.getBestSellingSizeByYear(year);
		try {
			while(rs.next()) {
				bestSellingSize2.setText("     " + String.valueOf(rs.getInt("width")) + "/" + String.valueOf(rs.getInt("aspect_ratio")) + 
						"R" + String.valueOf(rs.getInt("rim_size")) + "     " + String.valueOf(rs.getInt("total")) +
						" Sold");
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		bestSellingSize2.setStyle("-fx-font-size:1.5em");
		statBox.getChildren().add(bestSellingSize2);
	}
	
	/***
	 * creates a pie chart to analyze a month, takes the month and year as a parameter
	 * @param month
	 * @param year
	 */
	private void createMonthlyChart(String month, String year) {
		
		chartHBox.getChildren().clear();
		statBox.getChildren().clear();
		
		//set the data that will be added to the pie chart for brand sales
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		
		if(monthCombo.getSelectionModel().getSelectedIndex() >= 1) {
			ResultSet rs = analysisDAO.getMonthlyBrandSales(monthCombo.getSelectionModel().getSelectedItem(),
					yearCombo.getSelectionModel().getSelectedItem());
			
			try {
				while(rs.next()) {
					if(rs.getInt("total") != 0) {
						pieChartData.add(new PieChart.Data(rs.getString("brand"), rs.getInt("total")));	
					}
				}
				rs.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			return;
		}
		
		PieChart pieChart = new PieChart(pieChartData);
		pieChart.setMaxHeight(500);
		pieChart.setTitle("Brand Sales");
		pieChart.setLabelsVisible(false);
		pieChart.setClockwise(true);
		
		chartHBox.getChildren().add(pieChart);
		
		//set the data that will be added to the pie chart for model sales
		ObservableList<PieChart.Data> modelPieChartData = FXCollections.observableArrayList();
		
		ResultSet rs = null;
		rs = analysisDAO.getMonthlyModelSales(monthCombo.getSelectionModel().getSelectedItem(), 
				yearCombo.getSelectionModel().getSelectedItem());
		try {
			while(rs.next()) {
				if(rs.getInt(monthCombo.getSelectionModel().getSelectedItem().toLowerCase()) != 0) {
					modelPieChartData.add(new PieChart.Data(rs.getString("tire_model"), 
							rs.getInt(monthCombo.getSelectionModel().getSelectedItem())));
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		PieChart modelPieChart = new PieChart(modelPieChartData);
		modelPieChart.setMaxHeight(500);
		modelPieChart.setTitle("Model Sales");
		modelPieChart.setLabelsVisible(false);
		modelPieChart.setClockwise(true);
		
		chartHBox.getChildren().add(modelPieChart);
		
		Label lbl = new Label("Total Sales: ");
		lbl.setStyle("-fx-font-size:2em");
		statBox.getChildren().add(lbl);

		Label monthTotal = new Label();
		monthTotal.setText("     " + String.valueOf(analysisDAO.getMonthlyTotal(yearCombo.getSelectionModel().getSelectedItem(), 
				monthCombo.getSelectionModel().getSelectedItem())));
		monthTotal.setStyle("-fx-font-size:1.5em");
		statBox.getChildren().add(monthTotal);
		
	}
	
	@FXML protected void getDataButton(ActionEvent ae) {
		
		
		//first check to see which chart is requested, it will effect what else needs to be selected
		if(chartCombo.getSelectionModel().getSelectedItem() != null && 
				chartCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Sales by Year")) {
			//make sure that something is selected in year combo box
			if(yearCombo.getSelectionModel().getSelectedItem() != null) {
				createYearChart(yearCombo.getSelectionModel().getSelectedItem());
			} 
		} else if(chartCombo.getSelectionModel().getSelectedItem() != null &&
				chartCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Monthly")) {
			if(yearCombo.getSelectionModel().getSelectedItem() != null && 
					monthCombo.getSelectionModel().getSelectedItem() != null) {
				createMonthlyChart(monthCombo.getSelectionModel().getSelectedItem(), yearCombo.getSelectionModel().getSelectedItem());
			}
		}
	}

	
}
