import java.awt.EventQueue;

import javax.swing.JFrame;
//import java.awt.GridLayout;
import javax.swing.JButton;
//import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;
//import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import tcpClient.TCPClient;

import java.awt.Font;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
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
import javax.swing.event.ChangeEvent;

public class MainFrame {

	private JFrame frame;
	protected TCPClient client;
	protected boolean connected;

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
		frame = new JFrame();
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
		
		
		JLabel lblConnected = new JLabel("Vehicle Not Connected");
		lblConnected.setHorizontalAlignment(SwingConstants.CENTER);
		lblConnected.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblConnected.setOpaque(true);
		lblConnected.setBackground(Color.LIGHT_GRAY);
		lblConnected.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		
		JButton btnConnectToVehicle = new JButton("Connect to Vehicle");
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

		JButton btnFwd = new JButton("fwd");
		btnFwd.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//Send the move forward command
				try {
					System.out.println("Sending 'forward' command");
					client.sendTCPMessage("forward");
				} catch(IOException f) {
					f.printStackTrace();
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				//Send the stop command
				try {
					System.out.println("Sending 'stop' command");
					client.sendTCPMessage("stop");
				} catch(IOException f) {
					f.printStackTrace();
				}
			}
		});
		btnFwd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		JButton btnLeft = new JButton("left");
		btnLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Send the move left command
				try {
					System.out.println("Sending 'left' command");
					client.sendTCPMessage("left");
				} catch(IOException f) {
					f.printStackTrace();
				}
			
			}
		});
		
		JButton btnRight = new JButton("right");
		btnRight.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Send the move right command
				try {
					System.out.println("Sending 'right' command");
					client.sendTCPMessage("right");
				} catch(IOException f) {
					f.printStackTrace();
				}
			
			}
		});
		
		JButton btnBack = new JButton("back");
		btnBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//Send the move backward command
				try {
					System.out.println("Sending 'backward' command");
					client.sendTCPMessage("backward");
				} catch(IOException f) {
					f.printStackTrace();
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				//Send the stop command
				try {
					System.out.println("Sending 'stop' command");
					client.sendTCPMessage("stop");
				} catch(IOException f) {
					f.printStackTrace();
				}

			}
		});
		
		JButton btnMultiThreadTest = new JButton("Multi-Thread Test");
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
		

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(576, Short.MAX_VALUE)
					.addComponent(slider, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
					.addGap(297))
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(733, Short.MAX_VALUE)
					.addComponent(btnMultiThreadTest, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
					.addGap(111))
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addGap(697)
					.addComponent(lblMultiThreadResults, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
					.addGap(81))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(627)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnLeft)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(10)
									.addComponent(btnBack)
									.addContainerGap())
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
											.addGap(10)
											.addComponent(btnFwd))
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(btnCenter, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(btnRight)))
									.addGap(167))))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addGap(18)
							.addComponent(btnConnectToVehicle, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(lblConnected, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE)
							.addGap(19))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(350)
					.addComponent(btnMultiThreadTest, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblMultiThreadResults, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnConnectToVehicle, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblConnected, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
					.addGap(16)
					.addComponent(btnFwd)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnRight)
						.addComponent(btnLeft)
						.addComponent(btnCenter))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnBack)
					.addGap(18)
					.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18))
		);
		frame.getContentPane().setLayout(groupLayout);
		frame.setBounds(100, 100, 1000, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
