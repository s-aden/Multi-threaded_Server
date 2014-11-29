package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class User {

	private final static String HOST = "localhost";
	private final static int PORT_NUMBER = 4444;

	private static Socket socket;
	private static PrintWriter outputStreamWriter;
	private static BufferedReader inputStreamBuffer;
	private static BufferedReader stdIn;

	public static void main(String[] args) throws IOException{
		System.out.println ("\n-----------------------------------------------------------------------------------------------------------");
	    System.out.println ("User Client");
	    System.out.println ("-----------------------------------------------------------------------------------------------------------");
		try {
			// Connect to local host on the specified port
			socket = new Socket(HOST, PORT_NUMBER);
			
			// Get clients output stream to write out to
			outputStreamWriter = new PrintWriter(socket.getOutputStream(), true);
			// Get client input stream to read from
			inputStreamBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + HOST);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: "+ PORT_NUMBER);
			System.exit(1);
		}
		
		System.out.println("successfully connected to " + HOST + " on port "+ PORT_NUMBER);
		
		System.out.println("Enter your query e.g. help or '*' to end");
		stdIn = new BufferedReader(new InputStreamReader(System.in));
		// Ensure user isn't mistaken for a sensor
		// Input message doesn't matter
		outputStreamWriter.println("hey!");
		inputStreamBuffer.readLine();
		
		String query ;
		while (!((query = stdIn.readLine()).contentEquals("*"))) {
			// Send query
			outputStreamWriter.println(query);
			// Format and present the response
			formatAndPrint(inputStreamBuffer.readLine());
		}
		
		//Tidy up!
		stdIn.close();
		outputStreamWriter.close();
		inputStreamBuffer.close();
		socket.close();
	
	}
	
	private static void formatAndPrint(String result){
		if(result.contains("&")){
			
			System.out.println ("\n-----------------------------------------------------------------------------------------------------------");
		    System.out.println (String.format("%65s", "Respnse - List of available queries, case sensitive"));
		    System.out.println ("-----------------------------------------------------------------------------------------------------------");
		    System.out.println ("-----------------------------------------------------------------------------------------------------------");
		    System.out.println(String.format("%-52s%-52s", "Query", "Description"));
			System.out.println("-----------------------------------------------------------------------------------------------------------");
			String commands[] = result.split("&");
			
			for(String command : commands){
				String item[] = command.split("\\-");
				System.out.println(String.format("%-52s%-52s",item[0],item[1]));
			}
			
		} else if(!(result.isEmpty())) {
			try{
				System.out.println ("\n-----------------------------------------------------------------------------------------------------------");
			    System.out.println (String.format("%65s", "Respnse - Display Reading"));
			    System.out.println ("-----------------------------------------------------------------------------------------------------------");
			    System.out.println ("-----------------------------------------------------------------------------------------------------------");
			    
				result = result.replace("|", "\n");
				BufferedReader buffer = new BufferedReader(new StringReader(result));
				System.out.println(String.format("%-50s%-50s%-50s", "Sensor Type", "Date/Time", "Reading"));
				System.out.println ("-----------------------------------------------------------------------------------------------------------");
				
				String reading;
				while((reading = buffer.readLine()) != null){
					String dataSet[] = reading.split("\\,");
					System.out.println(String.format("%-50s%-50s%-50s", String.valueOf(dataSet[0]) , String.valueOf(dataSet[1]) , String.valueOf(dataSet[2])));
					//System.getProperty("line.separator");
				}
				buffer.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		} else {
			System.out.println("No data!");
		}
	}
}
