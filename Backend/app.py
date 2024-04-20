from flask import Flask, render_template, request, redirect, url_for, session, jsonify
from flask_pymongo import PyMongo
from pymongo.mongo_client import MongoClient
from pymongo.server_api import ServerApi
from werkzeug.security import generate_password_hash, check_password_hash
import tensorflow as tf
import sys
import os
import re
from bson import json_util
from bson.json_util import dumps
from db import get_db
from dotenv import load_dotenv
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
# app.config["MONGO_URI"] = "mongodb://localhost:27017/bss"  # MongoDB URI
# mongo = PyMongo(app)

EMAIL_REGEX = r'^[\w\.-]+@[a-zA-Z\d\.-]+\.[a-zA-Z]{2,}$'
load_dotenv()

url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="
api_key = "&apikey="
api_key_1 = os.getenv('API_KEY_1')
api_key_2 = os.getenv('API_KEY_2')
api_keys = [api_key_1, api_key_2]

db = get_db()


class NumpyArrayEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return JSONEncoder.default(self, obj)
    
def parse_json(data):
    return json.loads(json_util.dumps(data))


@app.route("/")
def hello():
    return "Welcome to machine learning model APIs!"


@app.route("/register", methods=["POST"])
def signup():
    try:
        data = request.json
        username = data.get('username')
        email = data.get('email')
        password = data.get('password')
        hashed_password = generate_password_hash(password)

        if db.users.find_one({"username": username}):
            return jsonify({'message': 'Username already exists'}), 400
        elif not re.match(EMAIL_REGEX, email):
            return jsonify({'message': 'Invalid email format'}), 400
        elif len(password) < 6:
            return jsonify({'message': 'Password must be at least 6 characters long'}), 400
        else:
            user_data = {
                "username": username,
                "email": email,
                "password": hashed_password,
                "stocks": [],
            }
            db.users.insert_one(user_data)
            return jsonify({'message': 'User registered successfully', 'user': parse_json(user_data)}), 200

    except Exception as e:
        print("An error occurred:", str(e))
        return jsonify({'message': 'Error in Authentication'}), 400


@app.route("/login", methods=["POST"])
def login():
    try:
        data = request.json
        username = data.get('username')
        password = data.get('password')
        user = db.users.find_one({"username": username})
        
        if user and check_password_hash(user["password"], password):
            # print (user)
            return jsonify({'message': 'Login successful', 'user': parse_json(user)}), 200
        else:
            return jsonify({'message': 'Invalid credentials'}), 401
    except Exception as e:
        print("An error occurred:", str(e))
        return jsonify({'message': 'Error in Authentication'}), 400

@app.route("/buy", methods=["POST"])
def buyStocks():
    try:
        data = request.json
        username = data.get('username')
        stockName = data.get('stockName')
        quantity = data.get('quantity')
        price = data.get('price')
        user = db.users.find_one({"username": username}, {"stocks": 1, "_id": 0})

        stocks = user["stocks"]
        stock = {"stockName": stockName, "quantity": quantity, "price": price}
        t = 0
        for key in stocks:
            if key["stockName"] == stock["stockName"]:  # Skip the 'email' key
                key["price"] = stock["price"]
                int1 = int(stock["quantity"])
                int2 = int(key["quantity"])
                key["quantity"] = str(int1 + int2)
                t = 1
        if t == 0:
            stocks.append(stock)
        print(stocks)
        db.users.update_one({"username": username}, {"$set": {"stocks": stocks}})
        return jsonify({'message': 'Stocks bought successfully', 'stocks': parse_json(stocks)}), 200

    except Exception as e:
        print("An error occurred:", str(e))
        return jsonify({'message': 'Error'}), 400


@app.route("/details", methods=["POST"])
def userDetails():
    try:
        data = request.json
        username = data.get('username')
        user = db.users.find_one(
            {"username": username}, {"_id": 0, "password": 0}
        )

        if user:
            return jsonify({'user': parse_json(user)}), 200
        else:
            return jsonify({'message', 'No User found'}), 404
    except Exception as e:
        print("An error occurred:", str(e))
        return jsonify({'message': 'Error'}), 400


@app.route("/stocks", methods=["POST"])
def stocksBought():
    try:
        data = request.json
        username = data.get('username')
        stocks = db.users.find_one(
            {"username": username}, {"stocks": 1, "_id": 0}
        )

        if stocks:
            return jsonify({'stocks': parse_json(stocks)}), 200
        else:
            return jsonify({'message', 'No User found'}), 404
    except Exception as e:
        print("An error occurred:", str(e))
        return jsonify({'message': 'Error'}), 400


@app.route("/predict", methods=["GET"])
def predict():
    if model:
        try:
            sc = joblib.load("stock_price/scaler.sav")

            dataset_columns = ["Open", "High", "Low", "Close", "Volume"]

            symbol = request.args.get("symbol")

            for key in api_keys:
                try:
                    dataset = requests.get(url + symbol + api_key + key)
                    break
                except:
                    continue

            dataset = dataset.json()
            dataset = dataset["Time Series (Daily)"]
            dataset = pd.DataFrame.from_dict(dataset, orient="index")
            dataset.columns = dataset_columns

            dataset = dataset["Close"]
            dataset = np.array(dataset)[-1::-1]

            transformed_values = sc.transform(dataset.reshape(-1, 1))

            for j in range(7):
                predicted_values = model.predict(
                    transformed_values[-100:, 0].reshape(1, 100, 1)
                )
                transformed_values = np.append(
                    transformed_values, predicted_values.reshape(1)
                ).reshape(-1, 1)

            prediction = sc.inverse_transform(transformed_values[:, 0].reshape(-1, 1))
            prediction = prediction[-7:, 0]
            prediction = np.round(prediction, 4)

            numpyData = {"array": prediction}
            encodedNumpyData = json.dumps(numpyData, cls=NumpyArrayEncoder)

            return jsonify(encodedNumpyData)

        except:
            return jsonify({"trace": traceback.format_exc()})
    else:
        return jsonify({"message": "Prediction Model not Found"})


@app.route("/portfolio_optimization", methods=["GET"])
def optimize():
    # ticks = ["MRNA","PFE","JNJ","GOOGL","META","AAPL","COST","WMT",'KR',"JPM","BAC","HSBC"]
    method = request.args.get("method")

    money_invested = float(request.args.get("value")) or 100000

    ticks = request.args.get("ticks")
    ticks = ticks.split(",")
    print(ticks, type(ticks))

    portfolio = yf.download(ticks)
    portfolio = portfolio["Close"]
    portfolio = portfolio[-1::-1]
    portfolio = portfolio[0:100]

    if method == "hrp":
        returns = portfolio.pct_change().dropna()

        hrp = HRPOpt(returns)
        hrp_weights = hrp.optimize()

        hrp.portfolio_performance(verbose=True)

        latest_prices = get_latest_prices(portfolio)
        discrete_allocation_hrp = DiscreteAllocation(
            hrp_weights, latest_prices, total_portfolio_value=money_invested
        )

        allocation, leftover = discrete_allocation_hrp.greedy_portfolio()

    elif method == "mcvar":
        returns = mean_historical_return(portfolio)
        covariance = portfolio.cov()
        ef_cvar = EfficientCVaR(returns, covariance)
        cvar_weights = ef_cvar.min_cvar()

        latest_prices = get_latest_prices(portfolio)
        discrete_allocation_cvar = DiscreteAllocation(
            cvar_weights, latest_prices, total_portfolio_value=money_invested
        )

        allocation, leftover = discrete_allocation_cvar.greedy_portfolio()

    else:
        returns = mean_historical_return(portfolio)
        covariance_shrinkage = CovarianceShrinkage(portfolio).ledoit_wolf()

        ef = EfficientFrontier(returns, covariance_shrinkage)
        weights = ef.max_sharpe()

        ef.portfolio_performance(verbose=True)

        latest_prices = get_latest_prices(portfolio)
        discrete_allocation = DiscreteAllocation(
            weights, latest_prices, total_portfolio_value=money_invested
        )

        allocation, leftover = discrete_allocation.greedy_portfolio()

    return jsonify({"Allocation": allocation, "Leftover": "${:.2f}".format(leftover)})


if __name__ == "__main__":
    model_file_name = "stock_price/mymodel.keras"

    try:
        port = int(sys.argv[1])
    except:
        port = 5000

    try:
        model = tf.keras.models.load_model(model_file_name)
        print("Model loaded")
    except:
        print("Error in Loading Model")

    app.run(port=port, debug=True)
