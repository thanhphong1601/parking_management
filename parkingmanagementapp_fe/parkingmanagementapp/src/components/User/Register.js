import { useEffect, useRef, useState } from "react";
import { Alert, Button, Container, Form } from "react-bootstrap";
import APIs, { endpoints } from "../../configs/APIs";
import { useNavigate } from "react-router-dom";
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

    const register = async (e) => {
        e.preventDefault();
        setErrMsg("");

        let form = new FormData();

        for (let key in user)
            if (key !== 'confirm') {
                form.append(key, user[key]);
                // console.info(user[key]);
            }

        try {
            if (user.confirm === user.password) {
                // console.log('User:', user);
                let res = await APIs.post(endpoints['register'], form)

                if (res.status === 201)
                    nav("/login");
            }

            else
                setErrMsg("Mật khẩu xác nhận sai. Hãy nhập lại!");

        } catch (ex) {
            console.error(ex);
            setErrMsg("Email không hợp lệ. Hãy kiểm tra lại email!");
        }
    }


    return <>
        <div style={{ alignItems: "center", alignContent: "center", backgroundColor: "lightblue", borderRadius: 10, height: 60, marginLeft: 20, marginRight: 20, margin: 10 }}>
            <h1 className="text-center mt-3 mb-3">Trang đăng ký</h1>
        </div>
        {errMsg == "" ? <></> : <>
            <Alert variant="danger">
                <div>
                    {errMsg}
                </div>
            </Alert>
        </>}
            <Form onSubmit={register}>
                {fields.map(f => <Form.Group className="mb-3" controlId={f.field}>
                    <Form.Label>{f.label}</Form.Label>
                    <Form.Control onChange={e => change(e, f.field)} value={user[f.field]} type={f.type} placeholder={f.placeholder} />
                </Form.Group>
                )}

                <Form.Group className="mb-3">
                    <Form.Label>Số CNMD</Form.Label>
                    <Form.Control onChange={e => change(e, "identityNumber")} 
                    value={user["identityNumber"]} 
                    type="text" 
                    placeholder="Hãy nhập CMND"
                    maxLength={12}/>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Số điện thoại</Form.Label>
                    <Form.Control onChange={e => change(e, "phone")} 
                    value={user["phone"]} 
                    type="text" 
                    placeholder="Hãy nhập số điện thoại"
                    maxLength={10}/>
                </Form.Group>

                <Form.Group className="mb-3 text-center">
                    <Button type="submit" variant="success" style={{ fontSize: 20, fontWeight: "bold" }}>Đăng ký</Button>
                </Form.Group>
            </Form>
    </>
}

export default Register;