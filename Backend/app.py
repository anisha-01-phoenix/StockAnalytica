from flask import Flask, render_template, request, redirect, url_for, session, jsonify
from flask_pymongo import PyMongo
from werkzeug.security import generate_password_hash, check_password_hash
import tensorflow as tf
import sys
import joblib
import json
from json import JSONEncoder
import pandas as pd
import numpy as np
import requests
import traceback
import yfinance as yf
from pypfopt import HRPOpt
from pypfopt.discrete_allocation import DiscreteAllocation, get_latest_prices
from pypfopt.expected_returns import mean_historical_return
from pypfopt.efficient_frontier import EfficientCVaR, EfficientFrontier
from pypfopt.risk_models import CovarianceShrinkage

app = Flask(__name__)
app.config['MONGO_URI'] = "mongodb://localhost:27017/bss"  # MongoDB URI
mongo = PyMongo(app)

url = 'https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol='
api_key = '&apikey='
api_keys = ['RNH7ARGY3ITHRCKH', 'F3TTN5YO8MQDWM43']

class NumpyArrayEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return JSONEncoder.default(self, obj)

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

@app.route('/predict', methods=['GET'])
def predict():
    if model:
        try:
            sc = joblib.load('stock_price/scaler.sav')

            dataset_columns = ['Open', 'High', 'Low', 'Close', 'Volume']

            symbol = request.args.get('symbol')

            for key in api_keys:
                try:
                    dataset = requests.get(url+symbol+api_key+key)
                    break
                except:
                    continue

            dataset = dataset.json()
            dataset = dataset['Time Series (Daily)']
            dataset = pd.DataFrame.from_dict(dataset, orient='index')
            dataset.columns = dataset_columns

            dataset = dataset['Close']
            dataset = np.array(dataset)[-1::-1]

            transformed_values = sc.transform(dataset.reshape(-1, 1))

            for j in range(7):
                predicted_values = model.predict(transformed_values[-100:, 0].reshape(1, 100, 1))
                transformed_values = np.append(transformed_values, predicted_values.reshape(1)).reshape(-1, 1)

            prediction = sc.inverse_transform(transformed_values[:, 0].reshape(-1, 1))
            prediction = prediction[-7:, 0]
            prediction = np.round(prediction, 4)

            numpyData = {"array": prediction}
            encodedNumpyData = json.dumps(numpyData, cls=NumpyArrayEncoder)

            return jsonify(encodedNumpyData)

        except:

            return jsonify({'trace': traceback.format_exc()})
    else:
        return jsonify({'message': 'Prediction Model not Found'})

@app.route('/portfolio_optimization', methods=['GET'])
def optimize():
    # ticks = ["MRNA","PFE","JNJ","GOOGL","META","AAPL","COST","WMT",'KR',"JPM","BAC","HSBC"]
    method = request.args.get('method')

    money_invested = float(request.args.get('value')) or 100000

    ticks = request.args.get('ticks')
    ticks = ticks.split(',')
    print(ticks, type(ticks))


    portfolio = yf.download(ticks)
    portfolio = portfolio['Close']
    portfolio = portfolio[-1::-1]
    portfolio = portfolio[0:100]

    if method == 'hrp':
        returns = portfolio.pct_change().dropna()

        hrp = HRPOpt(returns)
        hrp_weights = hrp.optimize()

        hrp.portfolio_performance(verbose=True)

        latest_prices = get_latest_prices(portfolio)
        discrete_allocation_hrp = DiscreteAllocation(hrp_weights, latest_prices, total_portfolio_value = money_invested)

        allocation, leftover = discrete_allocation_hrp.greedy_portfolio()

    elif method == 'mcvar':
        returns = mean_historical_return(portfolio)
        covariance = portfolio.cov()
        ef_cvar = EfficientCVaR(returns, covariance)
        cvar_weights = ef_cvar.min_cvar()

        latest_prices = get_latest_prices(portfolio)
        discrete_allocation_cvar = DiscreteAllocation(cvar_weights, latest_prices, total_portfolio_value = money_invested)

        allocation, leftover = discrete_allocation_cvar.greedy_portfolio()

    else:
        returns = mean_historical_return(portfolio)
        covariance_shrinkage = CovarianceShrinkage(portfolio).ledoit_wolf()

        ef = EfficientFrontier(returns, covariance_shrinkage)
        weights = ef.max_sharpe()

        ef.portfolio_performance(verbose=True)

        latest_prices = get_latest_prices(portfolio)
        discrete_allocation = DiscreteAllocation(weights, latest_prices, total_portfolio_value = money_invested)

        allocation, leftover = discrete_allocation.greedy_portfolio()

    return jsonify({'Allocation': allocation,'Leftover':"${:.2f}".format(leftover)})

if __name__ == '__main__':
    model_file_name = "stock_price/mymodel.keras"

    try:
        port = int(sys.argv[1])
    except:
        port = 12345

    try:
        model = tf.keras.models.load_model(model_file_name)
        print ('Model loaded')
    except:
        print("Error in Loading Model")


    app.run(port=port, debug=True)
