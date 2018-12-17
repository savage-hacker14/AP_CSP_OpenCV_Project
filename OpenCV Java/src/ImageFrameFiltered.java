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
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
// OpenCV Packages
import org.opencv.videoio.*;

import carPipeline.CarPipeline.Line;

public class ImageFrameFiltered extends ImageFrame {
	private ArrayList<MatOfPoint> carContours;
	private Rect carRect;
	private boolean carDetected;
	private double carSpeed;
	
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
		g2.setColor(new Color(255, 0, 213));
		g2.setStroke(new BasicStroke(3));	// Set line width to 3px
		
		// Draw car rectangle
		// Assuming only 1 car contour was found
		if (carContours != null && carContours.size() != 0) {
			Rect carRect = getCarBox();
			int x = (int)(carRect.tl().x);
			int y = (int)(carRect.tl().y) + 57;
			g2.drawRect(x, y, carRect.width, carRect.height);	
			// Do +30 or +57 on y coordinate depending on which computer program is run on
			
			// Draw car speed above box
			g2.setColor(new Color(255, 255, 0));	
			g2.setFont(new Font("Verdana", Font.BOLD, 30));
			String carSpeedToStr = Double.toString(carSpeed) + " mph";
			g2.drawString(carSpeedToStr, x, y);
			
		}
	}
	
	// Setter for carLine variable
	public void setCarContour(ArrayList<MatOfPoint> c) {
		carContours = c;
		
        if (carContours.size() != 0) {
        	carDetected = true;
        }
        else {
        	carDetected = false;
        }
	}
	
	public Point getCarBoxMidPt() {
		int midX = (int)(carRect.tl().x + carRect.br().x) / 2;
		int midY = (int)(carRect.tl().y + carRect.br().y) / 2;
		
		return new Point(midX, midY);
	}
	
	public Color getCarColor() {
		Point middle = getCarBoxMidPt();
		double[] pixelData = image.get((int)middle.x, (int)middle.y);
		
		// This is to fix a strange bug when the pixel data couldn't be acquired
		if (pixelData == null) {
			return new Color(0, 0, 0);
		}
		
		double r = pixelData[2];
		double g = pixelData[1];
		double b = pixelData[0];
		
		return new Color((int)r, (int)g, (int)b);
	}
	
	// Setter for carLine variable
	public void setCarSpeed(double s) {
		carSpeed = s;
	}
	
	public Rect getCarBox() {
		if (carContours != null && !(carContours.size() == 0)) {
			carRect = Imgproc.boundingRect(carContours.get(0));
			return carRect;
		}
		else {
			return new Rect();
		}
	}
	
	public boolean isCarDetected() {
		return carDetected;
	}
}
