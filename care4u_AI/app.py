from flask import Flask, jsonify, request
from predict import Predict

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    if request.method == 'POST':
        file_img = request.files['file']
        img_bytes = file_img.read()
        return jsonify({'classes': Predict().predict(img_bytes)})
    return 401, 'Unauthorized'

if __name__ == '__main__':
    app.run(host='0.0.0.0')