package controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Base64;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import module.Algorithm;
import module.MD5;
import module.RSA;
import module.SHA;
import view.BottomPanel;
import view.Frame;
import view.MiddlePanel;
import view.TopPanel;

public class MiddleActionListener {
	private MiddlePanel middlePanel;
	private TopPanel topPanel;
	private BottomPanel bottomPanel;
	private Frame frame;
	private Algorithm algorithm;
	private String selectedFileName, fileExtension;
	private RSA rsa;

	public MiddleActionListener(MiddlePanel middlePanel, TopPanel topPanel, BottomPanel bottomPanel, Frame frame) {
		this.middlePanel = middlePanel;
		this.topPanel = topPanel;
		this.bottomPanel = bottomPanel;
		this.frame = frame;
		for (Component component : frame.getSymmetryMenu().getMenuComponents()) {
			if (component instanceof JMenuItem) {
				JMenuItem menuItem = (JMenuItem) component;
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String type = menuItem.getText();
						setVisibleComponentForHash(true);
						middlePanel.setTitlePublicKeyBorder("Key");
						middlePanel.setVisiblePrivateKeyField(false);
						middlePanel.getEncryptionButton().setText("Encrypt");
						generateComboBoxByAlgorithm(type);
						generateSizeModePaddingComboBoxByType(type);
						handleEncryption(type);
						resetTextAreaAndTextField();
					}
				});
			}
		}
		frame.getRsa().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisibleComponentForHash(true);
				setVisibleComponentForRSA(true);
				middlePanel.getEncryptionButton().setText("Encrypt");
				generateSizeModePaddingComboBoxByType(e.getActionCommand());
				handleEncryption(e.getActionCommand());
				resetTextAreaAndTextField();

			}
		});

		for (Component component : frame.getHashMenu().getMenuComponents()) {
			if (component instanceof JMenuItem) {
				JMenuItem menuItem = (JMenuItem) component;
				menuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						String type = menuItem.getText();
						setVisibleComponentForHash(false);
						if (type.equals("MD5")) {
							topPanel.setVisibleSelectFileButton(true);
						} else {
							topPanel.setVisibleSelectFileButton(false);
						}
						handleHash(type);
						resetTextAreaAndTextField();
					}

				});
			}
		}

		frame.getFileIntegrity().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisibleComponentForHash(false);
				middlePanel.getEncryptionButton().setText("Check");
				topPanel.setVisibleSelectFileButton(true);
				middlePanel.setVisiblePublicKeyField(true);
				middlePanel.setTitlePublicKeyBorder("Hash value");
				handleFileIntegrity();
				resetTextAreaAndTextField();

			}
		});

		frame.getExitItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleExit();
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				handleExit();
			}
		});

	}

	private void handleExit() {
		int option = JOptionPane.showConfirmDialog(new JFrame(), "Do you want to close the application?", "Confirm",
				JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	private void resetTextAreaAndTextField() {
		JTextArea textInput = middlePanel.getTextAreaEncrip();
		JTextArea textOutput = middlePanel.getTextAreaDecrip();
		JTextField keyInput = middlePanel.getPublicKeyField();
		JTextField keyPrivateInput = middlePanel.getPrivateKeyField();
		JTextField vectorInput = middlePanel.getVectorInput();
		textInput.setText("");
		textOutput.setText("");
		keyInput.setText("");
		keyPrivateInput.setText("");
		vectorInput.setText("");
	}

	private void generateSizeModePaddingComboBoxByType(String algorithm) {
		if (algorithm.equals("SEED")) {
			topPanel.setSizes(new String[] { "128" });
		}
		if (algorithm.equals("Hill")) {
			topPanel.setSizes(new String[] { "9", "16", "25" });
		}
		if (algorithm.equals("Vigenere")) {
			topPanel.setSizes(new String[] { "9", "18", "36", "72", "144" });
		}
		if (algorithm.equals("Blowfish") || algorithm.equals("AES")) {
			topPanel.setSizes(new String[] { "128", "192", "256" });
			topPanel.setPaddings(new String[] { "PKCS5Padding", "PKCS7Padding" });
		}
		if (algorithm.equals("DES")) {
			topPanel.setSizes(new String[] { "56" });
			topPanel.setPaddings(new String[] { "PKCS5Padding", "NoPadding", "PKCS7Padding" });
		}
		if (algorithm.equals("DESede")) {
			topPanel.setSizes(new String[] { "192" });
			topPanel.setPaddings(new String[] { "PKCS5Padding", "NoPadding", "PKCS7Padding" });
		}
		if (algorithm.equals("RSA")) {
			topPanel.setSizes(new String[] { "2048", "1024", "3072", "4096" });
			topPanel.setPaddings(new String[] { "PKCS1Padding", "PKCS5Padding" });
		}
		topPanel.getPaddingComboBox().setModel(new DefaultComboBoxModel<>(topPanel.getPaddings()));
		topPanel.getSizeCombobox().setModel(new DefaultComboBoxModel<>(topPanel.getSizes()));
	}

	private void handleFileIntegrity() {
		frame.showMainPanel();
		MD5 md5 = new MD5();
		JButton encryptButton = middlePanel.getEncryptionButton();
		JButton selectedFileButton = topPanel.getSelectFile();
		JTextArea textInput = middlePanel.getTextAreaEncrip();
		JTextArea textOutput = middlePanel.getTextAreaDecrip();
		bottomPanel.setStatusLabel("YOU ARE USING THE ALGORITHM: File integrity");
		removeAllActionListeners(encryptButton);
		removeAllActionListeners(selectedFileButton);
		encryptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(new Frame(), "Please select the correct files !!!", "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
		});
		selectedFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAllActionListeners(encryptButton);
				JFileChooser fileChooser = new JFileChooser();
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFiles = fileChooser.getSelectedFile();
					String filePath = selectedFiles.getAbsolutePath();
					textInput.setText(" File 1: " + filePath);
					encryptButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								String hashValue = md5.hash(filePath);
								if (hashValue.equals(middlePanel.getPublicKeyField().getText())) {
									textOutput.setText(" File integrity!");
								} else {
									textOutput.setText(" File has been changed!");
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					});
				} else {
					JOptionPane.showMessageDialog(new Frame(), "Please select the correct two files !!!", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}

			}
		});

	}

	private void handleHash(String type) {
		frame.showMainPanel();
		SHA sha = new SHA();
		MD5 md5 = new MD5();
		JButton encryptButton = middlePanel.getEncryptionButton();
		JButton selectedFileButton = topPanel.getSelectFile();
		JTextArea textInput = middlePanel.getTextAreaEncrip();
		JTextArea textOutput = middlePanel.getTextAreaDecrip();
		bottomPanel.setStatusLabel("YOU ARE USING THE ALGORITHM: " + type);
		removeAllActionListeners(encryptButton);
		removeAllActionListeners(selectedFileButton);
		encryptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textHash = textInput.getText();
				if (textHash.length() != 0 && textHash != null) {
					String hash = sha.hash(textHash, type);
					textOutput.setText(hash);
				} else {
					JOptionPane.showMessageDialog(new Frame(), "Please enter complete information !!!", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		selectedFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAllActionListeners(encryptButton);
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					String filePath = selectedFile.getAbsolutePath();
					textInput.setText("Bạn đã chọn file: " + filePath);
					encryptButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								String hashText = md5.hash(filePath);
								textOutput.setText(hashText);
							} catch (Exception e1) {
								e1.printStackTrace();
							}

						}
					});
				}
			}
		});
	}

	private void setVisibleComponentForHash(boolean status) {
		middlePanel.setVisiblePrivateKeyField(status);
		middlePanel.setVisiblePublicKeyField(status);
		middlePanel.getEncryptionButton().setText("Hash");
		middlePanel.setVisibleDecryptionButton(status);
		topPanel.setVisibleCreateKey(status);
		topPanel.setVisibleModeComboBox(status);
		topPanel.setVisiblePaddingComboBox(status);
		topPanel.setVisibleSizeCombobox(status);
		generateVectorByComboBox("ECB", "SEED");
	}

	private void setVisibleComponentForRSA(boolean status) {
		JButton selectedFileButton = topPanel.getSelectFile();
		JComboBox<String> modeComboBox = topPanel.getModeComboBox();
		JComboBox<String> paddingComboBox = topPanel.getPaddingComboBox();
		middlePanel.setTitlePublicKeyBorder("Public key");
		middlePanel.setVisiblePrivateKeyField(true);
		topPanel.setVisibleSizeCombobox(true);
		selectedFileButton.setVisible(status);
		paddingComboBox.setVisible(status);
		modeComboBox.setVisible(status);
	}

	private void handleEncryption(String type) {
		frame.showMainPanel();
		algorithm = new Algorithm();
		rsa = new RSA();
		JButton encryptButton = middlePanel.getEncryptionButton();
		JButton decryptButton = middlePanel.getDecryptionButton();
		JButton createKeyButton = topPanel.getCreateKey();
		JButton createVetorButton = topPanel.getGenerateVector();
		JButton selectedFileButton = topPanel.getSelectFile();
		JTextArea textInput = middlePanel.getTextAreaEncrip();
		JTextArea textOutput = middlePanel.getTextAreaDecrip();
		JTextField keyInput = middlePanel.getPublicKeyField();
		JTextField keyPrivateInput = middlePanel.getPrivateKeyField();
		JTextField vectorInput = middlePanel.getVectorInput();

		JComboBox<String> modeComboBox = topPanel.getModeComboBox();
		JComboBox<String> paddingComboBox = topPanel.getPaddingComboBox();
		JComboBox<String> keySizeCombobox = topPanel.getSizeCombobox();

		String modeSelected = getSelectedItemFromComboBox(modeComboBox);
		String paddingSelected = getSelectedItemFromComboBox(paddingComboBox);

		bottomPanel.setStatusLabel("YOU ARE USING THE ALGORITHM: " + type);

		removeAllActionListeners(createKeyButton);
		removeAllActionListeners(encryptButton);
		removeAllActionListeners(decryptButton);
		removeAllActionListeners(createVetorButton);
		removeAllActionListeners(selectedFileButton);
		removeAllActionListeners(modeComboBox);

		generateVectorByComboBox(modeSelected, type);

		modeComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedMode = getSelectedItemFromComboBox(modeComboBox);
				generateVectorByComboBox(selectedMode, type);
			}
		});

		createKeyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int keySize = Integer.parseInt(getSelectedItemFromComboBox(keySizeCombobox));
				try {
					if (type.equals("RSA")) {
						rsa.generateKeyPair(keySize);
						keyInput.setText(rsa.exportPublicKey());
						keyPrivateInput.setText(rsa.exportPrivateKey());
					} else {
						algorithm.generateKey(type, keySize);
						keyInput.setText(algorithm.exportKey(type));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		createVetorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				algorithm.generateRandomIV(type);
				vectorInput.setText(algorithm.exportIV(type));
			}
		});
		encryptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (type.equals("RSA")) {
						String enterKey = keyInput.getText();
						rsa.createPublicKey(enterKey);
						String textEncrypt = rsa.encryptToBase64(textInput.getText());
						textOutput.setText(textEncrypt);

					} else {
						String textEncrypt = "";
						String enterKey = keyInput.getText();
						String modeSelected = getSelectedItemFromComboBox(modeComboBox);
						String enterVector = vectorInput.getText();
						if (checkKeyLength(enterKey, type)) {
							algorithm.importKey(enterKey, type);
							if (!modeSelected.equals("ECB")) {
								if (checkIVLength(enterVector, type)) {
									algorithm.importIV(enterVector, type);
									textEncrypt = algorithm.encryptToBase64(type, textInput.getText(), modeSelected,
											paddingSelected);
								}
							} else {
								textEncrypt = algorithm.encryptToBase64(type, textInput.getText(), modeSelected,
										paddingSelected);
							}
							textOutput.setText(textEncrypt);
						}
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(new Frame(), e1.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
		decryptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (type.equals("RSA")) {
						String enterPrivateKey = keyPrivateInput.getText();
						rsa.createPrivateKey(enterPrivateKey);
						String textEncrypt = rsa.decryptFromBase64(textInput.getText());
						textOutput.setText(textEncrypt);

					} else {
						String textDecrypt = "";
						String enterKey = keyInput.getText();
						String modeSelected = getSelectedItemFromComboBox(modeComboBox);
						String enterVector = vectorInput.getText();
						if (checkKeyLength(enterKey, type)) {
							algorithm.importKey(enterKey, type);
							if (!modeSelected.equals("ECB")) {
								if (checkIVLength(enterVector, type)) {
									algorithm.importIV(enterVector, type);
									textDecrypt = algorithm.decryptFromBase64(type, textInput.getText(), modeSelected,
											paddingSelected);
								}
							} else {
								textDecrypt = algorithm.decryptFromBase64(type, textInput.getText(), modeSelected,
										paddingSelected);
							}
							textOutput.setText(textDecrypt);
						}
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(new Frame(), e1.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
		selectedFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAllActionListeners(encryptButton);
				removeAllActionListeners(decryptButton);
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				String modeSelected = getSelectedItemFromComboBox(modeComboBox);
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					handleSelectedFile(type, modeSelected, paddingSelected, selectedFile);
					textInput.setText("Bạn đã chọn file: " + selectedFile.getAbsolutePath());
				}
			}
		});
	}

	private void handleSelectedFile(String type, String mode, String padding, File file) {
		algorithm = new Algorithm();
		JButton encryptButton = middlePanel.getEncryptionButton();
		JButton decryptButton = middlePanel.getDecryptionButton();
		JTextField keyInput = middlePanel.getPublicKeyField();
		JTextField keyPrivateInput = middlePanel.getPrivateKeyField();
		JTextField vectorInput = middlePanel.getVectorInput();
		String userHome = System.getProperty("user.home");
		String selectedFilePath = file.getAbsolutePath();
		JTextArea textOutput = middlePanel.getTextAreaDecrip();

		selectedFileName = file.getName();
		fileExtension = "";

		int dotIndex = selectedFileName.lastIndexOf(".");
		if (dotIndex > 0) {
			selectedFileName = selectedFileName.substring(0, dotIndex);
			fileExtension = file.getName().substring(dotIndex + 1);
		}

		encryptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createFolder();

				try {
					String savePath = userHome + File.separator + ".security" + File.separator + selectedFileName
							+ "_encrypt." + fileExtension;
					if (type.equals("RSA")) {
						String enterKey = keyInput.getText();

						rsa.createPublicKey(enterKey);
						rsa.generateAESKey();
						rsa.encryptFileWithRSAAndAESNoIV(selectedFilePath, savePath);
						textOutput.setText(savePath);
					} else {
						String result = "";
						String enterKey = keyInput.getText();
						String enterVector = vectorInput.getText();
						if (checkKeyLength(enterKey, type)) {
							algorithm.importKey(enterKey, type);
							if (!mode.equals("ECB")) {
								if (checkIVLength(enterVector, type)) {
									algorithm.importIV(enterVector, type);
									algorithm.encryptFile(type, selectedFilePath, savePath, mode, padding);
									result = savePath;
								}
							} else {
								algorithm.encryptFile(type, selectedFilePath, savePath, mode, padding);
								result = savePath;
							}
							textOutput.setText(result);
						}
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(new Frame(), e1.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
		decryptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createFolder();

				try {
					String savePath = userHome + File.separator + ".security" + File.separator + selectedFileName
							+ "_decrypt." + fileExtension;
					if (type.equals("RSA")) {
						String enterPrivateKey = keyPrivateInput.getText();
						rsa.createPrivateKey(enterPrivateKey);
						rsa.decryptFileWithRSAAndAESNoIV(selectedFilePath, savePath);
						textOutput.setText(savePath);
					} else {
						String result = "";
						String enterKey = keyInput.getText();
						String enterVector = vectorInput.getText();
						if (checkKeyLength(enterKey, type)) {
							algorithm.importKey(enterKey, type);
							if (!mode.equals("ECB")) {
								if (checkIVLength(enterVector, type)) {
									algorithm.importIV(enterVector, type);
									algorithm.decryptFile(type, selectedFilePath, savePath, mode, padding);
									result = savePath;
								}
							} else {
								algorithm.decryptFile(type, selectedFilePath, savePath, mode, padding);
								result = savePath;
							}
							textOutput.setText(result);
						}
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(new Frame(), e1.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
	}

	private String getSelectedItemFromComboBox(JComboBox<String> comboBox) {
		Object selected = comboBox.getSelectedItem();
		if (selected != null) {
			return selected.toString();
		} else {
			return null;
		}
	}

	private void generateVectorByComboBox(String itemSelected, String type) {
		JTextField vector = middlePanel.getVectorInput();
		JButton encryptButton = middlePanel.getEncryptionButton();
		JButton createVetorButton = topPanel.getGenerateVector();

		if (itemSelected.equals("ECB") || type.equals("RSA")) {
			vector.setVisible(false);
			createVetorButton.setVisible(false);
			encryptButton.setVisible(false);
			encryptButton.setVisible(true);
		} else {
			vector.setVisible(true);
			createVetorButton.setVisible(true);
			encryptButton.setVisible(false);
			encryptButton.setVisible(true);
		}
	}

	private void generateComboBoxByAlgorithm(String algorithm) {

		JComboBox<String> modeComboBox = topPanel.getModeComboBox();
		JComboBox<String> padding = topPanel.getPaddingComboBox();
		JButton selectedFileButton = topPanel.getSelectFile();

		if (algorithm.equals("Vigenere") || algorithm.equals("Hill")) {
			modeComboBox.setVisible(false);
			padding.setVisible(false);
			selectedFileButton.setVisible(false);
		} else {
			modeComboBox.setVisible(true);
			padding.setVisible(true);

		}
	}

	private void removeAllActionListeners(JButton jButton) {
		for (ActionListener listener : jButton.getActionListeners()) {
			jButton.removeActionListener(listener);
		}
	}

	private void removeAllActionListeners(JComboBox<String> jCombobox) {
		for (ActionListener listener : jCombobox.getActionListeners()) {
			jCombobox.removeActionListener(listener);
		}
	}

	public boolean checkKeyLength(String key, String type) {
		if (type.equals("Vigenere")) {
			if (key.length() == 9 || key.length() == 18 || key.length() == 36 || key.length() == 72
					|| key.length() == 144) {
				return true;
			} else {
				JOptionPane.showMessageDialog(new Frame(), "The key length must be 9, 18, 36, 72 or 144 characters !!!",
						"Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		if (type.equals("Hill")) {
			if (key.length() == 9 || key.length() == 16 || key.length() == 25) {
				return true;
			} else {
				JOptionPane.showMessageDialog(new Frame(), "The key length must be 4, 9, 16 or 25 characters !!!",
						"Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		byte[] decodedBytes = Base64.getDecoder().decode(key);
		int keyLength = decodedBytes.length;
		int length = 0;
		if (type.equals("Blowfish")) {
			if (keyLength > 0 && keyLength < 56) {
				return true;
			} else {
				JOptionPane.showMessageDialog(new Frame(), "The key length must be between 0 and 56 bit !!!", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		if (type.equals("AES")) {
			if (keyLength == 16 || keyLength == 24 || keyLength == 32) {
				return true;
			} else {
				JOptionPane.showMessageDialog(new Frame(), "The key length must be 128, 192, or 256 bit !!!", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		if (type.equals("DES")) {
			length = 8;
		}
		if (type.equals("DESede")) {
			length = 24;
		}
		if (type.equals("SEED")) {
			length = 16;
		}

		if (keyLength == length) {
			return true;
		} else {
			JOptionPane.showMessageDialog(new Frame(), "The key length must be " + length + " byte !!!", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}

	public boolean checkIVLength(String iv, String type) {
		byte[] decodedBytes = Base64.getDecoder().decode(iv);
		int keyLength = decodedBytes.length;
		int length = 0;
		if (type.equals("Blowfish")) {
			if (keyLength > 0 && keyLength < 56) {
				return true;
			} else {
				JOptionPane.showMessageDialog(new Frame(), "The IV length must be between 0 and 56 byte !!!", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		if (type.equals("AES")|| type.equals("SEED")) {
			length = 16;
		}
		if (type.equals("DES") || type.equals("DESede")) {
			length = 8;
		}
		if (keyLength == length) {
			return true;
		} else {
			JOptionPane.showMessageDialog(new Frame(), "The IV length must be " + length + " byte !!!", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}

	private boolean createFolder() {
		String userHome = System.getProperty("user.home");
		String savePath = userHome + File.separator + ".security";
		File directory = new File(savePath);
		if (!directory.exists()) {
			boolean success = directory.mkdirs();
			if (success) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}
}
