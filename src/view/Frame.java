package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import controller.MiddleActionListener;

@SuppressWarnings("serial")
public class Frame extends JFrame {
	private JMenu fileMenu, securityMenu, hashMenu, electronicSignatureMenu, symmetryMenu, asymmetryMenu;
	private JPanel cards;
	private CardLayout cardLayout;
	private JMenuItem exitItem, fileIntegrity, rsa;

	public JMenu getFileMenu() {
		return fileMenu;
	}

	public JMenu getSecurityMenu() {
		return securityMenu;
	}

	public JMenu getHashMenu() {
		return hashMenu;
	}

	public JMenu getElectronicSignatureMenu() {
		return electronicSignatureMenu;
	}

	public JMenu getSymmetryMenu() {
		return symmetryMenu;
	}

	public JMenu getAsymmetryMenu() {
		return asymmetryMenu;
	}

	public JMenuItem getExitItem() {
		return exitItem;
	}

	public JMenuItem getFileIntegrity() {
		return fileIntegrity;
	}

	public JMenuItem getRsa() {
		return rsa;
	}

	public Frame() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Security App");
		setSize(1000, 600);
		setMinimumSize(getSize());

		setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel(new BorderLayout());

		JMenuBar menuBar = new JMenuBar();

		fileMenu = new JMenu("File");
		securityMenu = new JMenu("Algorithm");
		hashMenu = new JMenu("Hash");
		electronicSignatureMenu = new JMenu("Electronic Signature");

		exitItem = new JMenuItem("Exit");
		fileIntegrity = new JMenuItem("File integrity");
		rsa = new JMenuItem("RSA");

		asymmetryMenu = new JMenu("Asymmetrical");
		symmetryMenu = createSymmetryMenu();
		asymmetryMenu.add(rsa);

		electronicSignatureMenu.add(fileIntegrity);
		hashMenu.add(createHashMenu());
		fileMenu.add(exitItem);

		securityMenu.add(symmetryMenu);
		securityMenu.add(asymmetryMenu);

		menuBar.add(fileMenu);
		menuBar.add(securityMenu);
		menuBar.add(hashMenu);
		menuBar.add(electronicSignatureMenu);

		setJMenuBar(menuBar);

		WellcomPanel wellcomPanel = new WellcomPanel();
		TopPanel topPanel = new TopPanel();
		MiddlePanel middlePanel = new MiddlePanel();
		BottomPanel bottomPanel = new BottomPanel();
		new MiddleActionListener(middlePanel, topPanel, bottomPanel, this);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(middlePanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		cards = new JPanel();
		cardLayout = new CardLayout();
		cards.setLayout(cardLayout);

		cards.add(wellcomPanel, "wellcomPanel");
		cards.add(mainPanel, "mainPanel");

		add(cards);
	}

	public void showMainPanel() {
		cardLayout.show(cards, "mainPanel");
	}

	private JMenu createSymmetryMenu() {
		JMenu symmetryMenu = new JMenu("Symmetry");
		String[] symmetryAlgorithms = { "SEED", "AES", "DES", "DESede", "Blowfish", "Vigenere", "Hill" };
		for (String algorithm : symmetryAlgorithms) {
			JMenuItem item = new JMenuItem(algorithm);
			symmetryMenu.add(item);
		}
		return symmetryMenu;
	}

	private JMenu createHashMenu() {
		String[] hashAlgorithms = { "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512/224", "SHA-512/256", "MD5" };
		for (String algorithm : hashAlgorithms) {
			JMenuItem item = new JMenuItem(algorithm);
			hashMenu.add(item);
		}
		return hashMenu;
	}

}
