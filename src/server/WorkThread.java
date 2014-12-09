package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;

import server_utils.ResourceManager;

public class WorkThread extends Thread {
	
	
	
	private Socket serverSocket;
	private PrintWriter outputStreamWriter;
	private BufferedReader inputReader;
	private ResourceManager resourceManager;
	private String clientName;
	
	
	public WorkThread(Socket serverSocket,ResourceManager resourceManager) {
		//super("WorkThread");
		
		this.serverSocket = serverSocket;
		this.resourceManager = resourceManager;
	}

	@Override
	public void run() {
		try {
			System.out.println("Starting thread: "+ Thread.currentThread().getName());
			// Get hold of the output stream so, that it can be written to
			outputStreamWriter = new PrintWriter(serverSocket.getOutputStream(), true);
			// Read input from the sensors(clients)
			inputReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			
			inputReader.mark(1000);
			detectClientName(inputReader.readLine());
			inputReader.reset();
			
			if(clientName!=null){
				String line;
				while((line = inputReader.readLine())!=null){
					resourceManager.acquireLock();
					// Process data and return result back the the requesting client
					String result = resourceManager.processInputData(clientName, line);
					System.out.println("Sending result to: " + clientName);
					outputStreamWriter.println(result);
					System.out.println("Result sent to: " + clientName);
					// Release lock for other uses
					resourceManager.releaseLock();
				}
			}else {
				System.out.println("No data recieved");
			}
			
		} catch (IOException | InterruptedException | ParseException e) {
			e.printStackTrace();
		}
	
	}
	
	private void detectClientName(String client) {
		String[] result;
		
		if((client!=null) && (!client.isEmpty())){
			String patterns[] = {
				"Temperature|Rainfall|Pressure",
				"\\w{7,12},\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2},\\d{1,5}\\s\\w{2,7}"};
			result = client.split(",");
			if((result[0].matches(patterns[0]))){
				clientName = result[0];
			} else{
				// Assume the client is User
				clientName = "User";
			}
		}else{
			clientName = null;
		}
	}

}
