import React from 'react';
import { Link } from 'react-router-dom';
import UploadTickers from './UploadTickers';

const StockPricePrediction = () => {
    return (
        <div>
            <h1>Stock Price Prediction</h1>
            <UploadTickers />
            <Link to="/stock-price-prediction/eda-analysis">EDA Analysis</Link>
            <Link to="/stock-price-prediction/prediction">Prediction</Link>
        </div>
    );
};

export default StockPricePrediction;
