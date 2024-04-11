import React from 'react';
import { Link } from 'react-router-dom';

const PortfolioOptimisation = () => {
    return (
        <div>
            <h1>Portfolio Optimisation</h1>
            {/* Add form to choose start date and end date */}
            <Link to="/portfolio-optimisation/portfolio-view">View Portfolio</Link>
        </div>
    );
};

export default PortfolioOptimisation;
