package com.pothole;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;

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

            // Edge check
            Mat edges = new Mat();
            opencv_imgproc.Canny(gray, edges, 50, 150);

            double edgeDensity = (double) opencv_core.countNonZero(edges) / (width * height);

            if (edgeDensity < 0.01 || edgeDensity > 0.6) return false;

            // Brightness check
            double brightness = opencv_core.mean(gray).get(0);
            if (brightness < 20 || brightness > 240) return false;

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

            int width = original.cols();
            int height = original.rows();

            // ROI (center road)
            int roiX = (int) (width * 0.30);
            int roiWidth = (int) (width * 0.40);

            Rect roi = new Rect(roiX, 0, roiWidth, height);
            Mat roadArea = new Mat(original, roi);

            // Gray
            Mat gray = new Mat();
            opencv_imgproc.cvtColor(roadArea, gray, opencv_imgproc.COLOR_BGR2GRAY);

            // Blur
            Mat blurred = new Mat();
            opencv_imgproc.GaussianBlur(gray, blurred, new Size(7, 7), 2);

            // Adaptive Threshold (IMPORTANT FIX)
            Mat thresh = new Mat();
            opencv_imgproc.adaptiveThreshold(
                    blurred,
                    thresh,
                    255,
                    opencv_imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                    opencv_imgproc.THRESH_BINARY_INV,
                    11,
                    2
            );

            // Morphological cleaning (REMOVE NOISE)
            Mat kernel = opencv_imgproc.getStructuringElement(
                    opencv_imgproc.MORPH_ELLIPSE,
                    new Size(5, 5)
            );

            opencv_imgproc.morphologyEx(thresh, thresh, opencv_imgproc.MORPH_CLOSE, kernel);
            opencv_imgproc.morphologyEx(thresh, thresh, opencv_imgproc.MORPH_OPEN, kernel);

            // Find contours
            MatVector contours = new MatVector();
            Mat hierarchy = new Mat();

            opencv_imgproc.findContours(
                    thresh,
                    contours,
                    hierarchy,
                    opencv_imgproc.RETR_EXTERNAL,
                    opencv_imgproc.CHAIN_APPROX_SIMPLE
            );

            for (int i = 0; i < contours.size(); i++) {

                double area = opencv_imgproc.contourArea(contours.get(i));

                // Updated area range (IMPORTANT)
                if (area < 1500 || area > 20000) continue;

                // Perimeter
                double perimeter = opencv_imgproc.arcLength(
                        new Mat(contours.get(i)),
                        true
                );

                if (perimeter == 0) continue;

                // Circularity check (IMPORTANT FIX)
                double circularity = (4 * Math.PI * area) / (perimeter * perimeter);

                if (circularity < 0.2) continue; // reject weird shapes

                // Edge density inside contour
                Rect rect = opencv_imgproc.boundingRect(contours.get(i));
                Mat roiCheck = new Mat(gray, rect);

                Mat edgeCheck = new Mat();
                opencv_imgproc.Canny(roiCheck, edgeCheck, 50, 150);

                double edgeDensity = (double) opencv_core.countNonZero(edgeCheck) /
                        (rect.width() * rect.height());

                // pothole should not be too smooth OR too sharp
                if (edgeDensity > 0.05 && edgeDensity < 0.25) {
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
