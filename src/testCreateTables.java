import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is to create the database and insert all the default valid information from default files 
 * as information stored in the database already
 *
 */
public class testCreateTables {
	static String [] dependentTables = {"Endorsement","review","Attendance"};
	static String [] independentTables= {"Customer", "Movie"};
	static java.sql.Date defaultDate = UIFunctions.convertToDate("2019-07-01");
	
	public static void main(String[] args) {
		try {
			Connection conn = Connect.newConnection();
			Statement stmt = conn.createStatement();
			CreateTables.create(conn, stmt);
			
			System.out.println("Inserting Default Data ...");
			setDefaultData.defaultData(conn, stmt);
			System.out.println("Print All Default Data ...");
			UIFunctions.printAllTable(conn);
			
			String print = "Testing if all tables can detect error inputs";
			System.out.println("\n"+print.toUpperCase()+"\n");
			//ensure all tables can correctly detect error input
			testCustomer(conn,stmt);
			testReviewTable(conn,stmt);
			testEndorsement(conn,stmt);
			conn.close();
					
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
	
	public static void testEndorsement(Connection conn, Statement stmt) {
		int rid = 22231;
		int cid = 12347;
		defaultDate = UIFunctions.convertToDate("2019-06-22");
		System.out.println("Test Function: checkEndorsementTable in Endorsement Table");
		try {
			System.out.println("Test if endorsing yourself");
			System.out.printf("Inserting { %s, %s, %s} into table review\n",rid,cid,defaultDate);
			PreparedStatement insertRow_Endorsement = conn.prepareStatement(
					"insert into Endorsement (reviewID, CustomerId, endorsementDATE)values(?, ?, ?)");
			insertRow_Endorsement.setInt(1, rid);
			insertRow_Endorsement.setInt(2, cid);
			insertRow_Endorsement.setDate(3, defaultDate);
			insertRow_Endorsement.execute();
					
		} catch (SQLException e) {
			System.out.println("One insertion fail in Endorsement\n");
		}
		
		try {
			cid = 12345;
			defaultDate = UIFunctions.convertToDate("2019-06-25");
			System.out.println("Test if endorsement date within 3 days of review");
			System.out.printf("Inserting { %s, %s, %s} into table review\n",rid,cid,defaultDate);
			PreparedStatement insertRow_Endorsement = conn.prepareStatement(
					"insert into Endorsement (reviewID, CustomerId, endorsementDATE)values(?, ?, ?)");
			insertRow_Endorsement.setInt(1, rid);
			insertRow_Endorsement.setInt(2, cid);
			insertRow_Endorsement.setDate(3, defaultDate);
			insertRow_Endorsement.execute();
					
		} catch (SQLException e) {
			System.out.println("One insertion fail in Endorsement\n");
		}
		
		try {
			rid = 22227;
			cid = 12348;
			defaultDate = UIFunctions.convertToDate("2019-06-23");
	
			System.out.println("Test if making endorsment of a review of a movie is a day apart");
			System.out.printf("Inserting { %s, %s, %s} into table review\n",rid,cid,defaultDate);
			PreparedStatement insertRow_Endorsement = conn.prepareStatement(
					"insert into Endorsement (reviewID, CustomerId, endorsementDATE)values(?, ?, ?)");
			insertRow_Endorsement.setInt(1, rid);
			insertRow_Endorsement.setInt(2, cid);
			insertRow_Endorsement.setDate(3, defaultDate);
			insertRow_Endorsement.execute();
					
		} catch (SQLException e) {
			System.out.println("One insertion fail in Endorsement\n");
		}
	}
	
	/**
	 * Test Customer Table Functions
	 * @param conn
	 * @param stmt
	 */
	public static void testCustomer(Connection conn, Statement stmt) {
		PreparedStatement insertRow_Customer;
		String name = "Delaney";
		String email= "Delaney@";
		System.out.println("Test Function: checkValidEmail in Customer Table:");
		try {
			insertRow_Customer = conn.prepareStatement(
					"Insert into Customer (CustomerID, Name, Email, joinedDate)values(?, ?, ?, ?)");
	        System.out.printf("Inserting { %s, %s, %s } into table customer\n",name,email,defaultDate);
			insertRow_Customer.setInt(1, UIFunctions.generateId(conn, "Customer"));
	        insertRow_Customer.setString(2, name);
	        insertRow_Customer.setString(3, email);
	        insertRow_Customer.setDate(4, defaultDate);
	        insertRow_Customer.execute();
			
		} catch (SQLException e) {
			System.out.println("One insertion fail in Customer\n");
		}
		
	}

	/**
	 * Test Review Table Functions
	 * @param conn
	 * @param stmt
	 */
	public static void testReviewTable(Connection conn, Statement stmt) {
		int mid = 66666;
		int cid = 12349;
		int rating = 5;
		String review="Testing";
		System.out.println("\nTest Function: checkReviewTableDates in Review Table:");
		
		try {
			System.out.println("Test if Watched a movie within 7 days");
			PreparedStatement insertRow_Review = conn.prepareStatement(
					"insert into Review(MovieId, CustomerId, rating, reviewDate, Review, reviewId)values(?, ?, ?, ?, ?, ?)");		
			System.out.printf("Inserting { %s, %s, %s, %s, %s} into table review\n",mid,cid,rating, defaultDate, review);
			insertRow_Review.setInt(1, mid);
			insertRow_Review.setInt(2, cid);
			insertRow_Review.setInt(3, rating);
			insertRow_Review.setDate(4, defaultDate);
			insertRow_Review.setString(5, review);
			insertRow_Review.setInt(6, UIFunctions.generateId(conn, "Review"));
			insertRow_Review.execute();
			
		} catch (SQLException e) {
			System.out.println("One insertion fail in Customer\n");
		}
		
		try {
			mid = 99999;
			cid = 12346;
			System.out.println("Test if watched a movie already");
			PreparedStatement insertRow_Review = conn.prepareStatement(
					"insert into Review(MovieId, CustomerId, rating, reviewDate, Review, reviewId)values(?, ?, ?, ?, ?, ?)");		
			System.out.printf("Inserting { %s, %s, %s, %s, %s} into table review\n",mid,cid,rating, defaultDate, review);
			insertRow_Review.setInt(1, mid);
			insertRow_Review.setInt(2, cid);
			insertRow_Review.setInt(3, rating);
			insertRow_Review.setDate(4, defaultDate);
			insertRow_Review.setString(5, review);
			insertRow_Review.setInt(6, UIFunctions.generateId(conn, "Review"));
			insertRow_Review.execute();
			
		} catch (SQLException e) {
			System.out.println("One insertion fail in Customer\n");
		}
		
		try {
			mid = 44444;
			cid = 12345;
			defaultDate = UIFunctions.convertToDate("2019-06-21");
			System.out.println("Test if already made an review");
			PreparedStatement insertRow_Review = conn.prepareStatement(
					"insert into Review(MovieId, CustomerId, rating, reviewDate, Review, reviewId)values(?, ?, ?, ?, ?, ?)");	
			System.out.printf("Inserting { %s, %s, %s, %s, %s} into table review\n",mid,cid,rating, defaultDate, review);
			insertRow_Review.setInt(1, mid);
			insertRow_Review.setInt(2, cid);
			insertRow_Review.setInt(3, rating);
			insertRow_Review.setDate(4, defaultDate);
			insertRow_Review.setString(5, review);
			insertRow_Review.setInt(6, UIFunctions.generateId(conn, "Review"));
			insertRow_Review.execute();
			
		} catch (SQLException e) {
			System.out.println("One insertion fail in Customer\n");
		}
		
	}
	
	
	

}


