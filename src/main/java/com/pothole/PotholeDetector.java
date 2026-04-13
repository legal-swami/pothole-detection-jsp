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
    
    /**
     * फोटो रस्त्याचा आहे की नाही ते तपासते.
     */
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
            
            // Standard deviation
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
    
    /**
     * फक्त रस्त्याच्या मध्यभागी (मधले 40% क्षेत्र) खड्डा शोधते.
     * यामुळे रस्त्याच्या बाजूला असलेले खड्डे, विहीर, कुंड यांचा चुकीचा अहवाल येणार नाही.
     */
    public static boolean detectPothole(String imagePath) {
        try {
            Mat original = opencv_imgcodecs.imread(imagePath);
            if (original.empty()) return false;
            
            int width = original.cols();
            int height = original.rows();
            
            // Region of Interest (ROI) - फोटोच्या मधले 30% ते 70% क्षेत्र
            // उदा. रुंदी 1000 पिक्सेल असेल तर 300 ते 700 पर्यंत (म्हणजे मधले 40%)
            int roiX = (int)(width * 0.30);
            int roiWidth = (int)(width * 0.40);
            
            // सुरक्षितता: roiX आणि roiWidth बाउंडच्या बाहेर नाहीत याची खात्री
            if (roiX < 0) roiX = 0;
            if (roiX + roiWidth > width) roiWidth = width - roiX;
            
            Rect roi = new Rect(roiX, 0, roiWidth, height);
            Mat roadArea = new Mat(original, roi);
            
            // ग्रेस्केल
            Mat gray = new Mat();
            opencv_imgproc.cvtColor(roadArea, gray, opencv_imgproc.COLOR_BGR2GRAY);
            
            // Gaussian blur
            Mat blurred = new Mat();
            opencv_imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 2);
            
            // थ्रेशोल्ड – गडद भाग शोधा
            Mat thresh = new Mat();
            opencv_imgproc.threshold(blurred, thresh, 80, 255, opencv_imgproc.THRESH_BINARY_INV);
            
            // कॉन्टूर्स शोधा
            MatVector contours = new MatVector();
            Mat hierarchy = new Mat();
            opencv_imgproc.findContours(thresh, contours, hierarchy, opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);
            
            // प्रत्येक contour चे क्षेत्रफळ तपासा
            for (int i = 0; i < contours.size(); i++) {
                double area = opencv_imgproc.contourArea(contours.get(i));
                // खड्ड्याचा सामान्य आकार (पिक्सेलमध्ये)
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
