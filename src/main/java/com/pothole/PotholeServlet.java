package com.pothole;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

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

            // Save file
            filePart.write(filePath);

            // ✅ SINGLE METHOD CALL (FINAL FIX)
            String result = PotholeDetector.analyzeImage(filePath);

            request.setAttribute("message", result);
            request.setAttribute("imagePath", fileName);

            // Error flag based on result
            if (result.contains("❌")) {
                request.setAttribute("isError", true);
            } else {
                request.setAttribute("isError", false);
            }

            request.getRequestDispatcher("result.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "❌ Error processing image");
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
