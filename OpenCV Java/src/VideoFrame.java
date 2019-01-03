// Graphics Packages
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import javax.swing.border.*;

// OpenCV packages
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class VideoFrame extends JFrame {
	private JPanel contentPane;
	private VideoCap videoCap;
	
	private ArrayList<MatOfPoint> carContours;
	private Rect carRect;
	private boolean carDetected = false;
	private double carSpeed;
	
	public VideoFrame() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
	}
	
	public VideoFrame(VideoCap v) {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        videoCap = v;
	}
	
	public void paint(Graphics g) {
		g = contentPane.getGraphics();
		g.drawImage(videoCap.getOneFrame(), 0, 0, this);
		
		// Draw car rectangle
		// Assuming only 1 car contour was found
		if (carContours != null && carContours.size() != 0) {
			// Draw car box
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(255, 0, 213));
			g2.setStroke(new BasicStroke(3));		// Set line width to 3px
			
			Rect carRect = getCarBox();
			int x = (int)(carRect.tl().x);
			int y = (int)(carRect.tl().y);
			g2.drawRect(x, y, carRect.width, carRect.height);	
			
			Point carMidPt = getCarBoxMidPt();
			g2.drawRect((int)carMidPt.x, (int)carMidPt.y, 1, 1);
			
			// Draw car speed above box
			g2.setColor(new Color(255, 255, 0));	
			g2.setFont(new Font("Verdana", Font.BOLD, 30));
			String carSpeedToStr = Double.toString(carSpeed) + " mph";
			g2.drawString(carSpeedToStr, x, y);
		}
	}
	
	public void getFrameMat(Mat matToSet) {
		videoCap.grab(matToSet);
	}
	
	// Setter for carLine variable
	public void setCarContour(ArrayList<MatOfPoint> c) {
		carContours = c;
		
        if (c != null && c.size() != 0) {
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
