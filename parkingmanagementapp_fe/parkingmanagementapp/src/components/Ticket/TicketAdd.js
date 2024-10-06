import { useEffect, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import MySpinner from "../common/MySpinner";
import { authApi, endpoints } from "../../configs/APIs";
import './TicketAdd.css'
import { useNavigate } from "react-router-dom";

const TicketAdd = () => {
    const nav = useNavigate();

    //ticket properties
    const [floor, setFloor] = useState(null);
    const [line, setLine] = useState(null);
    const [position, setPosition] = useState(null);
    const [security, setSecurity] = useState(null);
    const [customer, setCustomer] = useState(null);
    const [vehicle, setVehicle] = useState(null);
    const [startDay, setStartDay] = useState("");
    const [endDay, setEndDay] = useState("");
    const [ticketPrice, setTicketPrice] = useState(1);
    const [totalPrice, setTotalPrice] = useState(0);


    //list
    const [floors, setFloors] = useState([]);
    const [lines, setLines] = useState([]);
    const [positions, setPositions] = useState([]);
    const [securities, setSecurities] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [ticketPrices, setTicketPrices] = useState([]);

    //modal
    const [showCustomerModal, setShowCustomerModal] = useState(false);
    const [customerName, setCustomerName] = useState("");
    const [showSuccessModal, setShowSuccessModal] = useState(false);


    //example data
    // const floors = [
    //     {
    //         "id": 1,
    //         "floorNumber": 1
    //     },
    //     {
    //         "id": 2,
    //         "floorNumber": 2
    //     }
    // ];

    // const lines = [
    //     {
    //         "id": 1,
    //         "line": "A",
    //         "floor": {
    //             "id": 1,
    //             "floorNumber": 1
    //         }
    //     },
    //     {
    //         "id": 2,
    //         "line": "B",
    //         "floor": {
    //             "id": 1,
    //             "floorNumber": 1
    //         }
    //     }
    // ];

    //loading resources functions
    const loadFloors = async () => {
        try {
            let res = await authApi().get(endpoints['floor-list']);

            setFloors(res.data);
        } catch (ex) {
            console.error(ex);
        }
    };

    const loadSecurityList = async () => {
        try {
            let res = await authApi().get(endpoints['security-list']);

            setSecurities(res.data);
            setSecurity(securities[0].id);
        } catch (ex) {
            console.error(ex);
        }
    };

    const loadCustomerList = async () => {
        try {
            let url = `${endpoints['customer-list']}?name=${customerName}`

            let res = await authApi().get(url);

            setCustomers(res.data);
        } catch (ex) {
            console.error(ex);
        }
    };

    const loadVehicles = async () => {
        try {
            let res = await authApi().get(endpoints['vehicle-list-user'](customer.id));

            setVehicles(res.data);
            setVehicle(vehicles[0].id);
        } catch (ex) {
            console.error(ex);
        }
    };

    const loadPrice = async () => {
        try {
            let res = await authApi().get(endpoints['ticket-price-list']);

            setTicketPrices(res.data);
        } catch (ex) {
            console.error(ex);
        }
    };

    const calTotalDays = (startDay, endDay) => {
        const startD = new Date(startDay);
        const endD = new Date(endDay);
        const differenceInTime = endD.getTime() - startD.getTime();
        const totalDays = Math.ceil(differenceInTime / (1000 * 3600 * 24)) + 1;
        return totalDays;
    };

    const calTotalPrice = async () => {
        try {
            if (startDay != "" && endDay != ""){
                let url = `${endpoints['ticket-price-getPrice']}?startDay=${startDay}&endDay=${endDay}&id=${ticketPrice}`;
                let res = await authApi().get(url);
                setTotalPrice(res.data);
            }
        } catch (ex) {
            console.error(ex);
        }
        
    };

    //change value functions
    const changeFloor = async (e) => {
        e.preventDefault();

        setFloor(e.target.value);

        try {
            let url = `${endpoints['line-list']}?id=${e.target.value}`;

            let res = await authApi().get(url);
            setLines(res.data);

        } catch (ex) {
            console.error(ex);
        }
    };

    const changeLine = async (e) => {
        e.preventDefault();

        setLine(e.target.value);

        try {
            let url = `${endpoints['position-list']}?id=${e.target.value}`;

            let res = await authApi().get(url);
            setPositions(res.data);

        } catch (ex) {
            console.error(ex);
        }
    };

    const customerChoose = (user) => {
        setCustomer(user);
        closeModal();
    };

    //modal interaction
    const showModal = () => {
        setShowCustomerModal(true);
    };

    const closeModal = () => setShowCustomerModal(false);

    const showSuccess = () => {
        setShowSuccessModal(true);
    };

    const closeSuccess = () => setShowSuccessModal(false);

    //useEffect funcs
    useEffect(() => {
        loadFloors();
        loadSecurityList();
        loadCustomerList();
        loadPrice();
    }, []);

    useEffect(() => {
        loadCustomerList();
    }, [customerName]);

    useEffect(() => {
        loadVehicles();
    }, [customer]);

    useEffect(() => {
        calTotalPrice();
    }, [startDay, endDay, ticketPrice])

    const createTicket = async (e) => {
        e.preventDefault();

        let form = new FormData();
        form.append("floorId", floor);
        form.append("lineId", line);
        form.append("positionId", position);
        form.append("userCreateId", security);
        form.append("userOwnedId", customer["id"]);
        form.append("vehicleId", vehicle);
        form.append("startDay", startDay);
        form.append("endDay", endDay);
        form.append("priceId", ticketPrice);


        try {
            let res = await authApi().post(endpoints['ticket-create'], form);

            if (res.status == 201){
                showSuccess();

                setTimeout(() => {
                    closeSuccess();
                    nav('/ticket/list');
                }, 2000);
            }
        } catch (ex) {
            console.error(ex);
        }
    }

    return <>
        <div className="containerDiv">
            <div></div>
            <div className="mt-3 mb-3">
                <h1>Trang thông tin vé</h1>
                <div className="div-btn">
                    <Button onClick={showModal}> Tìm kiếm khách hàng </Button>
                </div>
            </div>
            <div>

                <Form onSubmit={createTicket}>
                    <Form.Group className="mb-3">
                        <Form.Label>Nhân viên:</Form.Label>
                        <Form.Select aria-label="Chọn nhân viên tạo" onChange={e => setSecurity(e.target.value)} value={security}>
                            {securities === null ? <MySpinner /> : <>
                                {securities.map(s => <option key={s.id} value={s.id}>
                                    {s.name}
                                </option>)}
                            </>}
                        </Form.Select>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Khách hàng:</Form.Label>
                        <Form.Control disabled value={customer ? customer.name : ""}></Form.Control>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Hãy chọn phương tiện:</Form.Label>
                        {customer === null ?
                            <Form.Control disabled></Form.Control>
                            :
                            <Form.Select aria-label="Hãy chọn phương tiện" onChange={e => setVehicle(e.target.value)} value={vehicle}>
                                {vehicles.map(v => <option key={v.id} value={v.id}>
                                    {v.name} - {v.plateLicense}
                                </option>)}
                            </Form.Select>
                        }
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Hãy chọn tầng:</Form.Label>
                        <Form.Select aria-label="Hãy chọn tầng" onChange={e => changeFloor(e)} value={floor}>
                            <option value={0}></option>
                            {floors === null ? <MySpinner /> : <>
                                {floors.map(f => <option key={f.id} value={f.id}>
                                    {f.floorNumber}
                                </option>)}
                            </>}
                        </Form.Select>
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Hãy chọn dãy:</Form.Label>
                        <Form.Select aria-label="Hãy chọn dãy" onChange={e => changeLine(e)} value={line}>
                            {floor !== null ? <>
                                {lines.map(l => <option key={l.id} value={l.id}>
                                    {l.line}
                                </option>)}
                            </> : <>
                                <option disabled selected>
                                    Hãy chọn tầng trước
                                </option>
                            </>}

                        </Form.Select>
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Hãy chọn vị trí trong dãy:</Form.Label>
                        <Form.Select aria-label="Hãy chọn vị trí trong dãy" onChange={e => setPosition(e.target.value)} value={position}>
                            {line !== null ? <>
                                {positions.map(p => <option key={p.id} value={p.id}>
                                    {p.position}
                                </option>)}
                            </> : <>
                                <option disabled selected>
                                    Hãy chọn dãy trước
                                </option>
                            </>}

                        </Form.Select>
                    </Form.Group>
                    <div className="d-flex justify-content-between">
                        <Form.Group className="mb-3">
                            <Form.Label>Chọn ngày bắt đầu</Form.Label>
                            <Form.Control onChange={e => setStartDay(e.target.value)} value={startDay} type="date" />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Chọn ngày kết thúc</Form.Label>
                            <Form.Control disabled={startDay !== null? false: true} onChange={e => setEndDay(e.target.value)} value={endDay} type="date" />
                        </Form.Group>
                    </div>
                    <Form.Group className="mb-3">
                        <Form.Label>Hãy chọn giá vé:</Form.Label>
                        <Form.Select aria-label="Hãy chọn giá vé" onChange={e => setTicketPrice(e.target.value)} value={ticketPrice}>
                            {ticketPrices === null ? <MySpinner /> :
                                <>
                                    {ticketPrices.map(p => <option key={p.id} value={p.id} selected={p.id === 1? true: false}>
                                        {p.price} VNĐ/ngày
                                    </option>)}
                                </>
                            }

                        </Form.Select>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Tổng giá vé:</Form.Label>
                        <Form.Control disabled value={totalPrice? totalPrice + " VNĐ": totalPrice + " VNĐ"}></Form.Control>
                    </Form.Group>

                    <Button disabled={customer === null || floor === null || line === null || position === null || startDay === "" || endDay === "" ? true: false} type="submit">Tạo vé</Button>
                </Form>
            </div>
        </div>

        <Modal show={showCustomerModal} onHide={closeModal} >
            <Modal.Header closeButton>
                <Modal.Title>Tìm kiếm người dùng</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="modal-container">
                    <div className="findBar">
                        <input value={customerName} onChange={(e) => setCustomerName(e.target.value)} placeholder="Nguyễn Văn A,..."></input>
                    </div>
                    <div className="customer-table">
                        <thead>
                            <tr>
                                <th>Tên khách hàng</th>
                                <th>CMND</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            {customers.map(c => <tr key={c.id}>
                                <td>{c.name}</td>
                                <td>{c.identityNumber}</td>
                                <td>
                                    <Button onClick={(e) => customerChoose(c)}>Chọn</Button>
                                </td>
                            </tr>)}
                        </tbody>
                    </div>
                </div>
            </Modal.Body>
        </Modal>

        <Modal show={showSuccessModal} onHide={closeSuccess}>
                <Modal.Header closeButton>
                    <Modal.Title><strong>Thông báo</strong></Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <h1>Tạo vé thành công!
                        Đang chuyển về danh sách vé!
                    </h1>
                </Modal.Body>
            </Modal>
    </>
};

export default TicketAdd;