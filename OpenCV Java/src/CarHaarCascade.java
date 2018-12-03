// Written by Jacob Krucinski on 12/02/18
// This program utilizes the Car HaarCascade

// Import required packages
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFrame;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

// I used some help from this link:
// https://www.programcreek.com/java-api-examples/?api=org.opencv.objdetect.CascadeClassifier

// After testing:
// Cascades don't work, I should find betters one
// Build a GRIP Pipeline instead!!

public class CarHaarCascade {
	// Load in opencv
    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
	public static void main (String[] args) {
		// Load in haar cascade for car detection
		CascadeClassifier carDetector = new CascadeClassifier(new File("HaarCascades/cars.xml").getPath());
		
		// Load in a test image
		Mat img = Imgcodecs.imread("ImagesForPipeline/010.jpeg");
		
		// Detect car using haar cascade
		MatOfRect cars = new MatOfRect();
		carDetector.detectMultiScale(img, cars);
		System.out.println(cars.toArray().length + " car(s) found!");
		
		for (Rect rect : cars.toArray()) {
			Imgproc.rectangle(img, new Point(rect.x, rect.y), 
					new Point(rect.x + rect.width, rect.y + rect.height), 
		            new Scalar(0, 255, 0));
		}
		
		// Convert final image (with recatangle) to buffered image
		Mat2Image imgConverter = new Mat2Image();
		BufferedImage carImg = imgConverter.getImage(img);
		
		// Build window
		JFrame window = new ObjDetectFrame(carImg);
		Container pane = window.getContentPane();
		window.setTitle("Image of Car");
		window.setSize((int)img.size().width, (int)img.size().height);			
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// allows program termination when x is clicked on
	}
}
