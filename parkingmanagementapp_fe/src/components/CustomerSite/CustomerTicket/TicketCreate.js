import React, { useContext, useEffect, useRef, useState } from "react";
import "./TicketCreate.css";
import { authApi, endpoints } from "../../../configs/APIs";
import MySpinner from "../../common/MySpinner";
import { Button, Form, Image, Modal, Spinner, ToggleButton, ToggleButtonGroup } from "react-bootstrap";
import { MyUserContext } from "../../../configs/Contexts";
import 'bootstrap/dist/css/bootstrap.min.css';
import SuccessOverlay from "../../common/SuccessOverlay";
import FullScreenLoading from "../../common/FullScreenLoading";
import FailOverlay from "../../common/FailOverlay";
import { useLocation, useNavigate } from "react-router-dom";


const TicketCreate = () => {
    const nav = useNavigate();
    const currentUser = useContext(MyUserContext);
    const [floors, setFloors] = useState([]);
    const [lines, setLines] = useState([]);
    const [positions, setPositions] = useState([]);
    const [ticketPrices, setTicketPrices] = useState([]);
    //const [types, setTypes] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [vehicleTypes, setVehicleTypes] = useState([]);
    const [plateImgUrl, setPlateImgUrl] = useState("https://placehold.co/250x150/png");
    const [ticketTypes, setTicketTypes] = useState([]);
    const [updatePositionWhenFloorChange, setUpdatePositionWhenFloorChange] = useState(0);
    const [totalPrice, setTotalPrice] = useState(0);
    const unit = {
        1: "Ngày",
        2: "Tháng",
        3: "Tháng"
    };

    const location = useLocation();
    const currentPath = location.pathname + location.search;
    if (currentUser == null) {
        nav(`/login?continue=${encodeURIComponent(currentPath)}`);
    }

    const file = useRef(); //for uploading plate img to get license plate

    //loading state
    const [finalLoading, setFinalLoading] = useState(false);

    //overlay state
    const [showSuccess, setShowSuccess] = useState(false);
    const [showError, setShowError] = useState(false);



    //message for create new vehicles
    const msgType = {
        success: "alert alert-success",
        failed: "alert alert-danger"
    };
    const [msg, setMsg] = useState("");


    const [form, setForm] = useState({
        floorId: "",
        lineId: "",
        positionId: "",
        userCreateId: "",
        userOwnedId: currentUser ? currentUser.id : "",
        vehicleId: "",
        startDay: new Date().toISOString().split("T")[0],
        numberOfDays: 0,
        typeId: 1
    });

    //modal vehicle
    const [showModal, setShowModal] = useState(false);
    const [newVehicle, setNewVehicle] = useState({
        name: "",
        type: "",
        plateLicense: "",
    });

    const handleOpenVehicleModal = () => {
        loadVehicleTypes();
        setShowModal(true);
    };

    const handleCancelVehicleModal = () => {
        setShowModal(false);
    };

    const handleInImgChange = async (e) => {
        e.preventDefault();
        setLoading(true);
        showLoading();
        let form = new FormData();

        const file = e.target.files[0]; // Lấy file đầu tiên từ input
        if (file) {
            const imageUrl = URL.createObjectURL(file); // Tạo URL tạm thời từ file
            setPlateImgUrl(imageUrl); // Cập nhật đường dẫn hình ảnh trong state
            form.append("file", file);
        };
        try {
            let res = await authApi().post(endpoints['analyze-plate'], form, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            })

            if (res.status == 200) {
                let licensePlateNumber = res.data.plate;
                setNewVehicle({ ...newVehicle, plateLicense: licensePlateNumber });
                setLoading(false);
            }
        } catch (ex) {
            if (ex.status == 409)
                console.error(ex + "hehe");
            else
                console.error(ex)
        };
    };

    const saveVehicle = async (e) => {
        setMsg("")
        e.preventDefault();
        closeConfirm();
        handleCancelVehicleModal();
        let form = new FormData();
        form.append("name", newVehicle.name);
        form.append("type", newVehicle.type);
        form.append("plateLicense", newVehicle.plateLicense);

        try {
            let res = await authApi().post(endpoints['vehicle-create-with-user'](currentUser.id), form);

            if (res.status == 200) {
                setMsg("Bạn vừa thêm một phương tiện mới!");
                loadVehicles();
            };
            setNewVehicle({
                name: "",
                type: "",
                plateLicense: ""
            });
        } catch (ex) {
            console.error(ex);
        }
    };

    //loading modal
    const [loading, setLoading] = useState(false);
    const [showLoadingModal, setShowLoadingModal] = useState(false);

    const showLoading = () => {
        setShowLoadingModal(true);
    };

    const closeLoading = () => {
        setShowLoadingModal(false);
    };

    //confirm modal
    const [confirm, setConfirm] = useState(false);
    const [showConfirmModal, setShowConfirmModal] = useState(false);

    const showConfirm = () => {
        setShowConfirmModal(true);
    };

    const closeConfirm = () => {
        setShowConfirmModal(false);
    };

    const handleChange = (name, e) => {
        setForm(prevForm => ({
            ...prevForm,
            [name]: e.target.value
        }));
    };

    //example
    const fetchDropdowns = async () => {
        // Thay thế bằng API thật
        setFloors(["1", "2", "3"]);
        setLines(["A", "B", "C"]);
        setPositions(["1", "2", "3", "4"]);
        // setVehicles(["Vision", "Lead"]);
        setTicketTypes(["Ngày", "Tháng"]);
    };

    const types = ([
        {
            id: 1,
            type: "Loại 1"
        },
        {
            id: 2,
            type: "Loại 2"
        }
    ]);

    //fetch api for datas
    const loadFloors = async () => {
        try {
            let res = await authApi().get(endpoints['floor-list']);

            setFloors(res.data);
        } catch (ex) {
            console.error(ex);
        };
    };

    const loadPrices = async () => {
        try {
            let res = await authApi().get(endpoints['ticket-price-list']);

            setTicketPrices(res.data);
        } catch (ex) {
            console.error(ex);
        }
    };

    const loadTypes = async () => {
        try {
            let res = await authApi().get(endpoints['ticket-type-list']);

            setTicketTypes(res.data);
        } catch (ex) {
            console.error(ex);
        }
    };

    const loadVehicles = async () => {
        try {
            let res = await authApi().get(endpoints['vehicle-list-user'](currentUser.id));

            if (res.data.length > 0) {
                setVehicles(res.data);
                setForm(prevForm => ({
                    ...prevForm,
                    "vehicleId": res.data[0].id
                }));
            } else {
                setVehicles([]);
                setForm(prevForm => ({
                    ...prevForm,
                    "vehicleId": ""
                }));
            }
        } catch (ex) {
            console.error(ex);
        }
    };

    const loadVehicleTypes = async () => {
        try {
            let res = await authApi().get(endpoints['vehicle-types']);

            if (res.data.length !== 0) {
                setVehicleTypes(res.data);
            }
        } catch (ex) {
            console.error(ex);
        }
    };

    const loadLines = async (e) => {
        try {
            let url = `${endpoints['line-list']}?id=${e.target.value}`;

            let res = await authApi().get(url);
            setLines(res.data);
        } catch (ex) {
            console.log(ex);
        }
    };

    const loadPositions = async (lineId) => {
        try {
            let url = `${endpoints['position-list']}?id=${lineId}`;

            let res = await authApi().get(url);
            setPositions(res.data);

        } catch (ex) {
            console.error(ex);
        };
    };


    //other funcs
    const calTotalPrice = async (e) => {
        try {
            if (form.startDay != "" && form.numberOfDays > -1) {
                let url = `${endpoints['ticket-price-getPrice']}?startDay=${form.startDay}&numberOfDays=${form.numberOfDays}&id=${form.typeId}`;
                let res = await authApi().get(url);
                setTotalPrice(res.data);
            }
        } catch (ex) {
            console.error(ex);
        }

    };

    //load data
    useEffect(() => {
        loadFloors();
        loadPrices();
        loadTypes();
        loadVehicles();
        loadVehicleTypes();
    }, []);

    const changeFloor = async (e) => {
        e.preventDefault();

        if (e.target.value == 0) {
            setLines([]);
            setPositions([]);
            handleChange("floorId", e);

        } else {
            try {
                loadLines(e);
                handleChange("floorId", e);
                setUpdatePositionWhenFloorChange(current => current + 1);
            } catch (ex) {
                console.error(ex);
            }
        }


    };

    const changeLine = async (e) => {
        e.preventDefault();

        handleChange("lineId", e);
        loadPositions(e.target.value);
    };

    useEffect(() => {
        if (lines.length > 0) {
            setForm(prev => ({ ...prev, lineId: lines[0].id }));
            loadPositions(lines[0].id);
        };
    }, [lines]);

    useEffect(() => {
        if (positions.length > 0)
            setForm(prev => ({ ...prev, positionId: positions[0].id }));
    }, [positions]);


    const changeNumberOfDays = (e) => {
        e.preventDefault();
        handleChange("numberOfDays", e);
        calTotalPrice();
    };


    useEffect(() => {
        calTotalPrice();
    }, [form.startDay, form.typeId, form.numberOfDays])

    useEffect(() => {
        if (vehicleTypes.length > 0) {
            setNewVehicle(prev => ({ ...prev, type: vehicleTypes[0].id }));
        }
    }, [vehicleTypes]);

    const createTicket = async (e) => {
        e.preventDefault();
        setFinalLoading(true);

        let ticketForm = new FormData();
        for (let key in form) {
            ticketForm.append(key, form[key]);
        }

        try {
            let res = await authApi().post(endpoints['ticket-create'], ticketForm);

            if (res.status == 201) {
                setShowSuccess(true);
            };
        } catch (ex) {
            console.error(ex);
            setShowError(true);
        } finally {
            setTimeout(() => {
                setFinalLoading(false);
                setShowError(false);
                setShowSuccess(false);
                nav(`/customer/${currentUser.id}/ticket/list`);
            }, 3000);
        }
    }


    const test = (e) => {
        e.preventDefault();
        for (let key in form) {
            console.log(key + ": " + form[key]);
        }
    };

    return <>
        <div className="register-container">
            <h2>Đăng ký vé xe</h2>

            <Form className="ticket-form">
                <Form.Group className="form-group-block">
                    <Form.Label>Tầng:</Form.Label>
                    <Form.Select onChange={e => changeFloor(e)} value={form.floorId}>
                        <option value={0}>--Chọn tầng--</option>
                        {floors?.map(f => (
                            <option key={f.id} value={f.id}>{f.floorNumber}</option>
                        ))}
                    </Form.Select>
                </Form.Group>

                <Form.Group className="form-group-block">
                    <Form.Label>Dãy:</Form.Label>
                    <Form.Select onChange={e => changeLine(e)} value={form.lineId} disabled={!form.floorId}>
                        {form.floorId ? lines.map(l => (
                            <option key={l.id} value={l.id}>{l.line}</option>
                        )) : <option disabled>Hãy chọn tầng trước</option>}
                    </Form.Select>
                </Form.Group>

                <Form.Group className="form-group-block">
                    <Form.Label>Vị trí:</Form.Label>
                    <Form.Select onChange={e => handleChange("positionId", e)} value={form.positionId}>
                        {form.lineId ? positions.map(p => (
                            <option key={p.id} value={p.id}>{p.position}</option>
                        )) : <option disabled>Hãy chọn dãy trước</option>}
                    </Form.Select>
                </Form.Group>


                <Form.Group className="radio-group">
                    <Form.Label>Chọn phương tiện:</Form.Label>
                    {vehicles.length === 0 ? (
                        <Spinner />
                    ) : (
                        vehicles.map((v) => (
                            <Form.Check
                                type="radio"
                                name="vehicle"
                                value={v.id}
                                label={v.name}
                                onChange={(e) => handleChange("vehicleId", e)}
                                className="radio-item"
                            />
                        ))
                    )}

                </Form.Group>

                <Form.Group className="form-group-block">
                    <button type="button" className="add-vehicle-btn" onClick={() => handleOpenVehicleModal()} >+ Thêm phương tiện</button>
                </Form.Group>

                {msg === "" ? <></> : <div class={msgType.success} role="alert">
                    {msg}
                </div>}

                <Form.Group className="form-group-block">
                    <Form.Label>Loại vé:</Form.Label>
                    <Form.Select value={form.typeId} onChange={e => handleChange("typeId", e)}>
                        {ticketTypes.map(t => (
                            <option key={t.id} value={t.id}>Loại {t.type}</option>
                        ))}
                    </Form.Select>
                </Form.Group>

                <Form.Group className="form-group-block">
                    <Form.Label>Giá vé:</Form.Label>
                    <Form.Control disabled value={`${ticketPrices[form.typeId - 1]?.price || 0} VNĐ`} />
                </Form.Group>

                <Form.Group className="form-group-block d-flex-column">
                    <div>
                        <Form.Label>Ngày bắt đầu:</Form.Label>
                        <Form.Control type="date" value={form.startDay} onChange={e => handleChange("startDay", e.target.value)} />
                    </div>
                    <div>
                        <Form.Label>Số lượng: ({<span className="fw-bold">Đơn vị: {unit[form.typeId]}</span>})</Form.Label>
                        <Form.Control type="number" min={1} onChange={e => changeNumberOfDays(e)} />
                    </div>
                </Form.Group>

                <Form.Group className="form-group-block">
                    <Form.Label>Tổng giá:</Form.Label>
                    <Form.Control disabled value={`${totalPrice} VNĐ`} />
                </Form.Group>

                <div className="form-footer">
                    <button className="submit-btn" onClick={e => createTicket(e)}>Đăng ký</button>
                </div>
            </Form>

            {finalLoading && <FullScreenLoading />}
            {showSuccess && <SuccessOverlay message="Đăng ký thành công!" />}
            {showError && <FailOverlay message="Lỗi: không thể đăng ký vé." />}

        </div>
        {/* Modal thêm phương tiện */}
        <Modal
            show={showModal}
            onHide={handleCancelVehicleModal}
            centered
            dialogClassName="custom-modal"
            backdrop="static"
        >
            <Modal.Header closeButton>
                <Modal.Title>Thêm phương tiện</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group className="mb-3">
                        <Form.Label>Tên xe</Form.Label>
                        <Form.Control
                            value={newVehicle.name}
                            onChange={(e) =>
                                setNewVehicle({ ...newVehicle, name: e.target.value })
                            }
                            placeholder="Nhập tên xe"
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Loại xe</Form.Label>
                        <Form.Select value={newVehicle.type} onChange={e => setNewVehicle({
                            ...newVehicle, type: e.target.value
                        })}>
                            {vehicleTypes.length === 0 ?
                                <option value={null}>Đang tải loại xe</option>
                                : vehicleTypes.map(t => (

                                    <option key={t.id} value={t.id}>{t.type}</option>
                                ))}
                        </Form.Select>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Biển số</Form.Label>
                        <Form.Control
                            value={newVehicle.plateLicense}
                            onChange={(e) =>
                                setNewVehicle({ ...newVehicle, plateLicense: e.target.value })
                            }
                            placeholder="Biển số xe"
                        />
                    </Form.Group>

                    <Form.Group className="mb-3 d-flex flex-column justify-content-center">
                        <Form.Label>Ảnh biển số xe</Form.Label>
                        <Form.Control
                            type="file"
                            accept=".png,.jpg"
                            ref={file}
                            onChange={e => handleInImgChange(e)}
                        />
                        <Image src={plateImgUrl} alt="Image" />
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleCancelVehicleModal}>
                    Hủy
                </Button>
                <Button variant="success" onClick={showConfirm}>
                    Lưu phương tiện
                </Button>
            </Modal.Footer>
        </Modal >

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
                    <p>Phân tích thành công</p>
                )}
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={closeLoading}>
                    Đóng
                </Button>
            </Modal.Footer>
        </Modal>

        <Modal size="lg"
            show={showConfirmModal}
            onHide={closeConfirm}
            centered
            dialogClassName="custom-modal"
            backdrop="static">
            <Modal.Header closeButton>
                <Modal.Title>Xác nhận lưu thông tin</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <h1 className='fw-bold text-center text-danger'>Bạn có chắc chắn muốn thêm thông tin sau?</h1>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="success" onClick={e => saveVehicle(e)}>
                    CHẮC CHẮN
                </Button>
                <Button variant="secondary" onClick={closeConfirm}>
                    SUY NGHĨ THÊM
                </Button>
            </Modal.Footer>
        </Modal>

    </>
};

export default TicketCreate;
