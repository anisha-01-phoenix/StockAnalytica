from flask import Flask, jsonify
from keras.models import load_model
import tensorflow as tf
import sys

app = Flask(__name__)


@app.route("/")
def hello():
    return "Welcome to machine learning model APIs!"

@app.route('/predict', methods=['POST']) # Your API endpoint URL would consist /predict
def predict():
    if model:
        print("Model exsits")
        # try:
        #     json_ = request.json
        #     query = query.reindex(columns=model_columns, fill_value=0)

        #     prediction = list(lr.predict(query))

        #     return jsonify({'prediction': prediction})

        # except:

        #     return jsonify({'trace': traceback.format_exc()})
    else:
        print ('Train the model first')
        return ('No model here to use')



if __name__ == '__main__':
    model_file_name = "stock_price/mymodel.keras"

    try:
        port = int(sys.argv[1]) # This is for a command-line argument
    except:
        port = 12345 # If you don't provide any port then the port will be set to 12345
    model = tf.keras.models.load_model(model_file_name) # Load "model.pkl"
    print ('Model loaded')
    app.run(port=port, debug=True)
