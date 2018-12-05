// Written by Jacob Krucinski on 12/05/18

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import carPipeline.CarPipeline.Line;

public class VideoFrameFiltered extends VideoFrame {
	// Instance variables
	Line carLine;
	
	// Default constructor
	public VideoFrameFiltered() {
		super();
	}
	
	// My custom constructor
	public VideoFrameFiltered(Line l) {
		super();
		carLine = l;
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
		g2.drawLine((int)carLine.x1, (int)carLine.x2, (int)carLine.y1, (int)carLine.y2);
	}
}
