
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
	static String [] dependentTables = {"Endorsement","review","Attendance"};
	static String [] independentTables= {"Customer", "Movie","Endorsement"};
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
	        dropTables(stmt, dependentTables);
			dropTables(stmt, independentTables);
			
			
			//build database
			
			//store_utilityFunctions(stmt);
			//store_booleanFunctions(stmt);
			createTables(stmt);
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
    public static void createTables(Statement stmt) {
    	try {
    		
    		//The information is entered by the theater when the customer registers.
    	    //If a customer is deleted, all of his or her reviews and endorsements are deleted
           String createTable_Customer =
            		  "create table Customer ("
            		+ "  CustomerID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 5),"
            		+ "  Name varchar(32) not null,"
            		+ "  Email varchar(64) not null,"
            		+ "  JoinedDate date not null,"
            		+ "  primary key (CustomerID)"
            		+ ")";
            stmt.executeUpdate(createTable_Customer);
            System.out.println("Customer Table Created");
           
            //This information is entered by the theater for each movie it plays.
           String createTable_Movie =
          		  "create table Movie ("
          		+ "  Title varchar(32) not null,"
          		+ "  MovieID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1),"
          		+ "  primary key (MoivieID)"
          		+ ")";
          stmt.executeUpdate(createTable_Movie);
          System.out.println("Movie Table Created");
          
          //This info is a record of a movie seen by a customer on a given date.
          //If a movie is deleted, all of its attendances are deleted. Attendance info is used to verify attendance when creating a review.
          String createTable_Attendance =
          		  "create table Attendance ("
          		+ "  MovieID int not null,"
          		+ "  CustomerID int not null,"
          		+ "  AttendanceDATE date not null,"
          		+ "  primary key (MovieID, CustomerID, AttendanceDATE),"
          		+ "  foreign key (MovieID) references movie(MovieID) on delete cascade,"
          		+ "  foreign key (CustomerID) references customer(CustomerID) on delete cascade"
          		+ ")";
          stmt.executeUpdate(createTable_Attendance);
          System.out.println("Attendance Table Created");
          
          //This is a review of a particular movie attended by a customer within the last week.
          //There can only be one movie review per customer, and the date of the review must be within 7 days of the most recent attendance of the movie.
          //If a movie is deleted, all of its reviews are also delete
          String createTable_Review =
          		  "create table Review ("
          		+ "  MovieID int not null,"
          		+ "  CustomerID int not null,"
          		+ "  Rating int not null,"
          		+ "  ReviewDate date not null,"
          		+ "  Review varchar(1000) not null,"
          		+ "  ReviewID int not null GENERATED ALWAYS AS IDENTITY (START WITH 100, INCREMENT BY 1),"
          		+ "  ReviewCount int default 0,"
          		+ "  primary key (ReviewID),"
          		+ "  check(Rating between 0 and 5),"
          		+ "  constraint foreign key(MovieID, CustomerID) references Attendance(MovieID, CustomerID) on delete cascade,"
          		+ "  check(ReviewCount < 1)"
          		+ "  check(datediff(day, ReviewDate, select AttendanceDATE from Review, Attendance"
          		+ " where(Review.MovieID = Attendance.MovieID and Review.CustomerID = Attendance.CustomerID)) < 7)"
          		+ "  )";
          stmt.executeUpdate(createTable_Review);
          System.out.println("Review Table Created");
          
          //This is an endorsement of a movie review by a customer.
          //A customer's current endorsement of a review for a movie must be at least one day after the customer's endorsement of a review for the same movie. 
          //If a review is deleted, all endorsements are also deleted.
          String createTable_Endorsement =
          		  "create table Endorsement ("
        		+ "  ReviewID int,"
          		+ "  CustomerID int,"
        		+ "  CurrentEndorsementDate date not null,"
          		+ "  LastEndorsementDate date not null default CurrentEndorsementDate,"
          		+ "  primary key (ReviewID , CustomerID),"
        		+ "  foreign key (ReviewID) references Review (ReviewID) on delete cascade,"
          		+ "  foreign key (CustomerID) references Customer (CustomerID) on delete cascade,"
          		+ "  check(CustomerID != (select Review.CustomerID from Endorsement, Review where (Endorsement.ReviewID = Review.ReviewID))),"
          		+ "  check(datediff(day, LastEndorsementDate, CurrentEndorsementDate) > 1),"
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
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}