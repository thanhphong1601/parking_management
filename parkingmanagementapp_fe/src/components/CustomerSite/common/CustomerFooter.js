import React from "react";
import "./CustomerFooter.css"

const CustomerFooter = () => {
    return (
        <footer className="footer">
          <div className="footer-content">
            <div className="footer-brand">
              <h3>SmartParking</h3>
              <p>Giải pháp đậu xe tiện lợi và thông minh cho đô thị hiện đại.</p>
            </div>
            <div className="footer-info">
              <p><strong>Hotline:</strong> 1900 9999</p>
              <p><strong>Email:</strong> support@smartparking.vn</p>
              <p><strong>Địa chỉ:</strong> HCMC OU</p>
            </div>
          </div>
          <div className="footer-bottom">
            <p>© 2025 SmartParking. All rights reserved.</p>
          </div>
        </footer>
      );
};

export default CustomerFooter;