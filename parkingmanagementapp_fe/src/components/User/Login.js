import { useContext, useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import APIs, { authApi, endpoints } from "../../configs/APIs";
import cookie from 'react-cookies'
import { Link, useLocation, useNavigate } from "react-router-dom";
import './Login.css'
import { MyDispatchContext } from "../../configs/Contexts";

const Login = () => {
    const fields = [{
        label: "Tài khoản",
        type: "text",
        field: "username",
        placeholder: "Tài khoản của bạn"
    }, {
        label: "Mật khẩu",
        type: "password",
        field: "password",
        placeholder: "Mật khẩu của bạn"
    }]

    const [r, setR] = useState([]);
    const [errMsg, setErrMsg] = useState("");
    const nav = useNavigate();
    const dispatch = useContext(MyDispatchContext);
    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const continueUrl = params.get('continue') || '/';


    const change = (e, field) => {
        setR(current => {
            return { ...current, [field]: e.target.value }
        })
    }

    const login = async (e) => {
        e.preventDefault();
        setErrMsg("");
        try {
            let res = await APIs.post(endpoints['login'], { ...r })
            localStorage.setItem("token", res.data);

            let u = await authApi().get(endpoints['current-user']);
            if (u.status == 200) {
                let url = `${endpoints['account-active-check']}?username=${r["username"]}`;
                let checkResponse = await APIs.get(url);

                if (checkResponse.status == 200 && checkResponse.data != "Activated") {
                    nav(`/verify?email=${encodeURIComponent(u.data.email)}`, { state: { user: u.data } });

                } else {
                    dispatch({
                        "type": "login",
                        "payload": u.data
                    })

                    if (u.data.role.id == 3) {
                        res = await authApi().put(endpoints['customer-ticket-status-update-all'](u.data.id));
                    } else {
                        res = await authApi().put(endpoints['admin-ticket-status-update-all']);
                    }

                    nav(continueUrl);
                }
            }
        } catch (ex) {
            console.error(ex);
            setErrMsg("Tài khoản hoặc mật khẩu không đúng! Hãy thử lại")
        }
    }

    const handleGoogleLogin = () => {
        //redirect google endpoint của springboot oauth2
        window.location.href = 'http://localhost:8080/oauth2/authorization/google';
    };

    return <>
        <div className="login-container">
            <div className="login-box">
                <Form className="login-form" onSubmit={login}>
                    <div className="login-header">
                        <h1>Đăng nhập</h1>
                    </div>
                    {errMsg !== "" && (
                        <Alert variant="danger" className="alert-danger">
                            <div>{errMsg}</div>
                        </Alert>
                    )}
                    {fields.map((f) => (
                        <Form.Group className="mb-3" controlId={f.field} key={f.field}>
                            <Form.Label className="form-label">{f.label}</Form.Label>
                            <Form.Control
                                onChange={(e) => change(e, f.field)}
                                value={r[f.field]}
                                type={f.type}
                                placeholder={f.placeholder}
                                className="form-control"
                            />
                        </Form.Group>
                    ))}
                    <div className="div-forget-password">
                        <Link className="forget-password-link" to="/forgetPassword">
                            Quên mật khẩu?
                        </Link>
                    </div>
                    <Form.Group className="mb-3 text-center">
                        <Button type="submit" variant="primary" className="btn-submit">
                            Đăng Nhập
                        </Button>
                    </Form.Group>
                    <div className="div-register-box">
                        Chưa có tài khoản? <Link to="/register">Đăng ký</Link>
                    </div>
                    <div className="divider">
                        <div className="divider-bar"></div>
                        <p className="divider-text">Hoặc</p>
                        <div className="divider-bar"></div>
                    </div>
                    <div className="alt-login-box">
                        <Button variant="outline-secondary" className="btn-login-google" onClick={handleGoogleLogin}>
                            Đăng nhập với Google
                        </Button>
                    </div>
                </Form>
            </div>
        </div>

    </>
}

export default Login;