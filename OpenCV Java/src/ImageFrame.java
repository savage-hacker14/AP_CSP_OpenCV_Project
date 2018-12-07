// Graphics Packages
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.border.*;

import org.opencv.core.Mat;
// OpenCV Packages
import org.opencv.videoio.*;

public class ImageFrame extends JFrame {
	protected JPanel contentPane;
	protected Mat image;
	
	public ImageFrame() {};
	
	public ImageFrame(Mat img) {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        image = img;
	}
	
	public void paint(Graphics g) {
		g = contentPane.getGraphics();
		
		Mat2Image converter = new Mat2Image();
		BufferedImage img_BuffImg = converter.getImage(image);
		
		g.drawImage(img_BuffImg, 0, 0, null);
	}
}
