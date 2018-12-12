// Written by Jacob Krucinski on 12/03/18
// Updated on 12/04/18
// This class is my EXECUTABLE for this project!


// Imports
// Import java packages for graphics and arrays
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.FilterReader;
import java.util.ArrayList;
import javax.swing.JFrame;

// Import opencv libraries required for image processing
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.videoio.VideoCapture;

// Import generated GRIP pipeline for car image processsing
import carPipeline.CarPipeline.Line;
import carPipelineThree.*;;


public class CarDetection_2Frames {
	// Static variables (used for distance calibration)
	private static double pixelsToFt = 8.925;			
	// Avg. car length = 15ft
	// Avg car pixel length @ ~58ft: 130 pixels
	private static double secToHr = 1 / 3600.0;
	private static int miInFeet = 5280;
	
	// General variables
	private static int speedLimit = 25; 	// [mph]
	//private static int frameRate = 30;
	private static int frameRate = 10;
	// 10 fps because sample images were taken every 0.1 sec so 10x per sec

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		// Initialize image processing object
		CarPipelineThree carDetector = new CarPipelineThree();
		
		// For image processing, load in baseline img for opencv absdiff
		Mat baseline = new Mat();
		baseline = Imgcodecs.imread(new File("ImagesForPipeline/Baseline.jpeg").getPath());
		
		// Load in car image
		Mat car = new Mat();
		car = Imgcodecs.imread(new File("ImagesForPipeline/018.jpeg").getPath());

		// Init graphics/GUI
		// 1: Raw camera feed
		JFrame raw = new ImageFrame();
		raw.setTitle("Raw Image");
		//raw.setLocation(0, 0);
		raw.setSize(640, 480);					
		raw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		// 2: Filtered camera feed
		JFrame filtered = new ImageFrameFiltered();
		filtered.setLocation(640, 0);
		filtered.setTitle("Filtered Image");
		filtered.setSize(640, 480);				
		filtered.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		// Display raw image
		raw.repaint();
		
		String i = "1";
		Rect prevCarBox = new Rect();
		while (Integer.parseInt(i) <= 20) {
			//System.out.print(filtered.getWidth() + "\t" + filtered.getHeight() + "\n");
			
			if (Integer.parseInt(i) < 10) {
				i = "0" + Integer.parseInt(i);
				System.out.println(i);
			}
			
			car = Imgcodecs.imread(new File("ImagesForPipeline/0" + i + ".jpeg").getPath());
			((ImageFrame) raw).setCarImg(car);
			raw.setVisible(true);
			raw.setSize(640, 480);
			raw.setTitle("Raw Image");
			((ImageFrameFiltered) filtered).setCarImg(car);
			filtered.setVisible(true);
			filtered.setTitle("Filtered Image");
			filtered.setSize(640, 480);	
			filtered.setLocation(640, 0);
			
			// Perform image processing on the car image
			carDetector.process(car, baseline);
			
			//System.out.println(carDetector.cvAbsdiffOutput().size());
			//System.out.println(carDetector.hsvThresholdOutput().size());
			
			// Obtain desired output from carDetector
			//ArrayList<Line> lines = carDetector.filterLinesOutput();
			ArrayList<MatOfPoint> carContour = carDetector.filterContoursOutput();
			
			// Display filter frame in new gui window
			((ImageFrameFiltered) filtered).setCarContour(carContour);
			filtered.repaint();
			
			Point carBoxMidPt1 = getBoxMidPt(prevCarBox);
			Point carBoxMidPt2 = new Point();
			
//			VideoCapture c = new VideoCapture();
//			c.open(0);
//			System.out.println(c.get(5));
			
			if (((ImageFrameFiltered) filtered).isCarDetected() == true) {
				// If getter doesn't return new Rect object (meaning there is a car detected
				prevCarBox = ((ImageFrameFiltered) filtered).getCarBox();	// Set prev car box to current car box
				carBoxMidPt2 = getBoxMidPt(((ImageFrameFiltered) filtered).getCarBox());
				//System.out.print(carBoxMidPt1 + "\t" + carBoxMidPt2 + "\n");
				double speed = getCarSpeed(carBoxMidPt1, carBoxMidPt2);				
				int speedRounded = (int)(speed * 100) / 100;
				
				((ImageFrameFiltered) filtered).setCarSpeed(speedRounded);
			}
			else {
				// If car is not detected, then set previous car point to previous car point not 0,0
				carBoxMidPt1 = carBoxMidPt2;
			}
			
			Thread.sleep(1000);
			i = Integer.toString(Integer.parseInt(i) + 1);
		}
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
