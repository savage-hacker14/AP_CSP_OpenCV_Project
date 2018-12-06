import java.awt.image.*;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;

public class VideoCap {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    VideoCapture cap;
    Mat2Image mat2Img = new Mat2Image();

    VideoCap(){
        cap = new VideoCapture();
        cap.open(0);
        
        // Init camera feed to 640x480 resolution
        int widthID = 3;
        int heightID = 4;
        cap.set(widthID, 640);
        cap.set(heightID, 480);
    } 
 
    BufferedImage getOneFrame() {
        cap.read(mat2Img.mat);
        return mat2Img.getImage(mat2Img.mat);
    }
}
