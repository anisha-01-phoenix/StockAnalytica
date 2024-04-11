import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import HomePage from './components/HomePage';
import StockPricePrediction from './components/StockPricePrediction';
import PortfolioOptimisation from './components/PortfolioOptimisation';
import EDAAnalysis from './components/EDAAnalysis';
import Prediction from './components/Prediction';
import PortfolioView from './components/PortfolioView';
import PortfolioOptimise from './components/PortfolioOptimise';

const App = () => {
    return (
        <Router>
            <Switch>
                <Route exact path="/" component={HomePage} />
                <Route exact path="/stock-price-prediction" component={StockPricePrediction} />
                <Route exact path="/portfolio-optimisation" component={PortfolioOptimisation} />
                <Route path="/stock-price-prediction/eda-analysis" component={EDAAnalysis} />
                <Route path="/stock-price-prediction/prediction" component={Prediction} />
                <Route path="/portfolio-optimisation/portfolio-view" component={PortfolioView} />
                <Route path="/portfolio-optimisation/optimise" component={PortfolioOptimise} />
            </Switch>
        </Router>
    );
};

export default App;
