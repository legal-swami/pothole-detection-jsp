<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Road Pothole Detection</title>
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
            max-width: 500px;
            width: 100%;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            text-align: center;
        }
        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 28px;
        }
        .subtitle {
            color: #666;
            margin-bottom: 30px;
            font-size: 14px;
        }
        .upload-area {
            border: 2px dashed #ccc;
            border-radius: 15px;
            padding: 30px;
            margin: 20px 0;
            transition: all 0.3s;
        }
        .upload-area:hover {
            border-color: #667eea;
            background: #f8f9ff;
        }
        input[type="file"] {
            width: 100%;
            padding: 10px;
            margin: 10px 0;
            border: none;
            background: transparent;
            cursor: pointer;
        }
        input[type="submit"] {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 12px 30px;
            font-size: 16px;
            border-radius: 30px;
            cursor: pointer;
            transition: transform 0.2s;
            margin-top: 10px;
        }
        input[type="submit"]:hover {
            transform: scale(1.05);
        }
        .footer {
            margin-top: 30px;
            font-size: 12px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚧 Road Pothole Detection</h1>
        <p class="subtitle">Upload a clear image of the road to check for potholes</p>
        
        <form action="PotholeServlet" method="post" enctype="multipart/form-data">
            <div class="upload-area">
                <input type="file" name="roadImage" accept="image/jpeg,image/png,image/jpg" required/>
                <p style="color:#888; margin-top:10px;">📸 JPG, PNG only (Max 10MB)</p>
            </div>
            <input type="submit" value="🔍 Detect Pothole">
        </form>
        
        <div class="footer">
            <p>Govt. Approved | AI-Powered Detection</p>
        </div>
    </div>
</body>
</html>
