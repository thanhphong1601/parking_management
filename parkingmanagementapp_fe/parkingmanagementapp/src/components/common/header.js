import { useContext, useState } from "react";
import { Container, Image, Nav, NavDropdown, Navbar } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { MyDispatchContext, MyUserContext } from "../../configs/Contexts";
import './header.css'

const Header = () => {
    const nav = useNavigate();
    const user = useContext(MyUserContext);
    const dispatch = useContext(MyDispatchContext);

    return (
        <Navbar expand="lg" className="bg-body-tertiary bg-info">
            <Navbar.Brand>
                <Link className="nav-link" to="/">Quản lý bãi đậu xe</Link>
            </Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="ms-auto">              
                    <Link to="/customer/list" className="nav-link">Khách hàng</Link>
                    <Link to="/ticket/list" className="nav-link">Vé xe</Link>
                    <NavDropdown title="Quản lý" id="basic-nav-dropdown">
                        <Link to="/manage/in" className="nav-link">Xe vào</Link>
                        <Link to="/manage/out" className="nav-link">Xe ra</Link>
                    </NavDropdown>
                    <Link to="/stats" className="nav-link">Thống kê</Link>

                    {user === null ? <>
                        <Link to="/login" className="nav-link text-dark">Đăng Nhập</Link>
                        <Link to="/register" className="nav-link text-info">Đăng Ký</Link>
                    </> : <>
                        <Link to="/" className="nav-link text-info">
                            {user.username}
                        </Link>
                        <Link onClick={() => dispatch({
                            "type": "logout"
                        })} className="nav-link text-info">Đăng Xuất</Link>
                    </>}
                </Nav>
            </Navbar.Collapse>
        </Navbar>
    );
}

export default Header;