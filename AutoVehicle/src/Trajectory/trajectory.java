package Trajectory;

import java.awt.Graphics;
import javax.swing.JPanel;

public class trajectory extends JPanel{
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	public void setPoint1(int x, int y) {
		x1 = x;
		y1 = y;
	}
	public void setPoint2(int x, int y) {
		x2 = x;
		y2 = y;
	}
	
	public void paintComponent(Graphics g) {
	    g.drawLine(x1, y1, x2, y2);
	}
}
