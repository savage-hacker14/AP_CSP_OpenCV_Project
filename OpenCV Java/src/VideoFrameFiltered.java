// Written by Jacob Krucinski on 12/05/18

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import carPipeline.CarPipeline.Line;

public class VideoFrameFiltered extends VideoFrame {
	// Instance variables
	private ArrayList<MatOfPoint> carContours;
	private Rect carRect;
	private boolean carDetected;
	private double carSpeed;
	
	// Default constructor
	public VideoFrameFiltered() {
		super();
	}
	
	// My custom constructor
	public VideoFrameFiltered(ArrayList<MatOfPoint> c) {
		super();
		carContours = c;
	}
	
	public void paint(Graphics g) {
		// Draw raw frame
		super.paint(g);
		
		// Draw car line
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 255, 0));	// Set green line color
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
	}
	
	public Point getCarBoxMidPt() {
		int midX = (int)(carRect.tl().x + carRect.br().x) / 2;
		int midY = (int)(carRect.tl().y + carRect.br().y) / 2;
		
		return new Point(midX, midY);
	}
	
	public Color getCarColor() {
		Point middle = getCarBoxMidPt();
		Mat image = new Mat();
		getFrameMat(image);
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
