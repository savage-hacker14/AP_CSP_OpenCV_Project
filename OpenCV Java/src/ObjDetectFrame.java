// Written by Jacob Krucinski on 12/02/18

// Required imports
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;
import org.opencv.core.*;

public class ObjDetectFrame extends JFrame {
	private JPanel contentPane;
	private BufferedImage img;
	private MatOfRect rects;
	
	public ObjDetectFrame(BufferedImage imgToDisp) {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        img = imgToDisp;
	}
	
	public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(img, 0, 0, getWidth(), getHeight(), null);	
	}
}
