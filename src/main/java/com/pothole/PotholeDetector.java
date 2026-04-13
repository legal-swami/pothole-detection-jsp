package com.pothole;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import java.util.ArrayList;

public class PotholeDetector {
    
    static {
        // JavaCV स्वतःच native library load करते
        System.load(org.bytedeco.javacpp.Loader.load(org.bytedeco.opencv.global.opencv_core.class));
    }
    
    public static boolean detectPothole(String imagePath) {
        try {
            // Image load करा
            Mat original = opencv_imgcodecs.imread(imagePath);
            if (original.empty()) return false;
            
            // Grayscale मध्ये रुपांतर
            Mat gray = new Mat();
            opencv_imgproc.cvtColor(original, gray, opencv_imgproc.COLOR_BGR2GRAY);
            
            // Gaussian blur
            Mat blurred = new Mat();
            opencv_imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 2);
            
            // Threshold (गडद भाग शोधा)
            Mat thresh = new Mat();
            opencv_imgproc.threshold(blurred, thresh, 80, 255, opencv_imgproc.THRESH_BINARY_INV);
            
            // Contours शोधा
            MatVector contours = new MatVector();
            Mat hierarchy = new Mat();
            opencv_imgproc.findContours(thresh, contours, hierarchy, opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);
            
            // प्रत्येक contour चे क्षेत्रफळ तपासा
            for (int i = 0; i < contours.size(); i++) {
                double area = opencv_imgproc.contourArea(contours.get(i));
                if (area > 500 && area < 5000) {
                    return true;
                }
            }
            return false;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
