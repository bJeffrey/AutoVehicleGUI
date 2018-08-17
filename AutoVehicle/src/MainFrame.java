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
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeEvent;
import RotateImage.RotateImage;
import tcpClient.TCPClient;

public class MainFrame {

	private JFrame frame;
	protected TCPClient client;
	protected boolean connected;
	protected String lastCommandSent;
	private double angle;
	protected Image carImg;//may be deleted when ready to delete the old car
	protected JLabel lblMultiThreadResults;
	

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

	public class Consumers implements Runnable {

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
	
	public class Producers implements Runnable {

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
				Thread.sleep(100);
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
		
	    RotateImage carRotateImage = new RotateImage();
	    carRotateImage.setTheta(0.00);
	    carRotateImage.setBounds(317, 261, 70, 70);
	    carRotateImage.setBackground(Color.white);
	    angle = 0.0;//start with the vehicle facing north

		JLabel lblCar = new JLabel("");
		lblCar.setBounds(273, 352, 66, 27);
		//Image carImg = new ImageIcon(this.getClass().getResource("/rsz_vehicle.jpg")).getImage();
		//carImg = new ImageIcon(this.getClass().getResource("/rsz_vehicle.jpg")).getImage();
		//lblCar.setIcon(new ImageIcon(carImg));
		
		frame.getContentPane().add(btnMultiThreadTest);
		frame.getContentPane().add(btnConnectToVehicle);
		frame.getContentPane().add(lblConnected);
		frame.getContentPane().add(btnKeyControl);
		frame.getContentPane().add(slider);
		frame.getContentPane().add(btnCenter);
		frame.getContentPane().add(lblMultiThreadResults);
		frame.getContentPane().add(carRotateImage);
		//frame.getContentPane().add(lblCar);
		frame.setBounds(20, 20, 1500, 800);
		//frame dimensions
		//left,top,right,bottom
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
				angle = angle + 0.1;
				carRotateImage.setTheta(angle);
				//frame.repaint(); //This works!
				carRotateImage.repaint();//This works as long as carRotateImage is above it
			}
		});
		btnRotateImageTest.setBounds(1280, 413, 140, 35);
		frame.getContentPane().add(btnRotateImageTest);
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
