import { useContext, useEffect, useRef, useState } from 'react';
import './ManageOut.css'
import { Button, Form, Image, Modal, Spinner } from 'react-bootstrap';
import { format, parse } from 'date-fns';
import { authApi, endpoints } from '../../configs/APIs';
import MySpinner from '../common/MySpinner';
import { MyUserContext } from '../../configs/Contexts';
import { useLocation, useNavigate } from 'react-router-dom';
import { getDayNowWithFormat } from '../Utils/FormatDate';

const ManageOut = () => {
    const [entryList, setEntryList] = useState([
        {
            "id": 6,
            "timeIn": "2024-09-29T16:47:13.048+00:00",
            "plateImgIn": null,
            "timeOut": null,
            "plateImgOut": null,
            "owner": {
                "id": 12,
                "name": "Phan Văn Hào",
                "identityNumber": "1",
                "phone": "1",
                "address": "testAdress",
                "birthday": "2024-09-16T17:00:00.000+00:00",
                "email": "pvh@gmail.com",
                "username": "khachhang1",
                "password": "$2a$10$YZHNakhZijoTopznKqusIeBnka5gMAhP0I/PE2lQ7ggJ7nIT4aTDm",
                "role": {
                    "id": 3,
                    "role": "ROLE_CUSTOMER"
                },
                "avatar": "",
                "file": null,
                "active": true,
                "enabled": true,
                "accountNonLocked": true,
                "accountNonExpired": true,
                "credentialsNonExpired": true
            },
            "creator": {
                "id": 2,
                "name": "Security",
                "identityNumber": "33333333",
                "phone": "1230",
                "address": "Security",
                "birthday": "2024-09-18T17:00:00.000+00:00",
                "email": "security@gmail.com",
                "username": "security",
                "password": "$2a$10$keZrZz7Tj4mIdrg0j5pkJeQ2EuH8QGLnT/tB12sQDrxn5nLGHvYhW",
                "role": {
                    "id": 2,
                    "role": "ROLE_SECURITY"
                },
                "avatar": "",
                "file": null,
                "active": true,
                "enabled": true,
                "accountNonLocked": true,
                "accountNonExpired": true,
                "credentialsNonExpired": true
            },
            "vehicle": {
                "id": 11,
                "name": "Vision",
                "type": {
                    "id": 2,
                    "type": "Xe máy"
                },
                "plateLicense": "IT20BOM",
                "user": {
                    "id": 12,
                    "name": "Phan Văn Hào",
                    "identityNumber": "1",
                    "phone": "1",
                    "address": "testAdress",
                    "birthday": "2024-09-16T17:00:00.000+00:00",
                    "email": "pvh@gmail.com",
                    "username": "khachhang1",
                    "password": "$2a$10$YZHNakhZijoTopznKqusIeBnka5gMAhP0I/PE2lQ7ggJ7nIT4aTDm",
                    "role": {
                        "id": 3,
                        "role": "ROLE_CUSTOMER"
                    },
                    "avatar": "",
                    "file": null,
                    "active": true,
                    "enabled": true,
                    "accountNonLocked": true,
                    "accountNonExpired": true,
                    "credentialsNonExpired": true
                }
            },
            "file": null
        },])

    const nav = useNavigate();
    const [inImgUrl, setInImgUrl] = useState("https://placehold.co/250x150/png");
    const [outImgUrl, setOutImgUrl] = useState("https://placehold.co/250x150/png");
    const [inPersonImgUrl, setInPersonImgUrl] = useState("https://placehold.co/250x150/png");
    const [outPersonImgUrl, setOutPersonImgUrl] = useState("https://placehold.co/250x150/png");
    const [timeIn, setTimeIn] = useState(null);
    const [uid, setUid] = useState(null);
    const [creatorId, setCreatorId] = useState(null);
    const [vehicleId, setVehicleId] = useState(null);
    const [ticketId, setTicketId] = useState("");
    const [ticketStartDate, setTicketStartDate] = useState(null);
    const [ticketEndDate, setTicketEndDate] = useState(null);
    const files = useRef();
    const files2 = useRef();
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState("");
    const [data, setData] = useState(null);
    const currentUser = useContext(MyUserContext);
    const [entryChoice, setEntryChoice] = useState(null);
    const [plateLicense, setPlateLicense] = useState("");
    const [positionIdFromTicket, setPositionIdFromTicket] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);

    const location = useLocation();
    const currentPath = location.pathname + location.search;
    if (currentUser == null) {
        nav(`/login?continue=${encodeURIComponent(currentPath)}`);
    }

    //list
    const [entries, setEntries] = useState([]);

    //pageable func
    const nextPage = () => {
        if (currentPage < totalPages - 1) setCurrentPage(currentPage + 1);
    };

    const prevPage = () => {
        if (currentPage > 0) setCurrentPage(currentPage - 1);
    };

    const goToPage = (pageNum) => {
        setCurrentPage(pageNum);
    };

    //modal interaction
    const [showLoadingModal, setShowLoadingModal] = useState(false);
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    const [showEntry, setShowEntry] = useState(false);

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

    const showEntryModal = () => {
        setShowEntry(true);
    };


    const closeEntryModal = () => {
        setShowEntry(false);
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

    const handleOutImgChange = async (e) => {
        e.preventDefault();
        setMsg("");

        const file = e.target.files[0]; // Lấy file đầu tiên từ input
        if (file) {
            const imageUrl = URL.createObjectURL(file); // Tạo URL tạm thời từ file
            setOutImgUrl(imageUrl); // Cập nhật đường dẫn hình ảnh trong state
        }
    };

    const handleOutPersonImgChange = async (e) => {
        e.preventDefault();
        setMsg("");

        const file = e.target.files[0]; // Lấy file đầu tiên từ input
        if (file) {
            const imageUrl = URL.createObjectURL(file); // Tạo URL tạm thời từ file
            setOutPersonImgUrl(imageUrl); // Cập nhật đường dẫn hình ảnh trong state
        }
    };

    const loadInfo = async (e) => {
        e.preventDefault();
        setMsg("");

        setLoading(true);
        showLoading();
        if (ticketId != "") {
            try {
                let url = `${endpoints['entry-ticket-process-with-entry-info']}?ticketId=${ticketId}`
                let res = await authApi().get(url);
                if (res.status == 200) {
                    setMsg("Đã lấy thông tin thành công");
                    setData(res.data);

                    //console.log(res.data);

                    setTimeout(() => {
                        closeLoading();
                    }, 1500);
                }



            } catch (ex) {
                if (ex.response.status == 404) {
                    setMsg(ex.response.data["Error: "]);
                    setLoading(false);
                    console.info(ex);
                }

            } finally {
                setLoading(false);
            }
        } else {
            setLoading(false);
            closeLoading();
            emptyInfo();
        }
    };

    const recordEntry = async (e) => {
        e.preventDefault();
        setMsg("");
        const keys = ["Error: ", "Plate Error: ", "Person Error: "];
        let errors = [];
        setLoading(true);
        showLoading();

        let form = new FormData();

        if (files && files2) {
            form.append('file1', files.current.files[0]);
            form.append('file2', files2.current.files[0]);
        }
        if (ticketId != "") {
            const now = new Date();
            const timeOut = now.toISOString().replace('Z', '+00:00');
            form.append("timeOut", timeOut);
            form.append("ticketId", ticketId);
            form.append("creatorId", currentUser.id);
            form.append("userId", data.ownerUserId);
            form.append("vehicleId", data.ticketVehicleId);
        }

        try {
            let res = await authApi().post(endpoints['entry-record-out'], form, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });


            if (res.data["Error: "] || res.data["Plate Error: "] || res.data["Person Error: "]) {
                keys.forEach(key => {
                    if (res.data[key]) {
                        errors.push(res.data[key]);
                    }
                });
                setMsg(errors.join("\n"));
                setLoading(false);
            } else if (res.status == 201) {
                closeLoading();
                setLoading(false);
                showSuccess();
                setTimeout(() => {
                    closeSuccess();
                    updatePositionState();
                }, 2000);
            };
        } catch (ex) {
            if (ex.status == 404) {
                setMsg(ex.response.data);
                setLoading(false);
            }
            //setMsg("Đã có lỗi xảy ra");
            console.error(ex);
        }
    };

    const updatePositionState = async () => {
        try {
            let url = `${endpoints['position-info-find-by-ticketId'](ticketId)}`;
            let res = await authApi().get(url);
            if (res.status == 200) {
                url = `${endpoints['position-state-update-vehicle-out'](res.data.position.id)}`;
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


    //loading resource funcs
    const loadEntries = async () => {
        try {
            let url = `${endpoints['entry-in-list-pageable']}?licenseNumber=${plateLicense}&page=${currentPage}`;

            let res = await authApi().get(url);
            setEntries(res.data.content);
            setTotalPages(res.data.totalPages);
        } catch (ex) {
            console.error(ex);
        }
    };

    const chooseEntry = async (e, entry) => {
        e.preventDefault();
        closeEntryModal();
        try {
            let url = `${endpoints['ticketId-get-by-entry'](entry.id)}`;
            let res = await authApi().get(url);

            if (res.status == 200) {
                setTicketId(res.data);
                loadInfo(e);
            }
        } catch (ex) {
            if (ex.response.status == 404) {
                console.error(ex.response.data);
            }
            console.error(ex);
        }
    };

    const payTicket = async (e) => {
        e.preventDefault();
        setMsg("");
        setLoading(true);
        showLoading();

        if (ticketId != "") {
            try {
                let url = `${endpoints['ticket-fast-pay']}?ticketId=${ticketId}`;
                let res = await authApi().put(url);
                if (res.status == 200) {
                    setMsg(res.data);
                    loadInfo(e);

                    let form = new FormData();
                    form.append("id", data.ticketId);
                    form.append("transactionDate", getDayNowWithFormat());
                    form.append("transactionNumber", generateRandom10Digits());
                    try {
                        let resReceiptCreate = await authApi().post(endpoints['receipt-create'], form);
                    } catch (ex) {
                        console.error(ex);
                    }
                }
            } catch (ex) {
                if (ex.response.status == 404) {
                    setMsg(ex.response.data);
                    setLoading(false);
                }
            } finally {
                setLoading(false);
            }
        } else {
            setLoading(false);
            closeLoading();
            emptyInfo();
        }
    }

    function generateRandom10Digits() {
        return Math.floor(1000000000 + Math.random() * 9000000000).toString();
    }


    // useEffect(() => {
    //     loadInfo();
    // }, [entryChoice]);

    useEffect(() => {
        loadEntries();
    }, [plateLicense]);

    const emptyInfo = () => {
        setInImgUrl("https://placehold.co/250x150/png");
        setOutImgUrl("https://placehold.co/250x150/png");
        setInPersonImgUrl("https://placehold.co/250x150/png");
        setOutPersonImgUrl("https://placehold.co/250x150/png");
        setData(null);
    };

    //example data
    const dataExample = {
        "ticketVehicleId": 21,
        "ticketType": "VIP",
        "ticketLicenseNumber": "it20bom",
        "ticketExpiredDate": "2025-06-17T17:00:00.000+00:00",
        "ticketStartDate": "2025-03-19T17:00:00.000+00:00",
        "personImgInUrl": "http://res.cloudinary.com/dstjar2iy/image/upload/v1742490680/yp2gx5sngj6ryflqe4re.jpg",
        "ownerUsername": "Anonymous",
        "plateImgInUrl": "http://res.cloudinary.com/dstjar2iy/image/upload/v1742490677/t3tqsnzl8guzjmy05hiw.jpg",
        "ticketStatus": "false",
        "ownerUserId": 17,
        "ticketTotalPrice": 900000,
        "ticketVehiclePlateNumber": "0",
        "ticketPosition": "",
        "timeIn": "2025-03-20T17:11:16.068+00:00"
    }

    return <>
        <div className="container">
            <h1>QUẢN LÝ XE RA</h1>
            <div className='divBtn'>
                <Button className='btn btn-info btnFind' onClick={showEntryModal}>Tra cứu lịch sử</Button>
            </div>
            <div className='ticket-input'>
                <label className='ticket-input-label' htmlFor="ticketInput">Mã vé</label>
                <input className='' id="ticketInput" type='text' placeholder='Id vé' value={ticketId} onChange={e => setTicketId(e.target.value)}></input>
                <div className=''>
                    <Button className='btn btn-success' onClick={e => loadInfo(e)}>Xác nhận</Button>
                </div>
                <div className=''>
                    <Button className='btn btn-success' onClick={e => payTicket(e)}>Thanh toán</Button>
                </div>
            </div>

            <div className="form-container">
                <div className="input-container">
                    <div className='d-flex justify-content-between p-1'>
                        <div className="form-group">
                            <label htmlFor="ownerName">Tên khách hàng</label>
                            <input disabled id="ownerName" className='me-3' type="text" placeholder="Tên khách hàng" value={data ? data.ownerUsername : ""} />
                        </div>
                    </div>
                    <div className='d-flex justify-content-between p-1'>
                        <div className="form-group">
                            <label htmlFor="vehicleName">Loại vé</label>
                            <input id="vehicleName" disabled className='me-2' type="text" placeholder="Loại vé" value={data ? data.ticketType : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="vehicleType">Trạng thái vé</label>
                            <input id="vehicleType" disabled className='me-2' type="text" placeholder="Trạng thái vé" value={data ? data.ticketStatus === "true" ? "Đã thanh toán" : "Chưa thanh toán" : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="plateLicense">Tổng giá tiền</label>
                            <input id="plateLicense" disabled type="text" placeholder="Tổng giá tiền" value={data ? data.ticketTotalPrice + " VNĐ" : ""} />
                        </div>
                    </div>

                    <div className='d-flex justify-content-between p-1'>
                        <div className="form-group">
                            <label htmlFor="startDay">Ngày bắt đầu</label>
                            <input id="startDay" disabled className='me-3' type="text" placeholder="Thời hạn vé từ" value={data ? formatDate(data.ticketStartDate) : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="endDay">Ngày kết thúc</label>
                            <input id="endDay" disabled type="text" placeholder="Đến" value={data ? formatDate(data.ticketExpiredDate) : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="position">Vị trí</label>
                            <input id="position" disabled className='me-3' type="text" placeholder="Vị trí" value={data ? data.ticketPosition === "" ? "Trống" : data.ticketPosition : "Trống"} />
                        </div>
                    </div>
                </div>

                <h5>Biển số xe</h5>
                <div className="image-container">
                    <Image src={data ? data.plateImgInUrl : inImgUrl} alt="Image 1" />
                    <Form.Group controlId="image" className="mb-3 me-1">
                        <Form.Label>Chọn hình ảnh biến số xe:</Form.Label>
                        <Form.Control type="file" accept=".png,.jpg" ref={files} onChange={e => handleOutImgChange(e)} />
                    </Form.Group>
                    <Image src={outImgUrl} alt="Image 2" />
                </div>
                <h5>Người dùng</h5>

                <div className="image-container mt-3">
                    <Image src={data ? data.personImgInUrl : inPersonImgUrl} alt="Image 1" />
                    <Form.Group controlId="image" className="mb-3 me-1">
                        <Form.Label>Chọn hình ảnh người dùng:</Form.Label>
                        <Form.Control type="file" accept=".png,.jpg" ref={files2} onChange={e => handleOutPersonImgChange(e)} />
                    </Form.Group>
                    <Image src={outPersonImgUrl} alt="Image 2" />
                </div>

                <div className="time-container">
                    <div className="form-group">
                        <input disabled id="checkInTime" type="text" placeholder="Thời gian vào" value={data ? data.timeIn ? formatDateWithHour(data.timeIn) : "Thời gian vào" : ""} />
                    </div>
                    <div className="form-group">
                        <input disabled id="checkOutTime" type="text" placeholder="Thời gian ra" />
                    </div>
                </div>

                <button className="submit-button" onClick={e => recordEntry(e)}>Lưu thông tin</button>
            </div>




        </div>

        <Modal show={showLoadingModal} onHide={closeLoading}>
            <Modal.Body className="text-center" style={{ whiteSpace: 'pre-line' }}>
                {loading ? (
                    <>
                        <Spinner animation="border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </Spinner>
                        <p className='text-inform'>Đang tiến hành phân tích thông tin...</p>
                    </>
                ) : (
                    <p className='text-inform'>{msg}</p>
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
                <p className='text-inform'>Đã Upload thành công</p>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={closeSuccess}>
                    Đóng
                </Button>
            </Modal.Footer>
        </Modal>

        <Modal show={showEntry} onHide={closeEntryModal} size='xl' >
            <Modal.Header closeButton>
                <Modal.Title>Tra cứu lịch sử</Modal.Title>
            </Modal.Header>
            <Modal.Body className="modal-body">
                <div className="entry-modal-container">
                    <div className="entry-search-bar">
                        <input
                            className="entry-search-input"
                            value={plateLicense}
                            onChange={(e) => setPlateLicense(e.target.value)}
                            placeholder="Nhập biển số phương tiện..."
                        />
                    </div>

                    <div className="entry-table-wrapper">
                        <table className="entry-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Tên phương tiện</th>
                                    <th>Biển số</th>
                                    <th>Thời gian vào</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                {entries.length > 0 ? (
                                    entries.map((entry) => (
                                        <tr key={entry.id}>
                                            <td>{entry.id}</td>
                                            <td>{entry.vehicle.name}</td>
                                            <td>{entry.ticket.licenseNumber.toUpperCase()}</td>
                                            <td>{formatDateWithHour(entry.timeIn)}</td>
                                            <td>
                                                <Button
                                                    variant="primary"
                                                    size="sm"
                                                    onClick={(e) => chooseEntry(e, entry)}
                                                >
                                                    Chọn
                                                </Button>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="5" style={{ textAlign: "center", padding: "1rem" }}>
                                            Không có dữ liệu.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                        <div className="pagination">
                            <Button
                                disabled={currentPage === 0}
                                onClick={() => prevPage()}
                            >
                                ←</Button>
                            {Array.from({ length: totalPages }, (_, i) => (
                                <Button
                                    key={i}
                                    variant={i === currentPage ? "dark" : "outline-dark"}
                                    onClick={() => setCurrentPage(i)}
                                >{i + 1}</Button>
                            ))}
                            <Button
                                disabled={currentPage === totalPages - 1}
                                onClick={() => nextPage()}
                            >
                                →</Button>
                        </div>
                    </div>
                </div>
            </Modal.Body>
        </Modal>
    </>
}

export default ManageOut;