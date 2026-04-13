package com.pothole;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/PotholeServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class PotholeServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        
        try {
            Part filePart = request.getPart("roadImage");
            String fileName = extractFileName(filePart);
            String filePath = uploadPath + File.separator + fileName;
            
            // Save the uploaded file
            filePart.write(filePath);
            
            // ✅ STEP 1: प्रथम इमेज वैध रस्त्याची आहे का ते तपासा
            boolean isValidRoad = PotholeDetector.isValidRoadImage(filePath);
            
            if (!isValidRoad) {
                // अवैध इमेज – error message पाठवा
                request.setAttribute("message", "❌ अवैध फोटो! कृपया फक्त रस्त्याचा स्पष्ट फोटो अपलोड करा.");
                request.setAttribute("imagePath", fileName);
                request.setAttribute("isError", true);
                request.getRequestDispatcher("result.jsp").forward(request, response);
                return;
            }
            
            // ✅ STEP 2: वैध रस्ता असल्यास खड्डा तपासा
            boolean hasPothole = PotholeDetector.detectPothole(filePath);
            
            String message;
            if (hasPothole) {
                message = "⚠️ POTHOLE FOUND on road! Please drive carefully.";
            } else {
                message = "✅ No pothole detected. Road is safe.";
            }
            
            request.setAttribute("message", message);
            request.setAttribute("imagePath", fileName);
            request.setAttribute("isError", false);
            request.getRequestDispatcher("result.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "❌ त्रुटी: फोटो प्रोसेस करताना बिघाड. कृपया पुन्हा प्रयत्न करा.");
            request.setAttribute("imagePath", "");
            request.setAttribute("isError", true);
            request.getRequestDispatcher("result.jsp").forward(request, response);
        }
    }
    
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf("=") + 2, token.length() - 1);
                fileName = new File(fileName).getName();
                String timestamp = String.valueOf(System.currentTimeMillis());
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    return fileName.substring(0, dotIndex) + "_" + timestamp + fileName.substring(dotIndex);
                } else {
                    return fileName + "_" + timestamp;
                }
            }
        }
        return "image_" + System.currentTimeMillis() + ".jpg";
    }
}
