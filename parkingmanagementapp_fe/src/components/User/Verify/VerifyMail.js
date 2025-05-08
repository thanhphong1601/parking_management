import { useContext, useEffect, useState } from "react";
import { Alert, Button, Form, InputGroup } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import './VerifyMail.css'
import { MyUserContext } from "../../../configs/Contexts";
import * as SendMail from "../../Utils/SendMail";
import APIs, { endpoints } from "../../../configs/APIs";
import { RiSafariFill } from "react-icons/ri";

const VerifyMail = () => {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const email = queryParams.get("email");
    const registerUser = location.state?.user;
    //const user = useContext(MyUserContext);
    const nav = useNavigate();


    const [code, setCode] = useState("");
    const [timer, setTimer] = useState(0);
    const [informMsg, setInformMsg] = useState("Đang tiến hành...");
    const [errMsg, setErrMsg] = useState("");

    const onResend = async (email) => {
        setInformMsg("Đang tiến hành...");

        try {
            let res = await APIs.post(endpoints['verify-mail'](email));
            if (res.status == 200){
                setInformMsg(res.data);
            }

        } catch (ex) {
            console.error(ex);
            setInformMsg("Đã có lỗi xãy ra");
        }
    };

    const onSubmit = async (e) => {
        e.preventDefault();
        setErrMsg("");

        let status = "failed";

        let form = new FormData();

        for (let key in registerUser) {
            if (key !== 'confirm') {
                form.append(key, registerUser[key]);
                // console.info(user[key]);
            }
        }

        // for (let [key, value] of form.entries()) {
        //     console.log(`${key}: ${value}`);
        // }


        try {
            let res = await APIs.post(endpoints['verify-otp'](code, email));
            if (res.status != 200)
                setErrMsg(res.data);
            else {
                if (registerUser["confirm"] != null) {
                    res = await APIs.post(endpoints['register'], form);
                    if (res.status == 201) {
                        status = "successful";
                        console.info("1 done");
                        nav(`/verify/inform?email=${encodeURIComponent(email)}&success=${status}`);
                    };
                } else {
                    res = await APIs.get(endpoints['active-user'](registerUser['email']));
                    if (res.status == 200){
                        status = "successful";
                        console.info("2 donee");
                        nav(`/verify/inform?email=${encodeURIComponent(email)}&success=${status}`);
                    }
                }
            }
        } catch (ex) {
            console.error(ex);
            if (ex.response && ex.status === 409)
                setErrMsg(ex.response.data);
            else
                setErrMsg("Đã có lỗi xảy ra. Hãy thử lại sau!");
        };
    };

    const handleResend = () => {
        onResend(email);
        setTimer(60);
    };

    useEffect(() => {
        handleResend();
    }, []);


    useEffect(() => {
        let interval;
        if (timer > 0) {
            interval = setInterval(() => setTimer(t => t - 1), 1000);
        }
        return () => clearInterval(interval);
    }, [timer]);

    return (
        <div className="verify-container">
            <div className="verify-box">
                <h2 className="verify-header">Xác thực tài khoản</h2>
                {informMsg !== "" && (
                    <Alert variant="success" className="alert-success">
                        <div>{informMsg}</div>
                    </Alert>
                )}

                {errMsg && (
                    <Alert variant="danger" className="alert-danger">
                        <div>{errMsg}</div>
                    </Alert>
                )}
                <p className="verify-instruction">
                    Vui lòng kiểm tra email <strong>{email}</strong> để lấy mã xác thực.
                </p>

                <Form onSubmit={e => onSubmit(e)} className="verify-form">
                    <InputGroup className="mb-3">
                        <Form.Control
                            type="text"
                            placeholder="Nhập mã xác thực"
                            value={code}
                            onChange={(e) => setCode(e.target.value)}
                            required
                            className="form-control"
                        />
                        <Button
                            variant="outline-secondary"
                            onClick={handleResend}
                            disabled={timer > 0}
                        >
                            {timer > 0 ? `Gửi lại (${timer}s)` : "Gửi lại mã"}
                        </Button>
                    </InputGroup>

                    <Button type="submit" variant="primary" className="btn-submit">
                        Xác nhận
                    </Button>
                </Form>
            </div>
        </div>
    );
}

export default VerifyMail;