package com.pothole;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;

public class PotholeDetector {

    static {
        org.bytedeco.javacpp.Loader.load(opencv_core.class);
    }

    // ================= MAIN =================
    public static String analyzeImage(String imagePath) {

        Mat img = opencv_imgcodecs.imread(imagePath);

        if (img.empty()) {
            return "❌ Invalid Image";
        }

        // Step 1: Road check
        if (!isRoad(img)) {
            return "❌ Not a Road Image";
        }

        // Step 2: Pothole detection
        if (hasPothole(img)) {
            return "⚠️ Pothole Detected";
        }

        return "✅ No Pothole";
    }

    // ================= ROAD DETECTION =================
    private static boolean isRoad(Mat img) {

        Mat gray = new Mat();
        opencv_imgproc.cvtColor(img, gray, opencv_imgproc.COLOR_BGR2GRAY);

        // Texture check using std deviation
        Mat mean = new Mat();
        Mat stddev = new Mat();
        opencv_core.meanStdDev(gray, mean, stddev);

        double std = stddev.createIndexer().getDouble(0);

        // LOGIC:
        // <20 → logo / smooth image
        // >90 → too noisy / random
        // between → road

        return std > 20 && std < 90;
    }

    // ================= POTHOLE DETECTION =================
    private static boolean hasPothole(Mat img) {

        int width = img.cols();
        int height = img.rows();

        // ✅ Focus on center area (road region)
        Rect roi = new Rect(
                (int) (width * 0.25),
                (int) (height * 0.30),
                (int) (width * 0.50),
                (int) (height * 0.40)
        );

        Mat road = new Mat(img, roi);

        Mat gray = new Mat();
        opencv_imgproc.cvtColor(road, gray, opencv_imgproc.COLOR_BGR2GRAY);

        // Blur
        Mat blur = new Mat();
        opencv_imgproc.GaussianBlur(gray, blur, new Size(7, 7), 0);

        // Dark regions
        Mat thresh = new Mat();
        opencv_imgproc.threshold(blur, thresh, 80, 255, opencv_imgproc.THRESH_BINARY_INV);

        // Edge detection
        Mat edges = new Mat();
        opencv_imgproc.Canny(blur, edges, 50, 150);

        // Combine edges + dark
        opencv_core.bitwise_and(thresh, edges, thresh);

        // Clean noise
        Mat kernel = opencv_imgproc.getStructuringElement(
                opencv_imgproc.MORPH_ELLIPSE,
                new Size(5, 5)
        );
        opencv_imgproc.morphologyEx(thresh, thresh, opencv_imgproc.MORPH_CLOSE, kernel);

        // Contours
        MatVector contours = new MatVector();
        opencv_imgproc.findContours(
                thresh,
                contours,
                new Mat(),
                opencv_imgproc.RETR_EXTERNAL,
                opencv_imgproc.CHAIN_APPROX_SIMPLE
        );

        double roiArea = roi.width() * roi.height();

        for (int i = 0; i < contours.size(); i++) {

            double area = opencv_imgproc.contourArea(contours.get(i));

            // Ignore small noise
            if (area < 2000) continue;

            // Ignore too large regions
            if (area > roiArea * 0.4) continue;

            double perimeter = opencv_imgproc.arcLength(new Mat(contours.get(i)), true);
            if (perimeter == 0) continue;

            double circularity = (4 * Math.PI * area) / (perimeter * perimeter);

            // pothole shape check
            if (circularity > 0.2 && circularity < 0.8) {
                return true;
            }
        }

        return false;
    }
}
