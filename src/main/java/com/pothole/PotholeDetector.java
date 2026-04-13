package com.pothole;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import java.util.ArrayList;

public class PotholeDetector {
    
    static {
        // JavaCV native library load
        System.load(org.bytedeco.javacpp.Loader.load(org.bytedeco.opencv.global.opencv_core.class));
    }
    
    /**
     * इमेज रस्त्याची आहे की नाही हे तपासते.
     * @param imagePath इमेजचा पाथ
     * @return true - रस्त्याची वैध इमेज, false - अवैध
     */
    public static boolean isValidRoadImage(String imagePath) {
        try {
            Mat img = opencv_imgcodecs.imread(imagePath);
            if (img.empty()) return false;
            
            // 1. आकार तपासा (खूप लहान किंवा खूप मोठी नसावी)
            int width = img.cols();
            int height = img.rows();
            if (width < 200 || height < 200 || width > 4000 || height > 4000) {
                return false;
            }
            
            // 2. ग्रॅस्केलमध्ये रुपांतर
            Mat gray = new Mat();
            opencv_imgproc.cvtColor(img, gray, opencv_imgproc.COLOR_BGR2GRAY);
            
            // 3. ब्लर करून नॉइज कमी करा
            Mat blurred = new Mat();
            opencv_imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 2);
            
            // 4. Edge detection (Canny) – रस्त्यात सरळ रेषा व कोपरे असतात
            Mat edges = new Mat();
            opencv_imgproc.Canny(blurred, edges, 50, 150);
            
            // 5. Edge density (edge pixels ची टक्केवारी)
            int totalPixels = width * height;
            int edgePixels = opencv_imgproc.countNonZero(edges);
            double edgeDensity = (double) edgePixels / totalPixels;
            
            // रस्त्यात साधारण 5% ते 40% edge density असते.
            // खूप जास्त edges (उदा. झाडे, कागद) किंवा खूप कमी (भिंत, आकाश) अवैध
            if (edgeDensity < 0.02 || edgeDensity > 0.60) {
                return false;
            }
            
            // 6. रंग तपासणी – रस्त्याचा रंग साधारण गडद ते हलका राखाडी/तपकीरी असतो.
            // सोपी पद्धत: average brightness मोजा
            Mat histogram = new Mat();
            // brightness मोजण्यासाठी grayscale चा mean
            Scalar mean = opencv_imgproc.mean(gray);
            double avgBrightness = mean.val(0);
            // रस्त्याची brightness 40 ते 200 दरम्यान असते. 40 पेक्षा कमी (खूप गडद) किंवा 200 पेक्षा जास्त (खूप उजळ) असल्यास अवैध
            if (avgBrightness < 40 || avgBrightness > 210) {
                return false;
            }
            
            // 7. Aspect ratio check – रस्त्याचे फोटो साधारण 1:1 ते 3:2 असतात.
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
