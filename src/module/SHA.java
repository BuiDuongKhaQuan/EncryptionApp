package module;

import java.math.BigInteger;
import java.security.MessageDigest;

public class SHA {
	public static String SHA_1 = "SHA-1";
	public static String SHA_224 = "SHA-224";
	public static String SHA_256 = "SHA-256";
	public static String SHA_384 = "SHA-384";
	public static String SHA_512_224 = "SHA-512/224";
	public static String SHA_512_256 = "SHA-512/256";
	private MD5 md5 = new MD5();

	public String hash(String input) {
		return hash(input, SHA_256);
	}

	public String hash(String input, String algorithms) {
		if (algorithms.equals("MD5")) {
			return md5.checksum(input);
		}
		try {
			MessageDigest md = MessageDigest.getInstance(algorithms);
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);
			return hashtext;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
