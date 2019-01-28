package serverSocket;
import java.io.*;
import java.net.*;

public class SocketServer {
	   
    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 9877;
    
    public void connect() throws ClassNotFoundException, IOException {
        //create the socket server object
        server = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates
        while(true){
            System.out.println("Waiting for client request");
            //creating socket and waiting for client connection
            Socket socket = server.accept();
            //read from socket to ObjectInputStream object
            System.out.println("Accepted connection");
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("Received the input");
            //convert ObjectInputStream object to String
            String message = (String) ois.readObject();
            System.out.println("Message Received: " + message);
            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //write object to Socket
            oos.writeObject("Hi Client "+message);
            //close resources
            ois.close();
            oos.close();
            socket.close();
            //terminate the server if client sends exit request
            if(message.equalsIgnoreCase("exit")) break;
        }
        System.out.println("Shutting down Socket server!!");
        //close the ServerSocket object
        server.close();
 
    }
    public void connectAttempt2() throws IOException {
        System.out.println(" == Java Server == ");
        String fromClient;
        String toClient;
 
        ServerSocket server = new ServerSocket(8080);
        System.out.println("wait for connection on port 8080");
 
        boolean run = true;

        Socket client = server.accept();
        
        System.out.println("got connection on port 8080");
        
        while(run) {

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(),true);
 
            fromClient = in.readLine();
            System.out.println("received: " + fromClient);            
            String msg = "got it";
            out.println(msg);

            if(fromClient.equals("exit")) {
                run = false;
            }
            
        }
        
    }
}
