// Written by Jacob Krucinski on 12/03/18
// Updated on 12/04/18
// This class is my EXECUTABLE for this project!


// Imports
// Import java packages for graphics and arrays
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;

// Import opencv libraries required for image processing
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.*;

// Import generated GRIP pipeline for car image processsing
import carPipeline.CarPipeline;
import carPipeline.CarPipeline.Line;


public class CarDetection_2Frames {
	// Static variables (used for distance calibration)
	private static int pixelsToFt = (int)(130 / 16.1); 			// At approx. 45 ft
	// Avg. car length = 16.1ft
	// Avg car pixel length @ ~45ft: 130 pixels
	private static double secToHr = 1 / 3600.0;
	private static int miInFeet = 5280;
	
	// General variables
	private static int speedLimit = 25; 	// [mph]
	private static int frameRate = 30;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Initialize image processing object
		CarPipeline carDetector = new CarPipeline();
		
		// For image processing, load in baseline img for opencv absdiff
		Mat baseline = new Mat();
		baseline = Imgcodecs.imread(new File("ImagesForPipeline/Baseline.jpeg").getPath());
		
		// Load in car image
		Mat car = new Mat();
		car = Imgcodecs.imread(new File("ImagesForPipeline/019.jpeg").getPath());

		// Init graphics/GUI
		// 1: Raw camera feed
		JFrame raw = new ImageFrame(car);
		raw.setTitle("Raw Image");
		raw.setSize(640, 480);					
		raw.setVisible(true);
		raw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		// 2: Filtered camera feed
		Line randomLine = new Line(0, 0, 0, 0);
		JFrame filtered = new ImageFrameFiltered(car, randomLine);
		filtered.setLocation(400, 400);
		filtered.setTitle("Filtered Image");
		filtered.setSize(640, 480);				
		filtered.setVisible(true);
		filtered.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		// Display raw image
		raw.repaint();
		
		// Perform image processing on the 2nd image
		carDetector.process(car, baseline);
		
		// Obtain desired output from carDetector
		//ArrayList<Line> lines = carDetector.filterLinesOutput();
		ArrayList<Line> lines = carDetector.findLinesOutput();
		System.out.println(carDetector.findLinesOutput().size());
		System.out.println(lines.size() + " car lines found!");
		Line carLine = lines.get(0);		// Assuming there will be only one line in this ArrayList if processing is done well
		
		// Display filter frame in new gui window
		((ImageFrameFiltered) filtered).setCarLine(carLine);
		filtered.repaint();
	}
	
	// My functions/algorithms for car speed detection
	/**
	 * 
	 * @param line l
	 * @return midpoint of line l (as object Point)
	 */
	public static Point getLineMidPt(Line l) {
		int x1 = (int)l.x1;
		int x2 = (int)l.x2;
		int y1 = (int)l.y1;
		int y2 = (int)l.y2;
		
		int avgX = (x1 + x2) / 2;
		int avgY = (y1 + y2) / 2;
		
		return new Point(avgX, avgY);
	}
	
	/**
	 * 
	 * @param a - midpoint of one car line
	 * @param b - midpoint of 
	 * @return speed of car [mph]
	 */
	public static double getCarSpeed(Point a, Point b) {
		// Get x values of two midpoints
		int aX = (int)a.x;
		int bX = (int)b.x;
		
		// Figure out distance [pixels] between first and second point
		int xDiff = aX - bX;
		
		// NOTE: These are distance traveled in the duration of one frame to 
		// another (1 / frameRate s, i.e 1/30 s)
		int pixelDistance = Math.abs(xDiff);
		double distanceFt  = (double)pixelDistance / pixelsToFt;
		double distanceMi = distanceFt / miInFeet;
		
		// Convert distance traveled in one frame to one second
		double miPerS = distanceMi * frameRate;
		double mph = miPerS * (1 / secToHr);
		
		return mph;
	}

}
