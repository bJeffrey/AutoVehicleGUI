package map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Map extends JPanel{
	BufferedImage mapImage;
	boolean removeLines;
	public Map(){
		super();
		
		removeLines = false;
		
		try {
			mapImage = ImageIO.read(new File("E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\img\\Scaled Test Map.jpg"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void remove(boolean r) {
		removeLines= r;
	}
	
	public void setTrajectory(Graphics g) {
		g.drawLine(0, 0, 100, 100);
		g.drawLine(100, 100, 100, 250);
		
	}
	//must draw the image first and then set the trajectory, in that order
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
	    
	    
	    if(removeLines) {
	    	System.out.println("removeLines is true");
//	    	g = Graphics();
//	    	g2d = (Graphics2D) g;
	    }
	    
	    g2d.drawImage(mapImage, 0, 0, null);
	    if(!removeLines) {
	    	System.out.println("removeLines is false");
	    	setTrajectory(g);
	    }
	    
	}
}
