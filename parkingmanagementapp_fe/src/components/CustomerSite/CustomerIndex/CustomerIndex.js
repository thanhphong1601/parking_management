import { useContext, useEffect, useState } from "react";
import { Button } from "react-bootstrap";
import "./CustomerIndex.css"
import CustomerFooter from "../common/CustomerFooter";
import { Link, useNavigate } from "react-router-dom";
import { MyUserContext } from "../../../configs/Contexts";

const images = [
    require("../../static/images/parking1.jpg"),
    require("../../static/images/parking2.jpg"),
    require("../../static/images/parking3.jpg")
];

const CustomerIndex = () => {
    const nav = useNavigate();
    const currentUser = useContext(MyUserContext);
    const [currentImage, setCurrentImage] = useState(0);

    useEffect(() => {
        const interval = setInterval(() => {
            setCurrentImage((prev) => (prev + 1) % images.length);
        }, 3000);

        return () => clearInterval(interval);
    }, []);

    useEffect(() => {
        const observer = new IntersectionObserver(
            (entries) => {
                entries.forEach((entry) => {
                    if (entry.isIntersecting) {
                        entry.target.classList.add("visible");
                    }
                });
            },
            {
                threshold: 0.2,
            }
        );

        const elements = document.querySelectorAll(".fade-slide");
        elements.forEach((el) => observer.observe(el));

        return () => {
            elements.forEach((el) => observer.unobserve(el));
        };
    }, []);

    const goToRegisterPage = () => {
        nav("/register");
    };

    return (
        <div className="homepage-container">
            {/* Section 1: Hero */}
            <section className="hero-section fade-slide">
                <div className="hero-content">
                    <h1 className="hero-title">
                        Nền tảng đậu xe thông minh dành cho bạn
                    </h1>
                    <p className="hero-subtitle">
                        Chúng tôi cung cấp giải pháp tối ưu cho việc quản lý và vận hành bãi đậu xe hiện đại, an toàn và tiện lợi.
                    </p>
                    <div className="hero-buttons">
                        <Button variant="light" className="hero-btn" onClick={goToRegisterPage}>Đăng ký tài khoản ngay</Button>
                        <Button variant="outline-light" className="hero-btn">Xem bảng giá</Button>
                    </div>
                </div>
                <div className="hero-image">
                    <img src={images[currentImage]} alt="Bãi đậu xe" />
                </div>
            </section>

            {/* Section 2: CTA */}
            <section className="cta-section fade-slide">
                <h2>Quản lý vé xe dễ dàng và nhanh chóng</h2>
                <p>Đã có tài khoản? Truy cập vào trang quản lý để kiểm tra thông tin vé xe và thao tác nhanh chóng.</p>
                <Link to={currentUser? `/customer/${currentUser.id}/ticket/list`: '/login'} className="cta-button">Vào trang quản lý vé</Link>
            </section>

            <section className="hero-section-2 fade-slide">
                <div className="hero-content-2">
                    <h1 className="hero-title-2">
                        Đăng ký trước vé xe ngay, tham gia dịch vụ liền tay
                    </h1>
                    <p className="hero-subtitle-2">
                        Bạn có thể đăng ký trước loại vé xe mình mong muốn
                    </p>
                    <div className="hero-buttons-2">
                        <Link variant="light" className="btn cta-button" to={currentUser? `/customer/${currentUser.id}/ticket/create`: '/login'}>Đăng ký vé ngay</Link>
                    </div>
                </div>
                <div className="hero-image">
                    <img src={images[currentImage]} alt="Bãi đậu xe" />
                </div>
            </section>
            
        </div>


    );
};

export default CustomerIndex;