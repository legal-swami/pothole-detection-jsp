package com.pothole;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;

public class PotholeDetector {

    static {
        org.bytedeco.javacpp.Loader.load(org.bytedeco.opencv.global.opencv_core.class);
    }

    // ===================== MAIN METHOD =====================
    public static String analyzeImage(String imagePath) {

        if (!isValidRoadImage(imagePath)) {
            return "❌ Invalid Image - Not a Road";
        }

        if (detectPothole(imagePath)) {
            return "⚠️ Pothole Detected";
        } else {
            return "✅ No Pothole (Road is Clear)";
        }
    }

    // ===================== ROAD VALIDATION =====================
    public static boolean isValidRoadImage(String imagePath) {
        try {
            Mat img = opencv_imgcodecs.imread(imagePath);
            if (img.empty()) return false;

            int width = img.cols();
            int height = img.rows();

            // Basic size check
            if (width < 200 || height < 200) return false;

            Mat gray = new Mat();
            opencv_imgproc.cvtColor(img, gray, opencv_imgproc.COLOR_BGR2GRAY);

            // Edge detection
            Mat edges = new Mat();
            opencv_imgproc.Canny(gray, edges, 50, 150);

            double edgeDensity = (double) opencv_core.countNonZero(edges) / (width * height);

            // 🚨 FIX: better filtering
            if (edgeDensity < 0.02 || edgeDensity > 0.25) {
                return false; // logo ya random image reject
            }

            // Texture check (VERY IMPORTANT)
            Mat laplacian = new Mat();
            opencv_imgproc.Laplacian(gray, laplacian, opencv_core.CV_64F);

            Mat mean = new Mat();
            Mat stddev = new Mat();
            opencv_core.meanStdDev(laplacian, mean, stddev);

            double texture = stddev.createIndexer().getDouble(0);

            if (texture < 10) return false; // smooth logo reject

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===================== POTHOLE DETECTION =====================
    public static boolean detectPothole(String imagePath) {
        try {
            Mat original = opencv_imgcodecs.imread(imagePath);
            if (original.empty()) return false;

            int width = original.cols();
            int height = original.rows();

            // Center ROI (road area)
            int roiX = (int) (width * 0.30);
            int roiWidth = (int) (width * 0.40);

            Rect roi = new Rect(roiX, 0, roiWidth, height);
            Mat roadArea = new Mat(original, roi);

            Mat gray = new Mat();
            opencv_imgproc.cvtColor(roadArea, gray, opencv_imgproc.COLOR_BGR2GRAY);

            // Blur
            Mat blurred = new Mat();
            opencv_imgproc.GaussianBlur(gray, blurred, new Size(7, 7), 2);

            // Adaptive threshold
            Mat thresh = new Mat();
            opencv_imgproc.adaptiveThreshold(
                    blurred,
                    thresh,
                    255,
                    opencv_imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                    opencv_imgproc.THRESH_BINARY_INV,
                    11,
                    3
            );

            // Morphology
            Mat kernel = opencv_imgproc.getStructuringElement(
                    opencv_imgproc.MORPH_ELLIPSE,
                    new Size(5, 5)
            );

            opencv_imgproc.morphologyEx(thresh, thresh, opencv_imgproc.MORPH_CLOSE, kernel);
            opencv_imgproc.morphologyEx(thresh, thresh, opencv_imgproc.MORPH_OPEN, kernel);

            // Contours
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

                // 🚨 FIX: better area
                if (area < 2000 || area > 30000) continue;

                double perimeter = opencv_imgproc.arcLength(new Mat(contours.get(i)), true);
                if (perimeter == 0) continue;

                double circularity = (4 * Math.PI * area) / (perimeter * perimeter);

                // pothole irregular hota hai
                if (circularity < 0.3 || circularity > 0.9) continue;

                // Darkness check (VERY IMPORTANT)
                Rect rect = opencv_imgproc.boundingRect(contours.get(i));
                Mat roiCheck = new Mat(gray, rect);

                double meanIntensity = opencv_core.mean(roiCheck).get(0);

                if (meanIntensity < 120) {
                    return true; // dark region = pothole
                }
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
