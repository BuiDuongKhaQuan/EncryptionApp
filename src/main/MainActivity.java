package main;

import view.Frame;

public class MainActivity {
	public static void main(String[] args) {
		try {
			Frame frame = new Frame();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
