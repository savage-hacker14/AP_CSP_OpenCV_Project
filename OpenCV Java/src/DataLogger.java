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
	private ArrayList<Boolean> isSpeeding;
	private ArrayList<Color> colors;		// Might come in as a Color object, then convert to string
	
	public DataLogger() {}
	
	public DataLogger(ArrayList<String> t, ArrayList<Double> s, ArrayList<Boolean> s2, ArrayList<Color> c) {
		times = t;
		speeds = s;
		isSpeeding = s2;
		colors = c;
	}
	
	public void logData() throws IOException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		
		String date = now.toString().substring(0, 10);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("LogFiles/" + date + ".txt"));
		
		// Header for log file
		writer.write(date + ".txt" + "\n\n");
		writer.write("Time [s]" + "\t\t" + "Speed [mph]" + "\t\t" + "Is Speeding?" + "\t\t" + "Colors" + "\n");
		writer.write("------------------------------------------------------------------------------------\n");
		
		// Then write data from variables
		for (int i = 0; i < times.size(); i++) {
			writer.write(times.get(i) + "\t" + speeds.get(i) +"\t\t\t" + isSpeeding.get(i) + "\t\t\t\t" + colors.get(i) + "\n");
		}
		
		calculateStats(writer);
		
		writer.flush();
		writer.close();
	}
	
	public void calculateStats(BufferedWriter writer) throws IOException {
		writer.write("\n");
		writer.write("Compiled Statistics: \n");
		writer.write("Average Speed [mph]:\t\t" + averageSpeed() + "\n");
		writer.write("Number of Speeding Cars:\t" + numSpeedingCars() + "\n");
	}
	
	private double averageSpeed() {
		double sumSpeeds = 0.0;
		
		for (Double s : speeds) {
			sumSpeeds += s.doubleValue();
		}
		
		double avgSpeed = sumSpeeds / speeds.size();
		double avgSpeedRounded = (int)(avgSpeed * 100) / 100.0;
		
		return avgSpeedRounded;
	}
	
	private int numSpeedingCars() {
		// Since the data logger is logging every frame (and not just one entry per car), 
		// this will be the number of frames with speeding cars
		
		int num = 0;
		
		for (Boolean b : isSpeeding) {
			if (b == true) {
				num++;
			}
		}
		
		return num;
	}
}
