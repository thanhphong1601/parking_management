import { useContext, useEffect, useRef, useState } from 'react';
import './ManageIn.css'
import { Button, Form, Image, Modal, Spinner } from 'react-bootstrap';
import { format, parse } from 'date-fns';
import { authApi, endpoints } from '../../configs/APIs';
import MySpinner from '../common/MySpinner';
import { MyUserContext } from '../../configs/Contexts';

const ManageIn = () => {
    // const data = {
    //     "owner": {
    //         "id": 12,
    //         "name": "Phan Văn Hào",
    //         "identityNumber": "1",
    //         "phone": "1",
    //         "address": "testAdress",
    //         "birthday": "2024-09-16T17:00:00.000+00:00",
    //         "email": "pvh@gmail.com",
    //         "username": "khachhang1",
    //         "password": "$2a$10$YZHNakhZijoTopznKqusIeBnka5gMAhP0I/PE2lQ7ggJ7nIT4aTDm",
    //         "role": {
    //             "id": 3,
    //             "role": "ROLE_CUSTOMER"
    //         },
    //         "avatar": "",
    //         "file": null,
    //         "active": true,
    //         "enabled": true,
    //         "accountNonExpired": true,
    //         "credentialsNonExpired": true,
    //         "accountNonLocked": true
    //     },
    //     "ticket": {
    //         "id": 1,
    //         "floor": {
    //             "id": 1,
    //             "floorNumber": 1
    //         },
    //         "line": {
    //             "id": 1,
    //             "line": "A",
    //             "floor": {
    //                 "id": 1,
    //                 "floorNumber": 1
    //             }
    //         },
    //         "position": {
    //             "id": 1,
    //             "position": 1,
    //             "line": {
    //                 "id": 1,
    //                 "line": "A",
    //                 "floor": {
    //                     "id": 1,
    //                     "floorNumber": 1
    //                 }
    //             }
    //         },
    //         "price": {
    //             "id": 1,
    //             "price": 15000
    //         },
    //         "userCreate": {
    //             "id": 2,
    //             "name": "Security",
    //             "identityNumber": "33333333",
    //             "phone": "1230",
    //             "address": "Security",
    //             "birthday": "2024-09-18T17:00:00.000+00:00",
    //             "email": "security@gmail.com",
    //             "username": "security",
    //             "password": "$2a$10$keZrZz7Tj4mIdrg0j5pkJeQ2EuH8QGLnT/tB12sQDrxn5nLGHvYhW",
    //             "role": {
    //                 "id": 2,
    //                 "role": "ROLE_SECURITY"
    //             },
    //             "avatar": "",
    //             "file": null,
    //             "active": true,
    //             "enabled": true,
    //             "accountNonExpired": true,
    //             "credentialsNonExpired": true,
    //             "accountNonLocked": true
    //         },
    //         "userOwned": {
    //             "id": 12,
    //             "name": "Phan Văn Hào",
    //             "identityNumber": "1",
    //             "phone": "1",
    //             "address": "testAdress",
    //             "birthday": "2024-09-16T17:00:00.000+00:00",
    //             "email": "pvh@gmail.com",
    //             "username": "khachhang1",
    //             "password": "$2a$10$YZHNakhZijoTopznKqusIeBnka5gMAhP0I/PE2lQ7ggJ7nIT4aTDm",
    //             "role": {
    //                 "id": 3,
    //                 "role": "ROLE_CUSTOMER"
    //             },
    //             "avatar": "",
    //             "file": null,
    //             "active": true,
    //             "enabled": true,
    //             "accountNonExpired": true,
    //             "credentialsNonExpired": true,
    //             "accountNonLocked": true
    //         },
    //         "vehicle": {
    //             "id": 11,
    //             "name": "Vision",
    //             "type": {
    //                 "id": 2,
    //                 "type": "Xe máy"
    //             },
    //             "plateLicense": "IT20BOM",
    //             "user": {
    //                 "id": 12,
    //                 "name": "Phan Văn Hào",
    //                 "identityNumber": "1",
    //                 "phone": "1",
    //                 "address": "testAdress",
    //                 "birthday": "2024-09-16T17:00:00.000+00:00",
    //                 "email": "pvh@gmail.com",
    //                 "username": "khachhang1",
    //                 "password": "$2a$10$YZHNakhZijoTopznKqusIeBnka5gMAhP0I/PE2lQ7ggJ7nIT4aTDm",
    //                 "role": {
    //                     "id": 3,
    //                     "role": "ROLE_CUSTOMER"
    //                 },
    //                 "avatar": "",
    //                 "file": null,
    //                 "active": true,
    //                 "enabled": true,
    //                 "accountNonExpired": true,
    //                 "credentialsNonExpired": true,
    //                 "accountNonLocked": true
    //             }
    //         },
    //         "startDay": "2024-09-23T17:00:00.000+00:00",
    //         "endDay": "2024-10-24T17:00:00.000+00:00",
    //         "totalPrice": 450000,
    //         "isPaid": false
    //     },
    //     "plate": "it20bom",
    //     "timeIn": "Sat Oct 05 20:41:42 ICT 2024",
    //     "vehicle": {
    //         "id": 11,
    //         "name": "Vision",
    //         "type": {
    //             "id": 2,
    //             "type": "Xe máy"
    //         },
    //         "plateLicense": "IT20BOM",
    //         "user": {
    //             "id": 12,
    //             "name": "Phan Văn Hào",
    //             "identityNumber": "1",
    //             "phone": "1",
    //             "address": "testAdress",
    //             "birthday": "2024-09-16T17:00:00.000+00:00",
    //             "email": "pvh@gmail.com",
    //             "username": "khachhang1",
    //             "password": "$2a$10$YZHNakhZijoTopznKqusIeBnka5gMAhP0I/PE2lQ7ggJ7nIT4aTDm",
    //             "role": {
    //                 "id": 3,
    //                 "role": "ROLE_CUSTOMER"
    //             },
    //             "avatar": "",
    //             "file": null,
    //             "active": true,
    //             "enabled": true,
    //             "accountNonExpired": true,
    //             "credentialsNonExpired": true,
    //             "accountNonLocked": true
    //         }
    //     }
    // };

    const [inImgUrl, setInImgUrl] = useState("https://via.placeholder.com/150");
    const [outImgUrl, setOutImgUrl] = useState("https://via.placeholder.com/150");
    const [timeIn, setTimeIn] = useState(null);
    const [uid, setUid] = useState(null);
    const [creatorId, setCreatorId] = useState(null);
    const [vehicleId, setVehicleId] = useState(null);
    const files = useRef();
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState("");
    const [data, setData] = useState(null);
    const currentUser = useContext(MyUserContext);
    const [vehicleChoice, setVehicleChoice] = useState(null);
    const [plateLicense, setPlateLicense] = useState("");

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

    const handleFileChange = async (event) => {
        event.preventDefault();
        setMsg("");

        const file = event.target.files[0]; // Lấy file đầu tiên từ input
        if (file) {
            const imageUrl = URL.createObjectURL(file); // Tạo URL tạm thời từ file
            setInImgUrl(imageUrl); // Cập nhật đường dẫn hình ảnh trong state
        }

        setLoading(true);
        showLoading();
        let form = new FormData();

        if (files)
            form.append('file', files.current.files[0]);

        try {
            let res = await authApi().post(endpoints['recognize-in'], form, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (res.status == 201) {
                setLoading(false);
                setData(res.data);
                setMsg("Đã phân tích thành công!");

                setTimeout(() => {
                    closeLoading();
                }, 1000);
            }
            if (res.data == "Cannot get plate license from image!") {
                setLoading(false);
                setMsg("Phân tích thất bại");

                setTimeout(() => {
                    closeLoading();
                }, 1000);
            };
            if (res.data == "User not found! Please check your infomation") {
                setLoading(false);
                setMsg("Không tìm thấy người dùng tương ứng");

                setTimeout(() => {
                    closeLoading();
                }, 1000);
            };

            if (res.data == "No valid ticket found!") {
                setLoading(false);
                setMsg("Người dùng hiện chưa đăng ký vé");

                setTimeout(() => {
                    closeLoading();
                }, 1000);
            };

            if (res.data == "Cannot get plate license from image!") {
                setLoading(false);
                setMsg("Phân tích thất bại");

                setTimeout(() => {
                    closeLoading();
                }, 1000);
            };

        } catch (ex) {
            setMsg("Đã có lỗi xảy ra");
            console.info(ex);
        }
    };

    const loadInfo = async () => {
        setMsg("");

        setLoading(true);
        showLoading();
        let form = new FormData();

        if (vehicleChoice != null) {
            form.append('vehicleId', vehicleChoice.id);

            try {
                let res = await authApi().post(endpoints['recognize-in'], form);
                
                if (res.status == 201) { 
                    setLoading(false);
                    setData(res.data);
                    setMsg("Đã phân tích thành công!");

                    setTimeout(() => {
                        closeLoading();
                    }, 1000);
                };
                if (res.data == "Cannot get plate license from image!") {
                    setLoading(false);
                    setMsg("Phân tích thất bại");

                    setTimeout(() => {
                        closeLoading();
                    }, 1000);
                };
                if (res.data == "User not found! Please check your infomation") {
                    setLoading(false);
                    setMsg("Không tìm thấy người dùng tương ứng");

                    setTimeout(() => {
                        closeLoading();
                    }, 1000);
                };

                if (res.data == "No valid ticket found!") {
                    setLoading(false);
                    setMsg("Người dùng hiện chưa đăng ký vé");

                    setTimeout(() => {
                        closeLoading();
                    }, 1000);
                };

                if (res.data == "Cannot get plate license from image!") {
                    setLoading(false);
                    setMsg("Phân tích thất bại");

                    setTimeout(() => {
                        closeLoading();
                    }, 1000);
                };


               

            } catch (ex) {
                setMsg("Đã có lỗi xảy ra");
                console.info(ex);
            }
        } else {
            setLoading(false);
            closeLoading();
        }
    };

    const uploadEntryInfo = async (e) => {
        e.preventDefault();

        let form = new FormData();
        if (files)
            form.append('file', files.current.files[0]);
        form.append("timeIn", data.timeIn);
        form.append("userId", data.owner.id);
        form.append("creatorId", currentUser.id);
        form.append("vehicleId", data.vehicle.id);

        try {
            let res = await authApi().post(endpoints['recognize-in-save'], form, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (res.status == 201) {
                showSuccess();
                setTimeout(() => {
                    closeSuccess();
                }, 2000);
            };
        } catch (ex) {
            console.error(ex);
        } finally {
            setData(null);
        }

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
        loadInfo();
    }, [vehicleChoice]);

    useEffect(() => {
        loadVehicles();
    }, [plateLicense]);

    return <>
        <div className="container">
            <h1>QUẢN LÝ XE VÀO</h1>
            <div className='divBtn'>
                <Button className='btn btn-info btnFind' onClick={showVehicle}>Tìm kiếm phương tiện</Button>
            </div>

            <div className="form-container">
                <div className="input-container">
                    <div className='d-flex justify-content-between p-1'>
                        <div className="form-group">
                            <label htmlFor="vehicleName">Tên xe</label>
                            <input id="vehicleName" disabled className='me-2' type="text" placeholder="Tên phương tiện" value={data ? data.vehicle.name : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="vehicleType">Loại xe</label>
                            <input id="vehicleType" disabled className='me-2' type="text" placeholder="Loại phương tiện" value={data ? data.vehicle.type.type : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="plateLicense">Biển số</label>
                            <input id="plateLicense" disabled type="text" placeholder="Biển số" value={data ? data.vehicle.plateLicense : ""} />
                        </div>
                    </div>

                    <div className='d-flex justify-content-between p-1'>
                        <div className="form-group">
                            <label htmlFor="startDay">Ngày bắt đầu</label>
                            <input id="startDay" disabled className='me-3' type="text" placeholder="Thời hạn vé từ" value={data ? formatDate(data.ticket.startDay) : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="endDay">Ngày kết thúc</label>
                            <input id="endDay" disabled type="text" placeholder="Đến" value={data ? formatDate(data.ticket.endDay) : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="position">Vị trí</label>
                            <input id="position" disabled className='me-3' type="text" placeholder="Vị trí" value={data ? `Tầng ${data.ticket.floor.floorNumber} - dãy ${data.ticket.line.line} - vị trí ${data.ticket.position.position}` : ""} />
                        </div>
                    </div>

                    <div className='d-flex justify-content-between p-1'>
                        <div className="form-group">
                            <label htmlFor="ownerName">Tên khách hàng</label>
                            <input disabled id="ownerName" className='me-3' type="text" placeholder="Tên khách hàng" value={data ? data.owner.name : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="phone">Số điện thoại</label>
                            <input disabled id="phone" className='me-3' type="text" placeholder="Số điện thoại" value={data ? data.owner.phone : ""} />
                        </div>
                        <div className="form-group">
                            <label htmlFor="identityNumber">CMND</label>
                            <input disabled id="identityNumber" type="text" placeholder="CMND" value={data ? data.owner.identityNumber : ""} />
                        </div>
                    </div>
                </div>

                <div className="image-container">
                    <Image src={inImgUrl} alt="Image 1" />
                    <Form.Group controlId="image" className="mb-3 me-1">
                        <Form.Label>Chọn hình ảnh:</Form.Label>
                        <Form.Control type="file" accept=".png,.jpg" ref={files} onChange={handleFileChange} />
                    </Form.Group>
                    <Image src={outImgUrl} alt="Image 2" />
                </div>

                <div className="time-container">
                    <div className="form-group">
                        <input disabled id="checkInTime" type="text" placeholder="Thời gian vào" value={data ? formatDateWithHour(data.timeIn) : "Thời gian vào"} />
                    </div>
                    <div className="form-group">
                        <input disabled id="checkOutTime" type="text" placeholder="Thời gian ra" />
                    </div>
                </div>

                <button className="submit-button" onClick={(e) => uploadEntryInfo(e)}>Lưu thông tin</button>
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
                    <p>{msg}</p>
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
                <h1>Đã Upload thành công</h1>
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