package module;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class MD5 {
	public String checksum(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			return number.toString(16);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public String hash(String file) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		InputStream is = new BufferedInputStream(new FileInputStream(file));
		DigestInputStream dis = new DigestInputStream(is, digest);

		byte[] buffer = new byte[1024];
		int read;
		do {
			read = dis.read(buffer);
		} while (read != -1);

		BigInteger number = new BigInteger(1, dis.getMessageDigest().digest());
		dis.close();
		return number.toString(16);

	}
	
}
