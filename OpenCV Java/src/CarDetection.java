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
import carPipelineTwo.CarPipelineTwo;


public class CarDetection {
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
		CarPipelineTwo carDetector = new CarPipelineTwo();
		
		// For image processing, load in baseline img for opencv absdiff
		Mat baseline = new Mat();
		baseline = Imgcodecs.imread(new File("ImagesForPipeline/Baseline2.jpeg").getPath());
		
		// Init graphics/GUI
		// 1: Raw camera feed
		JFrame raw = new VideoFrame();
		raw.setTitle("Raw Feed");
		raw.setSize(640, 480);					// Possibly optimize this line
		raw.setVisible(true);
		raw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// allows program termination when x is clicked on
		
		// 2: Filtered camera feed
		JFrame filtered = new VideoFrameFiltered();
		filtered.setTitle("Filtered Feed");
		filtered.setSize(640, 480);				// Possibly optimize this line
		filtered.setVisible(true);
		filtered.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// allows program termination when x is clicked on

//		// Debugging for my functions
//		Line a = new Line(0, 10, 10, 20);
//		Point midpoint = getLineMidPt(a);
//		System.out.println("Midpoint of line a is " + midpoint);
//		System.out.println("Speed: " + getCarSpeed(new Point(0, 50), new Point(8, 50)) + " mph");
		
		// Forever loop
		Point prevCarMidPt = new Point(0, 0);
		while (true) {
			// Capture one frame from webcam
			// (already done in VideoFrame class)
			// Display that frame in a gui window
			raw.repaint();
			
			// Perform image processing on the frame
			Mat rawFrame = new Mat();
			VideoCapture camera = new VideoCapture();
			camera.read(rawFrame);
			carDetector.process(rawFrame, baseline);
			
			// Obtain desired output from carDetector
			ArrayList<MatOfPoint> c = carDetector.findContoursOutput();
			
			// Display filter frame in new gui window
			((VideoFrameFiltered) filtered).setCarContours(c);
			filtered.repaint();
			
			// Set prevCarMidPt to carMidPt
			//prevCarMidPt = carMidPt.clone();
		}
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
