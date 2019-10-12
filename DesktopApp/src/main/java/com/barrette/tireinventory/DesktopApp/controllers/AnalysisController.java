package com.barrette.tireinventory.DesktopApp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.barrette.tireinventory.DesktopApp.AnalysisDAO;
import com.barrette.tireinventory.DesktopApp.App;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.Series;

public class AnalysisController {
	
	AnalysisDAO analysisDAO;
	int[] monthlySales, monthlySales2 = new int[12];
	boolean landscape;
	ScrollPane scroll = null;
	
	@FXML HBox chartHBox, mainChartBox;
	@FXML VBox statBox;
	@FXML ComboBox<String> yearCombo, monthCombo, chartCombo;

	@FXML public void initialize() {
		analysisDAO = new AnalysisDAO();
		List<String> temp = analysisDAO.getAllSalesYears();

		for(String t: temp) {
			yearCombo.getItems().add(t);
		}
		yearCombo.getSelectionModel().selectLast();
		
		String[] months = {"All", "January", "February", "March", "April", "May", "June", "July", "August",
						"September", "October", "November", "December"};
		
		monthCombo.getItems().addAll(months);
		monthCombo.getSelectionModel().selectFirst();

		
		String[] charts = {"Sales by Year", "Monthly Chart", "Monthly Details", "Compare to Last Year"};
		chartCombo.getItems().addAll(charts);
		chartCombo.getSelectionModel().selectFirst();
		
		createYearChart(yearCombo.getSelectionModel().getSelectedItem(), true);
	}
	
	/***
	 * creates the line chart to analyze a year, takes the year as a parameter
	 * @param year
	 */
	private void createYearChart(String year, boolean compare) {
		landscape = true;
		
		chartHBox.getChildren().clear();
		statBox.getChildren().clear();
		
		//get the info
		monthlySales = null;
		monthlySales = analysisDAO.getAllMonthTotals(year);
		monthlySales2 = null;
		
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
		series.setName(year);
		series.getData().add(new XYChart.Data<Number, Number>(0,0));
		
		for(int i = 0; i < monthlySales.length; ++i) {
			//reset the upper bound if the value in array is higher
			if(monthlySales[i] > salesAxis.getUpperBound()) {
				int tempInt = ((monthlySales[i] + 10)/10) * 10;
				salesAxis.setUpperBound(tempInt);
			}
			series.getData().add(new XYChart.Data<Number, Number>(i + 1, monthlySales[i]));
		}
		
		Series<Number, Number> series2 = new XYChart.Series<Number, Number>();

		String lastYear = String.valueOf(Integer.parseInt(year) - 1);

		//check if the year is in the combo chart for last year
		boolean present = false;
		
		
		if(compare) {

			monthlySales2 = analysisDAO.getAllMonthTotals(lastYear);

			series2.getData().add(new XYChart.Data<Number, Number>(0,0));
			
			for(int i = 0; i < monthlySales2.length; ++i) {
				//reset the upper bound if the value in array is higher
				if(monthlySales2[i] > salesAxis.getUpperBound()) {
					int tempInt = ((monthlySales2[i] + 10)/10) * 10;
					salesAxis.setUpperBound(tempInt);
				}
				series2.getData().add(new XYChart.Data<Number, Number>(i + 1, monthlySales2[i]));
			}
			series2.setName(lastYear);
		}
		
		
		LineChart<Number, Number> chart = new LineChart<Number, Number>(monthAxis, salesAxis);
		if(compare) {
			chart.setTitle("Comparison to Previous Year");
		} else {
			chart.setTitle("Monthly Sales for " + year);
		}
		chart.getData().add(series);
		if(compare) {
			chart.getData().add(series2);
		}
		chart.setPrefWidth(1000);
		chart.setMaxHeight(500);
		//chart.setLegendVisible(false);
		
		
		chartHBox.getChildren().add(chart);
		
		
		//fill in the data box with stats
		if(!compare) {
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
			ResultSet rs = analysisDAO.getBestSellerByYear(year);
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
	}
	
	/***
	 * creates a pie chart to analyze a month, takes the month and year as a parameter
	 * @param month
	 * @param year
	 */
	private void createMonthlyChart(String month, String year) {
		landscape = true;		//set for printing purposes
		
		chartHBox.getChildren().clear();
		statBox.getChildren().clear();
		
		
		//set the data that will be added to the pie chart for brand sales
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		
		if(monthCombo.getSelectionModel().getSelectedIndex() >= 1) {
			ResultSet rs = analysisDAO.getMonthlyBrandSales(month.toLowerCase(), year);
			
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
		pieChart.setTitle(month + " " + year +  " Brand Sales");
		pieChart.setLabelsVisible(false);
		pieChart.setClockwise(true);
		
		chartHBox.getChildren().add(pieChart);
		
		//set the data that will be added to the pie chart for model sales
		ObservableList<PieChart.Data> modelPieChartData = FXCollections.observableArrayList();
		
		ResultSet rs = null;
		rs = analysisDAO.getMonthlyModelSales(month, year);
		try {
			while(rs.next()) {
				if(rs.getInt(month) != 0) {
					modelPieChartData.add(new PieChart.Data(rs.getString("tire_model"), 
							rs.getInt(month)));
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		PieChart modelPieChart = new PieChart(modelPieChartData);
		modelPieChart.setMaxHeight(500);
		modelPieChart.setTitle(month + " " + year + " Model Sales");
		modelPieChart.setLabelsVisible(false);
		modelPieChart.setClockwise(true);
		
		chartHBox.getChildren().add(modelPieChart);
		
		Label lbl2 = new Label("Total Sales: ");
		lbl2.setStyle("-fx-font-size:2em");
		statBox.getChildren().add(lbl2);

		Label monthTotal = new Label();
		monthTotal.setText("     " + String.valueOf(analysisDAO.getMonthlyTotal(year, month.toLowerCase())));
		monthTotal.setStyle("-fx-font-size:1.5em");
		statBox.getChildren().add(monthTotal);
		
	}
	
	private void createMonthlyDetailsReport(String month, String year) {
		landscape = false;
		
		chartHBox.getChildren().clear();
		statBox.getChildren().clear();
		
		VBox vbox = new VBox();
		Label title = new Label();
		title.setText(month + " " + year + " Sales Details");
		title.setStyle("-fx-font-size:2em");
		vbox.getChildren().add(title);
		
		//list to hold all the labels that are created
		List<Label> labels = new ArrayList<Label>();
		
		ResultSet rs = analysisDAO.getMonthlyTireSales(month, year);
		try {
			while(rs.next()) {
				Label temp = new Label();
				temp.setText(rs.getString("brand") + " " + rs.getString("tire_model") + " " + rs.getInt("width") + 
						"/" + rs.getInt("aspect_ratio") + "R" + rs.getInt("rim_size") + "      " + rs.getInt(month) + 
						" Sold");
				temp.setStyle("-fx-font-size:1.25em");
				if(rs.getInt(month) != 0) {
					labels.add(temp);
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		for(Label l : labels) {
			vbox.getChildren().add(l);
		}
		
		scroll = new ScrollPane();
		scroll.setContent(vbox);
		scroll.setMaxHeight(500);
		
		chartHBox.getChildren().add(scroll);
	}
	
	@FXML protected void getDataButton(ActionEvent ae) {
		//first check to see which chart is requested, it will effect what else needs to be selected
		if(chartCombo.getSelectionModel().getSelectedItem() != null && 
				chartCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Sales by Year")) {
			//make sure that something is selected in year combo box
			if(yearCombo.getSelectionModel().getSelectedItem() != null) {
				createYearChart(yearCombo.getSelectionModel().getSelectedItem(), false);
			} 
		} else if(chartCombo.getSelectionModel().getSelectedItem() != null &&
				chartCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Monthly Chart")) {
			if(yearCombo.getSelectionModel().getSelectedItem() != null && 
					monthCombo.getSelectionModel().getSelectedItem() != null) {
				createMonthlyChart(monthCombo.getSelectionModel().getSelectedItem(), yearCombo.getSelectionModel().getSelectedItem());
			}
		} else if(chartCombo.getSelectionModel().getSelectedItem() != null &&
				chartCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Monthly Details")) {
			if(yearCombo.getSelectionModel().getSelectedItem() != null && 
					monthCombo.getSelectionModel().getSelectedItem() != null) {
				createMonthlyDetailsReport(monthCombo.getSelectionModel().getSelectedItem(), yearCombo.getSelectionModel().getSelectedItem());
			}
		} else if(chartCombo.getSelectionModel().getSelectedItem() != null && 
				chartCombo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Compare to last year")) {
			//make sure that something is selected in year combo box
			if(yearCombo.getSelectionModel().getSelectedItem() != null) {
				createYearChart(yearCombo.getSelectionModel().getSelectedItem(), true);
			}
		}
	
	}

	@FXML protected void printChartButton(ActionEvent ae) {
		Printer printer = Printer.getDefaultPrinter();

		PageLayout pageLayout = null;


		double scaleX;
		double scaleY;
		
		
		if(!landscape)	{		//is the details report, only print whats in the ScrollPane
			pageLayout = printer.createPageLayout(Paper.A4,  PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);

			//set the size of the pane to the printable size
			scaleX = pageLayout.getPrintableWidth() / scroll.getBoundsInParent().getWidth();
			scaleY = pageLayout.getPrintableHeight()/ scroll.getBoundsInParent().getHeight();

			//Scale to the correct size for printing
			Scale scale = new Scale(scaleX, scaleY);

			//create a temporary scroll pane that can be scaled
			ScrollPane temp = scroll;
			temp.getTransforms().add(scale);

			PrinterJob job = PrinterJob.createPrinterJob();

			job.getJobSettings().setPageLayout(pageLayout);

			if(job.showPrintDialog(App.mainStage)) {
				job.printPage(temp);
			}
			job.endJob();
			temp.getTransforms().remove(scale);
		} else { 					
			pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);

			//set the size to the printable size
			scaleX = (pageLayout.getPrintableWidth() -100) / mainChartBox.getBoundsInParent().getWidth();
			scaleY = (pageLayout.getPrintableHeight() - 100)/ mainChartBox.getBoundsInParent().getHeight();
			
			//scale to correct size for printing
			Scale scale = new Scale(scaleX, scaleY);

			HBox temp = mainChartBox;

			temp.getTransforms().add(scale);

			PrinterJob job = PrinterJob.createPrinterJob();
			job.getJobSettings().setPageLayout(pageLayout);

			if(job.showPrintDialog(App.mainStage)) {
				job.printPage(temp);
			}
			job.endJob();
			temp.getTransforms().remove(scale);
		}

		
	}
}
