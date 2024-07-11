package view;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BottomPanel extends JPanel {
	private JLabel statusLabel;

	public JLabel getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(String statusLabel) {
		this.statusLabel.setText(statusLabel);
	}

	public BottomPanel() {
		statusLabel = new JLabel("BẠN VUI LÒNG CHỌN 1 GIẢI THUẬT !!!");
		add(statusLabel, BorderLayout.CENTER);
	}
}
