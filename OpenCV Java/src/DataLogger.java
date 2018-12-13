// Written by Jacob Krucinski on 12/12/18
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DataLogger {
	// Instance variables (variables to be logged)	
	private ArrayList<String> times;		// Might come in as a long
	private ArrayList<Double> speeds;
	private ArrayList<Color> colors;		// Might come in as a Color object, then convert to string
	
	public DataLogger() {}
	
	public DataLogger(ArrayList<String> t, ArrayList<Double> s, ArrayList<Color> c) {
		times = t;
		speeds = s;
		colors = c;
	}
	
	public void logData() throws IOException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		
		String date = now.toString().substring(0, 10);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("LogFiles/" + date + ".txt"));
		
		// Header for log file
		writer.write("Time [s]" + "\t\t" + "Speed [mph]" + "\t\t" + "Colors");
		
		// Then write data from variables
		for (int i = 0; i < times.size(); i++) {
			writer.write(times.get(i) + "\t\t" + speeds.get(i) +"\t\t" + colors.get(i) + "\n");
		}
		
		writer.flush();
		writer.close();
	}
}
