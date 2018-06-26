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
	
	public TCPClient(String h, int p) {
		host = h;
		port = p;
	}
	
    public void connect() throws IOException {
        Socket socket = new Socket();
        
        try
        {
        	socket.connect(new InetSocketAddress(host , port));
        	
        	//beginning sending/receiving message to/from the server
        	
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
        socket.close();
    }
}
