// Written by Jacob Krucinski on 12/12/18
import java.awt.Color;

public class DataLogger {
	// Instance variables (variables to be logged)
	private String[] dates;		
	private String[] times;		// Might come in as a long
	private double[] speeds;
	private Color[] colors;		// Might come in as a Color object, then convert to string
	
	public DataLogger() {}
	
	public DataLogger(String[] d, String[] t, double[] s, Color[] c) {
		dates = d;
		times = t;
		speeds = s;
		colors = c;
	}
}
