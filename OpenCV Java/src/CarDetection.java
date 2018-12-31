// Written by Jacob Krucinski on 12/03/18
// Updated on 12/31/18
// This class is my EXECUTABLE for this project!


// Imports
// Import java packages for graphics and arrays
import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JFrame;

// Import opencv libraries required for image processing
import org.opencv.core.*;
import org.opencv.imgcodecs.*;

// Import generated GRIP pipeline for car image processsing
import carPipelineThree.*;


public class CarDetection {
	// Variables (used for speed calculation)
	private static double pixelsToFt = 8.925;		// Value came from calibration
	private static double secToHr = 1 / 3600.0;
	private static int miInFeet = 5280;
	
	// General variables (for data logger and speed calculator)
	private static int speedLimit = 25; 	// [mph]
	private static int frameRate;
	private static int speedingCarCounter = 1;

	public static void main(String[] args) throws InterruptedException {
		// Initialize image processing object
		CarPipelineThree carDetector = new CarPipelineThree();
		
		// Initialize video capture object
		VideoCap v = new VideoCap();

		// Init graphics/GUI
		// 1: Raw camera feed
		JFrame raw = new VideoFrame(v);
		raw.setTitle("Webcam Feed");
		raw.setSize(665, 552);				
		raw.setVisible(true);
		raw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// allows program termination when x is clicked on
		
		// 2: Filtered camera feed
		JFrame filtered = new VideoFrame(v);
		filtered.setLocation(640, 0);
		filtered.setTitle("Filtered Image");
		filtered.setSize(665, 552);		
		filtered.setVisible(true);
		filtered.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	

		// For image processing, load in baseline img for opencv absdiff
		Mat baseline = new Mat();
		((VideoFrame) raw).getFrameMat(baseline);	
		
		// Display display the windows
		raw.repaint();
		filtered.repaint();
		
		// Init variables used in main loop (for storing previous car data)
		Rect prevCarBox = new Rect();
		int framesSinceLastCar = 1;
		
		// Create ArrayLists of the variables logged by the data logger
		// At the end of the while loop
		ArrayList<String> times = new ArrayList<String>();
		ArrayList<Double> speeds = new ArrayList<Double>();
		ArrayList<Boolean> isSpeeding = new ArrayList<Boolean>();
		ArrayList<Color> colors = new ArrayList<Color>();
		
		// ** Main program loop **
		// counter variable counts the number of processed frames
		// and then terminates the loop after 10k
		long counter = 0;
		while (counter < 10000) {
			// A time variables used to define a variable frame rate
			// (since it isn't constant which greatly can impact car speeds)
			long start = System.currentTimeMillis();
			
			// Refresh both graphics windows
			raw.repaint();
			filtered.repaint();
				
			// Every 30 frames, update baseline
			if (counter % 30 == 0) {
				((VideoFrame) raw).getFrameMat(baseline);
			}
			
			// Grab current raw frame 
			Mat rawFrame = new Mat();
			((VideoFrame) raw).getFrameMat(rawFrame);
			
			// Perform image processing on the car image
			// ** ALL IMAGE PROCESSING IS DONE WITH THIS ONE LINE!!! **
			carDetector.process(rawFrame, baseline);

			// Obtain desired output from carDetector
			ArrayList<MatOfPoint> carContour = carDetector.filterContoursOutput();
			
			// Now give the car contour to the graphics object
			((VideoFrame) filtered).setCarContour(carContour);
			
			// Create point object to store the midpoint 
			// of the previous and current car boxes
			Point carBoxMidPt1 = getBoxMidPt(prevCarBox);
			Point carBoxMidPt2 = new Point();
			
			// Refresh windows once again
			raw.repaint();
			filtered.repaint();
			
			// If car is detected, calculate its speed and then acquire all
			// the variables for the data logger and put them into the 
			// designated ArrayLists
			if (((VideoFrame) filtered).isCarDetected() == true) {
				prevCarBox = ((VideoFrame) filtered).getCarBox();	// Set prev car box to current car box
				carBoxMidPt2 = getBoxMidPt(((VideoFrame) filtered).getCarBox());
				System.out.print(carBoxMidPt1 + "\t" + carBoxMidPt2 + "\n");
				
				double speed = getCarSpeed(carBoxMidPt1, carBoxMidPt2) / framesSinceLastCar;
				double speedRounded = (int)(speed * 100) / 100.0;
				System.out.println(speedRounded);
				
				// Give graphics object the car speed data so it can draw the 
				// value over the car
				((VideoFrame) filtered).setCarSpeed(speedRounded);
				
				// Add car data to array lists
				LocalDateTime now = LocalDateTime.now();	
				String time = now.toString().substring(11);
				times.add(time);
				speeds.add(new Double(speedRounded));
				
				// Calculate isSpeeding variable
				// And if so save the image of the speeding car
				if (speed > speedLimit) {
					isSpeeding.add(true);
					saveSpeedingCar((VideoFrame) filtered);
				}
				else {
					isSpeeding.add(false);
				}
				
				// Also save the car's color (as RGB)
				Color carColor = ((VideoFrame) filtered).getCarColor();
				colors.add(carColor);
				
				
				framesSinceLastCar = 1;
			}
			else {
				// If car is not detected, then set previous car point to previous car point not 0,0
				carBoxMidPt1 = carBoxMidPt2;
				framesSinceLastCar++;
			}
			
			counter++;
			
			// Calculate the dynamic frame rate variable
			long end = System.currentTimeMillis();
			long elapsedMS = end - start;
			double elapsedSEC = elapsedMS / 1000.0;
			frameRate = (int)(1 / elapsedSEC);
		}
		
		// When while loop terminates, create DataLogger object and then 
		// write all the data from the ArrayLists to the .txt file
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
		// This clears up large amounts of RAM that was used to store the
		// ArrayLists
		times = null;
		speeds = null;
		isSpeeding = null;
		colors = null;
	}
	
//------------------------------------------------------------------------------
// My functions/algorithms for car speed detection
	
	/**
	 * @param car box (as object Rect)
	 * @return midpoint of car box (as object Point)
	 */
	public static Point getBoxMidPt(Rect car) {
		int midX = (int)(car.tl().x + car.br().x) / 2;
		int midY = (int)(car.tl().y + car.br().y) / 2;
		
		return new Point(midX, midY);
	}
	
	/**
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
	
	public static void saveSpeedingCar(VideoFrame f) {
		// Acquire frame as Mat object
		Mat carMat = new Mat();
		f.getFrameMat(carMat);
		
		// Then write the file to the disk with a unique name
		Imgcodecs.imwrite("LogFiles/SpeedingCarFrames/SpeedingCar" + speedingCarCounter + ".jpg", carMat);

		// Increment this variable (so that next saved frame will have a unique name)
		speedingCarCounter++;
	}
}
