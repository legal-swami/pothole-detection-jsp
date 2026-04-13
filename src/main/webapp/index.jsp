<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panvel City Pothole Detection | Smart City Initiative</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', 'Poppins', 'Arial', sans-serif;
            background: linear-gradient(135deg, #e0eafc 0%, #cfdef3 100%);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        .container {
            background: rgba(255,255,255,0.96);
            backdrop-filter: blur(2px);
            border-radius: 28px;
            padding: 30px 40px;
            max-width: 650px;
            width: 100%;
            box-shadow: 0 25px 45px rgba(0,0,0,0.2);
            text-align: center;
            border: 1px solid rgba(0,0,0,0.05);
        }
        .logo-area {
            display: flex;
            justify-content: center;
            margin-bottom: 20px;
        }
        .logo {
            max-width: 180px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        h1 {
            font-size: 28px;
            color: #1e3c72;
            letter-spacing: -0.5px;
            margin: 10px 0 5px;
        }
        .tagline {
            font-size: 16px;
            color: #2a5298;
            font-weight: 500;
            margin-bottom: 15px;
            border-bottom: 2px solid #ffb347;
            display: inline-block;
            padding-bottom: 5px;
        }
        .sub {
            color: #4a627a;
            margin-bottom: 25px;
            font-size: 14px;
        }
        .upload-card {
            background: #f8faff;
            border-radius: 24px;
            padding: 20px;
            margin: 20px 0;
            border: 1px dashed #2a5298;
            transition: 0.3s;
        }
        .upload-card:hover {
            border-color: #ff8c42;
            background: #fff5eb;
        }
        input[type="file"] {
            width: 100%;
            padding: 14px;
            border: 1px solid #ccc;
            border-radius: 16px;
            background: white;
            font-size: 14px;
            cursor: pointer;
        }
        button {
            background: linear-gradient(95deg, #1e3c72 0%, #2a5298 100%);
            color: white;
            border: none;
            padding: 14px 28px;
            font-size: 18px;
            font-weight: 600;
            border-radius: 40px;
            cursor: pointer;
            transition: 0.2s;
            margin-top: 15px;
            box-shadow: 0 6px 14px rgba(0,0,0,0.1);
        }
        button:hover {
            transform: scale(1.02);
            background: linear-gradient(95deg, #ff8c42, #ffb347);
            color: #1e2f4e;
            box-shadow: 0 8px 20px rgba(0,0,0,0.15);
        }
        .footer {
            margin-top: 35px;
            font-size: 12px;
            color: #6c7a89;
            text-align: center;
            border-top: 1px solid #e2e8f0;
            padding-top: 20px;
        }
        .badge {
            background: #e9ecef;
            display: inline-block;
            padding: 6px 14px;
            border-radius: 40px;
            font-size: 12px;
            font-weight: 600;
            color: #1e3c72;
            margin-bottom: 15px;
        }
        @media (max-width: 550px) {
            .container { padding: 20px; }
            h1 { font-size: 22px; }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="logo-area">
        <img src="images/logo.jpeg" alt="Panvel Mahanagar Palika Logo" class="logo" onerror="this.onerror=null; this.src='https://via.placeholder.com/150?text=Panvel+2.0'">
    </div>
    <h1>🛣️ Panvel Smart Pothole Detection</h1>
    <div class="tagline">महापालिका अंतर्गत डिजिटल रस्ता सुरक्षा</div>
    <div class="badge">AI-Powered | Govt. Initiative</div>
    <p class="sub">रस्त्याचा स्पष्ट फोटो अपलोड करा. सिस्टीम खड्डा असल्यास त्वरित शोधेल.</p>

    <form action="PotholeServlet" method="post" enctype="multipart/form-data">
        <div class="upload-card">
            <input type="file" name="roadImage" accept="image/jpeg,image/png,image/jpg" required/>
            <small style="display:block; margin-top:12px;">📸 JPG / PNG (Max 10MB)</small>
        </div>
        <button type="submit">🔍 पोटहोल तपासा</button>
    </form>

    <div class="footer">
        <span>© A.R. Ghorpade – Panvel 2.0 | Not for Distribution</span><br/>
        <span>📞 हेल्पलाइन: +91 9833028028 | स्मार्ट सिटी मोहीम</span>
    </div>
</div>
</body>
</html>
