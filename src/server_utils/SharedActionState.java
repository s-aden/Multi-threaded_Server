package server_utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SharedActionState {

	private boolean accessing = false;
	private String filePath;

	public SharedActionState(String filePath) {
		this.filePath = filePath;
	}

	// Attempt to aquire a lock
	public synchronized void acquireLock() throws InterruptedException {
		// Get ref to the current thread
		Thread current = Thread.currentThread();
		System.out.println(current.getName()+ " is attempting to acquire a lock!");
		while (accessing) {
			System.out.println(current.getName()+ " waiting to get a lock as someone else is accessing...");
			// Wait for the lock to be released see releaseLock() below
			wait();
		}
		// If no thread has a lock, obtain it.
		accessing = true;
		System.out.println(current.getName() + " got a lock!");
	}

	// Releases a lock once a thread h
	public synchronized void releaseLock() {
		accessing = false;
		notifyAll();
		System.out.println(Thread.currentThread().getName()+ " released a lock!");
	}

	// Handle request
	public synchronized String processInputData(String clientName, String data) throws IOException, ParseException {
		// All sensors essentially do the same thing So, combine them into on condition
		if (clientName.equals("Temperature") || clientName.equals("Rainfall") || clientName.equals("Pressure")) {
			return IO.writeToFile(filePath, data) ? "error" : "done";
		} else if (clientName.equals("User")) {
			// Client is User/Browser
			return processUserRequest(data);
		}
		
		return "error";
	}

	private synchronized String processUserRequest(String request) throws IOException, ParseException {
		// Trim white spaces
		request = request.trim();
		
		String byNamePattern = "(Get\\s)(all\\s)(from\\s)(Temperature|Rainfall|Pressure)";
		String rangePattern = "(Range\\s)(\\d{1,2}/\\d{1,2}/\\d{2,4}@\\d"
				+ "{1,2}:\\d{1,2}:\\d{1,2}\\s)"
				+ "(\\d{1,2}/\\d{1,2}/\\d{2,4}@\\d{1,2}:\\d{1,2}:\\d{1,2})";
		
		if (request.equalsIgnoreCase("Get all")) {
			return IO.readFile(filePath);
			
		}  else if (request.matches(byNamePattern)) {
			
			// Get all words from the query
			String req[] = request.split(" ");
			// The last word in the array of words will be sensor name
			return IO.getAllFromSensor(filePath,req[req.length - 1]);
			
		} else if (request.matches(rangePattern)) {
			// Parse data from database/file
			ArrayList<Data> data = IO.parseFile(filePath);
			
			//Process and extract dates from the request
			String req[] = request.split(" ");
			Date start = IO.stringToDate(req[1].replace("@", " ")); // given the specified pattern start date
			Date end = IO.stringToDate(req[2].replace("@", " "));// given the specified pattern end date
			
			// Collect items within the given range
			// Parse all back to to string format for transmission
			DateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String collector="";
			for(Data d : data){
				Date date = d.getDate();
				if(!(date.before(start) || date.after(end))){
					String dt = formatDate.format(d.getDate());
					collector += d.getSensorType()+","+dt+","+d.getReading()+"|";
					
				}
			}

			return collector;
					
		} else {
			// Handle all requests that don't match all conditions above this.
			// return all possible commands
			String getAll = "Get all -to get all data from all sensors &";
			String range = "Range dd/mm/yy@hh:mm:ss dd/mm/yy@hh:mm:ss -to get data from all sensors for a given range &";
			String sensor = "Get all from [Sensor] -get all data from a particular sensore i.e. Temperature|Rainfall|Pressure";
			return getAll + range + sensor;
		}
		
	}

}
