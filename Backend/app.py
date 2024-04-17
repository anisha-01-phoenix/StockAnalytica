from flask import Flask, render_template, request, redirect, url_for, session, jsonify
from flask_pymongo import PyMongo
from werkzeug.security import generate_password_hash, check_password_hash
from keras.models import load_model
import tensorflow as tf
import sys

app = Flask(__name__)
app.config['MONGO_URI'] = "mongodb://localhost:27017/bss"  # MongoDB URI
mongo = PyMongo(app)


@app.route("/")
def hello():
    return "Welcome to machine learning model APIs!"

@app.route('/register', methods=['POST'])
def signup():

    try:
            username = request.form['username'] #Fields to be added
            email = request.form['email']
            password = request.form['password']
            hashed_password = generate_password_hash(password)
            

            if mongo.db.users.find_one({'username': username}):
                return 'Username already exists! Please use another username.', 400
            else:
                user_data = {
                'username': username,
                'email': email,
                'password': hashed_password,
                'stocks': []
                }
                mongo.db.users.insert_one(user_data)
                return 'Signup successful!', 200 
            
            
    except: 
        return 'some error occurred', 400

@app.route('/login', methods=['POST'])
def login():
    try:
        username = request.form['username']
        password = request.form['password']
        user = mongo.db.users.find_one({'username': username})
        
        if user and check_password_hash(user['password'], password):
            return 'login successfull', 200
        else:
            return 'login unsuccessfull',400 
    except:
        return 'some error occurred', 400
    
@app.route('/buy', methods=['POST'])
def buyStocks():
    try:
        username = request.form['username']
        stockName = request.form['stockName']
        quantity = request.form['quantity']
        price = request.form['price']
        user = mongo.db.users.find_one({'username': username},{'stocks':1,'_id':0})
        
        stocks = user["stocks"]
        stock = {
            'stockName': stockName,
            'quantity': quantity,
            'price': price
        }
        t=0
        for key in stocks:
                if key["stockName"] == stock["stockName"]:  # Skip the 'email' key
                    key["price"] = stock["price"]
                    int1 = int(stock['quantity'])
                    int2 = int(key['quantity'])
                    key["quantity"] = str(int1+int2)
                    t=1
        if t==0:
            stocks.append(stock)
        print(stocks)
        mongo.db.users.update_one({'username': username}, {'$set': {'stocks':stocks}})
        return 'stocks bought successfully', 200

    except:
        return 'some error occurred', 400
    
@app.route('/details', methods=['POST'])
def userDetails():
    try:
        username = request.form['username']
        user = mongo.db.users.find_one({'username': username},{'_id':0,'password':0})
        
        if user:
            return user, 200
        else:
            return 'no user found', 404
    except:
        return 'some error occurred', 400 

@app.route('/stocks', methods=['POST'])
def stocksBought():
    try:
        username = request.form['username']
        stocks = mongo.db.users.find_one({'username': username},{'stocks':1,'_id':0})
        
        if stocks:
            return stocks, 200
        else:
            return 'no user found', 404
    except:
        return 'some error occurred', 400 

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
    # model_file_name = "stock_price/mymodel.keras"

    # try:
    #     port = int(sys.argv[1]) # This is for a command-line argument
    # except:
    #     port = 12345 # If you don't provide any port then the port will be set to 12345
    # model = tf.keras.models.load_model(model_file_name) # Load "model.pkl"
    # print ('Model loaded')
    app.run( debug=True)
