package Vehicle;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class vehicle extends JPanel {
	BufferedImage carImage;
	private double thetaRadians;
	private double thetaDegreesRotatedFromBeginning = 90;
	
	public vehicle() {
		super();
		try {
			carImage = ImageIO.read(new File("E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\img\\vehicle_v2_right.jpg"));
//			int height = carImage.getHeight();
//			int width = carImage.getWidth();
//			System.out.println(height);
//			System.out.println(width);
			//carImage = ImageIO.read(new File("img/vehicle_v3.jpg"));
			//carImage = ImageIO.read(new File("E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\img\\rsz_vehicle.jpg"));
			//carImage = ImageIO.read(new File(fileLocation));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setTheta(double thetaHeadingInDegrees) {
		
		thetaRadians = Math.toRadians(thetaHeadingInDegrees);
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
	    g2d.translate(this.getWidth() / 2, this.getHeight() / 2);
	    g2d.rotate(thetaRadians);
	    g2d.translate(-carImage.getWidth(this) / 2, -carImage.getHeight(this) / 2);
	    g2d.drawImage(carImage, 0, 0, null);
	    
	    //g.drawLine(0, 0, 100, 100);
	}
}
