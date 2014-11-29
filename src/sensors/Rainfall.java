package sensors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Rainfall {

	private final static String HOST = "localhost";
	private final static int PORT_NUMBER = 4444;
	private final static String FILE = "rainfall_data.txt";

	private static Socket socket;
	private static PrintWriter outputStreamWriter;
	private static BufferedReader inputStreamBuffer;

	public static void main(String[] args) throws IOException, InterruptedException {

		System.out.println("\n-----------------------------------------------------------------------------------------------------------");
		System.out.println("Rainfall sensor");
		System.out.println("-----------------------------------------------------------------------------------------------------------");

		try {
			// Specify a stream socket and connect to localhost/loopback address on the specified port
			socket = new Socket(HOST, PORT_NUMBER);
			// Get output stream to write to the socket
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

		// read data from the file
		BufferedReader readingsData = new BufferedReader(new FileReader(FILE));
		
		// While not terminated, read data file and send it to server
		while (true) {
			// Send it to the server
			String reading;
			if ((reading = readingsData.readLine()) != null) {
				System.out.println("sending data to the server");
				outputStreamWriter.println(reading);
				String response = inputStreamBuffer.readLine();
				if (response.equalsIgnoreCase("error")) {
					outputStreamWriter.println(reading);
				}
				Thread.sleep(100);
			} else {
				System.out
						.println("All data has been transmited - closing connection");
				break;
			}
		}

		// Tidy up!
		outputStreamWriter.close();
		readingsData.close();
		socket.close();

	}
}
