/**
 * This class contains functions for parsing, validating, 
 * and converting bibliographic types to strings.
 * 
 * @author philip Gust
 *
 */
public class Biblio {
	/**
	 * This function parses an ISSN string of the form nnnn-nnnnN into a
	 * hexadecimal-encoded form with 'a' for X in the last digit N. 
	 * 
	 * @param s the ISSN string
	 * @return the hexadecimal-encoded issn
	 * @throws NumberFormatException if the string is not an ISSN
	 */
	static public int parseIssn(String s) throws NumberFormatException {
		if (!s.matches("(\\d{4})-(\\d{3})([\\dxX])$")) {
			throw new NumberFormatException("Malformed ISSN");
		}
		String issnStr = s.substring(0,4) + s.substring(5,9).replace("X","a");
		int issn = Integer.parseUnsignedInt(issnStr, 16);
		if (issn == 0) { // issn 0 is invalid
			throw new NumberFormatException("Invalid ISSN");
		}
		return issn;
	}

	/**
	 * Determines whether issn is a valid ISSN
	 * 
	 * @param issn the issn
	 * @return true if issn is a valid ISSN
	 */
	static public boolean isIssn(int issn) {
		return (issn != 0) && issnToString(issn).matches("(\\d{4})-(\\d{3})([\\dX])$");
	}
	
	/**
	 * This function returns the string representation of an int
	 * representing the 8 hex digits of an ISSN.
	 * 
	 * @param issn the ISSN value
	 * @return string representation of an ISSN
	 */
	static public String issnToString(int issn) {
	 	return String.format("%04x-%04x", (issn >> 16) & 0xFFFF, issn & 0xFFFF).replace("a","X");
	}
	
	/**
	 * This function parses an ORCID string of the form nnnn-nnnn-nnnn-nnnn
	 * into a decimal encoded form.
	 * 
	 * @param s
	 * @return
	 * @throws NumberFormatException
	 */
	static public long parseOrcid(String s) throws NumberFormatException {
		if (!s.matches("(\\d{4})-(\\d{4})-(\\d{4})-(\\d{4})$")) {
			throw new NumberFormatException("Malformed ORCID");
		}
		String orcidStr =
			s.substring(0,4) + s.substring(5,9) + s.substring(10, 14)+ s.substring(15,19);
		long orcid = Long.parseUnsignedLong(orcidStr, 10);
		if (orcid == 0) {
			throw new NumberFormatException("Invalid ORCID");
		}
		return orcid;
	}
	
	/**
	 * Determines whether orcid is a valid ORCID.
	 * 
	 * @param orcid the orcid
	 * @return true if orcid is a valid ORCID
	 */
	static public boolean isOrcid(long orcid) {
		return (orcid != 0) && orcidToString(orcid).matches("(\\d{4})-(\\d{4})-(\\d{4})-(\\d{4})$");
	}

	/**
	 * This function returns the string representation of a long
	 * representing  the 16 decimal digits of an ORCID.
	 * 
	 * @param orcid the long representation of an ORCID
	 * @return a string representation of the ORCID
	 */
	static public String orcidToString(long orcid) {
		return String.format("%04d-%04d-%04d-%04d",
			(orcid/1000000000000L)%10000L, 
			(orcid/100000000L)%10000L,
			(orcid/10000L)%10000L, 
			orcid%10000L);
	}
	
	/**
	 * Determines whether a string conforms to the pattern for a DOI.
	 * Note: does not validate doi at dx.doi.org
	 * 
	 * @param doi the DOI string
	 * @return true if doi is a valid DOI
	 * @see https://stackoverflow.com/questions/27910/finding-a-doi-in-a-document-or-page
	 */
	static public boolean isDoi(String doi) {
		return doi.matches("\\b(10[.][0-9]{3,}(?:[.][0-9]+)*/(?:(?![\"&\\'])\\S)+)\\b");
	}
}