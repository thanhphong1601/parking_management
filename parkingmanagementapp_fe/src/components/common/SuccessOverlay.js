import React from "react";
import "./SuccessOverlay.css";

const SuccessOverlay = ({ message = "Thành công!" }) => {
  return (
    <div className="check-overlay">
      <div className="check-box">
        <svg className="checkmark" viewBox="0 0 52 52">
          <circle className="checkmark-circle" cx="26" cy="26" r="25" />
          <path className="checkmark-check" fill="none" d="M14 27l7 7 17-17" />
        </svg>
        <p className="check-message">{message}</p>
      </div>
    </div>
  );
};

export default SuccessOverlay;
