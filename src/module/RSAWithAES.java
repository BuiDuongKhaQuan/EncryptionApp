package module;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.Arrays;

public class RSAWithAES {
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static void encryptFileWithRSAAndAES(String inputFile, String outputFile, PublicKey publicKey)
            throws Exception {
        SecretKey aesKey = generateAESKey();
        File fileInput = new File(inputFile);
        File fileOutput = new File(outputFile);
        // Tạo IV ngẫu nhiên
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // Mã hóa tệp tin bằng khóa AES và ghi khóa AES và IV vào đầu tệp
        encryptFileWithAES(fileInput, fileOutput, aesKey, iv, publicKey);
    }

    public static void decryptFileWithRSAAndAES(String inputFile, String outputFile, PrivateKey privateKey)
            throws Exception {
        File fileInput = new File(inputFile);
        File fileOutput = new File(outputFile);
        try (InputStream in = new FileInputStream(fileInput); OutputStream out = new FileOutputStream(fileOutput)) {

            byte[] aesKeyWithIVBytes = new byte[272]; // Đọc đủ dữ liệu cho khóa AES (256 bytes) và IV (16 bytes)
            if (in.read(aesKeyWithIVBytes) != aesKeyWithIVBytes.length) {
                throw new RuntimeException("Failed to read AES key and IV data from the input file.");
            }

            byte[] aesKeyBytes = Arrays.copyOfRange(aesKeyWithIVBytes, 0, 256);
            byte[] iv = Arrays.copyOfRange(aesKeyWithIVBytes, 256, 272);

            byte[] decryptedAESKeyBytes = decryptWithRSA(aesKeyBytes, privateKey);
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

    public static void encryptFileWithRSAAndAESNoIV(String inputFile, String outputFile, PublicKey publicKey)
            throws Exception {
        SecretKey aesKey = generateAESKey();
        File fileInput = new File(inputFile);
        File fileOutput = new File(outputFile);

        // Mã hóa tệp tin bằng khóa AES và ghi khóa AES vào đầu tệp
        encryptFileWithAESNoIV(fileInput, fileOutput, aesKey, publicKey);
    }

    public static void decryptFileWithRSAAndAESNoIV(String inputFile, String outputFile, PrivateKey privateKey)
            throws Exception {
        File fileInput = new File(inputFile);
        File fileOutput = new File(outputFile);
        try (InputStream in = new FileInputStream(fileInput); OutputStream out = new FileOutputStream(fileOutput)) {
            byte[] aesKeyBytes = new byte[256]; // Đọc đủ dữ liệu cho khóa AES (256 bytes)
            if (in.read(aesKeyBytes) != aesKeyBytes.length) {
                throw new RuntimeException("Failed to read AES key data from the input file.");
            }

            byte[] decryptedAESKeyBytes = decryptWithRSA(aesKeyBytes, privateKey);
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

    private static void encryptFileWithAES(File inputFile, File outputFile, SecretKey aesKey, byte[] iv,
            PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

        try (InputStream in = new FileInputStream(inputFile); OutputStream out = new FileOutputStream(outputFile)) {
            byte[] aesKeyBytes = aesKey.getEncoded();
            byte[] encryptedAESKey = encryptWithRSA(aesKeyBytes, publicKey);

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

    private static void encryptFileWithAESNoIV(File inputFile, File outputFile, SecretKey aesKey,
            PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        try (InputStream in = new FileInputStream(inputFile); OutputStream out = new FileOutputStream(outputFile)) {
            byte[] aesKeyBytes = aesKey.getEncoded();
            byte[] encryptedAESKey = encryptWithRSA(aesKeyBytes, publicKey);
           
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

    private static byte[] encryptWithRSA(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    private static byte[] decryptWithRSA(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static void main(String[] args) {
        try {
            KeyPair keyPair = RSAWithAES.generateRSAKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String inputFile = "text.txt";
            String encryptedFile = "text1.txt";
            String decryptedFile = "text2.txt";

            // Mã hóa tệp tin bằng RSA và AES với IV
            encryptFileWithRSAAndAES(inputFile, encryptedFile, publicKey);

            // Giải mã tệp tin bằng RSA và AES với IV
            decryptFileWithRSAAndAES(encryptedFile, decryptedFile, privateKey);

            // Mã hóa tệp tin bằng RSA và AES không sử dụng IV
            String encryptedFileNoIV = "text3.txt";
            String decryptedFileNoIV = "text4.txt";
            encryptFileWithRSAAndAESNoIV(inputFile, encryptedFileNoIV, publicKey);
            decryptFileWithRSAAndAESNoIV(encryptedFileNoIV, decryptedFileNoIV, privateKey);

            System.out.println("File encrypted and decrypted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
