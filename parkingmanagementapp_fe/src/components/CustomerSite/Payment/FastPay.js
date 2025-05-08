import { useContext, useState } from "react";
import { Modal, Spinner } from "react-bootstrap";
import APIs, { endpoints } from "../../../configs/APIs";
import { MyUserContext } from "../../../configs/Contexts";

const FastPay = () => {
    const currentUser = useContext(MyUserContext);
    const [showModal, setShowModal] = useState(false);
    const [msg, setMsg] = useState("Đang xử lý vé");
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        name: '',
        identityNumber: '',
        ticketId: ''
    });

    const handleChange = (e) => {
        const { id, value } = e.target;
        setFormData({ ...formData, [id]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMsg("Đang xử lý vé");
        setLoading(true);
        setShowModal(true);

        try {
            let url = `${endpoints['fast-payment']}?ticketId=${formData.ticketId}`;
            let res = await APIs.get(url);

            if (res.status == 200) {
                setShowModal(false);
                localStorage.setItem("ticketId", res.data.id);

                let form = new FormData;

                const info = `Người dùng ${formData.name} - CMND: ${formData.identityNumber} thanh toán vé mang id ${formData.ticketId}`;
                form.append("orderInfo", info);
                form.append("price", res.data.totalPrice);
                form.append("ticketId", res.data.id);

                res = await APIs.post(endpoints['create-payment'], form);


                window.location.href = res.data;
            }

        } catch (error) {
            if (error.response.status == 404 || error.response.status == 409) {
                setMsg(error.response.data);
            }
            setLoading(false);

            setTimeout(() => {
                setShowModal(false);
            }, 3000);
        }
    };

    return (<>
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-6 col-sm-10">
                    <div className="card shadow-sm rounded-4 p-4">
                        <h4 className="mb-4 text-center">Fast Payment</h4>
                        <form>
                            <div className="mb-3">
                                <label htmlFor="name" className="form-label">Họ tên</label>
                                <input minLength={20} maxLength={50} type="text" className="form-control" id="name" placeholder="Nhập họ tên" onChange={e => handleChange(e)} />
                            </div>
                            <div className="mb-3">
                                <label htmlFor="identityNumber" className="form-label">Số CMND</label>
                                <input type="text" minLength={16} maxLength={16} className="form-control" id="identityNumber" placeholder="Nhập số CMND" onChange={e => handleChange(e)} />
                            </div>
                            <div className="mb-3">
                                <label htmlFor="ticketId" className="form-label">Mã vé</label>
                                <input minLength={1} maxLength={16} type="text" className="form-control" id="ticketId" placeholder="Nhập mã vé" onChange={e => handleChange(e)} />
                            </div>
                            <div className="d-grid">
                                <button onClick={(e) => handleSubmit(e)} className="btn btn-primary">Thanh toán nhanhh</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <Modal show={showModal} centered backdrop="static" keyboard={false}>
                <Modal.Body className="text-center p-4">
                    <h5 className="mb-3">{msg}</h5>
                    {loading === true && <Spinner animation="border" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </Spinner>}

                </Modal.Body>
            </Modal>
        </div>
    </>);
}

export default FastPay;