package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;

import server_utils.SharedActionState;

public class WorkThread extends Thread {
	
	
	
	private Socket serverSocket;
	private PrintWriter outputStreamWriter;
	private BufferedReader inputReader;
	private SharedActionState sharedActionState;
	private String clientName;
	
	
	public WorkThread(Socket serverSocket,SharedActionState sharedActionState) {
		//super("WorkThread");
		
		this.serverSocket = serverSocket;
		this.sharedActionState = sharedActionState;
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
					sharedActionState.acquireLock();
					Long start = System.currentTimeMillis();
					// Process data and return result back the the requesting client
					String result = sharedActionState.processInputData(clientName, line);
					outputStreamWriter.println(result);
					// Release lock for other uses
					sharedActionState.releaseLock();
					System.out.println("Time taken: " + (System.currentTimeMillis() - start));
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
			if((result[0].matches(patterns[0])) && client.matches(patterns[1])){
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
