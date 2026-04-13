package com.pothole;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;

public class PotholeDetector {

    public static boolean detectPothole(String imagePath) {
        // OpenCV library load करा
        nu.pattern.OpenCV.loadLocally();

        try {
            // 1. Image वाचा
            Mat originalImage = Imgcodecs.imread(imagePath);
            if (originalImage.empty()) {
                return false;
            }

            // 2. Grayscale मध्ये रुपांतर करा
            Mat grayImage = new Mat();
            Imgproc.cvtColor(originalImage, grayImage, Imgproc.COLOR_BGR2GRAY);

            // 3. Blur करा (Noise कमी करण्यासाठी)
            Mat blurred = new Mat();
            Imgproc.GaussianBlur(grayImage, blurred, new Size(5, 5), 2);

            // 4. Threshold लावा (गडद भाग शोधण्यासाठी)
            Mat threshold = new Mat();
            Imgproc.threshold(blurred, threshold, 80, 255, Imgproc.THRESH_BINARY_INV);

            // 5. Contours शोधा
            ArrayList<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(threshold, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // 6. प्रत्येक Contour Check करा
            for (MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                // खड्ड्याच्या आकाराशी जुळणारे Area फिल्टर करा
                if (area > 500 && area < 5000) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
