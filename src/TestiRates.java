

import java.io.BufferedReader;
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
	static String [] independentTables= {"Customer", "Movie","Endorsement"};
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
			printTable(stmt,"customer",rs);
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	/**
	 * Test all the stored functions with invalid input to see if functions are accurately inserted
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	public static void testStoredFunctions_invalid(Connection  conn, Statement stmt, ResultSet rs) {
		String invalidValue = null;
		int invalidInt = 0;
		//test all invalid methods
				System.out.println("\n---------------------------------------------");
				System.out.println("Test functions with invalid values");
		for(String function: functions) {
			try {
				PreparedStatement invoke = conn.prepareStatement("values("+function+"(?))");
				invoke.setInt(1,invalidInt);
				rs = invoke.executeQuery();
				if(rs.next()) {
					String result = rs.getString(1);
					System.out.printf("Function: %s. Input: %d. Result: %s\n",function, invalidInt, result);
				}
			}
			catch(SQLException e) {
				System.err.printf("Function: %s has an invalid input: %s\n",function, invalidInt);
			}
		}


	}
	
	/**
	 * Test all the Stored Functions with values that should be passed and produce correct result
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	public static void testStoredFunctions(Connection  conn, Statement stmt, ResultSet rs) {
		try {
			String issnString = "1542-7730";
			String orcidString = "0000-0001-7303-7662";
			int issnInt=0;
			long orcidLong =0;
			String biblioString;
			int biblioInt=0;
			long biblioLong=0;
			boolean biblioBoolean=false;
			
			boolean isISSN = false;
			boolean isDOI = false;
			boolean isORCID = false;
			
			String doi = "10.1145/3134434.3136559";
			PreparedStatement invoke_issnToString = conn.prepareStatement("values(issnToString(?))");
			PreparedStatement invoke_isISSN =  conn.prepareStatement("values(isISSN(?))");
			PreparedStatement invoke_parseISSN =  conn.prepareStatement("values(parseISSN(?))");
			PreparedStatement invoke_isDoi =  conn.prepareStatement("values(isDoi(?))");
			PreparedStatement invoke_isORCID =  conn.prepareStatement("values(isORCID(?))");
			PreparedStatement invoke_parseORCID =  conn.prepareStatement("values(parseORCID(?))");
			PreparedStatement invoke_orcidToString = conn.prepareStatement("values(orcidToString(?))");
			
			
			System.out.println("\n---------------------------------------------");
			System.out.println("Testing All Stored Function: \n");
			
			invoke_parseISSN.setString(1, issnString);
			rs = invoke_parseISSN.executeQuery();
			if(rs.next()) {
				System.out.println("Testing ParseISSN");
				issnInt = rs.getInt(1);
				biblioInt = Biblio.parseIssn(issnString);
				System.out.printf("  original String issn: %s, converted int issn: %d\n",issnString,issnInt);
				System.out.printf("  Stored Function Result: %d, Biblio Result: %d\n",issnInt,biblioInt);
			}
			invoke_isISSN.setInt(1, issnInt);
			rs = invoke_isISSN.executeQuery();
			if(rs.next()) {
				System.out.println("Testing isISSN");
				isISSN = rs.getBoolean(1);
				biblioBoolean = Biblio.isIssn(issnInt);
				System.out.printf("  input value is %d and is it ISSN? %s\n",issnInt,isISSN);
				System.out.printf("  Stored Function Result: %s, Biblio Result: %s\n",isISSN,biblioBoolean);
			}
			
			invoke_issnToString.setInt(1,issnInt);
			rs = invoke_issnToString.executeQuery();
			if(rs.next()) {
				System.out.println("Testing isISSN");
				issnString = rs.getString(1);
				biblioString = Biblio.issnToString(issnInt);
				System.out.printf("  original int issn: %d, converted string issn: %s\n",issnInt,issnString);
				System.out.printf("  Stored Function Result: %s, Biblio Result: %s\n",issnString,biblioString);
			}
			
			invoke_isDoi.setString(1, doi);
			
			rs = invoke_isDoi.executeQuery();
			if(rs.next()) {
				System.out.println("Testing isDOI");
				isDOI = rs.getBoolean(1);
				biblioBoolean = Biblio.isDoi(doi);
				System.out.printf("  input value is %s and is it DOI? %s\n",doi,isDOI);
				System.out.printf("  Stored Function Result: %s, Biblio Result: %s\n",isDOI,biblioBoolean);
			}
			invoke_parseORCID.setString(1, orcidString);
			rs = invoke_parseORCID.executeQuery();
			if(rs.next()) {
				System.out.println("Testing parseORCID");
				orcidLong = rs.getLong(1);
				biblioLong = Biblio.parseOrcid(orcidString);
				System.out.printf("  original String orcid: %s, converted long orcid: %d\n", orcidString, orcidLong );
				System.out.printf("  Stored Function Result: %d, Biblio Result: %d\n",orcidLong,biblioLong);
			}
			
			invoke_isORCID.setLong(1, orcidLong);
			rs = invoke_isORCID.executeQuery();
			if(rs.next()) {
				System.out.println("Testing isORCID");
				isORCID = rs.getBoolean(1);
				biblioBoolean = Biblio.isOrcid(orcidLong);
				System.out.printf("  input value is %d and is it ORCID? %s\n", orcidLong,isORCID);
				System.out.printf("  Stored Function Result: %s, Biblio Result: %s\n",isORCID,biblioBoolean);
			}
			
			invoke_orcidToString.setLong(1,orcidLong);
			rs = invoke_orcidToString.executeQuery();
			if(rs.next()) {
				System.out.println("Testing orcidtoString");
				orcidString = rs.getString(1);
				biblioString = Biblio.orcidToString(orcidLong);
				System.out.printf("  original long orcid: %d, converted string orcid: %s\n",orcidLong,orcidString);
				System.out.printf("  Stored Function Result: %s, Biblio Result: %s\n",orcidString,biblioString);
			}
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void printTable(Statement stmt, String table, ResultSet rs) {
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
		        }
		     System.out.format(eventFormat, row);
		      
		   
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
