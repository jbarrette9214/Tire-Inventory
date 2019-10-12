package com.barrette.tireinventory.DesktopApp;

import com.barrette.tireinventory.DesktopApp.App;
import com.barrette.tireinventory.DesktopApp.DAO;
import com.barrette.tireinventory.DesktopApp.models.Tire;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class AnalysisDAO {

	private Connection conn;
	String[] months = {"january", "february", "march", "april", "may", "june", "july", "august", "september",
								"october", "november", "december"};
	
	
	public AnalysisDAO() {
		conn = DAO.getConnection();
	}
	
	/***
	 * returns a list of all the years that there is a sales table for
	 * @return
	 */
	public List<String> getAllSalesYears() {
		List<String> temp = new ArrayList<String>();
		
		//get all the tables that start with sales word
		try {
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, "SALES%", null);
			
			while(rs.next()) {
				String tempStr = rs.getString("TABLE_NAME");
				tempStr = tempStr.substring(5);
				temp.add(tempStr);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return temp;
	}
	
	/***
	 * returns the total number of tires sold in a particular month
	 * @param year
	 * @param month
	 * @return
	 */
	public int getMonthlyTotal(String year, String month) {
		int temp = -1;
		
		String query = "select sum (" + month.toUpperCase() + ") as total from sales" + year + ";";
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				temp = rs.getInt("total");
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			//table may not be present so just return 0
			temp = 0;
		}
		
		return temp;
	}
	
	/***
	 * gets all the sales for each month of a year and returns those stats as an array
	 * @param year
	 * @return
	 */
	public int[] getAllMonthTotals(String year) {
		int[] temp = new int[12];
		
		for(int i = 0; i < months.length; i++) {
			temp[i] = getMonthlyTotal(year, months[i]);
		}
		
		return temp;
	}
	
	/***
	 * returns the id best selling tire of each month
	 * @param year
	 * @param month
	 * @return
	 */
	public ResultSet getBestSellerByMonth(String year, String month) {
		ResultSet rs = null;
		
		String sql = "select * from sales" + year + " where " + month + " = (select max(" + month + 
				") from sales" + year + ");";
		
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
	public int getYearsTotalSales(String year) {
		String sql = "select sum(january + february + march + april + june + july + august + september + october " +
				"+ november + december) as total from sales" + year + ";";
		int total = -1;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				total = rs.getInt("total");
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return total;
	}
	
	public ResultSet getBestSellerByYear(String year) {
		ResultSet rs = null;
		
		String sql = "select *, sum(january + february + march + may + june + july + august + september + october + november + december) as total from" + 
				" (select * from sales" + year + " left join tire_inventory on sales" + year + ".tire_id=tire_inventory.id) group by tire_id order by total desc;";
				
		
		
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}
	
	public ResultSet getBestSellingBrandByYear(String year) {
		ResultSet rs = null;
		
		String sql = "select brand, sum(january + february + march + april + may + june + july + august + september + october + november + december) as total from (select * from sales" + year + 
				" left join tire_inventory on sales"+ year + ".tire_id=tire_inventory.id) group by brand order by total desc;";
		
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
	public ResultSet getBestSellingSizeByYear(String year) {
		ResultSet rs = null;
		
		String sql = "select width, aspect_ratio, rim_size, sum(january + february + march + april + " + 
				"may + june + july + august + september + october + november + december) as total from " + 
				"(select * from sales" + year + " left join tire_inventory on sales" + year + 
				".tire_id=tire_inventory.id) group by width, aspect_ratio, rim_size  order by total desc;";
		
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
	/***
	 * returns a resultset of the best selling tire model for a year
	 * @param year
	 * @return
	 */
	public ResultSet getBestSellingModelByYear(String year) {
		ResultSet rs = null;
		String sql = "select brand, tire_model, sum(january + february + march + april + may + june + july + august + september + " +
				"october + november + december) as total from (select * from sales" + year + " left join tire_inventory " +
				" on sales" + year + ".tire_id=tire_inventory.id) group by tire_model order by total desc;";
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	
		return rs;
	}
	
	/***
	 * returns a resultset of all the brands sold in a month
	 * @param month
	 * @param year
	 * @return
	 */
	public ResultSet getMonthlyBrandSales(String month, String year) {
		ResultSet rs = null;
		
		String sql = "select brand, sum(" + month + ") as total from (select * from sales" + year + " left join " +
				"tire_inventory on sales" + year + ".tire_id=tire_inventory.id) group by brand;";
		
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
	/***
	 * returns a result set of all model sales in a month
	 * @param month
	 * @param year
	 * @return
	 */
	public ResultSet getMonthlyModelSales(String month, String year) {
		ResultSet rs = null;
		
		String sql = "select tire_model, " + month + " from (select * from sales" + year + " left join tire_inventory " +
				"on sales" + year + ".tire_id=tire_inventory.id);";
		
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
/*	public int getMonthlyTotal(String month, String year) {
		ResultSet rs = null;
		
		String sql = "select sum(" + month + ") as total from sales" + year + ";";
		
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				return rs.getInt("total");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
*/
	public ResultSet getMonthlyTireSales(String month, String year) {
		ResultSet rs = null;
		
		String sql = "select brand, tire_model, width, aspect_ratio, rim_size, " + month + " from "+ 
				" (select * from sales" + year + " left join tire_inventory on sales" + year + 
				".tire_id=tire_inventory.id);";
		
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
}
