package module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SEED {
	private SecretKey key;
	private IvParameterSpec ivParameterSpec;

	public SEED() {
		Security.addProvider(new BouncyCastleProvider());
	}

	public IvParameterSpec generateRandomIV() {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		ivParameterSpec = new IvParameterSpec(iv);
		return ivParameterSpec;
	}

	public IvParameterSpec importIV(String base64IV) {
		byte[] decodedIV = Base64.getDecoder().decode(base64IV);
		ivParameterSpec = new IvParameterSpec(decodedIV);
		return ivParameterSpec;
	}

	public SecretKey generateKey(int size) throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("SEED", "BC");
		keyGenerator.init(size);
		key = keyGenerator.generateKey();
		return key;

	}

	public SecretKey importKey(String base64Key) throws NoSuchAlgorithmException {
		byte[] decodedKey = Base64.getDecoder().decode(base64Key);
		key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
		return key;
	}

	public String encryptToBase64(String text, String mode, String padding) throws Exception {
		if (key == null)
			return "";

		Cipher cipher = Cipher.getInstance("SEED/" + mode + "/" + padding, "BC");
		if (!mode.equals("ECB")) {
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
		} else {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		}

		byte[] plaintext = text.getBytes("UTF-8");
		byte[] cipherText = cipher.doFinal(plaintext);
		return Base64.getEncoder().encodeToString(cipherText);
	}

	public void encryptFile(String sourceFile, String destFile, String mode, String padding) throws Exception {
		if (key == null)
			throw new FileNotFoundException("Key not Found");
		File file = new File(sourceFile);
		if (file.isFile()) {
			Cipher cipher = Cipher.getInstance("SEED/" + mode + "/" + padding, "BC");
			if (!mode.equals("ECB")) {
				cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
			} else {
				cipher.init(Cipher.ENCRYPT_MODE, key);
			}

			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] input = new byte[64];
			int byteRead;
			while ((byteRead = fis.read(input)) != -1) {
				byte[] output = cipher.update(input, 0, byteRead);
				if (output != null)
					fos.write(output);

			}
			byte[] output = cipher.doFinal();
			if (output != null)
				fos.write(output);
			fis.close();
			fos.flush();
			fos.close();
			System.out.println("Encrypted");
		}
	}

	public String decryptFromBase64(String text, String mode, String padding) throws Exception {
		if (key == null)
			return null;

		Cipher cipher = Cipher.getInstance("SEED/" + mode + "/" + padding, "BC");
		if (!mode.equals("ECB")) {
			cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
		} else {
			cipher.init(Cipher.DECRYPT_MODE, key);
		}

		byte[] plaintext = cipher.doFinal(Base64.getDecoder().decode(text));
		String output = new String(plaintext, "UTF-8");
		return output;
	}

	public void decryptFile(String sourceFile, String destFile, String mode, String padding) throws Exception {
		if (key == null)
			throw new FileNotFoundException("Key not Found");
		File file = new File(sourceFile);
		if (file.isFile()) {
			Cipher cipher = Cipher.getInstance("SEED/" + mode + "/" + padding, "BC");
			if (!mode.equals("ECB")) {
				cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
			} else {
				cipher.init(Cipher.DECRYPT_MODE, key);
			}
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] input = new byte[64];
			int byteRead = 0;
			while ((byteRead = fis.read(input)) != -1) {
				byte[] output = cipher.update(input, 0, byteRead);
				if (output != null)
					fos.write(output);

			}
			byte[] output = cipher.doFinal();
			if (output != null)
				fos.write(output);
			fis.close();
			fos.flush();
			fos.close();
			System.out.println("Decrypted");
		}

	}

	public String exportKey() {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	public String exportIV() {
		byte[] ivBytes = ivParameterSpec.getIV();
		return Base64.getEncoder().encodeToString(ivBytes);
	}
	public static void main(String[] args) throws Exception {
		SEED des = new SEED();
		des.generateKey(128);
		des.generateRandomIV();
		System.out.println(des.exportIV());
		System.out.println(des.exportKey());
		String encrypt = des.encryptToBase64("Quân kha quân VBuif Dương", "CBC", "PKCS5Padding");
		System.out.println(encrypt);
		System.out.println(des.decryptFromBase64(encrypt, "CBC", "PKCS5Padding"));

	}
}
