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


public class CarDetection {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Initialize image processing object
		CarPipeline carDetector = new CarPipeline();
		
		// For image processing, load in baseline img for opencv absdiff
		Mat baseline = new Mat();
		baseline = Imgcodecs.imread(new File("XXXXXXX").getPath());
		
		// Init graphics/GUI
		// 1: Raw camera feed
		JFrame raw = new VideoFrame();
		Container pane = raw.getContentPane();
		raw.setTitle("Webcam Feed");
		raw.setSize(665, 552);					// Possibly optimize this line
		raw.setVisible(true);
		raw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// allows program termination when x is clicked on
		
		// 2: Filtered camera feed
		JFrame filtered = new VideoFrame();			// MODIFY THIS!!!
		Container pane2 = raw.getContentPane();
		filtered.setTitle("Webcam Feed");
		filtered.setSize(665, 552);					// Possibly optimize this line
		filtered.setVisible(true);
		filtered.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// allows program termination when x is clicked on

		
		// Forever loop
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
			ArrayList<Line> lines = carDetector.filterLinesOutput();
			
			
			// Display filter frame in new gui window
		}
	}
	
	public static Point getLineMidPt(Line l) {
		
	}

}
