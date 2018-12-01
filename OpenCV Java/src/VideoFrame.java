// Graphics Packages
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

// OpenCV Packages
import org.opencv.videoio.*;

public class VideoFrame extends JFrame {
	private JPanel contentPane;
	
	public VideoFrame() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
	}
	
	// Create VideoCap Object
	VideoCap videoCap = new VideoCap();
	
	public void paint(Graphics g) {
		g = contentPane.getGraphics();
		g.drawImage(videoCap.getOneFrame(), 0, 0, this);
	}
	
	public static void main(String[] args) {
		
	}
}
