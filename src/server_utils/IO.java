package server_utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class IO {
	
	public IO(){}
	
	public static String readFile(String filePath) throws IOException{
		BufferedReader buffer = new BufferedReader(new FileReader(filePath));
		StringBuilder data = new StringBuilder();
		String reading;
		
		while ((reading = buffer.readLine()) != null) {
			data.append(reading);
			data.append("|");
		}
		// Tidy up!
		buffer.close();
		return data.toString();
	}
	
	public static ArrayList<Data> parseFile(String path) throws IOException, ParseException{
		BufferedReader buffer = new BufferedReader(new FileReader(path));
		ArrayList<Data> readings = new ArrayList<Data>();
		String reading;
		
		while ((reading = buffer.readLine()) != null) {
			String data[] = reading.split(",");
			readings.add(new Data(data[0],stringToDate(data[1]),data[2]));
		}
		// Tidy up!
		buffer.close();
		return readings;
	}
	
	public static String getAllFromSensor(String filePath, String sensorName) throws IOException, ParseException{

		BufferedReader buffer = new BufferedReader(new FileReader(filePath));
		StringBuilder data = new StringBuilder();
		String reading;
		
		while ((reading = buffer.readLine()) != null) {
			if(reading.contains(sensorName)){
				data.append(reading);
				data.append("|");
			}
			
		}
		// Tidy up!
		buffer.close();
		return data.toString();
	}
	
	public static boolean writeToFile(String file, String data) throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
		PrintWriter fileWriter = new PrintWriter(bufferedWriter);
		// Write to file 
		fileWriter.println(data);
		// Tidy up!
		bufferedWriter.close();
		fileWriter.close();
		return fileWriter.checkError();
	}
	
	
	public static Date stringToDate(String date) throws ParseException{
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(date);
	}

}
