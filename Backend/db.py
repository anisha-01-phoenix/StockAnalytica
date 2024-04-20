from pymongo import MongoClient
import os
from dotenv import load_dotenv

load_dotenv()

MONGO_URI = os.getenv('MONGO_URI')

def get_db():
    try:
        client = MongoClient(MONGO_URI)
        db = client['stockanalytics'] 
        print("Successfully connected to MongoDB!")
        return db
    except Exception as e:
        print("Error connecting to MongoDB:", e)
        return None
