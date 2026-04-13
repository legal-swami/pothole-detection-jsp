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
     * फक्त अत्यंत स्पष्ट अवैध इमेज (उदा. पांढरा कागद, कोणताही किनारा नसलेला) नाकारते.
     */
    public static boolean isValidRoadImage(String imagePath) {
        try {
            Mat img = opencv_imgcodecs.imread(imagePath);
            if (img.empty()) return false;
            
            int width = img.cols();
            int height = img.rows();
            // खूप लहान फोटो (उदा. 50x50) नको
            if (width < 100 || height < 100) return false;
            
            Mat gray = new Mat();
            opencv_imgproc.cvtColor(img, gray, opencv_imgproc.COLOR_BGR2GRAY);
            
            // 1. Edge density (Canny)
            Mat edges = new Mat();
            opencv_imgproc.Canny(gray, edges, 50, 150);
            int totalPixels = width * height;
            int edgePixels = opencv_core.countNonZero(edges);
            double edgeDensity = (double) edgePixels / totalPixels;
            
            // जर edge density 0.7 पेक्षा जास्त (खूप गोंधळलेला, उदा. सांडलेला पेपर) किंवा 0.005 पेक्षा कमी (एकसारखा रंग) तर अवैध
            if (edgeDensity > 0.75 || edgeDensity < 0.005) {
                return false;
            }
            
            // 2. Brightness (सरासरी तेजस्वीता)
            Scalar mean = opencv_core.mean(gray);
            double avgBrightness = mean.get(0);
            // खूप गडद (रात्रीचा फोटो) किंवा खूप उजळ (सूर्यप्रकाशात पांढरा कागद) तपासा
            if (avgBrightness < 15 || avgBrightness > 245) {
                return false;
            }
            
            // 3. Brightness variance (मानक विचलन) – एकसारख्या फोटोसाठी (उदा. भिंत)
            Mat stdDev = new Mat();
            opencv_core.meanStdDev(gray, mean, stdDev);
            double stdDevVal = stdDev.createIndexer().getDouble(0);
            if (stdDevVal < 5.0) {  // खूप एकसारखा रंग, रस्ता नाही
                return false;
            }
            
            // 4. Aspect ratio – अतिशय लांब किंवा अरुंद फोटो (उदा. बारकोड) नाकारा
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
     * खड्डा शोधण्याची मूळ मेथड (बदललेली नाही)
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
