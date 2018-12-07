import java.awt.image.BufferedImage;
import java.nio.Buffer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.*;
import org.opencv.imgproc.Imgproc;

// From link:
// http://computervisionandjava.blogspot.com/2013/10/java-opencv-webcam.html

public class Mat2Image {
    Mat mat = new Mat();
    BufferedImage img;
    byte[] dat;
    public Mat2Image() {
    }
    public Mat2Image(Mat mat) {
        getSpace(mat);
    }
    
    
    // For regular RGB/BGR images
    public void getSpace(Mat mat) {
        this.mat = mat;
        int w = mat.cols(), h = mat.rows();
        if (dat == null || dat.length != w * h * 3)
            dat = new byte[w * h * 3];
        if (img == null || img.getWidth() != w || img.getHeight() != h
            || img.getType() != BufferedImage.TYPE_3BYTE_BGR)
                img = new BufferedImage(w, h, 
                            BufferedImage.TYPE_3BYTE_BGR);
        }
        BufferedImage getImage(Mat mat){
            getSpace(mat);
            mat.get(0, 0, dat);
            
            // BGR to RGB conversion
            // From link: https://stackoverflow.com/questions/41820240/convert-color-channels-from-bgr-to-rgb
            for (int i = 0; i < dat.length; i+= 3) {
                byte redChannel = dat[i];
                byte blueChannel = dat[i+2];

                dat[i] = blueChannel;		// Setting original red channel to blue channel
                dat[i+2] = redChannel;		// Setting original blue channel to red channel

            }
            
            img.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), dat);
            return img;
    }
    
    // For HSV images
    public void getSpaceHSV(Mat mat) {
        this.mat = mat;
        int w = mat.cols(), h = mat.rows();
        if (dat == null || dat.length != w * h * 4)
            dat = new byte[w * h * 4];
        if (img == null || img.getWidth() != w || img.getHeight() != h
            || img.getType() != BufferedImage.TYPE_BYTE_BINARY)
                img = new BufferedImage(w, h, 
                            BufferedImage.TYPE_BYTE_BINARY);
        }
    
        BufferedImage getImageHSV(Mat mat){
            getSpaceHSV(mat);
            mat.get(0, 0, dat);
            
            img.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), dat);
            return img;
    }
     
    
    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}