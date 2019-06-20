import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Functions {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static String defaultDate = "2019-06-30";
	static String template="2019-06-20";
	
	/**
	 * must be at least after 1 day of the review date
	 * @param date (today）
	 * @return
	 */
	static boolean validEndorsement (Date reviewDate, Date today) {
		boolean valid = false;
		//Date today = (Date) sdf.parse(defaultDate);
		if(reviewDate==null) return false;
		if(today.compareTo(reviewDate)>=1) valid= true;
		return valid;
	}
	/**
	 * within 7 days, return true; else return false
	 * @param date (attendance date)
	 * @return
	 */
	static boolean validReviewDate(Date lastAttendance, Date reviewDate) {
		boolean valid = false;
		if(lastAttendance.compareTo(reviewDate)<=7) valid= true;
		return valid;
	}
	static Date lastReviewDate(int CustomerID, int ReviewID) {
		
		
		return null;
	}
	static Date lastAttendanceDate(int CID, int MID) {
		String select="select attendancedate from attendance where customerid ="+CID+" and movieid = "+MID;
		return null;
	}
}
