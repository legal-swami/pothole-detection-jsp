<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detection Result</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        .container {
            background: white;
            border-radius: 20px;
            padding: 40px;
            max-width: 650px;
            width: 100%;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            text-align: center;
        }
        .found {
            color: #dc3545;
            font-size: 28px;
            margin: 15px 0;
        }
        .not-found {
            color: #28a745;
            font-size: 28px;
            margin: 15px 0;
        }
        .image-box {
            margin: 20px 0;
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
        img {
            max-width: 100%;
            height: auto;
            display: block;
        }
        .btn {
            display: inline-block;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            text-decoration: none;
            padding: 10px 25px;
            border-radius: 30px;
            margin-top: 20px;
            transition: transform 0.2s;
        }
        .btn:hover {
            transform: scale(1.05);
        }
        .back-btn {
            background: #6c757d;
            margin-left: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>🔍 Detection Result</h2>
        
        <%
            String message = (String) request.getAttribute("message");
            String imagePath = (String) request.getAttribute("imagePath");
            String statusClass = (message != null && message.toLowerCase().contains("pothole")) ? "found" : "not-found";
            if (message == null) message = "No result. Please try again.";
        %>
        
        <div class="<%= statusClass %>">
            <%= message %>
        </div>
        
        <div class="image-box">
            <img src="uploads/<%= imagePath %>" alt="Uploaded Road Image">
        </div>
        
        <div>
            <a href="index.jsp" class="btn">📸 Upload Another Image</a>
            <a href="index.jsp" class="btn back-btn">🏠 Back to Home</a>
        </div>
    </div>
</body>
</html>
