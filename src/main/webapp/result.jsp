<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Detection Result</title>
    <style>
        body { font-family: Arial; text-align: center; margin-top: 50px; }
        .container { width: 600px; margin: auto; padding: 20px; border: 2px solid #ccc; border-radius: 10px; }
        .pothole-found { color: red; font-size: 24px; }
        .pothole-not-found { color: green; font-size: 24px; }
        img { max-width: 100%; margin: 20px 0; }
    </style>
</head>
<body>
    <div class="container">
        <h2>🔍 Detection Result:</h2>
        <%
            String message = (String) request.getAttribute("message");
            String imagePath = (String) request.getAttribute("imagePath");
            String statusClass = message.contains("found") ? "pothole-found" : "pothole-not-found";
        %>
        <h3 class="<%= statusClass %>">📢 <%= message %></h3>
        <img src="uploads/<%= imagePath %>" alt="Uploaded Image"/>
        <br/>
        <a href="index.jsp">⬅ Upload Another Image</a>
    </div>
</body>
</html>
