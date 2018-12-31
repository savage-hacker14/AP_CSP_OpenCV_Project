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
	private ArrayList<String> times;		
	private ArrayList<Double> speeds;
	private ArrayList<Boolean> isSpeeding;
	private ArrayList<Color> colors;	
	
	public DataLogger() {}	// Default constructor necessary but is never used
	
	public DataLogger(ArrayList<String> t, ArrayList<Double> s, ArrayList<Boolean> s2, ArrayList<Color> c) {
		// Set all input parameters to this object's instance variables
		times = t;
		speeds = s;
		isSpeeding = s2;
		colors = c;
	}
	
	public void logData() throws IOException {
		// Acquire time date from system
		LocalDateTime now = LocalDateTime.now();
		String date = now.toString().substring(0, 10);
		
		// Create writer object (with proper name and save location)
		BufferedWriter writer = new BufferedWriter(new FileWriter("LogFiles/" + date + ".txt"));
		
		// Write header for .txt file
		writeHeader(writer, date);
		
		// Then write data from variables
		for (int i = 0; i < times.size(); i++) {
			writer.write(times.get(i) + "\t" + speeds.get(i) +"\t\t\t" + isSpeeding.get(i) + "\t\t\t\t" + colors.get(i) + "\n");
		}
		
		// Calculate statistics for the logged data
		calculateStats(writer);
		
		// Flush text from buffer and write it to the file
		writer.flush();
		writer.close();
	}
	
	/**
	 * Writes the header for the text file
	 * 
	 * @param the file writer
	 * @param the data (as a String object)
	 * @throws IOException
	 */
	private void writeHeader(BufferedWriter writer, String date) throws IOException {	
		String loggedVars = "Time [s]" + "\t\t" + "Speed [mph]" + "\t\t" + "Is Speeding?" + "\t\t" + "Color" + "\n";
		String lineBreak = "------------------------------------------------------------------------------------\n";
		
		writer.write(date + ".txt" + "\n\n");
		writer.write(loggedVars);
		writer.write(lineBreak);	
	}
	
	/**
	 * This method takes the data from the instance variables and 
	 * calculates useful/good-to-know stats about it
	 * 
	 * @param the file writer
	 * @throws IOException
	 */
	private void calculateStats(BufferedWriter writer) throws IOException {
		writer.write("\n");
		writer.write("Compiled Statistics: \n");
		writer.write("Average Speed [mph]:\t\t" + averageSpeed() + "\n");
		writer.write("Frames w/ Speeding Cars:\t" + numSpeedingCars() + "\n");
	}
	
	/**
	 * Calculates the average speed of all the speeds logged
	 * 
	 * @return the average speed rounded to 2 decimal places
	 */
	private double averageSpeed() {
		double sumSpeeds = 0.0;
		
		for (Double s : speeds) {
			sumSpeeds += s.doubleValue();
		}
		
		double avgSpeed = sumSpeeds / speeds.size();
		double avgSpeedRounded = (int)(avgSpeed * 100) / 100.0;
		
		return avgSpeedRounded;
	}
	
	/**
	 * Finds the number of frames where the car was speeding
	 * (num of instances of true in the isSpeeding list)
	 * 
	 * @return the number of frames where the car was speeding 
	 */
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
