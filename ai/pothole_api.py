from flask import Flask, request, jsonify
from ultralytics import YOLO
import cv2
import numpy as np

app = Flask(__name__)

# YOLO model load
model = YOLO("yolov8n.pt")

@app.route('/detect', methods=['POST'])
def detect():
    file = request.files['image']

    img_bytes = file.read()
    np_arr = np.frombuffer(img_bytes, np.uint8)
    img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    if img is None:
        return jsonify({"result": "❌ Invalid Image"})

    results = model(img)

    pothole_found = False

    for r in results:
        for box in r.boxes:
            cls = int(box.cls[0])

            # demo logic
            if cls in [0, 1]:
                pothole_found = True

    if pothole_found:
        return jsonify({"result": "⚠️ Pothole Detected"})
    else:
        return jsonify({"result": "✅ No Pothole"})

if __name__ == "__main__":
    app.run(port=5000)
