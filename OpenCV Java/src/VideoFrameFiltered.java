// Written by Jacob Krucinski on 12/05/18

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import carPipeline.CarPipeline.Line;

public class VideoFrameFiltered extends VideoFrame {
	// Instance variables
	ArrayList<MatOfPoint> carContours;
	
	// Default constructor
	public VideoFrameFiltered() {
		super();
	}
	
	// My custom constructor
	public VideoFrameFiltered(ArrayList<MatOfPoint> c) {
		super();
		carContours = c;
	}
	
	// Create VideoCap Object
	VideoCap videoCap = new VideoCap();
	
	public void paint(Graphics g) {
		// Draw raw frame
		super.paint(g);
		
		// Draw car line
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 255, 0));	// Set green line color
		g2.setStroke(new BasicStroke(3));	// Set line width to 3px
		
		Rect carRect = Imgproc.boundingRect(carContours.get(0));
		g2.drawRect((int)carRect.tl().x, (int)carRect.tl().y, carRect.width, carRect.height);

	}
	
	// Setter for carLine variable
	public void setCarContours(ArrayList<MatOfPoint> c) {
		carContours = c;
	}
}
