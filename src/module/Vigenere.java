package module;

public class Vigenere {
	private String key;

	public String getKey() {
		return key;
	}

	private static final String VIETNAMESE_ALPHABET = "aáàảãạăắằẳẵặâấầẩẫậbcdđeéèẻẽẹêếềểễệfghiíìỉĩịjklmnoóòỏõọôốồổỗộơớờởỡợpqrstuúùủũụưứừửữựvwxyỹỳỵỷýz"
			+ "AÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬBCDĐEÉÈẺẼẸÊẾỀỂỄỆFGHIÍÌỈĨỊJKLMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢPQRSTUÚÙỦŨỤƯỨỪỬỮỰVWXYZ0123456789!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~ ";

	public String createKey(String keyInput) {
		key = keyInput;
		return key;
	}

	public String encrypt(String plaintext) {
		StringBuilder ciphertext = new StringBuilder();
		int keyLength = key.length();
		int keyIndex = 0;

		for (int i = 0; i < plaintext.length(); i++) {
			char plainChar = plaintext.charAt(i);
			char keyChar = key.charAt(keyIndex);

			char encryptedChar = encryptChar(plainChar, keyChar);
			ciphertext.append(encryptedChar);

			if (VIETNAMESE_ALPHABET.indexOf(plainChar) != -1) {
				keyIndex = (keyIndex + 1) % keyLength; // Tăng chỉ số của khóa chỉ cho ký tự trong bảng chữ cái tiếng
														// Việt
			}
		}

		return ciphertext.toString();
	}

	public String decrypt(String ciphertext) {
		StringBuilder plaintext = new StringBuilder();
		int keyLength = key.length();
		int keyIndex = 0;

		for (int i = 0; i < ciphertext.length(); i++) {
			char cipherChar = ciphertext.charAt(i);
			char keyChar = key.charAt(keyIndex);

			char decryptedChar = decryptChar(cipherChar, keyChar);
			plaintext.append(decryptedChar);

			if (VIETNAMESE_ALPHABET.indexOf(cipherChar) != -1) {
				keyIndex = (keyIndex + 1) % keyLength; // Tăng chỉ số của khóa chỉ cho ký tự trong bảng chữ cái tiếng
														// Việt
			}
		}

		return plaintext.toString();
	}

	public char encryptChar(char plainChar, char keyChar) {
		int alphabetSize = VIETNAMESE_ALPHABET.length();
		int plainIndex = VIETNAMESE_ALPHABET.indexOf(plainChar);
		int keyIndex = VIETNAMESE_ALPHABET.indexOf(keyChar);

		if (plainIndex != -1) {
			int encryptedIndex = (plainIndex + keyIndex) % alphabetSize;
			return VIETNAMESE_ALPHABET.charAt(encryptedIndex);
		} else {
			return plainChar;
		}
	}

	public char decryptChar(char cipherChar, char keyChar) {
		int alphabetSize = VIETNAMESE_ALPHABET.length();
		int cipherIndex = VIETNAMESE_ALPHABET.indexOf(cipherChar);
		int keyIndex = VIETNAMESE_ALPHABET.indexOf(keyChar);

		if (cipherIndex != -1) {
			int decryptedIndex = (cipherIndex - keyIndex + alphabetSize) % alphabetSize;
			return VIETNAMESE_ALPHABET.charAt(decryptedIndex);
		} else {
			return cipherChar;
		}
	}

//	public static void main(String[] args) {
//		String plaintext = "Trong quá trình soạn thảo văn bản trên Word, để văn bản trình bày có tính thẩm mỹ thì việc định dạng các đoạn trong văn bản là rất quan trọng. Việc định dạng này không khó, tuy nhiên với những bạn không quen soạn thảo hoặc mới sử dụng Word thì rất cần một hướng dẫn để việc soạn thảo nhất là định dạng văn bản được chuẩn. ";
//		Vigenere vigenere = new Vigenere();
//		String key = vigenere.createKey("quankha0000");
//		String encryptedText = vigenere.encrypt(plaintext);
//		System.out.println("Encrypted: " + encryptedText);
//
//		String decryptedText = vigenere.decrypt(encryptedText);
//		System.out.println("Decrypted: " + decryptedText);
//	}
}
