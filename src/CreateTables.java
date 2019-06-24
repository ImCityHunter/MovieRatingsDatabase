import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * There class creates all the entity tables for Customer, Movie, Attendance, Review and Endorsement;
 * creates the stored functions for condition check as 
 * checkReviewTableDates, checkEndorsementTable, checkReviewOnce; 
 * and some table constraints for format check as valid_reviewDate, valid_reviewOnce and checkEndorsementTable.
 */

public class CreateTables {

	//The list of tables and stored functions
	static String [] functions = {"CHECKREVIEWTABLEDATES", "checkReviewOnce", "checkEndorsementTable"};
	static String [] independentTables= {"Customer", "Movie"};
	static String [] dependentTables = {"Attendance", "Review", "Endorsement"};
	

	/**
	 * Clear the database for dropping all functions, constraints and tables.
	 * And then create all tables and functions.
	 * @param conn
	 * @param stmt
	 */
	public static void create(Connection conn, Statement stmt) {
        	dropConstraints(stmt);
        	
        	// run drop tables twice, in case tables have complicated relations
	        dropFunctions(stmt);
	        dropTables(stmt, dependentTables);
			dropTables(stmt, independentTables);
			dropFunctions(stmt);
	        dropTables(stmt, dependentTables);
			dropTables(stmt, independentTables);
			
			// build tables
			store_functions(stmt);
			createTables(stmt);	
	}
	
	/**
	 * drop functions
	 * @param stmt
	 */
	private static void dropFunctions(Statement stmt) {
		for (String function: functions) {
			try {
				stmt.executeUpdate("Drop Function " + function);
			}
			catch(SQLException e) {
			}
		}

	}
	
	/**
	 * drop constraints
	 * @param stmt
	 */
	private static void dropConstraints(Statement stmt) {
		
		//drop each constraint separately instead of all in one try
		try {
			stmt.executeUpdate("alter table review drop constraint valid_reviewDate");
			
		} catch (SQLException e) {
			}
		try {
			stmt.executeUpdate("alter table review drop constraint valid_reviewOnce");
		} catch (SQLException e) {
			}
		try {
			stmt.executeUpdate("alter table endorsement drop constraint valid_endorsement");
		} catch (SQLException e) {
		}
	}
	
	
	/**
	 * drop tables
	 * @param stmt
	 * @param dbTables
	 */
	private static void dropTables(Statement stmt, String dbTables[]) {
    	for(String table:dbTables) {
            try {
            	stmt.executeUpdate("drop table "+table);
            } catch (SQLException e) {
            }  	
        }
    }
	
    /**
     * create tables
     * @param stmt
     */
    private static void createTables(Statement stmt) {
    	try {    		
    		//The information is entered by the theater when the customer registers.
           String createTable_Customer =
            		  "create table Customer ("
            		+ "  CustomerID int not null,"
            		+ "  Name varchar(32) not null,"
            		+ "  Email varchar(64) not null,"
            		+ "  JoinedDate date not null,"
            		+ "  primary key (CustomerID)"
            		+ ")";
            stmt.executeUpdate(createTable_Customer);
              
            //This information is entered by the theater for each movie it plays.
           String createTable_Movie =
          		  "create table Movie ("
          		+ "  Title varchar(32) not null,"
          		+ "  MovieID int not null,"
          		+ "  primary key (MovieID)"
          		+ ")";
          stmt.executeUpdate(createTable_Movie);
              
          //This info is a record of a movie seen by a customer on a given date.
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
              
          //This is a review of a particular movie attended by a customer within the last week.
          String createTable_Review =
          		  "create table Review ("
          		+ "  MovieID int not null,"
          		+ "  CustomerID int not null,"
          		+ "  Rating int not null,"
          		+ "  ReviewDate date not null,"
          		+ "  Review varchar(1000) not null,"
          		+ "  ReviewId int not null,"
          		+ "  CONSTRAINT valid_reviewDate check(checkReviewTableDates(CustomerID, MovieID, ReviewDate)),"
          		+ "  CONSTRAINT valid_reviewOnce check(checkReviewOnce(MovieID, CustomerID)),"
          		+ "  check(Rating between 0 and 5),"
          		+ "  primary key (ReviewID),"
          		+ "  foreign key (MovieID) references Movie(MovieID) on delete cascade,"
          		+ "  foreign key (CustomerID) references Customer(CustomerID) on delete cascade"
          		+ "  )";
          stmt.executeUpdate(createTable_Review);
          
          //This is an endorsement of a movie review by a customer.
          String createTable_Endorsement =
          		  "create table Endorsement ("
        		+ "  ReviewID int,"
          		+ "  CustomerID int,"
        		+ "  EndorsementDate DATE not null,"
          		+ "  CONSTRAINT valid_endorsement check(checkEndorsementTable(ReviewID, CustomerID, EndorsementDate)),"
          		+ "  primary key (ReviewID , CustomerID),"
        		+ "  foreign key (ReviewID) references Review (ReviewID) on delete cascade,"
          		+ "  foreign key (CustomerID) references Customer (CustomerID) on delete cascade"
          		+ " )";
          stmt.executeUpdate(createTable_Endorsement);
    	}catch(SQLException e){
    	}	
    	System.out.println("All Tables Created");
    }
    

   
    /**
     * Create stored functions
     * @param stmt
     */
    private static void store_functions (Statement stmt) {

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
    		
    		System.out.println("All Functions Created");
    		
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}