import { useContext, useEffect, useRef, useState } from 'react';
import { Button, Form, Image, Modal, Spinner } from 'react-bootstrap';
import { format, parse } from 'date-fns';
import { authApi, endpoints } from '../../configs/APIs';
import MySpinner from '../common/MySpinner';
import { MyUserContext } from '../../configs/Contexts';
import { useLocation, useNavigate } from 'react-router-dom';
import './ManageIn.css'


const ManageIn = () => {
    // const data2 = {
    //     "ticketStartDate": "2025-03-19T17:00:00.000+00:00",
    //     "ownerUsername": "Anonymous",
    //     "ticketStatus": "false",
    //     "ticketTotalPrice": 900000,
    //     "ticketType": "VIP",
    //     "ticketPosition": "",
    //     "ticketLicenseNumber": "",
    //     "ticketExpiredDate": "2025-06-17T17:00:00.000+00:00"
    // }
    const nav = useNavigate();
    const [inImgUrl, setInImgUrl] = useState("https://placehold.co/250x150/png");
    const [outImgUrl, setOutImgUrl] = useState("https://placehold.co/250x150/png");
    const [inPersonImgUrl, setInPersonImgUrl] = useState("https://placehold.co/250x150/png");
    const [outPersonImgUrl, setOutPersonImgUrl] = useState("https://placehold.co/250x150/png");
    const [timeIn, setTimeIn] = useState(null);
    const [uid, setUid] = useState(null);
    const [ticketId, setTicketId] = useState("");
    const [ticketStartDate, setTicketStartDate] = useState(null);
    const [ticketEndDate, setTicketEndDate] = useState(null);
    const files = useRef(); //license plate image
    const files2 = useRef(); //person image
    const file3 = useRef(); //ticket image
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState("");
    const [data, setData] = useState(null);
    const [data2, setData2] = useState(null);
    const currentUser = useContext(MyUserContext);
    const [vehicleChoice, setVehicleChoice] = useState(null);
    const [plateLicense, setPlateLicense] = useState("");
    const [updatedTicketId, setUpdatedTicketIt] = useState(0);

    const location = useLocation();
    const currentPath = location.pathname + location.search;
    if (currentUser == null) {
        nav(`/login?continue=${encodeURIComponent(currentPath)}`);
    }

    //list
    const [vehicles, setVehicles] = useState([]);

    //modal interaction
    const [showLoadingModal, setShowLoadingModal] = useState(false);
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    const [showVehicleModal, setShowVehicleModal] = useState(false);

    const showLoading = () => {
        setShowLoadingModal(true);
    };

    const closeLoading = () => {
        setShowLoadingModal(false);
    };

    const showSuccess = () => {
        setShowSuccessModal(true);
    };

    const closeSuccess = () => {
        setShowSuccessModal(false);
    };

    const showVehicle = () => {
        setShowVehicleModal(true);
    };


    const closeVehicle = () => {
        setShowVehicleModal(false);
        setPlateLicense("");
    };

    if (currentUser === null) {
        nav('/login');
    }


    //util func
    const formatDate = (isoDateString) => {
        const dateObj = new Date(isoDateString);
        return dateObj.toLocaleDateString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    const formatDateWithHour = (dateStr) => {
        const normalizedDateStr = dateStr.replace('ICT', 'GMT+0700');
        const date = new Date(normalizedDateStr);
        if (isNaN(date)) {
            return 'Invalid date'; // Xử lý lỗi nếu có
        }
        return format(date, 'dd/MM/yyyy HH:mm:ss');
    };

    const ticketImageChoose = async (e) => {
        e.preventDefault();
        setMsg("");
        setLoading(true);
        showLoading();

        const file = e.target.files[0];
        if (file) {
            try {
                let form = new FormData();
                form.append("file", file);
                let res = await authApi().post(endpoints['entry-ticket-image-text-extract'], form);

                if (res.status == 200) {
                    setTicketId(res.data);
                    loadInfo(res.data);
                    setLoading(false);
                    showSuccess();

                    setTimeout(() => {
                        closeSuccess();
                    }, 2000);
                }
            } catch (ex) {
                if (ex.response.status == 409) {
                    setMsg(ex.response.data);
                    setLoading(false);
                    setTicketId("");

                    setTimeout(() => {
                        closeLoading();
                    }, 2000);
                }
            }
        }
    };

    // useEffect(() => {
    //     if (ticketId !== "" && ticketId != null) {
    //         loadInfo();
    //         setLoading(false);
    //         showSuccess();

    //         setTimeout(() => {
    //             closeSuccess();
    //         }, 2000);
    //     }
    // }, [ticketId]);

    const handleInImgChange = async (event) => {
        event.preventDefault();
        setMsg("");

        const file = event.target.files[0]; // Lấy file đầu tiên từ input
        if (file) {
            const imageUrl = URL.createObjectURL(file); // Tạo URL tạm thời từ file
            setInImgUrl(imageUrl); // Cập nhật đường dẫn hình ảnh trong state
        }
    };

    const handleInPersonImgChange = async (event) => {
        event.preventDefault();
        setMsg("");

        const file = event.target.files[0]; // Lấy file đầu tiên từ input
        if (file) {
            const imageUrl = URL.createObjectURL(file); // Tạo URL tạm thời từ file
            setInPersonImgUrl(imageUrl); // Cập nhật đường dẫn hình ảnh trong state
        }
    };

    const loadInfo = async (ticketId) => {
        // e.preventDefault();
        setMsg("");
        setInImgUrl("https://placehold.co/250x150/png");
        setInPersonImgUrl("https://placehold.co/250x150/png");

        setLoading(true);
        showLoading();
        if (ticketId != "") {
            try {
                let url = `${endpoints['entry-ticket-process']}?ticketId=${ticketId}`
                let res = await authApi().get(url);

                if (res.status == 200) {
                    setData2(res.data);

                    setTimeout(() => {
                        closeLoading();
                    }, 1000);
                }
            } catch (ex) {
                if (ex.response.status == 400 || ex.response.status == 404) {
                    setMsg(ex.response.data["Ticket error: "]);
                    setLoading(false);
                }
                console.info(ex);
            }
        } else {
            setLoading(false);
            closeLoading();
        }
    };

    const recordEntry = async (e) => {
        e.preventDefault();
        setMsg("");
        setLoading(true);
        showLoading();

        let form = new FormData();

        if (files && files2) {
            form.append('file1', files.current.files[0]);
            form.append('file2', files2.current.files[0]);
        }
        if (ticketId != "") {
            const now = new Date();
            const timeIn = now.toISOString().replace('Z', '+00:00');
            form.append("timeIn", timeIn);
            form.append("ticketId", ticketId);
            form.append("creatorId", currentUser.id);
            form.append("userId", data2.ownerUserId);
            form.append("vehicleId", data2.ticketVehicleId);
        }

        try {
            let res = await authApi().post(endpoints['entry-record-in'], form, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            if (res.status == 201) {
                updatePositionState();
                closeLoading();
                setLoading(false);
                showSuccess();

                getPositionInfo();

                setTimeout(() => {
                    closeSuccess();
                    showLoading();
                }, 2000);
                setTimeout(() => {
                    closeLoading();
                }, 4000);
            }
        } catch (ex) {
            if (ex.response.status == 400 || ex.response.status == 404 || ex.response.status == 409) {
                setMsg(ex.response.data);

                setLoading(false);

                setTimeout(() => {
                    closeSuccess();
                    closeLoading();
                }, 2000);
            }
            console.info(ex);
        }
    };

    const updatePositionState = async () => {
        try {
            let url = `${endpoints['position-info-find-by-ticketId'](ticketId)}`;
            let res = await authApi().get(url);
            if (res.status == 200) {
                url = `${endpoints['position-state-update-vehicle-in'](res.data.position.id)}?ticketId=${ticketId}`;
                res = await authApi().post(url);

                if (res.status == 200) {
                    console.info(res.status);
                }
            }


        } catch (ex) {
            if (ex.response.status == 404) {
                console.error(ex.response.data);
            }
            console.error(ex);
        }
    };

    const getPositionInfo = async () => {
        setMsg("");

        try {
            let url = `${endpoints['position-info-find-by-ticketId'](ticketId)}`;
            let res = await authApi().get(url);

            if (res.status == 200) {
                let info = `
                Vị trí được xác định:
                Vị trí ${res.data.position.position} dãy ${res.data.line.line} thuộc tầng ${res.data.floor.floorNumber}
            `;
                setMsg(info);
            }
        } catch (ex) {
            console.error(ex);
        }
    };

    const createAnonymousTicket = async (e) => {
        e.preventDefault();
        setMsg("");

        setLoading(true);
        showLoading();

        try {
            let url = `${endpoints['ticket-create-anonymous']}?userCreateId=${currentUser.id}`;
            let res = await authApi().post(url);

            if (res.status == 201) {
                setLoading(false);
                setMsg("Đã tạo vé mới thành công.");

                res = await authApi().get(endpoints['ticket-get-newest']);
                if (res.status == 200) {
                    setTicketId(res.data);
                    loadInfo(res.data);
                    getPositionInfo();
                }

                setTimeout(() => {
                    closeLoading();
                }, 3000);
            }
        } catch (ex) {
            setMsg("Đã có lỗi xảy ra");

            console.error(ex);
        }
    };


    const test = (e) => {
        e.preventDefault();
        setMsg("");
        getPositionInfo();
        console.info(msg);
    }

    //loading resource funcs
    const loadVehicles = async () => {
        try {
            let url = `${endpoints['vehicle-list-choice']}?plateLicense=${plateLicense}`;

            let res = await authApi().get(url);
            setVehicles(res.data);
        } catch (ex) {
            console.error(ex);
        }
    };

    const chooseVehicle = (e, vehicle) => {
        e.preventDefault();
        setVehicleChoice(vehicle);
        closeVehicle();
    };

    useEffect(() => {
        loadVehicles();
    }, [plateLicense]);


    return <>
        <div className="container">
            <h1>QUẢN LÝ XE VÀO</h1>
            <div className='ticket-input'>
                <label className='ticket-input-label' htmlFor="ticketInput">Mã vé</label>
                <input className='' id="ticketInput" type='text' placeholder='Id vé' value={ticketId} onChange={e => setTicketId(e.target.value)}></input>
                <div className=''>
                    <Button onClick={e => loadInfo(ticketId)} className='btn btn-success'>Xác nhận</Button>
                </div>
                <div className=''>
                    <Button onClick={e => createAnonymousTicket(e)} className='btn btn-success'>Tạo vé mới</Button>
                </div>
                <Form.Control type="file" accept=".png,.jpg" ref={file3} onChange={(e) => ticketImageChoose(e)} />
            </div>

            <div className="form-container">
                <div className="input-container">
                    <div className='d-flex justify-content-between p-1'>
                        <div className="form-group">
                            <label htmlFor="ownerName">Tên khách hàng</label>
                            <input disabled id="ownerName" className='me-3' type="text" placeholder="Tên khách hàng" value={data2 ? data2.ownerUsername : ""} />
                        </div>
                    </div>
                    <div className='d-flex justify-content-between p-1'>
                        <div className="form-group">
                            <label htmlFor="vehicleName">Loại vé</label>
                            <input id="vehicleName" disabled className='me-2' type="text" placeholder="Loại vé" value={data2 ? data2.ticketType : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="vehicleType">Trạng thái vé</label>
                            <input id="vehicleType" disabled className='me-2' type="text" placeholder="Trạng thái vé" value={data2 ? data2.ticketStatus === "true" ? "Đã thanh toán" : "Chưa thanh toán" : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="plateLicense">Tổng giá tiền</label>
                            <input id="plateLicense" disabled type="text" placeholder="Tổng giá tiền" value={data2 ? data2.ticketTotalPrice + " VNĐ" : ""} />
                        </div>
                    </div>

                    <div className='d-flex justify-content-between p-1'>
                        <div className="form-group">
                            <label htmlFor="startDay">Ngày bắt đầu</label>
                            <input id="startDay" disabled className='me-3' type="text" placeholder="Thời hạn vé từ" value={data2 ? formatDate(data2.ticketStartDate) : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="endDay">Ngày kết thúc</label>
                            <input id="endDay" disabled type="text" placeholder="Đến" value={data2 ? formatDate(data2.ticketExpiredDate) : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="position">Vị trí</label>
                            <input id="position" disabled className='me-3' type="text" placeholder="Vị trí" value={data2 ? data2.ticketPosition === "" ? "Trống" : data2.ticketPosition : "Trống"} />
                        </div>
                    </div>


                </div>

                <h5>Biển số xe</h5>
                <div className="image-container">
                    <Image src={inImgUrl} alt="Image 1" />
                    <Form.Group controlId="image" className="mb-3 me-1">
                        <Form.Label>Chọn hình ảnh biến số xe:</Form.Label>
                        <Form.Control type="file" accept=".png,.jpg" ref={files} onChange={e => handleInImgChange(e)} />
                    </Form.Group>
                    <Image src={outImgUrl} alt="Image 2" />
                </div>
                <h5>Người dùng</h5>

                <div className="image-container mt-3">
                    <Image src={inPersonImgUrl} alt="Image 1" />
                    <Form.Group controlId="image" className="mb-3 me-1">
                        <Form.Label>Chọn hình ảnh người dùng:</Form.Label>
                        <Form.Control type="file" accept=".png,.jpg" ref={files2} onChange={e => handleInPersonImgChange(e)} />
                    </Form.Group>
                    <Image src={outPersonImgUrl} alt="Image 2" />
                </div>

                <div className="time-container">
                    <div className="form-group">
                        <input disabled id="checkInTime" type="text" placeholder="Thời gian vào" value={data ? formatDateWithHour(data.timeIn) : "Thời gian vào"} />
                    </div>
                    <div className="form-group">
                        <input disabled id="checkOutTime" type="text" placeholder="Thời gian ra" />
                    </div>
                </div>

                <button className="submit-button" onClick={e => recordEntry(e)} >Lưu thông tin</button>
            </div>




        </div>

        <Modal show={showLoadingModal} onHide={closeLoading}>
            <Modal.Body className="text-center">
                {loading ? (
                    <>
                        <Spinner animation="border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </Spinner>
                        <p>Đang tiến hành phân tích thông tin...</p>
                    </>
                ) : (
                    <h2>{msg}</h2>
                )}
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={closeLoading}>
                    Đóng
                </Button>
            </Modal.Footer>
        </Modal>

        <Modal show={showSuccessModal} onHide={closeSuccess}>
            <Modal.Body className="text-center">
                <h2>Đã Upload thành công</h2>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={closeSuccess}>
                    Đóng
                </Button>
            </Modal.Footer>
        </Modal>

        <Modal show={showVehicleModal} onHide={closeVehicle} >
            <Modal.Header closeButton>
                <Modal.Title>Tìm kiếm phương tiện</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="modal-container">
                    <div className="findBar">
                        <input value={plateLicense} onChange={(e) => setPlateLicense(e.target.value)} placeholder="Biển số phương tiện"></input>
                    </div>
                    <div className="customer-table">
                        <thead>
                            <tr>
                                <th>Tên phương tiện</th>
                                <th>Biển số</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            {vehicles.length > 0 && vehicles.map(v => <tr key={v.id}>
                                <td>{v.name}</td>
                                <td>{v.plateLicense}</td>
                                <td>
                                    <Button onClick={(e) => chooseVehicle(e, v)}>Chọn</Button>
                                </td>
                            </tr>)}
                        </tbody>
                    </div>
                </div>
            </Modal.Body>
        </Modal>
    </>
}

export default ManageIn;