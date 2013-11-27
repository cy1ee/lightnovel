package tw.com.lightnovel.apis;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.security.auth.PrivateCredentialPermission;

import android.R.integer;

public class Utility {
	private static final String placeHolder = "0123456789abcdef";
	
	public static String sha1Hash(String s) {
		StringBuffer sb = new StringBuffer();
		
		try {
			MessageDigest md = MessageDigest.getInstance("sha-1");
			byte[] hashBytes = md.digest(s.getBytes());
			
			for (byte b : hashBytes) {
				sb.append(placeHolder.charAt((b & 0xff) >>> 4));				
				sb.append(placeHolder.charAt(b & 0x0f));
			}
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}	
	
	public static String formatTitle(String title) {
		return title.trim().replace("&nbsp;", " ");
	}
	
	public static String getFixLenghString(String s) {
		int fixLength = 14;
		
		if (s.length() < fixLength) return s;
		
		s = s.substring(0, fixLength - 3);
		
		return s + "...";
	}
}
