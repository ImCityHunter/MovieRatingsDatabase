

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	static String dbTables[] = {
			"WrittenBy",		// relations
			"Author", "Article", "Journal", "Publisher"		// entities
	    };
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
			
			//call to test if all functions are stored in the database properly
			testStoredFunctions(conn,stmt,rs);
			testStoredFunctions_invalid(conn,stmt,rs);
			
			//call to store data into database
			refreshData(conn, stmt, rs);
			

			// delete article
			System.out.println("\nDeleting article 10.1145/2838730 from CACM with 3 authors");
			stmt.execute("delete from Article where doi = '10.1145/2838730'");
			PubUtil.printArticles(conn);
			PubUtil.printAuthors(conn);

			// delete publisher ACM
			System.out.println("\nDeleting publisher ACM");
			stmt.executeUpdate("delete from Publisher where name = 'ACM'");
			PubUtil.printPublishers(conn);
			PubUtil.printJournals(conn);
			PubUtil.printArticles(conn);
			PubUtil.printAuthors(conn);
			
			// delete journal Spectrum (0018-9235)
			System.out.println("\nDeleting journal Spectrum from IEEE");
			stmt.executeUpdate("delete from Journal where issn = " + Biblio.parseIssn("0018-9235"));
			PubUtil.printJournals(conn);
			PubUtil.printArticles(conn);
			PubUtil.printAuthors(conn);
			
			
			// delete journal Computer
			System.out.println("\nDeleting journal Computer from IEEE");
			stmt.executeUpdate("delete from Journal where title = 'Computer'");
			PubUtil.printPublishers(conn);
			PubUtil.printJournals(conn);
			PubUtil.printArticles(conn);
			PubUtil.printAuthors(conn);
		
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
		for(String function: functions) {
			try {
				PreparedStatement invoke = conn.prepareStatement("values("+function+"(?))");
				invoke.setString(1,invalidValue);
				rs = invoke.executeQuery();
				if(rs.next()) {
					String result = rs.getString(1);
					System.out.printf("Function: %s. Input: %s. Result: %s\n",function, invalidValue, result);
				}
			}
			catch(SQLException e) {
				System.err.printf("Function: %s has an invalid input: %s\n",function, invalidValue);
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
	
	
	/**
	 * Refreshs Data from Files
	 * @param conn
	 * @param stmt
	 */
	public static void refreshData(Connection  conn, Statement stmt, ResultSet rs) {

		// name of data file
		String fileName = "pubdata.txt";

		try (
				// open data file
				BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
				
				// insert prepared statements
				PreparedStatement insertRow_Publisher = conn.prepareStatement(
						"insert into Publisher values(?, ?)");
				PreparedStatement insertRow_Journal = conn.prepareStatement(
						"insert into Journal values(?, parseISSN(?), ?)");
				PreparedStatement insertRow_Article = conn.prepareStatement(
						"insert into Article values(?, ?, parseISSN(?))");
				PreparedStatement insertRow_Author = conn.prepareStatement(
						"insert into Author values(?, ?, parseORCID(?))");
				PreparedStatement insertRow_WrittenBy = conn.prepareStatement(
						"insert into WrittenBy values(?, parseORCID(?))");
			) {
	            
	            // clear data from tables
	            for (String tbl : dbTables) {
		            try {
		            	stmt.executeUpdate("delete from " + tbl);
		            	//System.out.println("Truncated table " + tbl);
		            } catch (SQLException ex) {
		            	System.out.println("Did not truncate table " + tbl);
		            }
	            }
	            
	            System.out.println("\nAll Data are Dropped");
	            
				String line;
				while ((line = br.readLine()) != null) {
					// split input line into fields at tab delimiter
					String[] data = line.split("\t");
					if (data.length != 9) continue;
				
					// get fields from input data
					String publisherName = data[0];
					String publisherCity = data[1];
					
					// add Publisher if does not exist
					try {
						insertRow_Publisher.setString(1, publisherName);
						insertRow_Publisher.setString(2, publisherCity);
						insertRow_Publisher.execute();
					} catch (SQLException ex) {
						// already exists
						// System.err.printf("Already inserted Publisher %s City %s\n", publisherName, publisherCity);
					}
					
					
					
					// get fields from input data
					String journalTitle = data[2];
					String journalIssn = data[3]; // no ISSN
					
					
					// add Journal if does not exist
					try {

						insertRow_Journal.setString(1, journalTitle);
						insertRow_Journal.setString(2, journalIssn);
						insertRow_Journal.setString(3, publisherName);
						insertRow_Journal.execute();
					} catch (SQLException ex) {
						// already exists
						// System.err.printf("Already inserted Journal %s Issn %s Publisher %s\n", 
						//		journalTitle, Biblio.issnToString(journalIssn), publisherName);
					} catch (NumberFormatException ex) {
						System.err.printf("Unable to insert Journal %s invalid Issn %s\n", journalTitle, data[3]);
						continue;
					}

					// add Article if does not exist
					String articleTitle = data[4];
					String articleDOI = data[5];
					if (!Biblio.isDoi(articleDOI)) {
						System.err.printf("Unable to insert Article \"%s\" invalid DOI %s\n", articleTitle, articleDOI);
						continue;
					}
					try {
						insertRow_Article.setString(1, articleTitle);
						insertRow_Article.setString(2, articleDOI);
						insertRow_Article.setString(3, journalIssn);
						insertRow_Article.execute();
					} catch (SQLException ex) {
						// already exists
						// System.err.printf("Already inserted Article %s DOI %s, Issn %s\n", 
						//		articleTitle, articleDOI, Biblio.issnToString(journalIssn));
					}

					// add Author if does not exist
					String authorFamilyName = data[6];
					String authorGivenName = data[7];
					String authorORCID = data[8];
					try {
						insertRow_Author.setString(1, authorFamilyName);
						insertRow_Author.setString(2, authorGivenName);
						insertRow_Author.setString(3, authorORCID);
						insertRow_Author.execute();
					} catch (SQLException ex) {
						// already exists
						// System.err.printf("Already inserted Author %s, %s ORCID %016d\n", 
						//		 authorFamilyName, authorGivenName, authorORCID);
					} catch (NumberFormatException ex) {
						 System.err.printf("Unable to insert Author %s, %s invalid ORCID %s\n", 
								 authorFamilyName, authorGivenName, data[8]);
						continue;
					}

					// add WrittenBy if does not exist
					try {
						insertRow_WrittenBy.setString(1, articleDOI);
						insertRow_WrittenBy.setString(2, authorORCID);
						insertRow_WrittenBy.execute();
					} catch (SQLException ex) {
						// already exists
						//System.err.printf("Already inserted WrittenBy %64s ORCID %d\n", articleDOI, authorORCID);
					}
				}
				
				System.out.println("Refresh Data: ");
				// print number of rows in tables
				for (String tbl : dbTables) {
					rs = stmt.executeQuery("select count(*) from " + tbl);
					if (rs.next()) {
						int count = rs.getInt(1);
						System.out.printf("Table %s : count: %d\n", tbl, count);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
