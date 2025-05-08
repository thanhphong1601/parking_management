import { useEffect, useState } from "react";
import { Alert, Button, Form, InputGroup } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import APIs, { authApi, endpoints } from "../../../configs/APIs";
import "./ForgetPassword.css"

const ForgetPassword = () => {
    const [email, setEmail] = useState("");
    const [code, setCode] = useState("");
    const [timer, setTimer] = useState(0);
    const [password, setPassword] = useState("");
    const [confirm, setConfirm] = useState("");
    const [informMsg, setInformMsg] = useState("");
    const [errMsg, setErrMsg] = useState("");
    const [disableButton, setDisableButton] = useState(false);


    const [progress, setProgress] = useState(1);
    const nav = useNavigate();

    const sendMail = async (e) => {
        e.preventDefault();
        setInformMsg("");
        setDisableButton(true);

        if (email != "" || email != null) {
            try {

                console.info(email);
                let url = `${endpoints['check-mail-valid']}?email=${email}`;
                let res = await APIs.post(url);
                if (res.status == 200) {
                    setTimer(60);
                    res = await APIs.post(endpoints['forget-pass-verify-mail'](email));

                    if (res.status == 200) {
                        setInformMsg(res.data);
                        setInformMsg("");
                        setErrMsg("");
                        goNextProgress(e);
                    }
                }
            } catch (ex) {
                if (ex.response.status == 409) { //conflict
                    setInformMsg(ex.response.data);
                }
                console.error(ex);
            } finally {
                setDisableButton(false);
            }
        }

    }

    const submitCode = async (e) => {
        e.preventDefault();
        setErrMsg("");
        setInformMsg("");

        try {
            if (code.length == 6) {
                let res = await APIs.post(endpoints['verify-otp'](code, email));

                if (res.status != 200)
                    setErrMsg(res.data);
                else {
                    setInformMsg("");
                    setErrMsg("");
                    goNextProgress(e);
                }
            }
            else {
                setErrMsg("Mã code không đúng. Hãy nhập lại");
            }
        } catch (ex) {
            console.error(ex);
        }
    }

    const handleResend = async (e) => {
        e.preventDefault();
        sendMail(e);
    }

    const confirmNewPassword = async (e) => {
        e.preventDefault();
        setInformMsg("");
        setErrMsg("");
        setDisableButton(true);

        let form = new FormData();
        form.append("email", email);
        form.append("password", password);

        if (password === confirm) {
            try {
                let res = await APIs.post(endpoints['forget-password'], form);

                if (res.status == 200) {
                    setInformMsg(res.data);

                    setTimeout(() => {
                        nav("/login");
                    }, 2000)
                }
            } catch (ex) {
                if (ex.response.status == 404)
                    setErrMsg(ex.response.data);
                console.error(ex);
            } finally {
                setDisableButton(false);
            }
        } else {
            setErrMsg("Mật khẩu không khớp. Hãy nhập lại");
        }

    }

    const goNextProgress = (e) => {
        e.preventDefault();
        setProgress(current => current + 1);
    }

    //timer
    useEffect(() => {
        let interval;
        if (timer > 0) {
            interval = setInterval(() => setTimer(t => t - 1), 1000);
        }
        return () => clearInterval(interval);
    }, [timer]);

    return (<>
        <div className="register-container">
            <div className="register-box">
                {progress ? progress === 1 ?
                    <Form className="register-form" onSubmit={sendMail}>
                        <div className="register-header">
                            <h1>Quên mật khẩu</h1>
                        </div>

                        <Form.Group className="mb-3">
                            <Form.Label className="form-label">Email</Form.Label>
                            <Form.Control
                                onChange={(e) => setEmail(e.target.value)}
                                value={email}
                                type="email"
                                placeholder="Hãy nhập email"
                                className="form-control"
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-3 text-center">
                            <Button disabled={disableButton} type="submit" variant="primary" className="btn-submit">
                                Gửi
                            </Button>
                        </Form.Group>

                    </Form>
                    : progress === 2 ? <>

                        <Form onSubmit={e => submitCode(e)} className="verify-form">
                            <div className="register-header">
                                <h1>Nhập mã xác thực</h1>
                            </div>

                            {informMsg && (
                                <Alert variant="success" className="alert-success">
                                    <div>{informMsg}</div>
                                </Alert>
                            )}

                            {errMsg && (
                                <Alert variant="danger" className="alert-danger">
                                    <div>{errMsg}</div>
                                </Alert>
                            )}


                            <InputGroup className="mb-3">
                                <Form.Control
                                    type="text"
                                    placeholder="Nhập mã xác thực"
                                    value={code}
                                    onChange={(e) => setCode(e.target.value)}
                                    required
                                    maxLength={6}
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

                            <div className="input-note">
                                *Mã code gồm 1 dãy 6 chữ số
                            </div>

                            <Button 
                            type="submit" 
                            variant="primary" 
                            className="btn-submit"
                            disabled={code.length !== 6}>
                                Xác nhận
                            </Button>

                        </Form></>
                        : <>
                            <Form className="verify-form" onSubmit={confirmNewPassword}>
                                <div className="register-header">
                                    <h1>Tạo mật khẩu mới</h1>
                                </div>

                                {informMsg && (
                                    <Alert variant="success" className="alert-success">
                                        <div>{informMsg}</div>
                                    </Alert>
                                )}

                                {errMsg && (
                                    <Alert variant="danger" className="alert-danger">
                                        <div>{errMsg}</div>
                                    </Alert>
                                )}

                                <Form.Group className="mb-3">
                                    <Form.Label className="form-label">Mật khẩu mới</Form.Label>
                                    <Form.Control
                                        onChange={(e) => setPassword(e.target.value)}
                                        value={password}
                                        type="password"
                                        minLength={1}
                                        placeholder="Mật khẩu"
                                        className="form-control"
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label className="form-label">Xác nhận mật khẩu</Form.Label>
                                    <Form.Control
                                        onChange={(e) => setConfirm(e.target.value)}
                                        value={confirm}
                                        type="password"
                                        minLength={1}
                                        placeholder="Xác nhận mật khẩu"
                                        className="form-control"
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3 text-center">
                                    <Button disabled={disableButton} type="submit" variant="primary" className="btn-submit">
                                        Xác nhận
                                    </Button>
                                </Form.Group>

                            </Form>
                        </> : <></>}

            </div>
        </div>
    </>);
};

export default ForgetPassword;