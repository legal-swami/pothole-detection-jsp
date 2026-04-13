package com.pothole;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;

public class PotholeDetector {
    
    // Static initializer to load OpenCV native library
    static {
        try {
            // For hosted environments (Render/Railway) - OpenCV is already in system path
            nu.pattern.OpenCV.loadLocally();
        } catch (Exception e) {
            System.err.println("OpenCV load error: " + e.getMessage());
        }
    }
    
    /**
     * Detects if the given image contains a pothole.
     * @param imagePath Absolute path to the image file.
     * @return true if pothole detected, false otherwise.
     */
    public static boolean detectPothole(String imagePath) {
        try {
            // Read image
            Mat original = Imgcodecs.imread(imagePath);
            if (original.empty()) {
                System.err.println("Could not load image: " + imagePath);
                return false;
            }
            
            // Convert to grayscale
            Mat gray = new Mat();
            Imgproc.cvtColor(original, gray, Imgproc.COLOR_BGR2GRAY);
            
            // Gaussian blur to reduce noise
            Mat blurred = new Mat();
            Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 2);
            
            // Adaptive threshold works better for varying lighting
            Mat thresh = new Mat();
            Imgproc.adaptiveThreshold(blurred, thresh, 255, 
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
            
            // Find contours
            ArrayList<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            
            // Analyze each contour
            for (MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                // Potholes typically have area between 500 and 5000 pixels
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
