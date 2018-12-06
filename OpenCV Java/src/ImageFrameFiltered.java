// Graphics Packages
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.border.*;

import org.opencv.core.Mat;
// OpenCV Packages
import org.opencv.videoio.*;

import carPipeline.CarPipeline.Line;

public class ImageFrameFiltered extends ImageFrame {
	Line carLine;
	
	public ImageFrameFiltered(Mat img, Line cLine) {
        super(img);
        carLine = cLine;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		// Draw car line
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 255, 0));	// Set green line color
		g2.setStroke(new BasicStroke(3));	// Set line width to 3px
		g2.drawLine((int)carLine.x1, (int)carLine.x2, (int)carLine.y1, (int)carLine.y2);
	}
	
	// Setter for carLine variable
	public void setCarLine(Line l) {
		carLine = l;
	}
}
