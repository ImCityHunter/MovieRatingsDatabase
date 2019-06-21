
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
		"CreateAttendance","CreateReview","CreateEndorsement"
	};
	static String [] functions = {
		"getLastReviewDate","validEndorsement","getlastAttendenceDate"
	};
	static String [] dependentTables = {"Endorsement","Review","Attendance","Endorsement"	};
	static String [] independentTables= {"Customer", "Movie"};
//	static String [] functions= {"isISSN","isDoi","isORCID","parseISSN","issnToString","orcidToString","parseOrcid"};
	public static void main(String[] args) {
	    // the default framework is embedded
		String protocol = "jdbc:derby:";
	    String dbName = "publication";
		String connStr = protocol + dbName+ ";create=true";
		String postGres = "jdbc:derby://localhost:1527/publication;create=true;user=user1;password=user1";
		String databaseURL = "jdbc:derby://localhost:1527/publication;create=true";
		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derby client frameworks
        props.put("user", "user1");
        props.put("password", "user1");
        
		try (
			
	        // connect to the database using URL
			//Connection conn = DriverManager.getConnection(postGres);
			Connection conn = DriverManager.getConnection(connStr, props);
				
	        // statement is channel for sending commands thru connection 
	        Statement stmt = conn.createStatement();
		){
	        System.out.println("Connected to and created database " + dbName);
            
			
			//clear database
	        dropFunctions(stmt);
	        dropTriggers(stmt);
	        dropTables(stmt, dependentTables);
			dropTables(stmt, independentTables);
			
			//build database
			//store_utilityFunctions(stmt);
			//store_booleanFunctions(stmt);
			createTables(stmt);
			//createTriggers(stmt);
			//store_functions(stmt);
		
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	 * drop functions
	 * @param stmt
	 */
	public static void dropFunctions(Statement stmt) {
		for (String function: functions) {
			try {
				stmt.executeUpdate("Drop function "+function);
				
			}
			catch(SQLException ex) {
				//System.out.println("No Trigger Dropped");
			}
		}
		System.out.println("All Triggers function");

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
            	//ex.printStackTrace();
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
    	    //If a customer is deleted, all of his or her reviews and endorsements are deleted.
           String createTable_Customer =
            		  "create table Customer ("
            		+ "  CustomerID int not null,"
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
          		+ "  MovieID int not null,"
          		//+ "  MovieID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1),"
          		+ "  primary key (MovieID)"
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
          		+ "  foreign key (MovieID) references Movie(MovieID) on delete cascade,"
          		+ "  foreign key (CustomerID) references Customer(CustomerID) on delete cascade"
          		+ ")";
          stmt.executeUpdate(createTable_Attendance);
          System.out.println("Attendance Table Created");
          
          //This is a review of a particular movie attended by a customer within the last week.
          //There can only be one movie review per customer; If a movie is deleted, all of its reviews are also delete
          String createTable_Review =
          		  "create table Review ("
          		+ "  MovieID int not null,"
          		+ "  CustomerID int not null,"
          		+ "  Rating int not null,"
          		+ "  ReviewDate date not null,"
          		+ "  Review varchar(1000) not null,"
          		//+ "  ReviewID int not null GENERATED ALWAYS AS IDENTITY (START WITH 100, INCREMENT BY 1),"
          		+ "  ReviewId int not null,"
          		+ "  primary key (ReviewID),"
          		+ "  check(Rating between 0 and 5),"
          		+ "  foreign key (MovieID) references Movie(MovieID) on delete cascade,"
          		+ "  foreign key (CustomerID) references Customer(CustomerID) on delete cascade"
          		+ "  )";
          stmt.executeUpdate(createTable_Review);
          System.out.println("Review Table Created");
          
          //This is an endorsement of a movie review by a customer.
          //and this customerid cannot be the same as the one if the reviewTable
          //If a review is deleted, all endorsements are also deleted.
          String createTable_Endorsement =
          		  "create table Endorsement ("
        		+ "  ReviewID int,"
          		+ "  EndorseCustomerID int,"
        		+ "  EndorsementDate DATE not null,"
          		+ "  primary key (ReviewID , EndorseCustomerID),"
        		+ "  foreign key (ReviewID) references Review (ReviewID) on delete cascade,"
          		+ "  foreign key (EndorseCustomerID) references Customer (CustomerID) on delete cascade"
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
    		//trigger for Review table
    		//The date of the review must be within 7 days of the most recent attendance of the movie.
        	String CreateTrigger_CreateReview = 
        			"create trigger CreateReview"
        			+ " after insert on Review"
        			+ " referencing new as new "
        			+ " for each statement"
        			+ " delete from review where ReviewDate < "
        			+ " (select AttendanceDATE from Attendance "
        			+ " where movieid = newid and CustomerID = new.CustomerID)";
//        			+ " for each statement"
//        			+ " insert from ";
//        			+ " for each row"
//        			+ " begin "
//        			+ " if(datediff(day, new.ReviewDate, (select AttendanceDATE from Attendance" 
//        			+ " where(MovieID = new.MovieID and CustomerID = Review.CustomerID)) > 7) then"
//        			+ " signal sqlstate 'ERROR' set message_text = 'The review can't be added.';"
//        			+ " end if;"
//        			+ " end;";
			stmt.execute(CreateTrigger_CreateReview);
		
			//trigger for Endorsement table
	        //A customer's current endorsement of a review for a movie must be at least one day after the customer's endorsement of a review for the same movie. 
			//A customer cannot endorse his or her own review. 
        	String CreateTrigger_CreateEndorsement = 
        			"create trigger CreateEndorsement"
        			+ " before insert on Endorsement"
        			+ " for each row"
        			+ " begin"
        			+ " if(new.CustomerID = (select CustomerID from Review where ReviewID = new.ReviewID)) then"
        			+ " signal sqlstate 'ERROR' set message_text = 'The customer cannot endorse his or her own review.';"
        			+ " end if;"
        			+ " if(new.CustomerID in (select CustomerID from Endorsement) &&"
        			+ " (select MovieID from Review where ReviewID = new.ReviewID) in (select MovieID from Endorsement, Review where Endorsement.ReviewID = Review.ReviewID) &&"
        			+ " timestampdiff(HOUR, new.EndorsementDate, LastEndorsementDate) < 1"
        			+ " signal sqlstate 'ERROR' set message_text = 'The endorsement for the same movie can't be added within one day.';"
        			+ " end if;"
        			+ " end;";
			//stmt.execute(CreateTrigger_CreateEndorsement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void store_functions (Statement stmt) {
    	try {
    		
    		//use for endorsement
//    		String getLastReviewDate =
//    				"CREATE FUNCTION getLastReviewDate(rID int)"
//    				+ " RETURNS DATE "
//    				+ " PARAMETER SQL "
//    				+ " LANGUAGE SQL "
//    				+ " BEGIN "
//    				+ "    DECLARE RDATE DATE "
//    				+ "    set RDATE = ("
//    				+ "      SELECT EndorsementDate FROM Endorsement "
//    				+ "      WHERE REVIEWID=RID "
//    				//+ "      ORDER BY EndorsementDate DES"
//    				+ "    )"
//    				+ " RETURN RDATE"
//    				+ " End ";
//    		stmt.executeUpdate(getLastReviewDate);
    		
    		String getlastAttendenceDate=
    				"CREATE FUNCTION getLastAttendance (CID INT)"
    				+" RETURNS int"
//    				+" PARAMETER STYLE JAVA "
//    				+" LANGUAGE JAVA "
//    				+" CONTAIN SQL"
					+" AS "
    				+" BEGIN "
    				+"    DECLARE LDATE INT; "
    				+"    SET LDATE=1;"
//    				+"    SELECT AttendanceDATE FROM ATTENDANCE "
//    				+"    WHERE MOVIEID=MID AND CUSTOMERID=CID"
//    				+"        ORDER BY AttendanceDATE DES)"
    				+"    RETURN 1; "
    				+" END ";
    		//stmt.executeUpdate(getlastAttendenceDate);
    		
    		String example=
    				"CREATE FUNCTION calcProfit(cost FLOAT, price FLOAT) RETURNS DECIMAL(9,2)"
    				+ " Externe"
    				+ " BEGIN "
    		 		+ " DECLARE profit DECIMAL(9,2);"
    		 		+ " IF price > cost THEN " 
    		 		+ "  SET profit = price - cost;" 
    				+ "  ELSE SET profit = 0; END IF;" 
    				+ " RETURN profit" 
    				+ " END";
    		//stmt.executeUpdate(example);
    		
    		
    		String validEndorsement =
    				"CREATE FUNCTION validEndorsement (rDate date, endorsementDate Date)"
    				+ " RETURNS BOOLEAN "
    				+ " PARAMETER STYLE JAVA "
    				+ " LANGUAGE JAVA "
    				+ " NO SQL "
    				+ " EXTERNAL NAME "
    				+ "'Functions.validEndorsement'";
    		stmt.executeUpdate(validEndorsement);
    		
    		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
