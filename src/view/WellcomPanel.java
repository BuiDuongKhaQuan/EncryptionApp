package view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class WellcomPanel extends JPanel {
	public WellcomPanel() {
		setLayout(new GridBagLayout());

		JLabel welcomeLabel = new JLabel("Welcome to the algorithm application!");

		Font labelFont = welcomeLabel.getFont();
		welcomeLabel.setFont(new Font(labelFont.getName(), Font.BOLD, 35));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(welcomeLabel, constraints);
	}
}
