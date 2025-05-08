import React from "react";
import "./FullScreenLoading.css";

const FullScreenLoading = ({ message = "Đang xử lý, vui lòng chờ..." }) => {
  return (
    <div className="fullscreen-loading-overlay">
      <div className="spinner-box">
        <div className="spinner-border text-primary" role="status" />
        <p className="loading-text">{message}</p>
      </div>
    </div>
  );
};

export default FullScreenLoading;
