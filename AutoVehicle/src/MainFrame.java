import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;


//import ConcurrentLinkedDequeDemo.*;

import java.util.ArrayList;
import java.util.List;

import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeEvent;
//import Trajectory.*;
import vehicle.*;
import tcpClient.TCPClient;
import decodePosition.*;
import map.*;

public class MainFrame {

	private JFrame frame;
	protected TCPClient client;
	protected boolean connected;
	protected String lastCommandSent;
	private double angle;
	protected Image carImg;//may be deleted when ready to delete the old car
	protected Vehicle vehicle;//vehicle image displayed on the map
	protected boolean keepMovingCar;// used to stop the car from moving (for testing purposes)
	protected boolean continueRunningPythonPrograms; // used to stop running the python programs
	protected JLabel lblMultiThreadResults;
	protected DecodePosition position;
//	protected trajectory myTrajectory;	//list of point pairs, or a single point pair, to draw the trajectory
	protected Map map;
	//List<String> listStrings = new LinkedList<String>();
	List<DecodePosition> positionList;
	protected static ConcurrentLinkedDeque<String> linkedDeque = new ConcurrentLinkedDeque<String>();
	//protected static ConcurrentLinkedDeque<DecodePosition> linkedDeque = new ConcurrentLinkedDeque<DecodePosition>();
	

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
	
	public class LinkedDequeIMU implements Runnable{
		@Override
		public void run() {
			/*
			for(int i=0;i<1000000;i++){
					linkedDeque.add("Thread A: "+i);
					//linkedDeque.add(new DecodePosition("some position from A" + i));
			}
			*/
			
			String runPythonFile = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\generateCirclingCoordinates2.py";
			String runPythonFileArguments = runPythonFile + " 1 2";
			try {
				Process p = Runtime.getRuntime().exec(runPythonFileArguments);
				BufferedReader IMUOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				int count = 0;
				while ((line = IMUOutput.readLine()) != null) {
					//linkedDeque.add(new DecodePosition(line));
					//System.out.println(line);
					linkedDeque.add(line);
					System.out.println("IMU produced: " + count);
					count += 1;
				}
			}
			catch(IOException e1) {
				e1.printStackTrace();
			}
			
		}
	}
	
	class LinkedDequeTDOA implements Runnable{
		@Override
		public void run() {
			/*
			for(int i=0;i<1000000;i++){
					linkedDeque.add("Thread B: "+i);
					//linkedDeque.add(new DecodePosition("some position from B" + i));
			}
			*/
			
			String runPythonFile = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\generateCirclingCoordinates.py";
			String runPythonFileArguments = runPythonFile + " 1 2";
			try {
				Process p = Runtime.getRuntime().exec(runPythonFileArguments);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				int count = 0;
				while ((line = stdInput.readLine()) != null) {
//					linkedDeque.add(new DecodePosition(line));
					linkedDeque.add(line);
					System.out.println("TDOA produced: " + count);
					count = count + 1;
//					System.out.println(line);
				}
			}
			catch(IOException e1) {
				e1.printStackTrace();
			}
			 
		}
	}
	
	class LinkedDequePythonRunner implements Runnable{
		private String IMUExecuter = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\generateCirclingCoordinates.py";
		private String IMUOutput = "";
		private String runTDOA = "python ";
		private String combinerExecuter = "python ";
		private String runTrajectory = "python ";
		private boolean runIMU = false;
		Process IMUProcess;
		Process combinerProcess;
		BufferedReader IMUReader;
		int IMUCount = 0;
		
		public void setRunIMU(boolean myRun) {
			runIMU = myRun;
		}
		
		public void run() {
			System.out.println("running the python runner");
	
			try {
				IMUProcess = Runtime.getRuntime().exec(IMUExecuter);
				IMUReader = new BufferedReader(new InputStreamReader(IMUProcess.getInputStream()));
				String IMULine;
				
				while(continueRunningPythonPrograms == true) {
					if(runIMU == true) {
						IMULine = IMUReader.readLine();
						
						if (IMULine == null) {
							runIMU = false;
							System.out.println("No output detected from IMU.py");
						}
						else {
							IMUCount += 1;
							IMUOutput += IMULine;
							System.out.println(IMULine);
						}
						/*
						if(IMUCount == 20) {
							//start the combiner program, reset count
							IMUCount = 0;
							combinerProcess = Runtime.getRuntime().exec(combinerExecuter);
							BufferedReader combinerInput = new BufferedReader(new InputStreamReader(combinerProcess.getInputStream()));
						}
						*/
					}
				}

			}
			catch(IOException e1) {
				e1.printStackTrace();
			}
			 
		}
	}
	
	class LinkedDequeCombiner implements Runnable{
		@Override
		public void run() {
			for(int i=0;i<2000000;i++){
					String s= linkedDeque.poll();
					System.out.println("Consumed: " + i);
					
					linkedDeque.add("IMU");
					System.out.println(s);
					
					//DecodePosition myposition = linkedDeque.poll();
//					System.out.println("Element received is: "+s);
//					String s = myposition.getTime();
//					if(myposition == null)
//						System.out.println("Received null");
//					else
						//System.out.println("Hello");
//						System.out.println("Element received is: " + s);
			}
		}
	}
	
	public class LinkedDeque{
		void demo() {
			int numThreads = 3;
			ExecutorService exService = Executors.newFixedThreadPool(numThreads);
			LinkedDequeIMU elementAdd = new LinkedDequeIMU();
			LinkedDequeCombiner elementGet = new LinkedDequeCombiner();
			LinkedDequeTDOA elementAdd2 = new LinkedDequeTDOA();
			exService.execute(elementAdd);
			exService.execute(elementAdd2);
			exService.execute(elementGet);
			exService.shutdown();
		}
	}
	public class LinkedDeque2{
		void demo() {
			int numThreads = 1;
			ExecutorService exService = Executors.newFixedThreadPool(numThreads);
			LinkedDequePythonRunner elementAdd = new LinkedDequePythonRunner();
			elementAdd.setRunIMU(true);
//			LinkedDequeCombiner elementGet = new LinkedDequeCombiner();
//			LinkedDequeTDOA elementAdd2 = new LinkedDequeTDOA();
			exService.execute(elementAdd);
//			exService.execute(elementAdd2);
//			exService.execute(elementGet);
			exService.shutdown();
		}
	}

	
	
	public class Consumers implements Runnable {

		protected BlockingQueue queue = null;

		public Consumers(BlockingQueue queue) {
			this.queue = queue;
		}

		public void run() {
			try {
				
				//How to interact with the list of class objects
				/*
				position = new DecodePosition("Hello to the Constructor");
				positionList = new ArrayList<DecodePosition>();
				positionList.add(position);
				DecodePosition testposition = positionList.get(0);
				//System.out.println(testposition.getTime());
				System.out.println(positionList.get(0).getTime());
				*/
				positionList = new ArrayList<DecodePosition>();
				String line;
				while((line = (String) queue.take()) != null) {
					//System.out.println("I'm still inside");
					//System.out.println("Consumed: " + line);
					//System.out.println("Consumed: " + queue.take());
					String singlePosition = (String) queue.take();
					position = new DecodePosition(singlePosition);
					positionList.add(position);
					
					System.out.println("PositionList size: " + positionList.size());
					System.out.println("Queue size: " + queue.size());
					
					/*
					EventQueue.invokeLater(new Runnable() {

						public void run() {
							//lblMultiThreadResults.setText("Consumed: " + position);
							lblMultiThreadResults.setText("In progress...");
							System.out.println("PositionList size: " + positionList.size());
							System.out.println("Queue size: " + queue.size());
							//System.out.println(queue.take());
						}
					});
					
					Thread.sleep(10);
				*/
				}
				System.out.println("I made it out");
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class Producers implements Runnable {

		private BlockingQueue bq = null;

		public Producers(BlockingQueue queue) {
			this.setBlockingQueue(queue);
		}

		public void run() {
			String runPythonFile = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\generateCirclingCoordinates.py";
			String runPythonFileArguments = runPythonFile + " 1 2";
			try {
				Process p = Runtime.getRuntime().exec(runPythonFileArguments);
				BufferedReader IMUOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = IMUOutput.readLine()) != null) {
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
	
	public class BlockingQueueExamples implements Runnable{

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
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
	
	class DriveCar implements Runnable {
		private int xCurrent;
		private int yCurrent;
		
		public void run() {
			int count = 0;
			int xStart = vehicle.getX();
			int yStart = vehicle.getY();
			
			while(keepMovingCar == true) {
				
				xCurrent = vehicle.getX();
				yCurrent = vehicle.getY();
				int xChange = (int) Math.round(4 * Math.cos(count / 40.0));
				int yChange = (int) Math.round(4 * Math.cos(count / 30.0));
				int xNext = xCurrent + xChange;
				int yNext = yCurrent + yChange;
				
				double newRise = yNext - yCurrent;
				double newRun = xNext - xCurrent;
				
				double newSlope;
				double newHeadingInDegrees;
				if (newRise > 0 && newRun == 0.0) {//facing straight up
					newHeadingInDegrees = 90;
				}
				else if (newRise < 0 && newRun == 0.0) {//facing straight down
					newHeadingInDegrees = 270;
				}
				else if(newRise == 0.0 && newRun < 0.0) {//facing left
					newHeadingInDegrees = 180;
				}
				else if(newRise == 0.0 && newRun > 0.0) {//facing right
					newHeadingInDegrees = 0;
				}
				else if(newRise < 0.0 && newRun < 0.0) {//We're in the 2nd quadrant. Flip the car 180 degrees
					newSlope = newRise / newRun;
					newHeadingInDegrees= Math.toDegrees(Math.atan(newSlope)) + 180;
				}
				else if(newRise > 0.0 && newRun < 0.0) {//We're in the 3rd quadrant. Flip the car 180 degrees
					newSlope = newRise / newRun;
					newHeadingInDegrees= Math.toDegrees(Math.atan(newSlope)) + 180;
				}
				else{//We're facing the 1st or 4th quadrant
					newSlope = newRise / newRun;
					newHeadingInDegrees= Math.toDegrees(Math.atan(newSlope));
				}
				
				/*
				System.out.println("newHeadingInDegrees: " + newHeadingInDegrees);
				System.out.println("yChange: " + yChange);
				System.out.println("xChange: " + xChange);
				System.out.println("newRise: " + newRise);
				System.out.println("newRun: " + newRun);
				*/
				
				vehicle.setTheta(newHeadingInDegrees);
				xCurrent = xNext;
				yCurrent = yNext;
				
				count = count + 1;
				if(count >= 100000) {
					count = 0;
				}
				EventQueue.invokeLater(new Runnable() {

					public void run() {

						
						vehicle.setBounds(xCurrent, yCurrent, 70, 70);
						vehicle.repaint();
					}
				});
			
				try {
					Thread.sleep(60);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		}
	}
	
	/*
	 * Class: trajectory
	 * Requirements:
	 * 		* Consume points from a Deque
	 * 		* draw a line between those points
	 * 		* remove lines between points when the points have been reached or when they are no longer in the Deque
	 */
	class manageTrajectory implements Runnable{
		
		public void run() {
			//do thread things
//			myTrajectory.setPoint1(1280, 100);
//			myTrajectory.setPoint2(1500, 400);
			
			
			EventQueue.invokeLater(new Runnable() {

				public void run() {

					
					//vehicle.setBounds(xCurrent, yCurrent, 70, 70);
					//vehicle.repaint();
					
//					myTrajectory.repaint(); this line was just removed
				}
			});
		
			try {
				Thread.sleep(60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Create the application.
	 */
	public MainFrame() {
	
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */	
	private void initialize() {
		
	    int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
	    InputMap inputMap = getInputMap(condition);
	    
	    for (Direction direction : Direction.values()) {
	    	//inputMap.put(direction.getKeyStroke(), direction.getText());
	        //actionMap.put(direction.getText(), new MyArrowBinding(direction.getText()));
	    }
		
		frame = new JFrame("Autonomous Vehicle Controller");
		frame.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 12));
		frame.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().setBackground(new Color(245, 245, 245));
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		    	//If we connected to the tcp server, close the socket
		    	if (connected == true) {
		        	System.out.println("Closing socket");
		        	//client.closeSocket();
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
		
		
		//JLabel lblMultiThreadResults = new JLabel("Multi-thread Results");
		lblMultiThreadResults = new JLabel("Multi-thread Results");
		lblMultiThreadResults.setBounds(1238, 538, 217, 40);
		lblMultiThreadResults.setOpaque(true);
		lblMultiThreadResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblMultiThreadResults.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblMultiThreadResults.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		lblMultiThreadResults.setBackground(Color.LIGHT_GRAY);
		
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
				
				//How to interact with the list of class objects
				/*
				position = new DecodePosition("Hello to the Constructor");
				positionList = new ArrayList<DecodePosition>();
				positionList.add(position);
				DecodePosition testposition = positionList.get(0);
				//System.out.println(testposition.getTime());
				System.out.println(positionList.get(0).getTime());
				*/
				
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
		
		vehicle = new Vehicle();
	    vehicle.setTheta(0.00);
	    vehicle.setBounds(394, 332, 70, 70);
	    vehicle.setBackground(Color.white);
	    angle = 0.0;//start with the vehicle facing north
	    

		//JLabel lblCar = new JLabel("");
		//lblCar.setBounds(273, 352, 66, 27);
		//Image carImg = new ImageIcon(this.getClass().getResource("/rsz_vehicle.jpg")).getImage();
		//carImg = new ImageIcon(this.getClass().getResource("/rsz_vehicle.jpg")).getImage();
		//lblCar.setIcon(new ImageIcon(carImg));
		
	    //JTextField textField = new JTextField(TEXT_FIELD_TEXT);
	    
	    /*
	    myTrajectory = new trajectory();
	    myTrajectory.setBounds(374, 332, 70, 70);
	    myTrajectory.setBackground(Color.white);
	    */
	    map = new Map();
	    map.setBounds(0, 0, 1197, 751);
	    
//		manageTrajectory trajectoryManagementThread = new manageTrajectory();
//		Thread t = new Thread(trajectoryManagementThread);
//		t.start();
	    
	    
	    
		frame.getContentPane().add(btnMultiThreadTest);
		frame.getContentPane().add(btnConnectToVehicle);
		frame.getContentPane().add(lblConnected);
		frame.getContentPane().add(btnKeyControl);
		frame.getContentPane().add(slider);
		frame.getContentPane().add(btnCenter);
		frame.getContentPane().add(lblMultiThreadResults);
		frame.getContentPane().add(vehicle);
		frame.getContentPane().add(map);	
//		frame.getContentPane().add(myTrajectory);
		frame.setBounds(20, 20, 1500, 800);
		//frame dimensions
		//left,top,right,bottom
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnTrajectoryTest = new JButton("Remove Trajectory");
		btnTrajectoryTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
	    
	    frame.getContentPane().setLayout(null);
		
		
		/*
		JLabel lblMap = new JLabel("");
		lblMap.setBackground(Color.GRAY);
		lblMap.setBounds(0, 0, 1197, 751);
		Image mapImg = new ImageIcon(this.getClass().getResource("/Scaled Test Map.jpg")).getImage();
		frame.getContentPane().setLayout(null);
		lblMap.setIcon(new ImageIcon(mapImg));
		frame.getContentPane().add(lblMap);	
		lblMap.setOpaque(true);
		*/
		
		vehicle.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseEntered(MouseEvent e) {
	        	//myposition + half the size of the vehicle width * (the total width of the map / the width of the map image)
	        	
	        	double scaledX = (vehicle.getX() + 35) * (10.0 / (double)map.getWidth());
	        	double scaledY = (vehicle.getY() + 35) * (10.0 / (double)map.getHeight());
	        	vehicle.setToolTipText("(" + scaledX + ", " + scaledY + ")");
	        }

	        @Override
	        public void mouseExited(MouseEvent e) {
	            //Set text of another component
	            //textField.setText(TEXT_FIELD_TEXT);
	        }
	    });
		
		
		JButton btnRotateImageTest = new JButton("Rotate Image Test");
		btnRotateImageTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				angle = angle + 0.1;
				vehicle.setTheta(angle);
				//frame.repaint(); //This works!
				vehicle.repaint();//This works as long as Vehicle is above it
			}
		});
		btnRotateImageTest.setBounds(1280, 448, 140, 35);
		frame.getContentPane().add(btnRotateImageTest);
		
		JButton btnMoveCarTest = new JButton("Move Car Test");
		btnMoveCarTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {				
				keepMovingCar = true;
				DriveCar car = new DriveCar();
				Thread t = new Thread(car);
				t.start();
			}
		});
		btnMoveCarTest.setBounds(1225, 403, 117, 35);
		frame.getContentPane().add(btnMoveCarTest);
		
		JButton btnLinkeddequeTest = new JButton("LinkedDeque Test");
		btnLinkeddequeTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				LinkedDeque deque = new LinkedDeque();
				deque.demo();
			}
		});
		btnLinkeddequeTest.setBounds(1280, 358, 140, 35);
		frame.getContentPane().add(btnLinkeddequeTest);
		
		JButton btnStopMovingCar = new JButton("Stop Moving Car");
		btnStopMovingCar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				keepMovingCar = false;
			}
		});
		btnStopMovingCar.setBounds(1357, 403, 117, 35);
		frame.getContentPane().add(btnStopMovingCar);
		
		btnTrajectoryTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				map.remove(true);
//				vehicle.repaint();
				map.repaint();
				vehicle.repaint();
//				frame.repaint();
				
//				manageTrajectory trajectoryManagementThread = new manageTrajectory();
//				Thread t = new Thread(trajectoryManagementThread);
//				t.start();
			}
		});
		btnTrajectoryTest.setBounds(1280, 313, 140, 35);
		frame.getContentPane().add(btnTrajectoryTest);
		
		JButton btnRunPythonScripts = new JButton("Run Python Scripts");
		btnRunPythonScripts.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				continueRunningPythonPrograms = true;
				LinkedDeque2 deque = new LinkedDeque2();
				deque.demo();
			}
		});
		btnRunPythonScripts.setBounds(1280, 268, 140, 35);
		frame.getContentPane().add(btnRunPythonScripts);
	}

	private InputMap getInputMap(int condition) {
		// TODO Auto-generated method stub
		return null;
	}

	private ActionMap getActionMap() {
		// TODO Auto-generated method stub
		return null;
	}
}
