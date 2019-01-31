package serverSocket;
import java.io.*;
import java.net.*;

public class SocketServer {
	 
    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 9877;
    
    public void connectAttempt2() throws IOException {
        System.out.println(" == Java Server == ");
        String fromClient;
        String toClient;
 
        ServerSocket server = new ServerSocket(port);
        System.out.println("wait for connection on port 9877");
 
        boolean run = true;

        Socket client = server.accept();
        
        System.out.println("got connection on port 9877");
        
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

