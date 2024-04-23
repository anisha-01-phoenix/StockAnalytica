import React from "react";
import Leftbar from "../Elements/leftbar";
import Select from "react-select";

import "react-date-picker/dist/DatePicker.css";
import "react-calendar/dist/Calendar.css";

export default class Optimize extends React.Component {
  constructor() {
    super();
    this.state = {
      theme: "dark",
      color: "#ddd",
      portfolioColor: "#ddd",
      bookmarkColor: "#ddd",
      companies: [
        "MRNA",
        "GOOGL",
        "PFE",
        "JNJ",
        "FB",
        "AAPL",
        "COST",
        "WMT",
        "KR",
        "JPM",
        "BAC",
        "HSBC",
      ],
      selectedCompanies: [],
      isDropDown: false,
      startDate: Date.now(),
      endDate: Date.now(),
      Results: null,
      method: null,
      amount: null,
    };
    this.status = React.createRef();
  }
  componentDidMount() {}

  clickCompany = (e) => {
    console.log(e);
  };

  CompanySelection() {
    const list = this.state.companies.map((e) => {
      return { value: e, label: e };
    });

    return (
      <div style={{ marginTop: "2rem" }}>
        <span>Company</span>
        <Select
          isMulti
          options={list}
          styles={{
            option: (provided, state) => ({
              ...provided,
              color: "black", // Change text color of options to black
            }),
          }}
          onChange={(e) => {
            this.setState({
              ...this.state,
              selectedCompanies: e.map((f) => {
                return f.value;
              }),
            });
          }}
        />
      </div>
    );
  }

  MethodSelection() {
    const list = [
      { value: "Mean Variance Method", label: "Mean Variance Method" },
      { value: "hrp", label: "HRP Method" },
      { value: "mcvar", label: "mCVAR Method" },
    ];

    return (
      <div style={{ marginTop: "2rem" }}>
        <span>Method</span>
        <Select
          options={list}
          styles={{
            option: (provided, state) => ({
              ...provided,
              color: "black", // Change text color of options to black
            }),
          }}
          onChange={(e) => {
            this.setState({ ...this.state, method: e.value });
          }}
        />
      </div>
    );
  }

  findOptimize = async () => {
    const sendData = {
      companies: this.state.selectedCompanies,
      method: this.state.method,
      amount: this.state.amount,
    };

    // Make sure to replace 'YOUR_API_ENDPOINT' with your actual API endpoint
    const apiUrl =
      "http://127.0.0.1:12345/portfolio_optimization?method=" +
      this.state.method +
      "&ticks=" +
      this.state.selectedCompanies.join(",") +
      "&value=" +
      this.state.amount;
    // const headers = {
    //   "Content-Type": "application/json",
    //   Authorization: "Bearer YOUR_AUTH_TOKEN", // Add your authorization token here if needed
    // };

    try {
      const res = await fetch(apiUrl, {
        method: "GET",
        // headers: headers,
      });

      if (!res.ok) {
        throw new Error("Network response was not ok");
      }

      const data = await res.json();
      console.log(data);

      this.setState({ ...this.state, Results: data });
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  render() {
    return (
      <section className="Dashboard" id="dashboard">
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            width: "100%",
          }}
        >
          <div style={{ display: "flex", height: "100%" }}>
            <Leftbar />
            <div className="panel">
              <h2>Optimize</h2>
              {this.CompanySelection()}
              {this.MethodSelection()}

              <div>
                <label>Amount To Invest</label>
                <input
                  type="number"
                  style={{
                    height: "2rem",
                    marginTop: "2rem",
                    marginLeft: "3rem",
                    borderRadius: "2px",
                  }}
                  onChange={(e) => this.setState({ amount: e.target.value })}
                ></input>
              </div>
              <div>
                <button
                  style={{
                    color: "white",
                    border: "1px solid white",
                    borderRadius: "3px",
                    marginTop: "2rem",
                    fontSize: "17px",
                  }}
                  onClick={this.findOptimize}
                >
                  {" "}
                  Optimize{" "}
                </button>
              </div>
              {this.state.Results ? (
                <div style={{ marginTop: "2rem" }}>
                  <p style={{ fontSize: "1rem" }}>
                    Allocation:{" "}
                    {Object.entries(this.state.Results.Allocation).map(
                      ([company, stocks], index, array) => (
                        <span key={company}>
                          {company}: {stocks}
                          {index !== array.length - 1 && " "}
                          {", "}
                          {/* Add space if not the last item */}
                        </span>
                      )
                    )}
                  </p>
                  <p style={{ fontSize: "1rem" }}>
                    Leftover : {this.state.Results.Leftover}
                  </p>
                </div>
              ) : (
                <></>
              )}
            </div>
          </div>
        </div>
      </section>
    );
  }
}
