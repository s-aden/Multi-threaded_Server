package server_utils;

import java.util.Date;

public class Data {
	private String sensorType;
	private Date date;
	private String reading;
	
	public Data(String sensorType, Date date, String reading){
		this.setSensorType(sensorType);
		this.setDate(date);
		this.setReading(reading);
	}
	

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getReading() {
		return reading;
	}

	public void setReading(String reading) {
		this.reading = reading;
	}
	
	
}
