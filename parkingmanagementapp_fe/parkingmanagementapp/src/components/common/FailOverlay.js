import React from "react";
import "./FailOverlay.css";

const FailOverlay = ({ message = "Thao tác thất bại!" }) => {
  return (
    <div className="fail-overlay">
      <div className="fail-box">
        <div className="fail-icon">❌</div>
        <p className="fail-message">{message}</p>
      </div>
    </div>
  );
};

export default FailOverlay;
