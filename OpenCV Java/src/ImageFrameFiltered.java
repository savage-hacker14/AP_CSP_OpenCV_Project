// Graphics Packages
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.swing.border.*;

import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
// OpenCV Packages
import org.opencv.videoio.*;

import carPipeline.CarPipeline.Line;

public class ImageFrameFiltered extends ImageFrame {
	ArrayList<MatOfPoint> carContours;
	private boolean carDetected;
	
	public ImageFrameFiltered() {
		super();
		carDetected = false;
	}
	
	public ImageFrameFiltered(Mat img) {
        super(img);
        carContours = new ArrayList<MatOfPoint>();
        carDetected = false;
	}
	
	public ImageFrameFiltered(Mat img, ArrayList<MatOfPoint> c) {
        super(img);
        carContours = c;
        
        if (c != null && c.size() != 0) {
        	carDetected = true;
        }
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
//		Mat2Image converter = new Mat2Image();
//		BufferedImage img_BuffImg = converter.getImageHSV(image);
//		g.drawImage(img_BuffImg, 0, 20, this.getWidth(), this.getHeight(), null);
		
		// Draw car line
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(255, 0, 213));	// Set green line color
		g2.setStroke(new BasicStroke(3));	// Set line width to 3px
		
		// Draw car rectangle
		// Assuming only 1 car contour was found
		if (carContours != null && carContours.size() != 0) {
			Rect carRect = Imgproc.boundingRect(carContours.get(0));
			g2.drawRect((int)(carRect.tl().x), (int)(carRect.tl().y) + 57, carRect.width, carRect.height);	
			// Do +30 or +57 on y coordinate depending on which computer program is run on
		}
	}
	
	// Setter for carLine variable
	public void setCarContour(ArrayList<MatOfPoint> c) {
		carContours = c;
		
        if (c != null && c.size() != 0) {
        	carDetected = true;
        }
	}
	
	public Rect getCarBox() {
		if (carContours != null && !(carContours.size() == 0)) {
			return Imgproc.boundingRect(carContours.get(0));
		}
		else {
			return new Rect();
		}
	}
	
	public boolean isCarDetected() {
		return carDetected;
	}
}
