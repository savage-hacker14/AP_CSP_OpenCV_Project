import java.awt.*;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.*;
import javax.swing.JFrame;

// Written by Jacob Krucinski on 12/01/18

public class ViewWebcam {
	
	public static void main (String[] args) {
		// Build window
		JFrame window = new VideoFrame();
		Container pane = window.getContentPane();
		window.setTitle("Webcam Feed");
		window.setSize(665, 552);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// allows program termination when x is clicked on
		
		// Constantly refresh/update window
		while (true) {
			window.repaint();
			Dimension d = window.getSize();
			//System.out.print("H: " + d.getHeight() + "\t\t");
			//System.out.print("W: " + d.getWidth() + "\n");
		}
	}
}
