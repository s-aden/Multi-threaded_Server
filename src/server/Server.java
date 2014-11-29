package server;

import java.io.IOException;
import java.net.ServerSocket;

import server_utils.SharedActionState;

public class Server {
	
	// Problem: How does the server differentiate clients
	// Possible solution is switch case where given a name of a client the appropriate action is taken
	
	private final static int PORT_NUMBER = 4444;
	private final static String FILE_PATH= "server_data.txt";
	private static ServerSocket serverSocket;
	private static SharedActionState sharedActionState;
	
	
	public static void main(String[] args){
		System.out.println ("\n-----------------------------------------------------------------------------------------------------------");
	    System.out.println ("Server");
	    System.out.println ("-----------------------------------------------------------------------------------------------------------");
		
		
		sharedActionState = new SharedActionState(FILE_PATH);
		
		try {
			// Bind socket to a specified port, in this case whatever PORT_NUMBER has
			serverSocket = new ServerSocket(PORT_NUMBER);
		} catch (IOException e) {
			System.err.print("Could not listen on port " + PORT_NUMBER + ", possibly in use!");
			// Kill server
			System.exit(1);
		}
		System.out.println("Server is ready!");
		
		// Continuously listen for a connection to be made
		while(true){
			try {
				// Accept a connection and continue, otherwise wait for a connection to be made (block!)
				// Fork a new thread for the connection
				new WorkThread(serverSocket.accept(), sharedActionState).start();
			} catch (IOException e) {
				System.err.println("Couldn't make a connection, server killed");
				System.exit(1);
			}
		}
		
	}
}
