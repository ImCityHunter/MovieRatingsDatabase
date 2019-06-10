

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * This program creates a publication database for the ER data model
 * for Assignment 3. There are entity tables for Publisher, Journal, 
 * Article, and Author, and relationship tables for the publishedBy, 
 * publishedIn, and writtenBy relations in the ER model. 
 * 
 * This version uses the relationship name for the single fields in 
 * 1:n relationships between entities, rather than relationship tables.
 * Adding information to the relation will require re-factoring the 
 * database to use a table for the relationship. 
 * 
 * @author philip gust
 */

public class iRateTables {
	
	//All the constraints
	static String [] triggers = {
		"trigger_writtenBy","trigger_Publisher","trigger_Journal"
	};
	static String [] relationTables = {"WrittenBy"};
	static String [] dbTables= {"Author", "Article", "Journal", "Publisher"};
	
	static String [] functions= {"isISSN","isDoi","isORCID","parseISSN","issnToString","orcidToString","parseOrcid"};
	public static void main(String[] args) {
	    // the default framework is embedded
	    String protocol = "jdbc:derby:";
	    String dbName = "publication";
		String connStr = protocol + dbName+ ";create=true";
		
		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derbyclient frameworks
        props.put("user", "user1");
        props.put("password", "user1");

		try (
	        // connect to the database using URL
			Connection conn = DriverManager.getConnection(connStr, props);
	        // statement is channel for sending commands thru connection 
	        Statement stmt = conn.createStatement();
		){
	        System.out.println("Connected to and created database " + dbName);
            
			
			//clear database
	        dropTriggers(stmt);
			dropTables(stmt, relationTables);
			dropTables(stmt, dbTables);
			dropFunctions(stmt);
			
			
			//build database
			store_utilityFunctions(stmt);
			store_booleanFunctions(stmt);
			createTables(stmt,dbTables);
			createTriggers(stmt);
		
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	/**
	 * call to create and store functions in database
	 * @param stmt
	 */
	static void store_utilityFunctions(Statement stmt) {
		
		try {
			String parseISSN=
				"CREATE FUNCTION parseISSN(ISSN VARCHAR(64))"
				+ " RETURNS int "
				+ " PARAMETER STYLE JAVA "
				+ " LANGUAGE JAVA "
				+ " EXTERNAL NAME "
				+ "'Biblio.parseIssn'";
			stmt.executeUpdate(parseISSN);
			
			String issnToString=
				"CREATE FUNCTION issnToString(ISSN Int)"
				+ " RETURNS VARCHAR(64) "
				+ " PARAMETER STYLE JAVA "
				+ " LANGUAGE JAVA "
				+ " EXTERNAL NAME "
				+ "'Biblio.issnToString'";
			stmt.executeUpdate(issnToString);
			
			String parseOrcid=
					"CREATE FUNCTION parseOrcid(ORCID VARCHAR(100))"
					+ " RETURNS bigint "
					+ " PARAMETER STYLE JAVA "
					+ " LANGUAGE JAVA "
					+ " EXTERNAL NAME "
					+ "'Biblio.parseOrcid'";
			stmt.executeUpdate(parseOrcid);
			
			String orcidToString=
					"CREATE FUNCTION orcidToString(ORCID bigint)"
					+ " RETURNS VARCHAR(64) "
					+ " PARAMETER STYLE JAVA "
					+ " LANGUAGE JAVA "
					+ " EXTERNAL NAME "
					+ "'Biblio.orcidToString'";
			stmt.executeUpdate(orcidToString);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("All Utility Functions Created ");
	}
	
	/**
	 * call to store functions in database
	 * @param stmt
	 */
	static void store_booleanFunctions(Statement stmt) {
		try {
		String isISSN =
				"CREATE FUNCTION isISSN(ISSN int)"
				+ " RETURNS BOOLEAN "
				+ " PARAMETER STYLE JAVA "
				+ " LANGUAGE JAVA "
				+ " DETERMINISTIC "
				+ " EXTERNAL NAME "
				+ "'Biblio.isIssn'";
		stmt.executeUpdate(isISSN);

		String isDoi =
				"CREATE FUNCTION isDoi(DOI varchar(64))"
				+ " RETURNS BOOLEAN "
				+ " PARAMETER STYLE JAVA "
				+ " LANGUAGE JAVA "
				+ " EXTERNAL NAME "
				+ "'Biblio.isDoi'";
		stmt.executeUpdate(isDoi);
		
		String isOrcid =
				"CREATE FUNCTION isORCID(ORCID bigint)"
				+ " RETURNS BOOLEAN "
				+ " PARAMETER STYLE JAVA "
				+ " LANGUAGE JAVA "
				+ " EXTERNAL NAME "
				+ "'Biblio.isOrcid'";
		stmt.executeUpdate(isOrcid);
		
		System.out.println("All Boolean Functions Created");
		
		
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Not all function created");
		}
		
	}
	/**
	 * drop functions
	 * @param stmt
	 */
	public static void dropFunctions(Statement stmt) {
		for (String function: functions) {
			try {
				stmt.executeUpdate("Drop function "+function);
			}
			catch(SQLException ex) {
				System.out.printf("Function %s not yet created\n",function);
			}
		}
		System.out.println("All Functions Dropped");
	}
	/**
	 * drop triggers
	 * @param stmt
	 */
	public static void dropTriggers(Statement stmt) {
		for (String trigger: triggers) {
			try {
				stmt.executeUpdate("Drop Trigger "+trigger);
				
			}
			catch(SQLException ex) {
				//System.out.println("No Trigger Dropped");
			}
		}
		System.out.println("All Triggers Dropped");

	}
	/**
	 * drop tables
	 * @param stmt
	 * @param dbTables
	 */
    public static void dropTables(Statement stmt, String dbTables[]) {
    	//with primary & foreign keys, need to reverse deleting orders
    	
    	for(String table:dbTables) {
            try {
            	stmt.executeUpdate("drop table "+table);
            } catch (SQLException ex) {
        		//System.out.println("Did not drop table "+table);
            }  	
        }
    	
    	System.out.println("All Tables Dropped");
    }
    /**
     * create tables
     * @param stmt
     */
    public static void createTables(Statement stmt, String [] dbTables) {
    	try {
    		// create the Publisher table
            String createTable_Publisher =
            		  "create table Publisher ("
            		+ "  Name varchar(32) not null,"
            		+ "  City varchar(16) not null,"
            		+ "  primary key (Name)"
            		+ ")";
            stmt.executeUpdate(createTable_Publisher);
       
            
            // create the Journal table
            // note approximate checks since ISSN is 32-bit unsigned: 
            //    ISSN between 0x00000001 and 0x7999999a as positive range
            //    ISSN between 0x80000000 and 0x9999999a as negative range
            String createTable_Journal =
            		  "create table Journal ("
            		+ "  Title varchar(32) not null,"
            		+ "  ISSN int not null,"
            		+ "  PublishedBy varchar(32) not null,"
            		+ "  primary key (ISSN),"
            		+ "  foreign key (PublishedBy) references Publisher (Name) on delete cascade,"
            		+ "	 check(isIssn(ISSN)),"
            		+ "  check ((ISSN between " + 0x00000001 + " and " + 0x7999999A + ")"  // positive range
            	    + "         or (ISSN between " + 0x9999999A + " and " + 0x80000000 + "))" // negative range
            		+ ")";
            stmt.executeUpdate(createTable_Journal);
            
            
            // create the Article table
            // note approximate check: 
            //    DOIs begin with '10.'
            String createTable_Article =
            		  "create table Article("
            		+ "  Title varchar(32) not null,"
            		+ "  DOI varchar(64) not null,"
            		+ "  PublishedIn int not null,"
            		+ "  primary key (DOI),"
            		+ "  foreign key (PublishedIn) references Journal (ISSN) on delete cascade,"
            		+ "  check (isDOI(DOI)),"
            		+ "  check (DOI like '10.%')"
            		+ ")";
            stmt.executeUpdate(createTable_Article);
           
            
            // create the Author entity table
            // note check
            //    ORCID between 0000000000000001 and 9999999999999999
            String createTable_Author =
            		  "create table Author("
            		+ "  FamilyName varchar(16) not null,"
            		+ "  GivenName varchar(16) not null,"
            		+ "  ORCID bigint not null,"
            		+ "  primary key (ORCID),"
            		+ "  check (isORCID(ORCID)),"
            		+ "  check (ORCID between 0000000000000001 and 9999999999999999)"
            		+ ")";
            stmt.executeUpdate(createTable_Author);

            // create the WrittenBy relation table
            String createTable_WrittenBy =
	        		  "create table WrittenBy("
	        		+ "  ArticleDOI varchar(64),"
	        		+ "  AuthorORCID bigint,"
	        		+ "  primary key (ArticleDOI, AuthorORCID),"
            		+ "  foreign key (ArticleDOI) references Article (DOI) on delete cascade,"
            		+ "  foreign key (AuthorORCID) references Author (ORCID) on delete cascade"
	        		+ ")";
            stmt.executeUpdate(createTable_WrittenBy);
            
            
    	}
    	catch(SQLException e) {
    		//if table already existed, re-run dropTables
    		e.printStackTrace();
    		
 
    	}
    	System.out.println("All Tables Created");
    }
    

   
    /**
     * Implement Triggers
     * @param stmt
     */
    public static void createTriggers(Statement stmt) {
    	try {
    		//trigger for writtenby Table
        	String create_trigger_writtenBy = 
        			"create Trigger trigger_writtenBy"
        			+ " After Delete On WrittenBy "
        			+ " For Each Statement"
        			+ " Delete From Author where ORCID Not In"
        			+ " (Select authorORCID From WrittenBy) ";
			stmt.execute(create_trigger_writtenBy);
			
			//trigger for Journal, Article updated from Cascade
        	String create_trigger_Journal = 
        			"create Trigger trigger_Journal"
        			+ " After Delete On Article "
        			+ " For Each Statement"
        			+ " Delete From Journal where ISSN Not In"
        			+ " (Select PublishedIn From Article) ";
			stmt.execute(create_trigger_Journal);
			
			System.out.println("All Triggers Are Created");
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
