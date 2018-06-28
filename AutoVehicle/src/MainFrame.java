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

public class MainFrame {

	private JFrame frame;
	private JTextField txtVehicleIp;

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
									lblNewMultithreadTest.setText("Consumed: " + queue.take());
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
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		JLabel lbllabelexample = new JLabel("label example");
		lbllabelexample.setHorizontalAlignment(SwingConstants.CENTER);
		lbllabelexample.setFont(new Font("Tahoma", Font.BOLD, 10));
		lbllabelexample.setOpaque(true);
		lbllabelexample.setBackground(Color.LIGHT_GRAY);
		lbllabelexample.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));

		txtVehicleIp = new JTextField();
		txtVehicleIp.setText("Vehicle IP");
		txtVehicleIp.setColumns(10);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//String HOST = txtVehicleIp.getText();
				TCPClient client = new TCPClient("www.google.com", 80);
				try {
					client.connect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		JButton btnFwd = new JButton("fwd");
		btnFwd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		JButton btnLeft = new JButton("left");
		
		JButton btnRight = new JButton("right");
		
		JButton btnBack = new JButton("back");
		
		JButton btnMultiThreadTest = new JButton("Multi-Thread Test");
		btnMultiThreadTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				BlockingQueueExamples testBlockingQueue = new BlockingQueueExamples("192.8.8.8");
				Thread t = new Thread(testBlockingQueue);
				t.start();
			}
		});
		
		JLabel lblMultiThreadResults = new JLabel("Multi-thread Results");
		lblMultiThreadResults.setOpaque(true);
		lblMultiThreadResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblMultiThreadResults.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblMultiThreadResults.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		lblMultiThreadResults.setBackground(Color.LIGHT_GRAY);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(629, Short.MAX_VALUE)
					.addComponent(btnBack)
					.addGap(302))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(601)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblMultiThreadResults, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnLeft)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnRight))
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(28)
									.addComponent(btnFwd)))
							.addPreferredGap(ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
							.addComponent(lbllabelexample, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addComponent(txtVehicleIp, GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)))
					.addGap(119))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(665, Short.MAX_VALUE)
					.addComponent(btnMultiThreadTest, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
					.addGap(179))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(350)
					.addComponent(btnMultiThreadTest, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblMultiThreadResults, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addGap(15)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtVehicleIp, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lbllabelexample, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnFwd)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnRight)
								.addComponent(btnLeft))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnBack)
					.addContainerGap(74, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
		frame.setBounds(100, 100, 1000, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
