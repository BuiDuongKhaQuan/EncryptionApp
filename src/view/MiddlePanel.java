package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class MiddlePanel extends JPanel {
	private JTextArea textAreaEncrip, textAreaDecrip;
	private JButton encryption, decryption;
	private JTextField publicKey, privateKey, vectorInput;
	private TitledBorder encripBorder, decripBorder, publicKeyBorder, privateKeyBorder, vetorBorder;

	public JTextArea getTextAreaEncrip() {
		return textAreaEncrip;
	}

	public JTextArea getTextAreaDecrip() {
		return textAreaDecrip;
	}

	public JButton getEncryptionButton() {
		return encryption;
	}

	public void setVisibleEncryptionButton(boolean status) {
		this.encryption.setVisible(status);
	}

	public JButton getDecryptionButton() {
		return decryption;
	}

	public void setVisibleDecryptionButton(boolean status) {
		this.decryption.setVisible(status);
	}

	public JTextField getPublicKeyField() {
		return publicKey;
	}

	public void setVisiblePublicKeyField(boolean status) {
		this.publicKey.setVisible(status);
	}

	public JTextField getPrivateKeyField() {
		return privateKey;
	}

	public void setVisiblePrivateKeyField(boolean status) {
		this.privateKey.setVisible(status);
	}

	public TitledBorder getDecripBorder() {
		return decripBorder;
	}

	public TitledBorder getEncripBorder() {
		return encripBorder;
	}

	public TitledBorder getPublicKeyBorder() {
		return publicKeyBorder;
	}

	public void setTitlePublicKeyBorder(String title) {
		this.publicKeyBorder.setTitle(title);
	}

	public TitledBorder getPrivateKeyBorder() {
		return privateKeyBorder;
	}

	public JTextField getVectorInput() {
		return vectorInput;
	}

	public MiddlePanel() {
		setLayout(new GridBagLayout());

		JPanel keyPanel = new JPanel(new GridBagLayout());
		JPanel buttonPanel = new JPanel(new GridBagLayout());

		encryption = new JButton("Encrypt");
		decryption = new JButton("Decrypt");

		textAreaEncrip = new JTextArea(10, 40);
		textAreaDecrip = new JTextArea(10, 40);
		textAreaEncrip.setLineWrap(true);
		textAreaDecrip.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textAreaEncrip);
		JScrollPane scrollPane1 = new JScrollPane(textAreaDecrip);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		publicKey = new JTextField();
		privateKey = new JTextField();
		vectorInput = new JTextField();

		publicKeyBorder = BorderFactory.createTitledBorder("Public key");
		publicKey.setBorder(publicKeyBorder);

		privateKeyBorder = BorderFactory.createTitledBorder("Private key");
		privateKey.setBorder(privateKeyBorder);

		vetorBorder = BorderFactory.createTitledBorder("Initialization Vector (IV) ");
		vectorInput.setBorder(vetorBorder);

		encripBorder = BorderFactory.createTitledBorder("Text Area Input");
		textAreaEncrip.setBorder(encripBorder);

		decripBorder = BorderFactory.createTitledBorder("Text Area Output");
		textAreaDecrip.setBorder(decripBorder);

		GridBagConstraints gbcKey = new GridBagConstraints();
		gbcKey.insets = new Insets(2, 2, 2, 2);

		gbcKey.gridy = 0;
		gbcKey.gridx = 0;
		gbcKey.weightx = 1.0;
		gbcKey.fill = GridBagConstraints.HORIZONTAL;

		keyPanel.add(publicKey, gbcKey);

		gbcKey.gridx = 1;
		keyPanel.add(privateKey, gbcKey);

		gbcKey.gridx = 2; // Điều này sẽ đặt ô vector dưới ô privateKey
		keyPanel.add(vectorInput, gbcKey);

		GridBagConstraints gbcMiddle = new GridBagConstraints();
		gbcMiddle.insets = new Insets(1, 1, 1, 1);

		gbcMiddle.gridy = 0;
		gbcMiddle.weightx = 1.0;
		gbcMiddle.weighty = 1.0;
		gbcMiddle.fill = GridBagConstraints.BOTH;

		add(scrollPane, gbcMiddle);

		gbcMiddle.gridy = 1;
		add(keyPanel, gbcMiddle);

		GridBagConstraints gbcButton = new GridBagConstraints();
		gbcButton.anchor = GridBagConstraints.WEST;
		buttonPanel.add(encryption, gbcButton);
		gbcButton.insets = new Insets(1, 10, 1, 1);
		buttonPanel.add(decryption, gbcButton);

		gbcMiddle.gridy = 2;
		add(buttonPanel, gbcMiddle);

		gbcMiddle.gridy = 3;
		add(scrollPane1, gbcMiddle);

	}
}
