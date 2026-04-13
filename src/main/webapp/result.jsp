<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>परिणाम - Panvel Pothole Detection</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', 'Poppins', Arial, sans-serif;
            background: linear-gradient(135deg, #e0eafc 0%, #cfdef3 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        .container {
            background: white;
            border-radius: 32px;
            padding: 30px;
            max-width: 700px;
            width: 100%;
            box-shadow: 0 20px 35px rgba(0,0,0,0.2);
            text-align: center;
        }
        .logo-small {
            max-width: 100px;
            margin-bottom: 15px;
            border-radius: 12px;
        }
        .result-badge {
            font-size: 28px;
            font-weight: bold;
            padding: 12px 20px;
            border-radius: 60px;
            display: inline-block;
            margin: 15px 0;
        }
        .pothole {
            background: #ffe6e6;
            color: #c0392b;
            border-left: 6px solid #c0392b;
        }
        .safe {
            background: #e0f7e8;
            color: #2e7d32;
            border-left: 6px solid #2e7d32;
        }
        .image-box {
            margin: 25px 0;
            border-radius: 24px;
            overflow: hidden;
            box-shadow: 0 12px 28px rgba(0,0,0,0.15);
            background: #f2f2f2;
        }
        img {
            max-width: 100%;
            height: auto;
            display: block;
        }
        .btn-group {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
            margin-top: 25px;
        }
        .btn {
            background: #1e3c72;
            color: white;
            text-decoration: none;
            padding: 12px 26px;
            border-radius: 40px;
            font-weight: 600;
            transition: 0.2s;
            display: inline-block;
        }
        .btn-secondary {
            background: #ff8c42;
        }
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 18px rgba(0,0,0,0.1);
        }
        .footer {
            margin-top: 35px;
            font-size: 11px;
            color: #5a6874;
            border-top: 1px solid #e9ecef;
            padding-top: 20px;
        }
        @media (max-width: 550px) {
            .container { padding: 20px; }
            .btn { padding: 8px 18px; font-size: 14px; }
        }
    </style>
</head>
<body>
<div class="container">
    <img src="images/logo.jpeg" alt="Panvel Logo" class="logo-small" onerror="this.src='https://via.placeholder.com/100'">
    <h2>🔍 पोटहोल तपासणी परिणाम</h2>

    <%
        String message = (String) request.getAttribute("message");
        String imagePath = (String) request.getAttribute("imagePath");
        if (message == null) message = "तांत्रिक बिघाड, कृपया पुन्हा प्रयत्न करा.";
        boolean isPothole = message.toLowerCase().contains("pothole") || message.toLowerCase().contains("खड्डा");
    %>

    <div class="result-badge <%= isPothole ? "pothole" : "safe" %>">
        <%= isPothole ? "⚠️ खड्डा आढळला !" : "✅ रस्ता सुरक्षित, खड्डा नाही." %>
    </div>
    <p style="margin: 5px 0 10px; font-weight:500;"><%= message %></p>

    <div class="image-box">
        <img src="uploads/<%= imagePath %>" alt="Road Image">
    </div>

    <div class="btn-group">
        <a href="index.jsp" class="btn">📸 नवीन फोटो तपासा</a>
        <a href="index.jsp" class="btn btn-secondary">🏠 मुख्यपृष्ठ</a>
    </div>

    <div class="footer">
        © A.R. Ghorpade – Panvel 2.0 | Not for Distribution<br/>
        ही सेवा पनवेल महानगरपालिका स्मार्ट सिटी अंतर्गत.
    </div>
</div>
</body>
</html>
