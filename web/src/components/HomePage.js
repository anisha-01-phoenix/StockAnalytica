import React from 'react';
import { Link } from 'react-router-dom';

const HomePage = () => {
    return (
        <div>
            <h1>Welcome to the Website</h1>
            <Link to="/stock-price-prediction">Stock Price Prediction</Link>
            <Link to="/portfolio-optimisation">Portfolio Optimisation</Link>
        </div>
    );
};

export default HomePage;
