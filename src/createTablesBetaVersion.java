


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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

public class createTablesBetaVersion {

	//All the constraints
	static String [] triggers = {
		"CreateAttendance","CreateReview","CreateEndorsement"
	};
	static String [] functions = {"checkReviewTableDates","checkEndorsementTable","checkReviewOnce"};
	static String [] dependentTables = {"Endorsement","Review","Attendance"};
	static String [] independentTables= {"Customer", "Movie"};
//	static String [] functions= {"isISSN","isDoi","isORCID","parseISSN","issnToString","orcidToString","parseOrcid"};


	
	public static void main(String[] args) throws SQLException {
			
			try {
				Connection conn = Connect.getConnection();
				Statement stmt = conn.createStatement();
				//clear database
		        dropFunctions(stmt);
		        dropConstraints(stmt);
		        dropTriggers(stmt);
		        dropTables(stmt, dependentTables);
				dropTables(stmt, independentTables);

				store_functions(stmt);
				createTables(stmt);
				
				
				stmt.close();
				conn.close();

			} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		


    }
	public static void dropConstraints(Statement stmt) {
		try {
			stmt.execute("alter table review drop constraint valid_reviewDate");
			stmt.execute("alter table review drop constraint valid_reviewOnce");
			stmt.execute("alter table endorsement drop constraint checkEndorsementTable");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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
				stmt.executeUpdate("Drop Function "+function);
				System.out.println(function+" is dropped ");
				
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
          		+ "  CONSTRAINT valid_reviewDate check(checkReviewTableDates(CustomerID,MovieID,ReviewDate)),"
          		+ "  CONSTRAINT valid_reviewOnce check(checkReviewOnce(MovieID, CustomerID)),"
          		+ "  check(Rating between 0 and 5),"
          		+ "  primary key (ReviewID),"
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
          		+ "  CustomerID int,"
        		+ "  EndorsementDate DATE not null,"
          		+ "  CONSTRAINT valid_endorsement check(checkEndorsementTable(ReviewID,CustomerID,EndorsementDate)),"
          		+ "  primary key (ReviewID , CustomerID),"
        		+ "  foreign key (ReviewID) references Review (ReviewID) on delete cascade,"
          		+ "  foreign key (CustomerID) references Customer (CustomerID) on delete cascade"
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
     * Create Functions
     * @param stmt
     */
    public static void store_functions (Statement stmt) {
    	try {
    		String checkReviewTableDates =
    				"CREATE FUNCTION checkReviewTableDates(cid int, mid int, reviewDate date)"
    				+ " RETURNS BOOLEAN "
    				+ " PARAMETER STYLE JAVA "
    				+ " LANGUAGE JAVA "
    				+ " NO SQL "
    				+ " EXTERNAL NAME "
    				+ "'DBFunctions.checkReviewTableDates'";
    		stmt.executeUpdate(checkReviewTableDates);
    		//System.out.println("forReviewTable function success");
    		
    		String checkEndorsementTable =
    				"CREATE FUNCTION checkEndorsementTable(rid int, cid int, EndorsementDate date)"
    				+ " RETURNS BOOLEAN "
    				+ " PARAMETER STYLE JAVA "
    				+ " LANGUAGE JAVA "
    				+ " NO SQL "
    				+ " EXTERNAL NAME "
    				+ "'DBFunctions.checkEndorsementTable'";
    		stmt.executeUpdate(checkEndorsementTable);
    		
    		String checkReviewOnce =
    				"CREATE FUNCTION checkReviewOnce(mid int, cid int)"
    				+ " RETURNS BOOLEAN "
    				+ " PARAMETER STYLE JAVA "
    				+ " LANGUAGE JAVA "
    				+ " NO SQL "
    				+ " EXTERNAL NAME "
    				+ "'DBFunctions.checkReviewOnce'";
    		stmt.executeUpdate(checkReviewOnce);
    		
    		
		} catch (SQLException e) {
			System.err.println("caustion when creating functions");
			//e.printStackTrace();
		}
    }
}
