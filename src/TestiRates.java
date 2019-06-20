

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;





/**
 * This program tests the version of the publication database tables for Assignment 5 
 * that uses attributes for the PublishedBy and PublishedIn relations. The sample
 * data is stored in a tab-separated data file The columns of the data file are:
 * pubName, pubCity, jnlName, jnlISSN, artTitle, artDOI, auFamiily, auGiven, auORCID
 * 
 * @author philip gust
 */
public class TestiRates {
	static String [] dependentTables = {"Endorsement","review","Attendance"};
	static String [] independentTables= {"Customer", "Movie"};
	static String [] functions= {"isISSN","isDoi","isORCID","parseISSN","issnToString","orcidToString","parseOrcid"};
	public static void main(String[] args) {
	    // the default framework is embedded
	    String protocol = "jdbc:derby:";
	    String dbName = "publication";
		String connStr = protocol + dbName+ ";create=true";

	    // tables tested by this program

		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derbyclient frameworks
        props.put("user", "user1");
        props.put("password", "user1");

        // result set for queries
        ResultSet rs = null;

		try {
			Connection  conn = DriverManager.getConnection(connStr, props);
			Statement stmt = conn.createStatement();
			//call to store data into database
			setDefaultData.defaultData(conn,stmt,rs);
			
			for(String table: independentTables) {
				System.out.println("Table: "+table);
				printTable(stmt,table,rs);
			}
			for(String table: dependentTables) {
				System.out.println("Table: "+table);
				printTable(stmt,table,rs);
			}
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	static void printTable(Statement stmt, String table, ResultSet rs) {
		try {
			rs = stmt.executeQuery("SELECT * From "+table);
			ResultSetMetaData rsmd = rs.getMetaData();
			String eventFormat = "";
			int numberOfColumns = rsmd.getColumnCount();
			String [] row = new String [numberOfColumns];
	        
			for(int i=0;i<numberOfColumns;i++) {
	        	eventFormat+="%-20s";
	        }
			eventFormat+="\n";
	        
		    for (int i = 1; i <= numberOfColumns; i++) {
		         String columnName = rsmd.getColumnName(i);
		         row[i-1] = columnName;
		     }
		     System.out.format(eventFormat,row);
		     while (rs.next()) {
		        for (int i = 1; i <= numberOfColumns; i++) {
		        	
		            String columnValue = rs.getString(i);
		            row [i-1] = columnValue;
		          } 
		        System.out.format(eventFormat, row);
		     }
		     System.out.println();
		      
		   
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
