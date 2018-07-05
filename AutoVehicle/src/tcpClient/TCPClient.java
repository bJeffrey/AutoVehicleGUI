package tcpClient;

import java.io.*;
import java.net.*;
//import java.awt.*;
//import java.awt.event.*;
//import javax.swing.*;
//import java.util.Scanner;


/*
public class TCPClient{
    public static void main(String[] args) throws IOException 
    {
        Socket socket = new Socket();

        String host = "www.google.com";
        
         
        try
        {
        socket.connect(new InetSocketAddress(host , 80));
        
        //attempting to send message to google
        //Send the message to the server
        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);

        String stringMessage = "GET";

        String sendMessage = stringMessage + "\n";
        bw.write(sendMessage);
        bw.flush();
        System.out.println("Message sent to the server : "+sendMessage);

        //Get the return message from the server
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String message = br.readLine();
        System.out.println("Message received from the server : " +message);
        
        //end of sending message to google
        
        }
         
        //Host not found
        catch (UnknownHostException e) 
        {
            System.err.println("Don't know about host : " + host);
            System.exit(1);
        }
         
        System.out.println("Connected");
        //out.print(cmd);
        socket.close();
    }
}
*/

public class TCPClient{
	private String host;
	private int port;
	
	//variables to send message to server
	Socket socket = new Socket();
	OutputStream os;
	OutputStreamWriter osw;
	BufferedWriter bw;
	
	//variables to receive message from server
	InputStream is;
	InputStreamReader isr;
	BufferedReader br;
	
	public TCPClient() throws IOException {

	}
	
    public boolean connect(String h, int p) throws IOException {
        //Socket socket = new Socket();
		host = h;
		port = p;
    	
        
        try
        {
        	socket.connect(new InetSocketAddress(host , port));
        	
        	//beginning sending/receiving message to/from the server
        	
        	//Send the message to the server
        	//OutputStream os = socket.getOutputStream();
        	//OutputStreamWriter osw = new OutputStreamWriter(os);
        	//BufferedWriter bw = new BufferedWriter(osw);
        	os = socket.getOutputStream();
        	osw = new OutputStreamWriter(os);
        	bw = new BufferedWriter(osw);
        	
        	/*
        	String stringMessage = "GET";
        	String sendMessage = stringMessage + "\n";
        	bw.write(sendMessage);
        	bw.flush();
        	System.out.println("Message sent to the server : "+sendMessage);
			*/
        	
        	//Get the return message from the server
        	//InputStream is = socket.getInputStream();
        	//InputStreamReader isr = new InputStreamReader(is);
        	//BufferedReader br = new BufferedReader(isr);
        	is = socket.getInputStream();
        	isr = new InputStreamReader(is);
        	br = new BufferedReader(isr);
        	
        	/*
        	String message = br.readLine();
        	System.out.println("Message received from the server : " +message);
        	*/
        	//end of sending message to google
        
        }
         
        //Host not found
        catch (UnknownHostException e) 
        {
            System.err.println("Don't know about host : " + host);
            return false;
            //System.exit(1);
        }
         
        System.out.println("Connected");
        return true;
        //socket.close();
    }
    public void sendTCPMessage(String message) throws IOException{
    	String sendMessage = message + "\n";
    	bw.write(sendMessage);
    	bw.flush();
    	System.out.println("Message sent to the server : "+sendMessage);
    }
    public void sendTestTCPMessage() throws IOException{
        //Socket socket = new Socket();
        
        try
        {
        	String stringMessage = "GET";
        	String sendMessage = stringMessage + "\n";
        	bw.write(sendMessage);
        	bw.flush();
        	System.out.println("Message sent to the server : "+sendMessage);
       	
        	String message = br.readLine();
        	System.out.println("Message received from the server : " +message);        
        }
         
        //Host not found
        catch (UnknownHostException e) 
        {
            System.err.println("Don't know about host : " + host);
            System.exit(1);
        }
         
    }
    public void closeSocket() {
    	try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
