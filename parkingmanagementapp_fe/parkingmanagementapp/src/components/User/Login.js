import { useContext, useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import APIs, { authApi, endpoints } from "../../configs/APIs";
import cookie from 'react-cookies'
import { useNavigate } from "react-router-dom";
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
            // console.log(res.data);
            //cookie.save("token", res.data);
            localStorage.setItem("token", res.data);

            let u = await authApi().get(endpoints['current-user']);

            dispatch({
                "type": "login",
                "payload": u.data
            })
            nav("/");
        } catch (ex) {
            console.error(ex);
            setErrMsg("Tài khoản hoặc mật khẩu không đúng! Hãy thử lại")
        }
    }

    return <>
        <div className="login-container">
            <div className="login-box">
                <div className="login-header">
                    <h1>Đăng nhập</h1>
                </div>
                {errMsg !== "" && (
                    <Alert variant="danger" className="alert-danger">
                        <div>{errMsg}</div>
                    </Alert>
                )}
                <Form className="login-form" onSubmit={login}>
                    {fields.map(f => (
                        <Form.Group className="mb-3" controlId={f.field} key={f.field}>
                            <Form.Label className="form-label">{f.label}</Form.Label>
                            <Form.Control
                                onChange={e => change(e, f.field)}
                                value={r[f.field]}
                                type={f.type}
                                placeholder={f.placeholder}
                                className="form-control"
                            />
                        </Form.Group>
                    ))}
                    <Form.Group className="mb-3 text-center">
                        <Button type="submit" variant="success" className="btn-submit">Đăng Nhập</Button>
                    </Form.Group>
                </Form>
            </div>
        </div>

    </>
}

export default Login;