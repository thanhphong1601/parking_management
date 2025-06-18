import { useEffect, useRef, useState } from "react";
import { Alert, Button, Container, Form } from "react-bootstrap";
import APIs, { endpoints } from "../../configs/APIs";
import { Link, useNavigate } from "react-router-dom";
import MySpinner from "../common/MySpinner";
import './Register.css'

const Register = () => {
    const fields = [{
        label: "Họ tên",
        type: "text",
        field: "name",
        placeholder: "Họ tên của bạn"
    }, {
        label: "Email",
        type: "email",
        field: "email",
        placeholder: "Email của bạn"
    }, {
        label: "Tài khoản",
        type: "text",
        field: "username",
        placeholder: "Tài khoản của bạn"
    }, {
        label: "Mật khẩu",
        type: "password",
        field: "password",
        placeholder: "Mật khẩu của bạn"
    }, {
        label: "Xác nhận mật khẩu",
        type: "password",
        field: "confirm",
        placeholder: "Xác nhận mật khẩu"
    }, {
        label: "Địa chỉ nhà",
        type: "text",
        field: "address",
        placeholder: "Địa chỉ nhà"
    }, {
        label: "Ngày sinh",
        type: "date",
        field: "birthday",
        placeholder: "Ngày sinh"
    }]

    const [user, setUser] = useState({
        "role": 3,
    });

    const [dateOfBirth, setDateOfBirth] = useState('');
    const [loading, setLoading] = useState(false);
    const [errMsg, setErrMsg] = useState("");

    const nav = useNavigate();

    const change = (e, field) => {
        setUser(current => {
            return { ...current, [field]: e.target.value }
        })
    }

    const handleChange = (e, field) => {
        change(e, field);
        const value = e.target.value;
    
        // Validate email format
        if (field === "email") {
          const isValidEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
          setErrMsg((prev) => ({ ...prev, email: !isValidEmail }));
        }
      };

      const register = async (e) => {
        e.preventDefault();
        setErrMsg("");
        localStorage.removeItem("register_form");


        let form = new FormData();

        for (let key in user)
            if (key !== 'confirm') {
                form.append(key, user[key]);
                // console.info(user[key]);
            }

        try {
            if (user.confirm === user.password) {
                // console.log('User:', user);
                //let res = await APIs.post(endpoints['register'], form)
                let res = await APIs.get(endpoints['check-mail-and-identity-number'](user.email, user.identityNumber, user.username));

                if (res.status === 200) {
                    //localStorage.setItem("register_user", user);
                    nav(`/verify?email=${encodeURIComponent(user.email)}`, { state: { user: user } });        
                }
            }
            else
                setErrMsg("Mật khẩu xác nhận sai. Hãy nhập lại!");

        } catch (ex) {
            console.error(ex);
            if (ex.response && ex.status === 409)
                setErrMsg(ex.response.data);
            else
                setErrMsg("Email không hợp lệ. Hãy kiểm tra lại email!");

        }
    }


    return <>
        <div className="register-container">
            <div className="register-box">
                <Form className="register-form" onSubmit={register}>
                    <div className="register-header">
                        <h1>Đăng ký tài khoản</h1>
                    </div>

                    {errMsg && (
                        <Alert variant="danger" className="alert-danger">
                            <div>{errMsg}</div>
                        </Alert>
                    )}

                    {fields.map((f) => (
                        <Form.Group className="mb-3" controlId={f.field} key={f.field}>
                            <Form.Label className="form-label">{f.label}</Form.Label>
                            <Form.Control
                                onChange={(e) => change(e, f.field)}
                                value={user[f.field]}
                                type={f.type}
                                placeholder={f.placeholder}
                                className="form-control"
                                required
                            />
                        </Form.Group>
                    ))}

                    <Form.Group className="mb-3" controlId="identityNumber">
                        <Form.Label className="form-label">Số CMND/CCCD</Form.Label>
                        <Form.Control
                            onChange={(e) => change(e, "identityNumber")}
                            value={user["identityNumber"]}
                            type="text"
                            placeholder="Nhập số CMND/CCCD"
                            maxLength={12}
                            minLength={12}
                            className="form-control"
                        />
                    </Form.Group>

                    <Form.Group className="mb-4" controlId="phone">
                        <Form.Label className="form-label">Số điện thoại</Form.Label>
                        <Form.Control
                            onChange={(e) => change(e, "phone")}
                            value={user["phone"]}
                            type="text"
                            placeholder="Nhập số điện thoại"
                            maxLength={10}
                            minLength={8}
                            className="form-control"
                        />
                    </Form.Group>

                    <Form.Group className="text-center">
                        <Button type="submit" variant="primary" className="btn-submit">
                            Đăng ký
                        </Button>
                    </Form.Group>

                    <div className="div-login-box">
                        Đã có tài khoản? <Link to="/login">Đăng nhập</Link>
                    </div>
                </Form>
            </div>
        </div>
    </>
}

export default Register;