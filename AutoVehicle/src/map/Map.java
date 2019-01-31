package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JFrame;
import vehicle.*;

public class Map extends JPanel{
	BufferedImage mapImage;
	boolean removeLines;
	private Vehicle vehicle = new Vehicle();
	private Trajectory trajectory = new Trajectory();
	protected String OSName = System.getProperty("os.name");
	private int scaledMapWidth = 710;
	private int scaledMapHeight = 770;
	private int shiftedMapWidth = 250;
	private int shiftedMapHeight = 105;
	
	
	public Map(){
		super();
		
		removeLines = false;
		
		try {
			//mapImage = ImageIO.read(new File("E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\img\\Scaled Test Map.jpg"));

			if (OSName.equalsIgnoreCase("Windows 10")) {
//				runPrintOne = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\printOne.py";
//				runPrintOne = "python printOne.py";	
				mapImage = ImageIO.read(new File("E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\img\\workspace v1.jpg"));
			}
			else
				mapImage = ImageIO.read(new File("/home/workstation/Pictures/autovehicle/workspace v1.jpg"));
//				runPrintOne = "python printOne.py";
			
			
			
//			mapImage = ImageIO.read(new File("E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\img\\workspace v1.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void remove(boolean r) {
		removeLines= r;
	}
	
	public void setTrajectory(Graphics g) {
		g.drawLine(394, 368, 500, 368);
		g.drawLine(500, 368, 800, 250);
		g.drawLine(800, 250, 900, 100);
	}
	
	public void setVehicleTheta(double thetaHeadingInDegrees) {
		vehicle.setTheta(thetaHeadingInDegrees);
	}
	
	public int getScaledMapWidth() {
		return scaledMapWidth;
	}
	
	public int getScaledMapHeight() {
		return scaledMapHeight;
	}
	
	public int getShiftedMapWidth() {
		return shiftedMapWidth;
	}
	
	public int getShiftedMapHeight() {
		return shiftedMapHeight;
	}
	
	public int getVehicleX() {
		return vehicle.getX();
	}
	public int getVehicleY() {
		return vehicle.getY();
	}
	
	
	//must draw the image first and then set the trajectory, in that order
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
	    
	    
	    if(removeLines) {
//	    	System.out.println("removeLines is true");
//	    	g = Graphics();
//	    	g2d = (Graphics2D) g;
	    }
	    
	    g2d.drawImage(mapImage, 0, 0, null);
	    if(!removeLines) {
//	    	System.out.println("removeLines is false"); this is called whenever the window is resized
	    	setTrajectory(g);
//	    	g.setColor(Color.WHITE);
	    }
	    
	}
}

class Trajectory extends JPanel{
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	protected String OSName = System.getProperty("os.name");
	
	public void setPoint1(int x, int y) {
		x1 = x;
		y1 = y;
	}
	public void setPoint2(int x, int y) {
		x2 = x;
		y2 = y;
	}
	
	BufferedImage carImage;
	private double thetaRadians;
	private double thetaDegreesRotatedFromBeginning = 90;
	
	public Trajectory() {
		super();
		try {
			if (OSName.equalsIgnoreCase("Windows 10")) {
				carImage = ImageIO.read(new File("E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\img\\vehicle_v2_right.jpg"));
			}
			else
				carImage = ImageIO.read(new File("/home/workstation/Pictures/autovehicle/vehicle_v2_right.jpg"));
//			int height = carImage.getHeight();
//			int width = carImage.getWidth();
//			System.out.println(height);
//			System.out.println(width);
			//carImage = ImageIO.read(new File("/home/workstation/Pictures/autovehicle/vehicle_v3.jpg"));
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
	    
	    //g.drawLine(x1, y1, x2, y2);
	}

}