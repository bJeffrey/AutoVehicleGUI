import java.awt.EventQueue;

import javax.swing.JFrame;
//import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComponent;

//import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;
//import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import tcpClient.TCPClient;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import java.awt.Image;
import java.awt.event.KeyEvent;

import javax.swing.event.ChangeEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MainFrame {

	private JFrame frame;
	protected TCPClient client;
	protected boolean connected;
	protected String lastCommandSent;
	private int width, height;
	private Dimension dim;
	private BufferedImage rotatedCar;
	private BufferedImage newCarImg;
	private double angle;
	protected Image carImg;
	
	//private rectangleImage rectangleImageCar = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/*
    public double getAngle() {
        return angle;
    }
    public void setAngle(double angle) {
        this.angle = angle;

        double rads = Math.toRadians(getAngle());
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = newCarImg.getWidth();
        int h = newCarImg.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        rotatedCar = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedCar.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        //int x = clickPoint == null ? w / 2 : clickPoint.x;
        //int y = clickPoint == null ? h / 2 : clickPoint.y;
        int x = 50;
        int y = 50;
        
        
        at.rotate(Math.toRadians(getAngle()), x, y);
        g2d.setTransform(at);
        g2d.drawImage(newCarImg, 0, 0, (ImageObserver) this);
        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        g2d.dispose();

        repaint();
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (rotatedCar != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            //int x = (getWidth() - rotatedCar.getWidth()) / 2;
            //int y = (getHeight() - rotatedCar.getHeight()) / 2;
            g2d.drawImage(rotatedCar, x, y, (ImageObserver) this);

            g2d.setColor(Color.RED);

            //x = clickPoint == null ? getWidth() / 2 : clickPoint.x;
            //y = clickPoint == null ? getHeight()/ 2 : clickPoint.y;
            int x = y = 50;
            System.out.println("x");
            System.out.println(x);
            System.out.println("y");
            System.out.println(y);
            x -= 5;
            y -= 5;

            g2d.drawOval(x, y, 10, 10);
            g2d.dispose();
        }
    }    
	*/

	public class AL extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if(keyCode == e.VK_LEFT) {
				System.out.println("Left was pressed");
			}
			if(keyCode == e.VK_RIGHT) {
				System.out.println("Right was pressed");
			}
			if(keyCode == e.VK_UP) {
				System.out.println("Up was pressed");
			}
			if(keyCode == e.VK_DOWN) {
				System.out.println("Down was pressed");
			}
		}
		public void keyReleased(KeyEvent e) {
			
		}
	}
	
	//arrow key code

    class MyArrowBinding extends AbstractAction {
        public MyArrowBinding(String text) {
           super(text);
           putValue(ACTION_COMMAND_KEY, text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
           String actionCommand = e.getActionCommand();
           System.out.println("Key Binding: " + actionCommand);
        }
     }

/*		
    ActionMap actionMap = getActionMap();
    int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
    InputMap inputMap = getInputMap(condition);

    for (Direction direction : Direction.values()) {
    	inputMap.put(direction.getKeyStroke(), direction.getText());
        actionMap.put(direction.getText(), new MyArrowBinding(direction.getText()));
    }
*/	    
	//end of arrow key code

	
	
	
	/**
	 * Create the application.
	 */
	public MainFrame() {
/*
	    ActionMap actionMap = getActionMap();
	    int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
	    InputMap inputMap = getInputMap(condition);

	    for (Direction direction : Direction.values()) {
	    	inputMap.put(direction.getKeyStroke(), direction.getText());
	        actionMap.put(direction.getText(), new MyArrowBinding(direction.getText()));
	    }
*/		
		//addKeyListener(new AL());
		
		initialize();
	}

	/*
	 * Rotate the image
	 */

	
	/**
	 * Initialize the contents of the frame.
	 */	
	private void initialize() {
		
	    ActionMap actionMap = getActionMap();
	    int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
	    InputMap inputMap = getInputMap(condition);
	    
	    for (Direction direction : Direction.values()) {
	    	//inputMap.put(direction.getKeyStroke(), direction.getText());
	        //actionMap.put(direction.getText(), new MyArrowBinding(direction.getText()));
	    }
		
	    
		
		//addKeyListener(new AL());
		frame = new JFrame("Autonomous Vehicle Controller");
		frame.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 12));
		frame.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().setBackground(new Color(245, 245, 245));
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		    	//If we connected to the tcp server, close the socket
		    	if (connected == true) {
		        	System.out.println("Closing socket");
		        	client.closeSocket();
		        }
		        else {
		        	System.out.println("Exiting...");
		        }

		    }
		}));
		

		try {
			client = new TCPClient();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		

		
		JLabel lblMultiThreadResults = new JLabel("Multi-thread Results");
		lblMultiThreadResults.setBounds(1238, 538, 217, 40);
		lblMultiThreadResults.setOpaque(true);
		lblMultiThreadResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblMultiThreadResults.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblMultiThreadResults.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		lblMultiThreadResults.setBackground(Color.LIGHT_GRAY);
		
		
		

		
		

		class Consumers implements Runnable {

			protected BlockingQueue queue = null;

			public Consumers(BlockingQueue queue) {
				this.queue = queue;
			}

			public void run() {
				try {
					String line;
					while((line = (String) queue.take()) != null) {
						//System.out.println("I'm still inside");
						//System.out.println("Consumed: " + line);
						//System.out.println("Consumed: " + queue.take());
						
						EventQueue.invokeLater(new Runnable() {

							public void run() {
								try {
									lblMultiThreadResults.setText("Consumed: " + queue.take());
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						
						Thread.sleep(20);
					}
					System.out.println("I made it out");
					
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		class Producers implements Runnable {

			private BlockingQueue bq = null;

			public Producers(BlockingQueue queue) {
				this.setBlockingQueue(queue);
			}

			public void run() {
				String runPythonFile = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\generatePositionString.py";
				
				try {
					Process p = Runtime.getRuntime().exec(runPythonFile);
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line;
					while ((line = stdInput.readLine()) != null) {
						//System.out.println(line);
						try {
							bq.put(line);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				catch(IOException e1) {
					e1.printStackTrace();
				}
				
				/*
				Random rand = new Random();
				int res = 0;
				try {
					res = Addition(rand.nextInt(100), rand.nextInt(50));
					System.out.println("Produced: " + res);
					bq.put("Hello");
					Thread.sleep(100);
					res = Addition(rand.nextInt(100), rand.nextInt(50));
					System.out.println("Produced: " + res);
					bq.put(res);
					Thread.sleep(100);
					res = Addition(rand.nextInt(100), rand.nextInt(50));
					System.out.println("Produced: " + res);
					bq.put(res);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				*/
			}

			public void setBlockingQueue(BlockingQueue bq) {
				this.bq = bq;
			}

			public int Addition(int x, int y) {
				int result = 0;
				result = x + y;
				return result;
			}

		}
		
		class BlockingQueueExamples implements Runnable{

			String ipAddress;
			BlockingQueue bq = new ArrayBlockingQueue(1000);
			
			BlockingQueueExamples(String address){
				//producer = new Producers(bq);
				//consumer = new Consumers(bq);
				ipAddress = address;
			}
			public void run() {
				//BlockingQueue bq = new ArrayBlockingQueue(1000);
				Producers producer = new Producers(bq);
				Consumers consumer = new Consumers(bq);
				new Thread(producer).start();
				new Thread(consumer).start();

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		/*
		class RotateImage{
			public void rotateImage(double degrees, ImageObserver o) {
				//Image img = new ImageIcon(this.getClass().getResource("/rsz_vehicle.jpg")).getImage();
				ImageIcon icon = new ImageIcon("img/rsz_vehicle.jpg");
				BufferedImage blankCanvas = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
				Graphics2D g2 = (Graphics2D)blankCanvas.getGraphics();
				g2.rotate(Math.toRadians(degrees), icon.getIconWidth() / 2, icon.getIconHeight() / 2);
				g2.drawImage(this.carImg, 0, 0, o);
			}
		}
		*/
		
		JLabel lblConnected = new JLabel("Vehicle Not Connected");
		lblConnected.setBounds(1352, 599, 122, 40);
		lblConnected.setHorizontalAlignment(SwingConstants.CENTER);
		lblConnected.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblConnected.setOpaque(true);
		lblConnected.setBackground(Color.LIGHT_GRAY);
		lblConnected.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		
		JButton btnConnectToVehicle = new JButton("Connect to Vehicle");
		btnConnectToVehicle.setBounds(1225, 602, 117, 35);
		btnConnectToVehicle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnConnectToVehicle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String HOST = "192.168.2.6";
				
				try {
					//TCPClient client = new TCPClient("www.google.com", 80);
					//TCPClient client = new TCPClient();
					connected = client.connect(HOST, 21567);//vehicle is at host 192.168.2.6, port 21567
					//client.sendTestTCPMessage();
					//client.sendTCPMessage("Hello from the client");
					//client.closeSocket();
					lblConnected.setText("Vehicle Connected");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		JButton btnMultiThreadTest = new JButton("Multi-Thread Test");
		btnMultiThreadTest.setBounds(1280, 493, 140, 35);
		btnMultiThreadTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnMultiThreadTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				BlockingQueueExamples testBlockingQueue = new BlockingQueueExamples("192.8.8.8");
				Thread t = new Thread(testBlockingQueue);
				t.start();
			}
		});
		
		JButton btnCenter = new JButton("center");
		btnCenter.setBounds(1400, 716, 74, 21);
		btnCenter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Send the move forward command
				try {
					System.out.println("Sending 'home' command");
					client.sendTCPMessage("home");
				} catch(IOException f) {
					f.printStackTrace();
				}
			}
		});
		
		JSlider slider = new JSlider();
		slider.setBounds(1263, 715, 111, 22);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				String speed = Integer.toString(slider.getValue());
				
				System.out.println("Setting Speed to " + speed);
				try {
					client.sendTCPMessage(speed);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		JButton btnKeyControl = new JButton("Manual Control");
		btnKeyControl.setBounds(1300, 653, 145, 49);
		btnKeyControl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnKeyControl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				int keyCode = arg0.getKeyCode();
				if(keyCode == arg0.VK_LEFT) {
					if(lastCommandSent != "left") {
						System.out.println("left");
						lastCommandSent = "left";
						try {
							client.sendTCPMessage("left");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				if(keyCode == arg0.VK_RIGHT) {
					if(lastCommandSent != "right") {
						System.out.println("right");
						lastCommandSent = "right";
						try {
							client.sendTCPMessage("right");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
				if(keyCode == arg0.VK_UP) {
					if(lastCommandSent != "forward") {
						System.out.println("forward");
						lastCommandSent = "forward";
						try {
							client.sendTCPMessage("forward");
						} catch (IOException e) {
							e.printStackTrace();
						}						
					}
				}
				if(keyCode == arg0.VK_DOWN) {
					if(lastCommandSent != "backward") {
						System.out.println("backward");
						lastCommandSent = "backward";
						try {
							client.sendTCPMessage("backward");
						} catch (IOException e) {
							e.printStackTrace();
						}						
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if(keyCode == e.VK_LEFT || keyCode == e.VK_RIGHT) {
					if(lastCommandSent != "home") {
						System.out.println("home");
						lastCommandSent = "home";
						try {
							client.sendTCPMessage("home");
						} catch (IOException f) {
							f.printStackTrace();
						}						
					}
				}
				if(keyCode == e.VK_UP || keyCode == e.VK_DOWN) {
					if(lastCommandSent!= "stop") {
						System.out.println("stop");
						lastCommandSent = "stop";
						try {
							client.sendTCPMessage("stop");
						} catch (IOException f) {
							f.printStackTrace();
						}
					}
				}
			}
		});
		
		

		/*
	    try {
	        newCarImg = ImageIO.read(new File("E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\img\\rsz_vehicle.jpg"));
	        BufferedImage scaled = new BufferedImage(newCarImg.getWidth() / 2, newCarImg.getHeight() / 2, BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g2d = scaled.createGraphics();
	        g2d.setTransform(AffineTransform.getScaleInstance(0.5d, 0.5d));
	        g2d.drawImage(newCarImg, 0, 0, (ImageObserver) this);
	        g2d.dispose();
	        newCarImg = scaled;
	        setAngle(0d);
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
		*/

		JLabel lblCar = new JLabel("");
		lblCar.setBounds(284, 297, 66, 27);
		//Image carImg = new ImageIcon(this.getClass().getResource("/rsz_vehicle.jpg")).getImage();
		carImg = new ImageIcon(this.getClass().getResource("/rsz_vehicle.jpg")).getImage();
		/*
		try {
			newCarImg = ImageIO.read(new File("img/rsz_vehicle.jpg"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		lblCar.setIcon(new ImageIcon(carImg));
		//lblCar.setIcon(new ImageIcon(newCarImg));
		
		
		
		
		frame.getContentPane().add(btnMultiThreadTest);
		frame.getContentPane().add(btnConnectToVehicle);
		frame.getContentPane().add(lblConnected);
		frame.getContentPane().add(btnKeyControl);
		frame.getContentPane().add(slider);
		frame.getContentPane().add(btnCenter);
		frame.getContentPane().add(lblMultiThreadResults);
		frame.getContentPane().add(lblCar);
		frame.setBounds(20, 20, 1500, 800);
		//frame dimensions
		//left,top,right,bottom
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		width = frame.getWidth();
		height = frame.getHeight();
		dim = new Dimension(width, height);
		//frame.getMinimumSize(dim);
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
			}
		});
		JLabel lblMap = new JLabel("");
		lblMap.setBackground(Color.GRAY);
		lblMap.setBounds(0, 0, 1197, 751);
		Image mapImg = new ImageIcon(this.getClass().getResource("/new_resized_map.jpg")).getImage();
		frame.getContentPane().setLayout(null);
		lblMap.setIcon(new ImageIcon(mapImg));
		frame.getContentPane().add(lblMap);	
		lblMap.setOpaque(true);
		
		JButton btnRotateImageTest = new JButton("Rotate Image Test");
		btnRotateImageTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}
		});
		btnRotateImageTest.setBounds(1280, 413, 140, 35);
		frame.getContentPane().add(btnRotateImageTest);
	}
	public void rotateImage(double degrees, ImageObserver o) {
		ImageIcon icon = new ImageIcon("img/rsz_vehicle.jpg");
		BufferedImage blankCanvas = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics2D g2 = (Graphics2D)blankCanvas.getGraphics();
		g2.rotate(Math.toRadians(degrees), icon.getIconWidth() / 2, icon.getIconHeight() / 2);
		g2.drawImage(this.carImg, 0, 0, o);
		this.carImg = blankCanvas;
	}
	/*
	public void paint(Graphics g) {
		this.setSize();
		if(rectangleImageCar == null) {
			rectangleImageCar = new RectangleImage(getImage("img.rsz_vehicle.jpg"), 25, 50);
		}
		Graphics2D g2 = (Graphics2D)g;
		carImg.draw(g2, this);
		//From this video, has some of this function at 2:33.  There's a RectangeImage that we may need to declare.
		//https://www.youtube.com/watch?v=vxNBSVuNKrc
	}

	private void addKeyListener(AL al) {
		// TODO Auto-generated method stub
		
	}
	*/


	private InputMap getInputMap(int condition) {
		// TODO Auto-generated method stub
		return null;
	}

	private ActionMap getActionMap() {
		// TODO Auto-generated method stub
		return null;
	}
}
