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
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeEvent;
//import Trajectory.*;
import vehicle.*;
import tcpClient.TCPClient;
import decodePosition.*;
import map.*;
import serverSocket.SocketServer;

public class MainFrame {

	private JFrame frame;
	protected TCPClient vehicleTCPClient;//vehicle directional commands
	protected TCPClient backEndTCPClient;//client to receive messages from the back end
	protected boolean connected;
	protected String lastCommandSent; //last command sent to the vehicle
	protected String currentDriveState = "Stop"; //keeps track of the 8 possible drive states
	protected double tireRadius = 0.12954; //tire radius (in meters)
	protected double fullSpeedRPM = 165;
	protected int SPEEDSAFETYLIMIT = 40;
	protected int DEGREESPERSECONDSAFETYLIMIT = 30;
	protected int MAXWHEELDIFFERENTIAL = 100 - SPEEDSAFETYLIMIT;
	protected double DEGREESPERSECOND = 45;
	protected double vehicleWidth = 0.74;
//	protected double r2 = wheelSpeedDifferential + vehicleWidth; //outer circle radius for outer wheel
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
	protected JLabel lblLinkedDequeResults = new JLabel("Linked Deque Results");
	protected String OSName = System.getProperty("os.name");
	protected String runPrintOne;//runs by absolute file path (different for Windows/Linux)
	protected String runGenerateCirclingCoordinates2 = "python generateCirclingCoordinates2.py";
	protected String runGenerateCirclingCoordinates;
	protected String runCoordinator;
	protected ServerSock serverSock = new ServerSock();//connects to the back-end Coordinator
	protected Thread backEndServerThread = new Thread(serverSock);//thread to run the back-end Coordinator
	protected ClientSock clientSock = new ClientSock();//connects to the back-end coordinator as the client
	protected Thread backEndClientThread = new Thread(clientSock);//thread to connect to the backend coordinator as a client
	protected Socket backEndClient = null;
	protected boolean connectedToBackEnd = false;
	protected Process backEndProcess;//process to run the backend coordinator with tcp localhost
	protected String messageToServer = "";//allows the "Calibrate IMU" command to be sent to the backend
	protected boolean sendMessageToServer = false;
	private final Set<Integer> pressed = new HashSet<Integer>();//set of current directional commands
	
	

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
			
			//String runPythonFile = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\generateCirclingCoordinates2.py";
//			runGenerateCirclingCoordinates2 = "python generateCirclingCoordinates2.py";
			String runPythonFileArguments = runGenerateCirclingCoordinates2 + " 1 2";
			System.out.println("generateCirclingCoordinates2.py is in...");
			System.out.println(System.getProperty("user.dir"));
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
					/*
					lblLinkedDequeResults.setText("IMU running");
					lblLinkedDequeResults.repaint();
					 EventQueue.invokeLater(new Runnable() {

						public void run() {
							//lblMultiThreadResults.setText("Consumed: " + position);
							lblLinkedDequeResults.setText("IMU running");
							lblLinkedDequeResults.repaint();
							
							//System.out.println("PositionList size: " + positionList.size());
							//System.out.println("Queue size: " + queue.size());
							//System.out.println(queue.take());
						}
					});
					
					Thread.sleep(10);
					*/ 
				}
			}
			catch(IOException e1) {
				e1.printStackTrace();
			} 
			/*
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
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
			
			runGenerateCirclingCoordinates = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\generateCirclingCoordinates.py";
			if (OSName.equalsIgnoreCase("Windows 10")) {
//				runPrintOne = "python printOne.py";
				runGenerateCirclingCoordinates = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\generateCirclingCoordinates.py";
			}
			else
				runGenerateCirclingCoordinates = "python generateCirclingCoordinates.py";
			
			
			String runPythonFileArguments = runGenerateCirclingCoordinates + " 1 2";
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
//			String runGenerateCirclingCoordinates = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\generateCirclingCoordinates.py";

			if (OSName.equalsIgnoreCase("Windows 10")) {
//				runPrintOne = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\printOne.py";
//				runPrintOne = "python printOne.py";	
				runGenerateCirclingCoordinates = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\generateCirclingCoordinates.py";
			}
			else
				runGenerateCirclingCoordinates = "python generateCirclingCoordinates.py";
			
			
			
			String runPythonFileArguments = runGenerateCirclingCoordinates + " 1 2";
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
	
	class ClientSock implements Runnable{
		
		public void run() {
			try {
				System.out.println("Attempting to connect to server on port 21564");
				backEndTCPClient.connect("localhost", 21564);
				System.out.println("Server accepted connection on port 21564");
			} catch (IOException e) {
				System.out.println("Server did not accept connection");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		
			
			while(true) {

				if(sendMessageToServer) {
					try {
						System.out.println("Attempting to send " + messageToServer + " to the server...");
						backEndTCPClient.sendTCPMessage(messageToServer);
						sendMessageToServer = false;
						System.out.println("Sent " + messageToServer + " to the server.");
					} catch (IOException e) {
						System.out.println("Failed to send " + messageToServer + " to the server.");
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			
		}
	}
	
	class ServerSock implements Runnable{
		//static ServerSocket variable
		private int port = 9875;
		private ServerSocket server = null;
		//socket server port on which it will listen
		BufferedReader in = null;//input stream from client
		PrintWriter out = null;//output stream to client
		int xCurrent, yCurrent, heading;//current x and y coordinates of the vehicle
		
		boolean keepRunningBackEnd = true;
		String fromClient = null;
		String toClient;
		
		Socket backEndClient = null;
		
		public void closeSocket() {
			try {
				System.out.println("Closing server socket");
				server.close();
				System.out.println("Server socket closed successfully");
			} catch (IOException e) {
				System.out.println("Failed to close server socket");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void connect() {
			System.out.println(" == Java Server == ");

			try {
				server = new ServerSocket(port);
			} catch (IOException e1) {
				System.out.println("Failed to create socket.");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			System.out.println("wait for connection on port " + Integer.toString(port));

		
			try {
				System.out.println("Attempting to accept connection");
				backEndClient = server.accept();
				System.out.println("Accepted connection");
			} catch (IOException e) {
				System.out.println("Failed to accept connection");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connectedToBackEnd = true;
			System.out.println("got connection on port " + Integer.toString(port));
			
//			BufferedReader in = null;
			try {
				//Initialize the input stream from the client
				in = new BufferedReader(new InputStreamReader(backEndClient.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			PrintWriter out = null;
			try {
				//Initialize the output stream from the client
				out = new PrintWriter(backEndClient.getOutputStream(),true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void updatePosition() {
			vehicle.setTheta(heading);

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
		
		public void interpretMessage() {
			String[] splitMessage = fromClient.split(":");
			
			if(splitMessage[0].equals("position")) {
				xCurrent = Integer.parseInt(splitMessage[1]);
				yCurrent = Integer.parseInt(splitMessage[2]);
				heading = Integer.parseInt(splitMessage[3]);
				xCurrent = map.getWidth() * xCurrent / map.getScaledMapWidth() + map.getShiftedMapWidth();
				yCurrent = map.getHeight() * yCurrent / map.getScaledMapHeight() + map.getShiftedMapHeight();
				updatePosition();
			}
			if(splitMessage[0].equals("trajectory")) {
				
			}
		}
		
		public void run(){
			connect();
	     
			while(keepRunningBackEnd) {

				try {
					//read a message from the client
					fromClient = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("received: " + fromClient);            
				String msg = "got it";
				
				interpretMessage();//interpret the message sent from the back-end
				
//				if(calibrateIMU == true) {
//					out.println("Clear IMU");
//					calibrateIMU = false;
//				}
				
				
				//send a message to the client
				out.println(msg);//Send string to client	

				if(fromClient.equals("exit")) {
					keepRunningBackEnd = false;
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
	
	class RunAll implements Runnable{
		public void run() {
			if (OSName.equalsIgnoreCase("Windows 10")) {
				runCoordinator = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\coordinator.py";	
			}
			else
				runCoordinator = "python coordinator.py";
			
			try {
				Process p = Runtime.getRuntime().exec(runCoordinator);
				BufferedReader CoordinatorOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = CoordinatorOutput.readLine();
				System.out.println(line);

			}
			catch(IOException e1) {
				e1.printStackTrace();
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
		        	System.out.println("Closing vehicle socket");
		        	vehicleTCPClient.closeSocket();
		        }
		        else {
		        	System.out.println("Exiting...");
		        }
		    	if(connectedToBackEnd == true) {
		    		System.out.println("Closing back-end socket");
		    		try {
						backEndClient.close();
						backEndTCPClient.closeSocket();
						
					} catch (IOException e) {
						System.out.println("Failed to close back-end socket");
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	
		    	}

		    }
		}));
		

		try {
			vehicleTCPClient = new TCPClient();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			backEndTCPClient = new TCPClient();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		//JLabel lblMultiThreadResults = new JLabel("Multi-thread Results");
		lblMultiThreadResults = new JLabel("Multi-thread Results");
		lblMultiThreadResults.setBounds(1238, 380, 217, 40);
		lblMultiThreadResults.setOpaque(true);
		lblMultiThreadResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblMultiThreadResults.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblMultiThreadResults.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		lblMultiThreadResults.setBackground(Color.LIGHT_GRAY);
		
		JLabel lblConnected = new JLabel("Vehicle Not Connected");
		lblConnected.setBounds(1340, 430, 122, 40);
		lblConnected.setHorizontalAlignment(SwingConstants.CENTER);
		lblConnected.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblConnected.setOpaque(true);
		lblConnected.setBackground(Color.LIGHT_GRAY);
		lblConnected.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		
		JButton btnConnectToVehicle = new JButton("Connect to Vehicle");
		btnConnectToVehicle.setBounds(1165, 433, 165, 35);
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
					connected = vehicleTCPClient.connect(HOST, 21567);//vehicle is at host 192.168.2.6, port 21567
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
		btnMultiThreadTest.setBounds(1280, 335, 140, 35);
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
		btnCenter.setBounds(1381, 566, 74, 21);
		btnCenter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Send the move forward command
				try {
					System.out.println("Sending 'home' command");
					vehicleTCPClient.sendTCPMessage("home");
				} catch(IOException f) {
					f.printStackTrace();
				}
			}
		});
		
		JSlider turningDegreesSlider = new JSlider();
		turningDegreesSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				System.out.println("Setting wheelSpeedDifferential to " + turningDegreesSlider.getValue());
			}
		});
		turningDegreesSlider.setBounds(1213, 566, 111, 22);
		turningDegreesSlider.setValue(20);
		frame.getContentPane().add(turningDegreesSlider);
//		degreesPerSecondSlider.setMaximum(DEGREESPERSECONDSAFETYLIMIT);
//		degreesPerSecondSlider.setValue(DEGREESPERSECONDSAFETYLIMIT/2);
		turningDegreesSlider.setMaximum(MAXWHEELDIFFERENTIAL);
		turningDegreesSlider.setValue(MAXWHEELDIFFERENTIAL/2);
		
		JSlider speedSlider = new JSlider();
		speedSlider.setBounds(1084, 566, 111, 22);
//		speedSlider.setValue(SPEEDSAFETYLIMIT);
		speedSlider.setMaximum(SPEEDSAFETYLIMIT);
		speedSlider.setValue(SPEEDSAFETYLIMIT/2);
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				String speed = Integer.toString(speedSlider.getValue());
				
				System.out.println("Setting Speed to " + speed);
//				try {
//					vehicleTCPClient.sendTCPMessage(speed);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}
		});
		
		JButton btnKeyControl = new JButton("Manual Control");
		btnKeyControl.setBounds(1317, 489, 145, 49);
		btnKeyControl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnKeyControl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				

//				Format for the commands:
//					F/R Space Speed_1 Speed_2 Speed_3 Space F/R Space Speed_1 Speed_2 Speed_3
				
//				Formula to change degrees / second to speed of motor 0 - 100
//				x (degrees / 1 second) * (2 * pi * wheelSpeedDifferential / 360 degrees) * (1 tire revolution / 2 * pi * tireRadius) * (60 sec / 1 min) * 100/165
				int wheelSpeedDifferential = turningDegreesSlider.getValue();
				
				int keyCode = arg0.getKeyCode();
	
				if(keyCode == arg0.VK_LEFT) {					
					
					if(lastCommandSent != "left") {
						System.out.println("left");
						lastCommandSent = "left";							
						try {
							vehicleTCPClient.sendTCPMessage("left");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				if(keyCode == arg0.VK_RIGHT) {
					if(lastCommandSent != "right") {
						System.out.println("right");
						lastCommandSent = "right";
						try {
							vehicleTCPClient.sendTCPMessage("right");
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
							vehicleTCPClient.sendTCPMessage("forward");
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
							vehicleTCPClient.sendTCPMessage("backward");
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
							vehicleTCPClient.sendTCPMessage("home");
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
							vehicleTCPClient.sendTCPMessage("stop");
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
	    //map.setBounds(0, 0, 1197, 751);
	    map.setBounds(0, 0, 1047, 648);
	    
//		manageTrajectory trajectoryManagementThread = new manageTrajectory();
//		Thread t = new Thread(trajectoryManagementThread);
//		t.start();
	    
	    
	    
		frame.getContentPane().add(btnMultiThreadTest);
		frame.getContentPane().add(btnConnectToVehicle);
		frame.getContentPane().add(lblConnected);
		frame.getContentPane().add(btnKeyControl);
		frame.getContentPane().add(speedSlider);
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
		btnRotateImageTest.setBounds(1280, 290, 140, 35);
		frame.getContentPane().add(btnRotateImageTest);
		
		JButton btnMoveCarTest = new JButton("Move Car Test");
		btnMoveCarTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnMoveCarTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {				
				keepMovingCar = true;
				DriveCar car = new DriveCar();
				Thread t = new Thread(car);
				t.start();
			}
		});
		btnMoveCarTest.setBounds(1214, 245, 117, 35);
		frame.getContentPane().add(btnMoveCarTest);
		
		JButton btnLinkeddequeTest = new JButton("LinkedDeque Test");
		btnLinkeddequeTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				LinkedDeque deque = new LinkedDeque();
				deque.demo();
			}
		});
		btnLinkeddequeTest.setBounds(1165, 190, 140, 35);
		frame.getContentPane().add(btnLinkeddequeTest);
		
		JButton btnStopMovingCar = new JButton("Stop Moving Car");
		btnStopMovingCar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				keepMovingCar = false;
			}
		});
		btnStopMovingCar.setBounds(1352, 245, 117, 35);
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
		btnTrajectoryTest.setBounds(1261, 145, 140, 35);
		frame.getContentPane().add(btnTrajectoryTest);
		
		JButton btnRunPythonScripts = new JButton("Run Python Scripts");
		btnRunPythonScripts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnRunPythonScripts.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				continueRunningPythonPrograms = true;
				LinkedDeque2 deque = new LinkedDeque2();
				deque.demo();
			}
		});
		btnRunPythonScripts.setBounds(1104, 145, 140, 35);
		frame.getContentPane().add(btnRunPythonScripts);
		
		
		JLabel lblSuccessfailure = new JLabel("Success/Failure");
		lblSuccessfailure.setOpaque(true);
		lblSuccessfailure.setHorizontalAlignment(SwingConstants.CENTER);
		lblSuccessfailure.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblSuccessfailure.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		lblSuccessfailure.setBackground(Color.LIGHT_GRAY);
		lblSuccessfailure.setBounds(1295, 100, 156, 35);
		frame.getContentPane().add(lblSuccessfailure);
		
		
		//JLabel lblLinkedDequeResults = new JLabel("Linked Deque Results");
		lblLinkedDequeResults.setOpaque(true);
		lblLinkedDequeResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblLinkedDequeResults.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblLinkedDequeResults.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		lblLinkedDequeResults.setBackground(Color.LIGHT_GRAY);
		lblLinkedDequeResults.setBounds(1318, 190, 156, 35);
		frame.getContentPane().add(lblLinkedDequeResults);
		
		JButton btnTestPythonForLinuxEnvironment = new JButton("Test Python for Linux Environment");
		btnTestPythonForLinuxEnvironment.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (OSName.equalsIgnoreCase("Windows 10")) {
					runPrintOne = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\printOne.py";
//					runPrintOne = "python printOne.py";	
				}
				else
					runPrintOne = "python printOne.py";
				
				System.out.println(OSName);
//				
				System.out.println("printOne.py is in...");
				System.out.println(System.getProperty("user.dir"));
				try {
					Process p = Runtime.getRuntime().exec(runPrintOne);
					BufferedReader IMUOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line = IMUOutput.readLine();
					System.out.println(line);
					String expectedOutput = "1";
					System.out.println(expectedOutput);
					
					if(line.equals("1"))
						lblSuccessfailure.setText("Success");
					else
						lblSuccessfailure.setText("Failure");
				}
				catch(IOException e1) {
					e1.printStackTrace();
				} 

			}
		});
		btnTestPythonForLinuxEnvironment.setBounds(1096, 100, 189, 35);
		frame.getContentPane().add(btnTestPythonForLinuxEnvironment);
		
		JButton btnTestLocalSocket = new JButton("Test Local Socket to Python");
		btnTestLocalSocket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnTestLocalSocket.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				runCoordinator = "python combiner.py >> combinerLog.txt";
				if (OSName.equalsIgnoreCase("Windows 10")) {
					runCoordinator = "python E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\combiner.py >> E:\\Workstation\\eclipse-workspace\\AutoVehicleGUI\\AutoVehicle\\src\\combinerLog.txt";
				}
				else
					runCoordinator = "python combiner.py";

				
				try {
					backEndProcess = Runtime.getRuntime().exec(runCoordinator);
				} catch (IOException e) {
					System.out.println("Failed to created back-end process");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				backEndServerThread.start();
				backEndClientThread.start();
				
//				try {
//					System.out.println("Attempting to connect to server on port 21564");
//					backEndTCPClient.connect("localhost", 21564);
//					System.out.println("Server accepted connection on port 21564");
//				} catch (IOException e) {
//					System.out.println("Server did not accept connection");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

				
//				SocketServer backEndSocket = new SocketServer();
//				try {
//					backEndSocket.connectAttempt2();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		});
		btnTestLocalSocket.setBounds(1273, 10, 189, 35);
		frame.getContentPane().add(btnTestLocalSocket);
		
		JButton btnNewVehicleManual = new JButton("New Vehicle Manual Control");
		btnNewVehicleManual.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewVehicleManual.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {

				int keyCode = arg0.getKeyCode();
				pressed.add(keyCode);
				String leftWheelSpeed = Integer.toString(speedSlider.getValue());
				String rightWheelSpeed = Integer.toString(speedSlider.getValue());
				String leftWheelDirection = "";
				String rightWheelDirection = "";
				int speedGain = speedSlider.getValue();
				int wheelSpeedDifferential = turningDegreesSlider.getValue();

//				if(pressed.contains(arg0.VK_UP))
//					System.out.println("up is being pressed.");
				
				if(pressed.size() == 1) {//forward,left,right, or backward
					if(pressed.contains(arg0.VK_UP)) {
						if(!currentDriveState.equals("F")) {
							currentDriveState = "F";
							System.out.println("Drive forward");
							leftWheelDirection = "F ";
							rightWheelDirection = " F ";
						}
					}
					else if(pressed.contains(arg0.VK_DOWN)) {
						if(!currentDriveState.equals("B")) {
							currentDriveState = "B";
							System.out.println("Drive backward");
							leftWheelDirection = "B ";
							rightWheelDirection = " B ";
						}
					}
					else if(pressed.contains(arg0.VK_LEFT)) {
						if(!currentDriveState.equals("L")) {
							currentDriveState = "L";
							System.out.println("Drive left");
							leftWheelSpeed = rightWheelSpeed = Integer.toString(wheelSpeedDifferential);
							leftWheelDirection = "B ";
							rightWheelDirection = " F ";
						}
					}
					else if(pressed.contains(arg0.VK_RIGHT)) {
						if(!currentDriveState.equals("R")) {
							currentDriveState = "R";
							System.out.println("Drive right");
							leftWheelSpeed = rightWheelSpeed = Integer.toString(wheelSpeedDifferential);
							leftWheelDirection = "F ";
							rightWheelDirection = " B ";
						}
					}
					else {
							currentDriveState = "Stop";
							System.out.println("Stop");
							leftWheelSpeed = rightWheelSpeed = "000";
							leftWheelDirection = "F ";
							rightWheelDirection = " F ";
					}
				}
				if(pressed.size() == 2) {//fwd-left, bckwd-left, fwd-right, bckwd-right
					if(pressed.contains(arg0.VK_UP) && pressed.contains(arg0.VK_RIGHT)) {//drive forward and right
						if(!currentDriveState.equals("FR")) {
							currentDriveState = "FR";
							System.out.println("Drive forward and right");
							leftWheelSpeed = Integer.toString((speedGain + wheelSpeedDifferential));
							leftWheelDirection = "F ";
							rightWheelDirection = " F ";
						}
					}
					else if(pressed.contains(arg0.VK_UP) && pressed.contains(arg0.VK_LEFT)) {//drive forward and left
						if(!currentDriveState.equals("FL")) {
							currentDriveState = "FL";
							System.out.println("Drive forward and left");
							rightWheelSpeed = Integer.toString((speedGain + wheelSpeedDifferential));
							leftWheelDirection = "F ";
							rightWheelDirection = " F ";
						}
					}
					else if(pressed.contains(arg0.VK_DOWN) && pressed.contains(arg0.VK_RIGHT)) {//drive backward and right
						if(!currentDriveState.equals("BR")) {
							currentDriveState = "BR";
							System.out.println("Drive backward and right");
							leftWheelSpeed = Integer.toString((speedGain + wheelSpeedDifferential));
							leftWheelDirection = "B ";
							rightWheelDirection = " B ";
						}
					}
					else if(pressed.contains(arg0.VK_DOWN) && pressed.contains(arg0.VK_LEFT)) {//drive backward and left
						if(!currentDriveState.equals("BL")) {
							currentDriveState = "BL";
							System.out.println("Drive backward and left");
							rightWheelSpeed = Integer.toString((speedGain + wheelSpeedDifferential));
							leftWheelDirection = "B ";
							rightWheelDirection = " B ";
						}
					}
					else {
						currentDriveState = "Stop";
						leftWheelSpeed = rightWheelSpeed = "000";
						leftWheelSpeed = rightWheelSpeed = "000";
						System.out.println("Stop");
						leftWheelDirection = "F ";
						rightWheelDirection = " F ";
					}
						
				}
				
				if(leftWheelDirection.length() > 0) {
					if(leftWheelSpeed.length() == 1)
						leftWheelSpeed = "00" + leftWheelSpeed;
					if(leftWheelSpeed.length() == 2)
						leftWheelSpeed = "0" + leftWheelSpeed;
					if(rightWheelSpeed.length() == 1)
						rightWheelSpeed = "00" + rightWheelSpeed;
					if(rightWheelSpeed.length() == 2)
						rightWheelSpeed = "0" + rightWheelSpeed;
					try {
						System.out.println("Sending command to vehicle: " + leftWheelDirection + leftWheelSpeed + rightWheelDirection + rightWheelSpeed);
						vehicleTCPClient.sendTCPMessage(leftWheelDirection + leftWheelSpeed + rightWheelDirection + rightWheelSpeed);
						System.out.println("Command sent successfully");
					} catch (IOException e) {
						System.out.println("Command failed to send");
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				
				int keyCode = arg0.getKeyCode();
				pressed.remove(keyCode);
				String leftWheelSpeed = Integer.toString(speedSlider.getValue());
				String rightWheelSpeed = Integer.toString(speedSlider.getValue());
				String leftWheelDirection = "";
				String rightWheelDirection = "";
				int speedGain = speedSlider.getValue();
				int wheelSpeedDifferential = turningDegreesSlider.getValue();
				
				if(pressed.size() == 1) {//fwd, bckwd, left, right
					if(pressed.contains(arg0.VK_UP)) {
						if(!currentDriveState.equals("F")) {
							currentDriveState = "F";
							System.out.println("Drive forward");
							leftWheelDirection = "F ";
							rightWheelDirection = " F ";
						}
					}
					else if(pressed.contains(arg0.VK_DOWN)) {
						if(!currentDriveState.equals("B")) {
							currentDriveState = "B";
							System.out.println("Drive backward");
							leftWheelDirection = "B ";
							rightWheelDirection = " B ";
						}
					}
					else if(pressed.contains(arg0.VK_LEFT)) {
						if(!currentDriveState.equals("L")) {
							currentDriveState = "L";
							System.out.println("Drive left");
							leftWheelSpeed = rightWheelSpeed = Integer.toString(wheelSpeedDifferential);
							leftWheelDirection = "B ";
							rightWheelDirection = " F ";
						}
					}
					else if(pressed.contains(arg0.VK_RIGHT)) {
						if(!currentDriveState.equals("R")) {
							currentDriveState = "R";
							System.out.println("Drive right");
							leftWheelSpeed = rightWheelSpeed = Integer.toString(wheelSpeedDifferential);
							leftWheelDirection = "F ";
							rightWheelDirection = " B ";
						}
					}
					else {
							currentDriveState = "Stop";
							System.out.println("Stop");
							leftWheelSpeed = rightWheelSpeed = "000";
							leftWheelDirection = "F ";
							rightWheelDirection = " F ";
					}

				}
				else {
					currentDriveState = "Stop";
					System.out.println("Stop");
					leftWheelSpeed = rightWheelSpeed = "000";
					leftWheelDirection = "F ";
					rightWheelDirection = " F ";
				}
				
				if(leftWheelDirection.length() > 0) {
					if(leftWheelSpeed.length() == 1)
						leftWheelSpeed = "00" + leftWheelSpeed;
					if(leftWheelSpeed.length() == 2)
						leftWheelSpeed = "0" + leftWheelSpeed;
					if(rightWheelSpeed.length() == 1)
						rightWheelSpeed = "00" + rightWheelSpeed;
					if(rightWheelSpeed.length() == 2)
						rightWheelSpeed = "0" + rightWheelSpeed;
					try {
						System.out.println("Sending command to vehicle: " + leftWheelDirection + leftWheelSpeed + rightWheelDirection + rightWheelSpeed);
						vehicleTCPClient.sendTCPMessage(leftWheelDirection + leftWheelSpeed + rightWheelDirection + rightWheelSpeed);
						System.out.println("Command sent successfully");
					} catch (IOException e) {
						System.out.println("Command failed to send");
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
		btnNewVehicleManual.setBounds(1084, 489, 223, 49);
		frame.getContentPane().add(btnNewVehicleManual);
		
		JLabel lblTurningDegrees = new JLabel("Turning Degrees");
		lblTurningDegrees.setBounds(1217, 598, 130, 35);
		frame.getContentPane().add(lblTurningDegrees);
		
		JLabel lblSpeed = new JLabel("Speed");
		lblSpeed.setBounds(1084, 598, 107, 35);
		frame.getContentPane().add(lblSpeed);
		
		JButton btnClearImu = new JButton("Begin IMU Calibration");
		btnClearImu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnClearImu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(btnClearImu.getText().equals("Begin IMU Calibration")) {
					System.out.println("Beginning IMU Calibration...");
					messageToServer = "Calibrate IMU";
					sendMessageToServer = true;
					btnClearImu.setText("Finished IMU Calibration");					
				}
				else if(btnClearImu.getText().equals("Finished IMU Calibration")) {
					System.out.println("Finished IMU Calibration");
					messageToServer = "Finished IMU Calibration";
					sendMessageToServer = true;
					btnClearImu.setText("Begin IMU Calibration");
				}
				else {
					System.out.println("Error in conditional statements for btnClearImu");
				}
			}
		});
		btnClearImu.setBounds(1074, 10, 189, 35);
		frame.getContentPane().add(btnClearImu);

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
