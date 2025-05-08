import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import './VerifyInform.css'

const VerifyInform = () => {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const email = queryParams.get("email");
    const status = queryParams.get("success");
    const nav = useNavigate();


    const [countdown, setCountdown] = useState(5);


    useEffect(() => {
        const timer = setInterval(() => {
            setCountdown((prev) => prev - 1);
        }, 1000);

        const redirectTimeout = setTimeout(() => {
            nav("/login"); // hoặc điều hướng đến trang chính nào đó
        }, 5000);

        return () => {
            clearInterval(timer);
            clearTimeout(redirectTimeout);
        };
    }, [nav]);

    const isSuccess = status === "successful";

    return (
        <div className="redirect-container">
            <div className={`redirect-box ${isSuccess ? "success" : "failure"}`}>
                <h2>{isSuccess ? "Đăng ký thành công!" : "Đăng ký thất bại!"}</h2>
                <p>
                    Tài khoản với email <strong>{email}</strong> {isSuccess ? "đã được xác thực thành công." : "không thể xác thực. Vui lòng thử lại."}
                </p>
                <p className="redirect-countdown">
                    Chuyển hướng về trang đăng nhập sau <strong>{countdown}</strong> giây...
                </p>
            </div>
        </div>
    );
}

export default VerifyInform;