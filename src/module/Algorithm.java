package module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Algorithm {
	private SecretKey key;
	private IvParameterSpec ivParameterSpec;
	public String DES = "DES";
	public String DES3 = "DESede";
	public String AES = "AES";
	public String Blowfish = "Blowfish";
	private SEED seed = new SEED();
	private Vigenere vigenere = new Vigenere();
	private Hill hill = new Hill();

	public String generateRandomString(int n) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder(6);
		for (int i = 0; i < n; i++) {
			int randomIndex = random.nextInt(characters.length());
			char randomChar = characters.charAt(randomIndex);
			sb.append(randomChar);
		}
		return sb.toString();
	}

	public SecretKey generateKey(String algorithm, int size) throws NoSuchAlgorithmException {
		if (algorithm.equals("SEED")) {
			try {
				seed.generateKey(size);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (algorithm.equals("Hill")) {
			hill.enterKeyString(generateRandomString(size));
		} else if (algorithm.equals("Vigenere")) {
			vigenere.createKey(generateRandomString(size));
		} else {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
			keyGenerator.init(size);
			key = keyGenerator.generateKey();
		}
		return key;
	}

	public SecretKey importKey(String base64Key, String algorithm) {

		if (algorithm.equals("SEED")) {
			try {
				seed.importKey(base64Key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (algorithm.equals("Hill")) {
			hill.enterKeyString(base64Key);
		} else if (algorithm.equals("Vigenere")) {
			vigenere.createKey(base64Key);
		} else {
			byte[] keyBytes = Base64.getDecoder().decode(base64Key);
			key = new SecretKeySpec(keyBytes, algorithm);
		}
		return key;
	}

	public IvParameterSpec importIV(String base64IV, String algorithm) {
		byte[] ivBytes = Base64.getDecoder().decode(base64IV);
		if (algorithm.equals("SEED")) {
			seed.importIV(base64IV);
		}
		if ((ivBytes.length < 8 || ivBytes.length > 64) && algorithm.equals("Blowfish")) {
			throw new IllegalArgumentException("IV Blowfish phải có độ dài từ 8 đến 64 bytes.");
		}
		if (ivBytes.length != 16 && algorithm.equals("AES")) {
			throw new IllegalArgumentException("IV AES phải có độ dài 16, 24 hoặc 32 bytes.");
		}
		if (ivBytes.length != 8 && algorithm.equals("DES")) {
			throw new IllegalArgumentException("IV DES phải có độ dài 8 bytes.");
		}
		if (ivBytes.length != 8 && algorithm.equals("DESede")) {
			throw new IllegalArgumentException("IV 3DES phải có độ dài 8 bytes.");
		}
		ivParameterSpec = new IvParameterSpec(ivBytes);
		return ivParameterSpec;
	}

	public IvParameterSpec generateRandomIV(String algorithm) {
		SecureRandom secureRandom = new SecureRandom();
		int ivLength = 0;
		if (algorithm.equals("SEED")) {
			seed.generateRandomIV();
		}
		if (algorithm.equals("Blowfish")) {
			ivLength = 50;
		}
		if (algorithm.equals("AES")) {
			ivLength = 16;
		}
		if (algorithm.equals("DES") || algorithm.equals("DESede")) {
			ivLength = 8;
		}
		byte[] ivBytes = new byte[ivLength];
		secureRandom.nextBytes(ivBytes);
		ivParameterSpec = new IvParameterSpec(ivBytes);
		return ivParameterSpec;
	}

	public String encryptToBase64(String algorithm, String text, String mode, String padding) throws Exception {

		if (algorithm.equals("SEED")) {
			return seed.encryptToBase64(text, mode, padding);
		}
		if (algorithm.equals("Hill")) {
			return hill.encrypt(text);
		}
		if (algorithm.equals("Vigenere")) {
			return vigenere.encrypt(text);
		} else {
			Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding);
			if (mode == "ECB") {
				cipher.init(Cipher.ENCRYPT_MODE, key);
			} else {
				cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
			}
			byte[] plaintext = text.getBytes("UTF-8");
			byte[] cipherText = cipher.doFinal(plaintext);

			return Base64.getEncoder().encodeToString(cipherText);
		}
	}

	public String decryptFromBase64(String algorithm, String text, String mode, String padding) throws Exception {

		if (algorithm.equals("SEED")) {
			return seed.decryptFromBase64(text, mode, padding);
		}
		if (algorithm.equals("Hill")) {
			return hill.decrypt(text);
		}
		if (algorithm.equals("Vigenere")) {
			return vigenere.decrypt(text);
		} else {
			Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding);
			if (mode == "ECB") {
				cipher.init(Cipher.DECRYPT_MODE, key);
			} else {
				cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
			}
			byte[] plaintext = cipher.doFinal(Base64.getDecoder().decode(text));
			String output = new String(plaintext, "UTF-8");

			return output;
		}
	}

	public void encryptFile(String algorithm, String sourceFile, String destFile, String mode, String padding)
			throws Exception {
		if (algorithm.equals("SEED")) {
			seed.encryptFile(sourceFile, destFile, mode, padding);
		} else {
			File f = new File(sourceFile);
			if (f.isFile()) {

				Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding);
				if (mode == "ECB") {
					cipher.init(Cipher.ENCRYPT_MODE, key);
				} else {
					cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
				}

				FileInputStream fis = new FileInputStream(f);
				@SuppressWarnings("resource")
				FileOutputStream fos = new FileOutputStream(destFile);
				byte[] input = new byte[64];
				int bytesRead;

				while ((bytesRead = fis.read(input)) != -1) {
					byte[] output = cipher.update(input, 0, bytesRead);
					if (output != null)
						fos.write(output);
				}
				byte[] output = cipher.doFinal();
				if (output != null)
					fos.write(output);

				fis.close();
				fos.flush();
				fis.close();
				System.out.println("Encrypt");
			}
		}
	}

	public void decryptFile(String algorithm, String sourceFile, String destFile, String mode, String padding)
			throws Exception {
		if (algorithm.equals("SEED")) {
			seed.decryptFile(sourceFile, destFile, mode, padding);
		} else {
			File file = new File(sourceFile);
			if (file.isFile()) {
				Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding);
				if (mode == "ECB") {
					cipher.init(Cipher.DECRYPT_MODE, key);
				} else {
					cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
				}
				FileInputStream fis = new FileInputStream(file);
				FileOutputStream fos = new FileOutputStream(new File(destFile));

				byte[] input = new byte[64];
				int readByte = 0;

				while ((readByte = fis.read(input)) != -1) {
					byte[] output = cipher.update(input, 0, readByte);
					if (output != null) {
						fos.write(output);
					}
				}
				byte[] output = cipher.doFinal();
				if (output != null) {
					fos.write(output);
				}

				fis.close();
				fos.flush();
				fis.close();
				System.out.println("Decrypted");
			}
		}
	}

	public String exportKey(String algorithm) {
		if (algorithm.equals("Hill")) {
			return hill.exportKeyString();
		}
		if (algorithm.equals("Vigenere")) {
			return vigenere.getKey();
		}
		if (algorithm.equals("SEED")) {
			return seed.exportKey();
		}
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	public String exportIV(String algorithm) {
		if (algorithm.equals("SEED")) {
			return seed.exportIV();
		}
		return Base64.getEncoder().encodeToString(ivParameterSpec.getIV());
	}

	public static void main(String[] args) throws Exception {
//		Algorithm algorithm = new Algorithm();
//		String mode = "CBC";
//		String padding = "PKCS5Padding";
//		String algorithmType = "Vigenere";
//		algorithm.createKey("1111111111111111", algorithmType);
//		algorithm.createIV("11111111", algorithmType);
//		String textEncrypt = algorithm.encryptToBase64(algorithmType, "Bùi Dương Khả Quân", mode, padding);
//		System.out.println(textEncrypt);
//		System.out.println(algorithm.decryptFromBase64(algorithmType, textEncrypt, mode, padding));
//		algorithm.encryptFile(algorithmType, "text.txt", "text1.txt", mode, padding);
//		algorithm.decryptFile(algorithmType, "text1.txt", "text2.txt", mode, padding);

	}

}
