

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;


public class DBFunctions {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	static ResultSet rs= null;

	static public boolean checkReviewTableDates (int cid, int mid, java.sql.Date reviewDate) {
		String query = "select attendanceDate from Attendance where customerid = "+cid+" and movieId = "+ mid+" order by attendancedate desc";
		boolean valid = false;
		try {
			Connection conn = Connect.getConnection();
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				java.sql.Date attendanceDate = rs.getDate("attendancedate");
				valid = validReviewDate(attendanceDate,reviewDate);
			}
			else {
				System.err.println("Hello, "+cid+" please watch the movie first");
				valid = false;
			}
			conn.close();
			stmt.close();
			
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			System.err.println("Hello, "+cid+"please watch the movie first");
		}
		
		return valid;
	}
	static public boolean checkEndorsementTable(int rid, int cid, java.sql.Date endorsedate) {
		
		boolean valid = false;
		try {
			Connection conn = Connect.getConnection();
			Statement stmt = conn.createStatement();
			String query = "select customerID, movieID, reviewDate from Review where reviewID = " + rid;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int cid1 = rs.getInt("customerID");
				int mid = rs.getInt("movieID");
				Date revdate = rs.getDate("reviewDate");
				if(!endorsesomeone(cid,cid1)) return false;
				if(!endorsementOpen(revdate, endorsedate)) return false;
				
				Statement stmt1 = conn.createStatement();
				query = "select endorsementDate from Review join Endorsement on Review.movieID = " + mid + " order by endorsementDate DESC";
				ResultSet res1 = stmt1.executeQuery(query);
				Date lastEndorseDate = null;
				if(res1.next()) {
					lastEndorseDate = res1.getDate(1);
					if(endorsementDateRule(lastEndorseDate,endorsedate)) valid = true;
					else valid = false;
				}
				else valid = true; // this means that this is first time endorsing a review 
				
			}
			conn.close();
			stmt.close();
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			System.err.printf("review_id: %s does not exist \n", rid);
		}
		return valid;
	}
	/**
	 * cannot endorse 3 days after review date
	 * @param reviewDate
	 * @param endorseDate
	 * @return
	 */
	static boolean endorsementOpen (Date reviewDate, Date endorseDate) {
		if(reviewDate == null) {System.err.println("no review in this date to check"); return false;}
		//convert time diff to day
		long diff = endorseDate.getTime()-reviewDate.getTime();
		long diffday = diff / (24 * 60 * 60 * 1000);
		
		if(diffday>3) {
			System.err.printf("reviewdate: %s, endorseDate: %s \n", reviewDate ,endorseDate);
			System.err.println("Sorry. Cannot endorse a review 3 days after that review was made");
			return false;
		}
		else return true;
		
	}
	/**
	 * check if this customer has reviewed this movie
	 * @param mid
	 * @param cid
	 * @return
	 */
	static public boolean checkReviewOnce(int mid, int cid) {
		
		String query = "select customerID, movieID from Review where movieID = " + mid + " and customerID = " + cid;
		boolean valid = false;
		try {
			Connection conn = Connect.getConnection();
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next()) {
				System.err.println("Sorry, we only let you review once");
			    valid = false;
			    }
			else valid =  true; // means first time reviewing;
			stmt.close();
			conn.close();
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			
		}
	
		return valid;
	}
	/**
	 * check if endorse review on same movie within 1 day
	 * @param date (today）
	 * @return
	 */
	static boolean endorsementDateRule (Date lastEndorseDate, Date endorseDate) {
		if(lastEndorseDate == null) return true;
		//convert time diff to day
		long diff = endorseDate.getTime()-lastEndorseDate.getTime();
		long diffday = diff / (24 * 60 * 60 * 1000);
		
		if(diffday>1) return true;
		else {
			System.err.println("you cannot endorse reviews on the same movie within 24 hours");
		}
		return false;
	}
	
	/**
	 * check if two customers are the same
	 * @param date (today）
	 * @return
	 */
	static boolean endorsesomeone (int endorse_cid, int cid) {
		if (endorse_cid==cid) {
			System.err.println("you cannot endorse yourself");
			return false;
		}
		return true;
	}
	/**
	 * within 7 days, return true; else return false
	 * @param date (attendance date)
	 * @return
	**/
	static boolean validReviewDate(Date lastAttendance, Date reviewDate) {
		long diff = reviewDate.getTime()-lastAttendance.getTime();
		long diffday = diff / (24 * 60 * 60 * 1000);
		if(diffday<=7) {
			return true;
		}
		else {
			System.err.println("sorry you cannot make a review because you watched this movie "+diffday+" days ago");
		}
		return false;
	}
}
