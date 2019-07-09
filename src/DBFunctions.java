import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

/**
 * This class is to implement all the stored functions for table constraints
 *
 */
public class DBFunctions {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static ResultSet rs= null;
	
	static public boolean checkValidEmail(String email) {
		   String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		   boolean valid = email.matches(regex);
		   if(email.isEmpty() || !valid) {
			   System.err.println(email+ " is an Invalid Email Format");
		   }
		   return valid;
		
	}
	/**
	 * Check if the review is allowed.
	 * @param cid the customer to review
	 * @param mid the movie for review
	 * @param reviewDate the date to review
	 * @return true if the review can be created, otherwise false
	 */
	static public boolean checkReviewTableDates (int cid, int mid, java.sql.Date reviewDate) {
		String query = "select attendanceDate from Attendance where customerid = " + cid + " and movieId = " + mid + " order by attendancedate desc";
		boolean valid = false;
		try {
			Connection conn = Connect.newConnection();
			//PreparedStatement stmt = conn.prepareStatement(query);
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				java.sql.Date attendanceDate = rs.getDate("attendancedate");
				valid = validReviewDate(attendanceDate, reviewDate);
			}
			else {
				System.err.println("Sorry, you cannot review, please register and watch the movie first.");
				valid = false;
			}
			conn.close();
			stmt.close();
			
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
		}	
		return valid;
	}
	
	/**
	 * One customer can only review a movie once
	 * @param mid the movie for review
	 * @param cid the customer to review
	 * @return true if the customer is the first time to review, otherwise false
	 */
	static public boolean checkReviewOnce(int mid, int cid) {	
		String query = "select customerID, movieID from Review where movieID = " + mid + " and customerID = " + cid;
		boolean valid = false;
		try {
			Connection conn = Connect.newConnection();
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next()) {
				System.err.println("Sorry, you can only review once for the same movie.");
			    valid = false;
			    }
			else valid =  true; 
			stmt.close();
			conn.close();
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {		
		}	
		return valid;
	}
	
	/**
	 * check the review date is within the 7 days of the most recent attendance of the movie
	 * @param lastAttendance the latest date for the attendance of the customer for the movie
	 * @param reviewDate the date to review
	 * @return true if the date is valid, otherwise false
	 */
	static boolean validReviewDate(Date lastAttendance, Date reviewDate) {
		long diff = reviewDate.getTime() - lastAttendance.getTime();
		long diffday = diff / (24 * 60 * 60 * 1000);
		if(diffday <= 7) {
			return true;
		}
		else {
			System.err.println("Sorry, you cannot make a review because you watched this movie after 7 days.");
			return false;
		}
	}
	
	/**
	 * Check if the endorsement is allowed.
	 * @param rid the review to endorse
	 * @param endorser_id the customer to endorse
	 * @param endorsedate the date to endorse
	 * @return
	 */
	static public boolean checkEndorsementTable(int rid, int endorser_id, java.sql.Date endorsedate) {		
		boolean valid = true;
		try {
			Connection conn = Connect.newConnection();
			Statement stmt = conn.createStatement();
			String query = "select customerID, movieID, reviewDate from Review where reviewID = " + rid;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int cid1 = rs.getInt("customerID");
				int mid = rs.getInt("movieID");
				Date revdate = rs.getDate("reviewDate");
				if(!endorsesomeone(endorser_id,cid1)) return false;
				if(!endorsementOpen(revdate, endorsedate)) return false;
				Statement stmt1 = conn.createStatement();
				query = "select review.reviewid from review where review.movieid = "+mid;
				
				//get all reviews of the THAT movie
				ResultSet res1 = stmt1.executeQuery(query);
				while(res1.next()) {
					//get all endorsement from this endorser if match any of given reviewid
					int other_rid = res1.getInt("ReviewId");
					query = "select endorsementdate from endorsement where reviewid = "+other_rid+" and customerid = "+endorser_id;
					ResultSet res2 = stmt1.executeQuery(query);
					if(res2.next()) {
						java.sql.Date lastEndorseDate = res2.getDate("endorsementDate");
						if(!endorsementDateRule(lastEndorseDate, endorsedate)) valid = false;
					}
					
				}			
			}
			conn.close();
			stmt.close();
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			
		}
		return valid;
	}
	
	/**
	 * Endorse is closed for reviews of the movie written three days ago.
	 * @param reviewDate the date review made
	 * @param endorseDate the date to endorse
	 * @return
	 */
	static boolean endorsementOpen (Date reviewDate, Date endorseDate) {
		if(reviewDate == null) {System.err.println("no review in this date to check"); return false;}
		long diff = endorseDate.getTime() - reviewDate.getTime();
		long diffday = diff / (24 * 60 * 60 * 1000);	
		if(diffday > 3) {
			System.err.println("Sorry, you cannot endorse a review 3 days after that review was made.");
			return false;
		}
		else return true;		
	}
	
	/**
	 * The endorsement of a review for a movie must be at least one day 
	 * after the customer's endorsement of a review for the same movie
	 * @param lastEndorseDate last latest endorse date for the same movie from the customer
	 * @param endorseDate the current endorse date
	 * @return
	 */
	static boolean endorsementDateRule (Date lastEndorseDate, Date endorseDate) {
		if(lastEndorseDate == null) return true;
		long diff = endorseDate.getTime() - lastEndorseDate.getTime();
		long diffday = diff / (24 * 60 * 60 * 1000);		
		if(diffday >= 1 || diffday < 0 ) return true;
		else {
			System.err.println("Sorry, you cannot endorse reviews on the same movie within 24 hours.");
		}
		return false;
	}
	
	/**
	 * A customer cannot endorse his or her own review.
	 * @param endorse_cid the customer to endorse
	 * @param cid the customer to review
	 * @return
	 */
	static boolean endorsesomeone (int endorse_cid, int cid) {
		if (endorse_cid == cid) {
			System.err.println("Sorry, you cannot endorse yourself.");
			return false;
		}
		return true;
	}
	
}
