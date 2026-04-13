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
    fileSizeThreshold = 1024 * 1024 * 2,   // 2MB
    maxFileSize = 1024 * 1024 * 10,        // 10MB
    maxRequestSize = 1024 * 1024 * 50      // 50MB
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
            
            // Perform detection
            boolean hasPothole = PotholeDetector.detectPothole(filePath);
            
            String message;
            if (hasPothole) {
                message = "⚠️ POTHOLE DETECTED! Please drive carefully.";
            } else {
                message = "✅ No pothole detected. Road is safe.";
            }
            
            request.setAttribute("message", message);
            request.setAttribute("imagePath", fileName);
            request.getRequestDispatcher("result.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error processing image: " + e.getMessage());
            request.setAttribute("imagePath", "");
            request.getRequestDispatcher("result.jsp").forward(request, response);
        }
    }
    
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf("=") + 2, token.length() - 1);
                // Sanitize filename
                fileName = new File(fileName).getName();
                // Add timestamp to avoid conflicts
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
