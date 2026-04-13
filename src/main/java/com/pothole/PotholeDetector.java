package com.pothole;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import java.util.ArrayList;

public class PotholeDetector {
    
    static {
        // JavaCV native library load
        org.bytedeco.javacpp.Loader.load(org.bytedeco.opencv.global.opencv_core.class);
    }
    
    /**
     * इमेज रस्त्याची आहे की नाही ते तपासते.
     */
    public static boolean isValidRoadImage(String imagePath) {
        try {
            Mat img = opencv_imgcodecs.imread(imagePath);
            if (img.empty()) return false;
            
            int width = img.cols();
            int height = img.rows();
            if (width < 200 || height < 200 || width > 4000 || height > 4000) {
                return false;
            }
            
            Mat gray = new Mat();
            opencv_imgproc.cvtColor(img, gray, opencv_imgproc.COLOR_BGR2GRAY);
            
            Mat blurred = new Mat();
            opencv_imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 2);
            
            Mat edges = new Mat();
            opencv_imgproc.Canny(blurred, edges, 50, 150);
            
            int totalPixels = width * height;
            int edgePixels = opencv_core.countNonZero(edges);
            double edgeDensity = (double) edgePixels / totalPixels;
            
            if (edgeDensity < 0.02 || edgeDensity > 0.60) {
                return false;
            }
            
            Scalar mean = opencv_core.mean(gray);
            double avgBrightness = mean.get(0);  // get(0) instead of val(0)
            if (avgBrightness < 40 || avgBrightness > 210) {
                return false;
            }
            
            double aspect = (double) width / height;
            if (aspect < 0.6 || aspect > 2.5) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * खड्डा शोधण्याची मूळ मेथड
     */
    public static boolean detectPothole(String imagePath) {
        try {
            Mat original = opencv_imgcodecs.imread(imagePath);
            if (original.empty()) return false;
            
            Mat gray = new Mat();
            opencv_imgproc.cvtColor(original, gray, opencv_imgproc.COLOR_BGR2GRAY);
            
            Mat blurred = new Mat();
            opencv_imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 2);
            
            Mat thresh = new Mat();
            opencv_imgproc.threshold(blurred, thresh, 80, 255, opencv_imgproc.THRESH_BINARY_INV);
            
            MatVector contours = new MatVector();
            Mat hierarchy = new Mat();
            opencv_imgproc.findContours(thresh, contours, hierarchy, opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);
            
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
