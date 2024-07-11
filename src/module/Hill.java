package module;

public class Hill {
	private int[][] matrixKey;
	private int matrixSize; // Thêm thuộc tính độ dài của ma trận

	private static final String VIETNAMESE_ALPHABET = "aáàảãạăắằẳẵặâấầẩẫậbcdđeéèẻẽẹêếềểễệfghiíìỉĩịjklmnoóòỏõọôốồổỗộơớờởỡợpqrstuúùủũụưứừửữựvwxyỹỳỵỷýz"
			+ "AÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬBCDĐEÉÈẺẼẸÊẾỀỂỄỆFGHIÍÌỈĨỊJKLMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢPQRSTUÚÙỦŨỤƯỨỪỬỮỰVWXYZ1234567890!\"#$%&'()*+,-./:;<=>?@[]^_`{|}~ ";

	public int[][] createKey(int size) {
		matrixSize = size;
		int[][] randomMatrix;
		while (true) {
			randomMatrix = new int[matrixSize][matrixSize];

			for (int i = 0; i < matrixSize; i++) {
				for (int j = 0; j < matrixSize; j++) {
					randomMatrix[i][j] = (int) (Math.random() * VIETNAMESE_ALPHABET.length());
				}
			}

			int det = determinant(randomMatrix);
			int detInverse = modInverse(det, VIETNAMESE_ALPHABET.length());

			if (det != 0 && detInverse != -1) {
				matrixKey = randomMatrix;
				return matrixKey;
			}
		}
	}

	public String exportKeyString() {
		StringBuilder keyString = new StringBuilder();
		for (int i = 0; i < matrixKey.length; i++) {
			for (int j = 0; j < matrixKey[i].length; j++) {
				keyString.append(VIETNAMESE_ALPHABET.charAt(matrixKey[i][j]));
			}
		}
		return keyString.toString();
	}

	public int[][] enterKeyString(String keyInput) {
		int keyLength = keyInput.length();
		matrixSize = (int) Math.sqrt(keyLength);
		if (keyLength == 0 || matrixSize * matrixSize != keyLength) {
			return null; // Key không hợp lệ
		}

		int[][] matrix = new int[matrixSize][matrixSize];
		int index = 0;
		for (int i = 0; i < matrixSize; i++) {
			for (int j = 0; j < matrixSize; j++) {
				int charIndex = VIETNAMESE_ALPHABET.indexOf(keyInput.charAt(index));
				if (charIndex >= 0) {
					matrix[i][j] = charIndex;
				} else {
					return null; // Ký tự không hợp lệ trong key
				}
				index++;
			}
		}
		matrixKey = matrix;
		return matrixKey;
	}

	public String encrypt(String plaintext) {
		int[][] keyMatrix = matrixKey;

		if (keyMatrix == null) {
			return "Khóa không hợp lệ.";
		}

		StringBuilder encryptedText = new StringBuilder();

		for (int i = 0; i < plaintext.length(); i += 3) {
			int[] block = new int[3];
			for (int j = 0; j < 3; j++) {
				if (i + j < plaintext.length()) {
					char c = plaintext.charAt(i + j);
					int index = VIETNAMESE_ALPHABET.indexOf(c);
					if (index != -1) {
						block[j] = index;
					} else {
						block[j] = VIETNAMESE_ALPHABET.length() - 1; // Xử lý ký tự không nằm trong VIETNAMESE_ALPHABET
					}
				} else {
					block[j] = VIETNAMESE_ALPHABET.indexOf(' '); // Thêm dấu cách
				}
			}

			int[] encryptedBlock = new int[3];

			for (int j = 0; j < 3; j++) {
				int sum = 0;
				for (int k = 0; k < 3; k++) {
					sum += keyMatrix[j][k] * block[k];
				}
				encryptedBlock[j] = sum % VIETNAMESE_ALPHABET.length();
			}

			for (int j = 0; j < 3; j++) {
				encryptedText.append(VIETNAMESE_ALPHABET.charAt(encryptedBlock[j]));
			}
		}

		return encryptedText.toString();
	}

	@SuppressWarnings("unused")
	public String decrypt(String ciphertext) {
		int[][] keyMatrix = matrixKey;

		if (keyMatrix == null) {
			return "Khóa không hợp lệ.";
		}

		int[][] inverseKeyMatrix = getInverseMatrix(keyMatrix);

		if (inverseKeyMatrix == null) {
			return "Không thể tính toán ma trận nghịch đảo.";
		}
		int blockSize = keyMatrix.length;
		int numBlocks = (int) Math.ceil((double) ciphertext.length() / blockSize);

		StringBuilder decryptedText = new StringBuilder();
		int plaintextLength = 0;
		for (int i = 0; i < ciphertext.length(); i += 3) {
			int[] block = new int[3];
			for (int j = 0; j < 3; j++) {
				if (i + j < ciphertext.length()) {
					char c = ciphertext.charAt(i + j);
					int index = VIETNAMESE_ALPHABET.indexOf(c);
					if (index != -1) {
						block[j] = index;
					} else {
						block[j] = VIETNAMESE_ALPHABET.length() - 1; // Xử lý ký tự không nằm trong VIETNAMESE_ALPHABET
					}
				} else {
					block[j] = VIETNAMESE_ALPHABET.indexOf(' '); // Thêm dấu cách
				}
			}

			int[] decryptedBlock = new int[3];

			for (int j = 0; j < 3; j++) {
				int sum = 0;
				for (int k = 0; k < 3; k++) {
					sum += inverseKeyMatrix[j][k] * block[k];
				}
				decryptedBlock[j] = (sum % VIETNAMESE_ALPHABET.length() + VIETNAMESE_ALPHABET.length())
						% VIETNAMESE_ALPHABET.length();
			}

			for (int j = 0; j < 3; j++) {
				decryptedText.append(VIETNAMESE_ALPHABET.charAt(decryptedBlock[j]));
			}
		}

		return decryptedText.toString();
	}

	private int[][] getInverseMatrix(int[][] matrix) {
		int det = determinant(matrix);
		int detInverse = modInverse(det, VIETNAMESE_ALPHABET.length());

		if (det == 0 || detInverse == -1) {
			return null;
		}

		int[][] adjugateMatrix = adjugate(matrix);

		int[][] inverseMatrix = new int[3][3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				inverseMatrix[i][j] = (adjugateMatrix[i][j] * detInverse) % VIETNAMESE_ALPHABET.length();
				if (inverseMatrix[i][j] < 0) {
					inverseMatrix[i][j] += VIETNAMESE_ALPHABET.length();
				}
			}
		}

		return inverseMatrix;
	}

	private int determinant(int[][] matrix) {
		int det = matrix[0][0] * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1])
				- matrix[0][1] * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0])
				+ matrix[0][2] * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]);
		return (det % VIETNAMESE_ALPHABET.length() + VIETNAMESE_ALPHABET.length()) % VIETNAMESE_ALPHABET.length();
	}

	private int modInverse(int a, int m) {
		for (int i = 1; i < m; i++) {
			if ((a * i) % m == 1) {
				return i;
			}
		}
		return -1;
	}

	private int[][] adjugate(int[][] matrix) {
		int[][] adjugateMatrix = new int[3][3];
		adjugateMatrix[0][0] = (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1]);
		adjugateMatrix[0][1] = (matrix[0][2] * matrix[2][1] - matrix[0][1] * matrix[2][2]);
		adjugateMatrix[0][2] = (matrix[0][1] * matrix[1][2] - matrix[0][2] * matrix[1][1]);

		adjugateMatrix[1][0] = (matrix[1][2] * matrix[2][0] - matrix[1][0] * matrix[2][2]);
		adjugateMatrix[1][1] = (matrix[0][0] * matrix[2][2] - matrix[0][2] * matrix[2][0]);
		adjugateMatrix[1][2] = (matrix[0][2] * matrix[1][0] - matrix[0][0] * matrix[1][2]);

		adjugateMatrix[2][0] = (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]);
		adjugateMatrix[2][1] = (matrix[0][1] * matrix[2][0] - matrix[0][0] * matrix[2][1]);
		adjugateMatrix[2][2] = (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]);

		return adjugateMatrix;
	}

}
