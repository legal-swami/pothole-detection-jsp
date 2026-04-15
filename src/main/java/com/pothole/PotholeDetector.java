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
        if (img.empty()) return "❌ Invalid Image";

        if (!isRoad(img)) {
            return "❌ Not a Road Image";
        }

        if (hasPothole(img)) {
            return "⚠️ Pothole Detected";
        }

        return "✅ No Pothole";
    }

    // ================= ROAD DETECTION =================
    private static boolean isRoad(Mat img) {

        Mat gray = new Mat();
        opencv_imgproc.cvtColor(img, gray, opencv_imgproc.COLOR_BGR2GRAY);

        // texture check
        Mat mean = new Mat();
        Mat stddev = new Mat();
        opencv_core.meanStdDev(gray, mean, stddev);

        double std = stddev.createIndexer().getDouble(0);

        // road = medium texture
        if (std < 15) return false;   // too smooth (logo etc)
        if (std > 80) return false;   // too noisy (random)

        return true;
    }

    // ================= POTHOLE DETECTION =================
    private static boolean hasPothole(Mat img) {

        Mat gray = new Mat();
        opencv_imgproc.cvtColor(img, gray, opencv_imgproc.COLOR_BGR2GRAY);

        // blur
        Mat blur = new Mat();
        opencv_imgproc.GaussianBlur(gray, blur, new Size(5,5), 0);

        // detect dark regions
        Mat thresh = new Mat();
        opencv_imgproc.threshold(blur, thresh, 85, 255, opencv_imgproc.THRESH_BINARY_INV);

        // clean noise
        Mat kernel = opencv_imgproc.getStructuringElement(
                opencv_imgproc.MORPH_ELLIPSE,
                new Size(5,5)
        );
        opencv_imgproc.morphologyEx(thresh, thresh, opencv_imgproc.MORPH_CLOSE, kernel);

        // find contours
        MatVector contours = new MatVector();
        opencv_imgproc.findContours(
                thresh,
                contours,
                new Mat(),
                opencv_imgproc.RETR_EXTERNAL,
                opencv_imgproc.CHAIN_APPROX_SIMPLE
        );

        double imgArea = img.rows() * img.cols();

        for (int i = 0; i < contours.size(); i++) {

            double area = opencv_imgproc.contourArea(contours.get(i));

            // ignore small noise
            if (area < 1500) continue;

            // ignore very large regions
            if (area > imgArea * 0.25) continue;

            // shape check
            double peri = opencv_imgproc.arcLength(new Mat(contours.get(i)), true);
            if (peri == 0) continue;

            double circularity = (4 * Math.PI * area) / (peri * peri);

            // pothole = irregular shape
            if (circularity > 0.2 && circularity < 0.85) {
                return true;
            }
        }

        return false;
    }
}
