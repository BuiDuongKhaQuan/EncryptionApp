package module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RSA {
	private KeyPair keyPair;
	private PublicKey publicKey;
	private PrivateKey privateKey;

	public void generateKeyPair(int keySize) throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(keySize);
		keyPair = keyPairGenerator.generateKeyPair();
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();
	}

	public PublicKey createPublicKey(String publicKeyBase64) {
		try {
			byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid Public Key");
		}
	}

	public PrivateKey createPrivateKey(String privateKeyBase64) throws Exception {
		try {
			byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePrivate(keySpec);

		} catch (Exception e) {
			
			throw new IllegalArgumentException("Invalid Private Key");
		}
	}

	public String exportPublicKey() {
		byte[] publicKeyBytes = publicKey.getEncoded();
		return Base64.getEncoder().encodeToString(publicKeyBytes);
	}

	public String exportPrivateKey() {
		byte[] privateKeyBytes = privateKey.getEncoded();
		return Base64.getEncoder().encodeToString(privateKeyBytes);
	}

	public byte[] encrypt(String text) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] plaintext = text.getBytes("UTF-8");
		byte[] ciphertext = cipher.doFinal(plaintext);
		return ciphertext;
	}

	public byte[] decrypt(byte[] ciphertext) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] plaintext = cipher.doFinal(ciphertext);
		return plaintext;
	}

	public String encryptToBase64(String text) throws Exception {
		byte[] ciphertext = encrypt(text);
		return Base64.getEncoder().encodeToString(ciphertext);
	}

	public String decryptFromBase64(String base64Text) throws Exception {
		byte[] ciphertext = Base64.getDecoder().decode(base64Text);
		byte[] plaintext = decrypt(ciphertext);
		return new String(plaintext, "UTF-8");
	}

	// Encrypt file with AES

	public SecretKey generateAESKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256);
		return keyGenerator.generateKey();
	}

	public void encryptFileWithRSAAndAES(String inputFile, String outputFile) throws Exception {
		SecretKey aesKey = generateAESKey();
		File fileInput = new File(inputFile);
		File fileOutput = new File(outputFile);
		// Tạo IV ngẫu nhiên
		byte[] iv = new byte[16];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);

		encryptFileWithAES(fileInput, fileOutput, aesKey, iv);
	}

	public void decryptFileWithRSAAndAES(String inputFile, String outputFile) throws Exception {
		File fileInput = new File(inputFile);
		File fileOutput = new File(outputFile);
		try (InputStream in = new FileInputStream(fileInput); OutputStream out = new FileOutputStream(fileOutput)) {

			byte[] aesKeyWithIVBytes = new byte[272]; // Đọc đủ dữ liệu cho khóa AES (256 bytes) và IV (16 bytes)
			if (in.read(aesKeyWithIVBytes) != aesKeyWithIVBytes.length) {
				throw new RuntimeException("Failed to read AES key and IV data from the input file.");
			}

			byte[] aesKeyBytes = Arrays.copyOfRange(aesKeyWithIVBytes, 0, 256);
			byte[] iv = Arrays.copyOfRange(aesKeyWithIVBytes, 256, 272);

			byte[] decryptedAESKeyBytes = decryptWithRSA(aesKeyBytes);
			SecretKey aesKey = new SecretKeySpec(decryptedAESKeyBytes, "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

			byte[] inputBuffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = in.read(inputBuffer)) > 0) {
				byte[] decryptedBytes = cipher.update(inputBuffer, 0, bytesRead);
				out.write(decryptedBytes);
			}
			byte[] finalBytes = cipher.doFinal();
			out.write(finalBytes);
		}
	}

	public void encryptFileWithRSAAndAESNoIV(String inputFile, String outputFile) throws Exception {
		SecretKey aesKey = generateAESKey();
		File fileInput = new File(inputFile);
		File fileOutput = new File(outputFile);

		// Mã hóa tệp tin bằng khóa AES và ghi khóa AES vào đầu tệp
		encryptFileWithAESNoIV(fileInput, fileOutput, aesKey);
	}

	public void decryptFileWithRSAAndAESNoIV(String inputFile, String outputFile) throws Exception {
		File fileInput = new File(inputFile);
		File fileOutput = new File(outputFile);
		try (InputStream in = new FileInputStream(fileInput); OutputStream out = new FileOutputStream(fileOutput)) {
			byte[] aesKeyBytes = new byte[256]; // Đọc đủ dữ liệu cho khóa AES (256 bytes)
			if (in.read(aesKeyBytes) != aesKeyBytes.length) {
				throw new RuntimeException("Failed to read AES key data from the input file.");
			}

			byte[] decryptedAESKeyBytes = decryptWithRSA(aesKeyBytes);
			SecretKey aesKey = new SecretKeySpec(decryptedAESKeyBytes, "AES");

			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, aesKey);

			byte[] inputBuffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = in.read(inputBuffer)) > 0) {
				byte[] decryptedBytes = cipher.update(inputBuffer, 0, bytesRead);
				out.write(decryptedBytes);
			}
			byte[] finalBytes = cipher.doFinal();
			out.write(finalBytes);
		}
	}

	private void encryptFileWithAES(File inputFile, File outputFile, SecretKey aesKey, byte[] iv) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

		try (InputStream in = new FileInputStream(inputFile); OutputStream out = new FileOutputStream(outputFile)) {
			byte[] aesKeyBytes = aesKey.getEncoded();
			byte[] encryptedAESKey = encryptWithRSA(aesKeyBytes);

			byte[] aesKeyWithIVBytes = new byte[272];
			System.arraycopy(encryptedAESKey, 0, aesKeyWithIVBytes, 0, 256);
			System.arraycopy(iv, 0, aesKeyWithIVBytes, 256, 16);

			out.write(aesKeyWithIVBytes);

			byte[] inputBuffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = in.read(inputBuffer)) > 0) {
				byte[] encryptedBytes = cipher.update(inputBuffer, 0, bytesRead);
				out.write(encryptedBytes);
			}
			byte[] finalBytes = cipher.doFinal();
			out.write(finalBytes);
		}
	}

	private void encryptFileWithAESNoIV(File inputFile, File outputFile, SecretKey aesKey) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);

		try (InputStream in = new FileInputStream(inputFile); OutputStream out = new FileOutputStream(outputFile)) {
			byte[] aesKeyBytes = aesKey.getEncoded();
			byte[] encryptedAESKey = encryptWithRSA(aesKeyBytes);
			out.write(encryptedAESKey);

			byte[] inputBuffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = in.read(inputBuffer)) > 0) {
				byte[] encryptedBytes = cipher.update(inputBuffer, 0, bytesRead);
				out.write(encryptedBytes);
			}
			byte[] finalBytes = cipher.doFinal();
			out.write(finalBytes);
		}
	}

	private byte[] encryptWithRSA(byte[] data) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}

	private byte[] decryptWithRSA(byte[] data) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	public PublicKey getPublicKey() {
		return keyPair.getPublic();
	}

	public PrivateKey getPrivateKey() {
		return keyPair.getPrivate();
	}

	public void main(String[] args) throws Exception {
		
		
//		RSA rsaEncryption = new RSA();
//		rsaEncryption.generateKeyPair(2048);
//
//		String originalText = "Hello, RSA encryption!";
//		String encryptedText = rsaEncryption.encryptToBase64(originalText);
//
//		System.out.println("Encrypted Text: " + encryptedText);
//
//		String decryptedText = rsaEncryption.decryptFromBase64(encryptedText);
//		System.out.println("Decrypted Text: " + decryptedText);
//
//		String sourceFile = "text.txt";
//		String encryptedFile = "text1.txt";
//		String decryptedFile = "text2.txt";

		// Encrypt file
//		rsaEncryption.encryptFile(sourceFile, encryptedFile);
//		System.out.println("File encrypted.");

		// Decrypt file
//		rsaEncryption.decryptFile(encryptedFile, decryptedFile);
//		System.out.println("File decrypted.");
	}
}