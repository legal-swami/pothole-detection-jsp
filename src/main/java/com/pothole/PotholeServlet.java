package com.pothole;

import java.io.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/PotholeServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class PotholeServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Uploaded file मिळवा
            Part filePart = request.getPart("roadImage");
            String fileName = extractFileName(filePart);

            // 2. Upload directory तयार करा
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            // 3. File सेव्ह करा
            String filePath = uploadPath + File.separator + fileName;
            filePart.write(filePath);

            // 4. Pothole Detection Logic Call करा
            boolean hasPothole = PotholeDetector.detectPothole(filePath);

            // 5. Result message तयार करा
            String message;
            if (hasPothole) {
                message = "⚠️ POTHOLE found on road! Please drive carefully.";
            } else {
                message = "✅ No pothole detected. Road looks good!";
            }

            // 6. Result Page वर पाठवा
            request.setAttribute("message", message);
            request.setAttribute("imagePath", fileName);
            request.getRequestDispatcher("result.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error: " + e.getMessage());
            request.getRequestDispatcher("result.jsp").forward(request, response);
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf("=") + 2, token.length() - 1);
                return new File(fileName).getName();
            }
        }
        return "image_" + System.currentTimeMillis() + ".jpg";
    }
}
