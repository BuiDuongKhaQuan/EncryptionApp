package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TopPanel extends JPanel {
	private JComboBox<String> modeComboBox, paddingComboBox, sizeCombobox;
	private JButton createKey, selectFile, generateVector;
	private String sizes[], modes[], paddings[];

	public JComboBox<String> getModeComboBox() {
		return modeComboBox;
	}

	public void setVisibleModeComboBox(boolean status) {
		this.modeComboBox.setVisible(status);
	}

	public JComboBox<String> getPaddingComboBox() {
		return paddingComboBox;
	}

	public void setVisiblePaddingComboBox(boolean status) {
		this.paddingComboBox.setVisible(status);
	}

	public JButton getCreateKey() {
		return createKey;
	}

	public void setVisibleCreateKey(boolean status) {
		this.createKey.setVisible(status);
	}

	public JButton getSelectFile() {
		return selectFile;
	}

	public void setVisibleSelectFileButton(boolean status) {
		this.selectFile.setVisible(status);
	}

	public JButton getGenerateVector() {
		return generateVector;
	}

	public JComboBox<String> getSizeCombobox() {
		return sizeCombobox;
	}

	public void setSizeCombobox(JComboBox<String> sizeCombobox) {
		this.sizeCombobox = sizeCombobox;
	}

	public void setVisibleSizeCombobox(boolean status) {
		this.sizeCombobox.setVisible(status);
	}

	public String[] getSizes() {
		return sizes;
	}

	public void setSizes(String[] sizes) {
		this.sizes = sizes;
	}

	public String[] getModes() {
		return modes;
	}

	public void setModes(String[] modes) {
		this.modes = modes;
	}

	public String[] getPaddings() {
		return paddings;
	}

	public void setPaddings(String[] paddings) {
		this.paddings = paddings;
	}


	public TopPanel() {
		setLayout(new GridBagLayout());

		createKey = new JButton("Create key");
		selectFile = new JButton("Select file");
		generateVector = new JButton("Create vector");

		sizes = new String[] { "2048", "515", "1024", "3072", "4096" };
		modes = new String[] { "ECB", "CBC", "CFB", "OFB" };
		paddings = new String[] { "PKCS5Padding", "NoPadding", "PKCS7Padding" };

		modeComboBox = new JComboBox<>(modes);
		paddingComboBox = new JComboBox<>(paddings);
		sizeCombobox = new JComboBox<>(sizes);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 1;
		gbc.insets = new Insets(15, 15, 15, 15);

		add(modeComboBox, gbc);
		add(paddingComboBox, gbc);
		add(sizeCombobox, gbc);
		add(createKey, gbc);
		add(generateVector, gbc);
		add(selectFile, gbc);

	}
}
