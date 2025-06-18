import { useContext, useState } from "react";
import { Container, Image, Nav, NavDropdown, Navbar } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { MyDispatchContext, MyUserContext } from "../../configs/Contexts";
import './header.css'

const Header = () => {
    const nav = useNavigate();
    const user = useContext(MyUserContext);
    const dispatch = useContext(MyDispatchContext);

    // if (user !== null) {
    //     console.log(user.username);
    // }

    return (
        <Navbar expand="lg" className="bg-body-tertiary bg-info">
            <Navbar.Brand>
                <Link className="nav-link" to="/">Quản lý bãi đậu xe</Link>
            </Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="ms-auto">
                    {/* {user && (user.role.id === 1 || user.role.id === 2) ? <>
                        <Link to="/customer/list" className="nav-link">Khách hàng</Link>
                        <Link to="/ticket/list" className="nav-link">Vé xe</Link>
                        <NavDropdown title="Quản lý" id="basic-nav-dropdown">
                            <Link to="/manage/in" className="nav-link">Xe vào</Link>
                            <Link to="/manage/out" className="nav-link">Xe ra</Link>
                        </NavDropdown>
                        <Link to="/stats" className="nav-link">Thống kê</Link>
                        <Link to="#" className="nav-link">Test Login</Link>
                    </> : <> {user && user.role.id === 1 ? <>
                        <Link to="#" className="nav-link">Thông tin cá nhân</Link>
                        <Link to="#" className="nav-link">Vé xe</Link>
                        <Link to="#" className="nav-link">Hóa đơn</Link>
                    </>:<></>}
                    </>} */}
                    <Link to='/' className="nav-link">Trang chủ</Link>
                    <Link to="/customer/list" className="nav-link">Khách hàng</Link>
                    <Link to="/ticket/list" className="nav-link">Vé xe</Link>
                    <NavDropdown title="Quản lý" id="basic-nav-dropdown">
                        <Link to="/manage/in" className="nav-link">Xe vào</Link>
                        <Link to="/manage/out" className="nav-link">Xe ra</Link>
                        <Link to="/manage/map/show" className="nav-link">Bản đồ</Link>
                    </NavDropdown>
                    <Link to="/entry/list" className="nav-link">Lịch sử ra vào</Link>

                    <Link to={user ? `/customer/${user.id}/receipt/list` : "#"} className="nav-link">Lịch sử thanh toán</Link>

                    <Link to={'#'} className="nav-link">||</Link>
                    <Link to='/fast-payment' className="nav-link">Thanh toán nhanh</Link>

                    <Link to={user ? `/customer/${user.id}/ticket/list` : '/login'} className="nav-link">Quản lý vé</Link>
                    <Link to={user ? `/customer/${user.id}/ticket/create` : '/login'} className="nav-link">Đăng ký vé</Link>

                    <Link to={user ? `/customer/${user.id}/vehicle/list` : "#"} className="nav-link">Phương tiện</Link>


                    {user === null ? <>
                        <Link to="/login" className="nav-link text-dark">Đăng Nhập</Link>
                        <Link to="/register" className="nav-link text-info">Đăng Ký</Link>
                    </> : <>
                        <Link to={`/profile/${user.id}`} className="nav-link text-info">
                            {user.name}
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