// Graphics Packages
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.border.*;

import org.opencv.core.Mat;
// OpenCV Packages
import org.opencv.videoio.*;

import carPipeline.CarPipeline.Line;

public class ImageFrameFiltered extends ImageFrame {
	ArrayList<Line> allLines;
	
	public ImageFrameFiltered(Mat img, ArrayList<Line> lines) {
        super(img);
        allLines = lines;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		// Draw car line
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 255, 0));	// Set green line color
		g2.setStroke(new BasicStroke(3));	// Set line width to 3px
		
		for (int i = 0; i < allLines.size(); i++) {
			Line aLine = allLines.get(i);
			g2.drawLine((int)aLine.x1, (int)aLine.x2, (int)aLine.y1, (int)aLine.y2);
		}
	}
	
	// Setter for carLine variable
	public void setCarLines(ArrayList<Line> l) {
		allLines = l;
	}
}
