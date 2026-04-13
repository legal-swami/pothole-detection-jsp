<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Road Pothole Detection</title>
    <style>
        body { font-family: Arial; text-align: center; margin-top: 50px; }
        .container { width: 500px; margin: auto; padding: 20px; border: 2px solid #ccc; border-radius: 10px; }
        input[type="file"] { margin: 20px 0; }
        input[type="submit"] { background: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; }
    </style>
</head>
<body>
    <div class="container">
        <h2>🚧 Road Pothole Detection System</h2>
        <form action="PotholeServlet" method="post" enctype="multipart/form-data">
            Select Road Image: <input type="file" name="roadImage" accept="image/*" required/><br/>
            <input type="submit" value="🔍 Detect Pothole"/>
        </form>
    </div>
</body>
</html>
