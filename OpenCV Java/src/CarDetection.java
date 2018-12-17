// Written by Jacob Krucinski on 12/03/18
// Updated on 12/17/18
// This class is my EXECUTABLE for this project!


// Imports
// Import java packages for graphics and arrays
import java.awt.Color;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JFrame;

// Import opencv libraries required for image processing
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.videoio.VideoCapture;

// Import generated GRIP pipeline for car image processsing
import carPipeline.CarPipeline.Line;
import carPipelineThree.*;;


public class CarDetection {
	// Static variables (used for distance calibration)
	private static double pixelsToFt = 8.925;			
	// Avg. car length = 15ft
	// Avg car pixel length @ ~58ft: 130 pixels
	private static double secToHr = 1 / 3600.0;
	private static int miInFeet = 5280;
	
	// General variables
	private static int speedLimit = 25; 	// [mph]
	private static int frameRate = 30;

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		// Initialize image processing object
		CarPipelineThree carDetector = new CarPipelineThree();
		
		// Initialize video capture object
		VideoCap v = new VideoCap();

		// Init graphics/GUI
		// 1: Raw camera feed
		JFrame raw = new VideoFrame(v);
		raw.setTitle("Webcam Feed");
		raw.setSize(665, 552);					// Possibly optimize this line
		raw.setVisible(true);
		raw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// allows program termination when x is clicked on

		// For image processing, load in baseline img for opencv absdiff
		Mat baseline = new Mat();
		((VideoFrame) raw).getFrameMat(baseline);
		
		// 2: Filtered camera feed
		JFrame filtered = new VideoFrame(v);
		filtered.setLocation(640, 0);
		filtered.setTitle("Filtered Image");
		filtered.setSize(665, 552);		
		filtered.setVisible(true);
		filtered.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		// Display raw image
		raw.repaint();
		filtered.repaint();
		
		
		Rect prevCarBox = new Rect();
		int framesSinceLastCar = 1;
		
		ArrayList<String> times = new ArrayList<String>();
		ArrayList<Double> speeds = new ArrayList<Double>();
		ArrayList<Boolean> isSpeeding = new ArrayList<Boolean>();
		ArrayList<Color> colors = new ArrayList<Color>();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); 
		
		long counter = 0;
		while (counter < 10000) {
			//System.out.print(filtered.getWidth() + "\t" + filtered.getHeight() + "\n");
			raw.repaint();
			filtered.repaint();
			
			// Grab current raw frame 
			Mat rawFrame = new Mat();
			((VideoFrame) raw).getFrameMat(rawFrame);
			
			// Perform image processing on the car image
			carDetector.process(rawFrame, baseline);

			// Obtain desired output from carDetector
			ArrayList<MatOfPoint> carContour = carDetector.filterContoursOutput();
			
			// Display filter frame in new gui window
			((VideoFrame) filtered).setCarContour(carContour);
			filtered.repaint();
			
			Point carBoxMidPt1 = getBoxMidPt(prevCarBox);
			Point carBoxMidPt2 = new Point();
			
			// Display raw image
			raw.repaint();
			filtered.repaint();
			
			if (((VideoFrame) filtered).isCarDetected() == true) {
				// If getter doesn't return new Rect object (meaning there is a car detected
				prevCarBox = ((VideoFrame) filtered).getCarBox();	// Set prev car box to current car box
				carBoxMidPt2 = getBoxMidPt(((VideoFrame) filtered).getCarBox());
				//System.out.print(carBoxMidPt1 + "\t" + carBoxMidPt2 + "\n");
				//System.out.println(framesSinceLastCar);
				double speed = getCarSpeed(carBoxMidPt1, carBoxMidPt2) / framesSinceLastCar;
				double speedRounded = (int)(speed * 100) / 100.0;
				System.out.println(speedRounded);
				
				((VideoFrame) filtered).setCarSpeed(speedRounded);
				
				// Add car data to array lists
				LocalDateTime now = LocalDateTime.now();	
				String time = now.toString().substring(11);
				times.add(time);
				speeds.add(new Double(speedRounded));
				
				if (speed > speedLimit) {
					isSpeeding.add(true);
				}
				else {
					isSpeeding.add(false);
				}
				
				Color carColor = ((VideoFrame) filtered).getCarColor();
				colors.add(carColor);
				
				
				framesSinceLastCar = 1;
			}
			else {
				// If car is not detected, then set previous car point to previous car point not 0,0
				carBoxMidPt1 = carBoxMidPt2;
				framesSinceLastCar++;
			}
			
			//System.out.println(counter);
			counter++;
		}
		
		DataLogger d = new DataLogger(times, speeds, isSpeeding, colors);
		try {
			System.out.print("Logging data...\t");
			d.logData();
			System.out.println("Done!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Clear time, speed, and color lists
		times = null;
		speeds = null;
		isSpeeding = null;
		colors = null;
	}
	
	// My functions/algorithms for car speed detection
	/**
	 * 
	 * @param car box (as object Rect)
	 * @return midpoint of car box (as object Point)
	 */
	public static Point getBoxMidPt(Rect car) {
		int midX = (int)(car.tl().x + car.br().x) / 2;
		int midY = (int)(car.tl().y + car.br().y) / 2;
		
		return new Point(midX, midY);
	}
	
	/**
	 * 
	 * @param a - midpoint of one car box
	 * @param b - midpoint of another car box
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
	
	/**
	 * @deprecated
	 * @param l
	 */
	public static void dispLineArray(ArrayList<Line> l) {
		for (Line line : l) {
			System.out.println((int)line.x1 + ", " + (int)line.x2 + ", " + (int)line.y1 + ", " + (int)line.y2);
		}
	}

}
