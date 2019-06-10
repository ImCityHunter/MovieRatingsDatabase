

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
	static String [] relationTables = {"Endorsement","review","Attendance"};
	static String [] dbTables= {"Customer", "Movie","Endorsement"};
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
	        //dropTriggers(stmt);
	        dropTables(stmt, relationTables);
			dropTables(stmt, dbTables);
			dropTables(stmt, relationTables);
			dropTables(stmt, dbTables);
			
			
			//build database
			
			//store_utilityFunctions(stmt);
			//store_booleanFunctions(stmt);
			createTables(stmt,dbTables);
			//createTriggers(stmt);
		
	        
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
    		
    		
    	//The information is entered by the theater when the customer registers.
    	//If a customer is deleted, all of his or her reviews and endorsements are deleted
           String createTable_Customer =
            		  "create table Customer ("
            		+ "  Name varchar(32) not null,"
            		+ "  Email varchar(64) not null,"
            		+ "  CustomerID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1),"
            		+ "  joinedDate timestamp not null,"
            		+ "  primary key (CustomerID)"
            		+ ")";
            stmt.executeUpdate(createTable_Customer);
            System.out.println("Customer Table Created");
           
            //This information is entered by t he theater for each movie it plays.
           String createTable_Movie =
          		  "create table Movie ("
          		+ "  Title varchar(32) not null,"
          		+ "  movieID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1),"
          		+ "  primary key (movieid)"
          		+ ")";
          stmt.executeUpdate(createTable_Movie);
          System.out.println("Movie Table Created");
          
          //If a movie is deleted, all of its attendances are deleted.
          //Attendance info is used to verify attendance when creating a review.
          String createTable_Attendance =
          		  "create table Attendance ("
          		+ "  movie_id int not null,"
          		+ "  customer_id int not null,"
          		+ "  primary key (movie_id,customer_id),"
          		+ "  foreign key (movie_id) references movie (movieID) on delete cascade,"
          		+ "  foreign key (customer_id) references customer (customerID) on delete cascade,"
          		+ "  attendanceDATE timestamp not null"
          		+ ")";
          stmt.executeUpdate(createTable_Attendance);
          System.out.println("Attendance Table Created");
          
          //There can only be one movie review per customer, 
          //and the date of the review must be within 7 days of the most recent attendance of the movie.
          //If a movie is deleted, all of its reviews are also delete
          String createTable_review =
          		  "create table review ("
          		+ "  movie_id int not null,"
          		+ "  customer_id int not null,"
          		+ "  rating int not null,"
          		+ "  review varchar(1000) not null,"
          		+ "  reviewid int not null GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1),"
          		+ "  primary key (reviewid),"
          		+ "  check(rating between 1 and 5),"
          		+ "  foreign key (movie_id) references movie(movieID) on delete cascade,"
          		+ "  foreign key (customer_id) references customer(customerID) on delete cascade,"
          		+ "  reviewdate timestamp not null"
          		+ "  )";
          stmt.executeUpdate(createTable_review);
          System.out.println("review Table Created");
          
          
          //A customer's current endorsement of a review for a movie must be at least one day after the customer's endorsement of a review for the same movie. 
          //The endorsement includes the review ID, the customerID of the endorser, 
          //and the endoresemnt date. A customer cannot endorse his or her own review.
          //If a review is deleted, all endorsements are also deleted.
          String createTable_Endorsement =
          		  "create table Endorsement ("
        		+ " review_id int,"
          		+ " customer_id int,"
        		+ " endorsementdate timestamp,"
          		+ " primary key (review_id , customer_id),"
        		+ " foreign key (review_id) references review (reviewid) on delete cascade,"
          		+ " foreign key (customer_id) references customer (customerid) on delete cascade"
          		+ " )";
          stmt.executeUpdate(createTable_Endorsement);
          System.out.println("Endorsement Table Created");
          
          
            
            
            
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
