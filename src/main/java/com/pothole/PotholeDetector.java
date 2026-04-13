package com.pothole;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import java.util.ArrayList;

public class PotholeDetector {
    
    static {
        org.bytedeco.javacpp.Loader.load(org.bytedeco.opencv.global.opencv_core.class);
    }
    
    public static boolean isValidRoadImage(String imagePath) {
        try {
            Mat img = opencv_imgcodecs.imread(imagePath);
            if (img.empty()) return false;
            
            int width = img.cols();
            int height = img.rows();
            if (width < 100 || height < 100) return false;
            
            Mat gray = new Mat();
            opencv_imgproc.cvtColor(img, gray, opencv_imgproc.COLOR_BGR2GRAY);
            
            // Edge density
            Mat edges = new Mat();
            opencv_imgproc.Canny(gray, edges, 50, 150);
            int totalPixels = width * height;
            int edgePixels = opencv_core.countNonZero(edges);
            double edgeDensity = (double) edgePixels / totalPixels;
            
            if (edgeDensity > 0.75 || edgeDensity < 0.005) {
                return false;
            }
            
            // Brightness
            Scalar meanScalar = opencv_core.mean(gray);
            double avgBrightness = meanScalar.get(0);
            if (avgBrightness < 15 || avgBrightness > 245) {
                return false;
            }
            
            // Standard deviation using Mat outputs
            Mat meanMat = new Mat();
            Mat stddevMat = new Mat();
            opencv_core.meanStdDev(gray, meanMat, stddevMat);
            double stdDevVal = stddevMat.createIndexer().getDouble(0);
            if (stdDevVal < 5.0) {
                return false;
            }
            
            double aspect = (double) width / height;
            if (aspect < 0.3 || aspect > 3.5) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
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
