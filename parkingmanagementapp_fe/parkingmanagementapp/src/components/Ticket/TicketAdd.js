import { useContext, useEffect, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import MySpinner from "../common/MySpinner";
import { authApi, endpoints } from "../../configs/APIs";
import './TicketAdd.css'
import { useNavigate } from "react-router-dom";
import { MyUserContext } from "../../configs/Contexts";

const TicketAdd = () => {
    const nav = useNavigate();
    const currentUser = useContext(MyUserContext);
    if (currentUser === null){
        nav('/login');
    }

    //ticket properties
    const [floor, setFloor] = useState(null);
    const [line, setLine] = useState(null);
    const [position, setPosition] = useState(null);
    const [security, setSecurity] = useState(null);
    const [customer, setCustomer] = useState(null);
    const [vehicle, setVehicle] = useState(null);
    const [startDay, setStartDay] = useState("");
    const [endDay, setEndDay] = useState("");
    const [ticketPrice, setTicketPrice] = useState(0);
    const [totalPrice, setTotalPrice] = useState(1);
    const [numberOfDays, setNumberOfDays] = useState(1);
    const [ticketType, setTicketType] = useState(1);

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);


    //list
    const [floors, setFloors] = useState([]);
    const [lines, setLines] = useState([]);
    const [positions, setPositions] = useState([]);
    const [securities, setSecurities] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [ticketPrices, setTicketPrices] = useState([]);
    const [ticketTypes, setTicketTypes] = useState([]);


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

    const types = [
        {
            "id": 1,
            "type": "Normal"
        },
        {
            "id": 2,
            "type": "VIP"
        },
        {
            "id": 3,
            "type": "Month"
        }
    ]

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
            let url = `${endpoints['customer-list']}?name=${customerName}&page=${page}`
            let res = await authApi().get(url);

            setCustomers(res.data.content);
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

    const loadTypes = async () => {
        try {
            let res = await authApi().get(endpoints['ticket-type-list']);

            setTicketTypes(res.data);
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
            if (startDay != "" && numberOfDays > -1) {
                let url = `${endpoints['ticket-price-getPrice']}?startDay=${startDay}&numberOfDays=${numberOfDays}&id=${ticketType}`;
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

    const nextPage = () => {
        if (page < totalPages - 1) setPage(page + 1);
    };

    const prevPage = () => {
        if (page > 0) setPage(page - 1);
    };

    const goToPage = (pageNum) => {
        setPage(pageNum);
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

    const changeNumberOfDays = (e) => {
        e.preventDefault();
        setNumberOfDays(e.target.value);
        calTotalPrice();
    };

    const changeTicketPriceType = (e) => {
        e.preventDefault();
        setTicketType(e.target.value);
        setTicketPrice(ticketType);
    }

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
        loadTypes();
    }, []);

    useEffect(() => {
        loadCustomerList();
    }, [customerName]);

    useEffect(() => {
        loadVehicles();
    }, [customer]);

    useEffect(() => {
        calTotalPrice();
    }, [startDay, ticketType, numberOfDays])

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
        form.append("numberOfDays", numberOfDays);
        form.append("typeId", ticketType);


        try {
            // for (const [key, value] of form.entries()) {
            //     console.log(`${key}: ${value}`);
            // }
            let res = await authApi().post(endpoints['ticket-create'], form);

            if (res.status == 201) {
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
                                <option disabled selecte value={null}>
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
                                <option disabled selected value={null}>
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
                            <Form.Control value={numberOfDays} onChange={e => {changeNumberOfDays(e)}} type="number" min={1}/>
                        </Form.Group>
                    </div>
                    <Form.Group className="mb-3">
                        <Form.Label>Hãy chọn loại vé:</Form.Label>
                        <Form.Select aria-label="Hãy chọn loại vé" onChange={e => changeTicketPriceType(e)} value={ticketType}>
                            {types === null ? <MySpinner /> :
                                <>
                                    {types.map(t => <option key={t.id} value={t.id} selected={t.id === 1 ? true : false}>
                                        Loại {t.type}
                                    </option>)}
                                </>
                            }

                        </Form.Select>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Giá vé:</Form.Label>
                        <Form.Control disabled value={ticketPrices[ticketType - 1]?.price + " VNĐ"}></Form.Control>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Tổng giá vé:</Form.Label>
                        <Form.Control disabled value={totalPrice ? totalPrice + " VNĐ" : totalPrice + " VNĐ"}></Form.Control>
                    </Form.Group>

                    <Button disabled={customer === null || startDay === "" ? true : false} type="submit">Tạo vé</Button>
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
                            {customers.length > 0 ? <>
                                {customers.map(c => <tr key={c.id}>
                                <td>{c.name}</td>
                                <td>{c.identityNumber}</td>
                                <td>
                                    <Button onClick={(e) => customerChoose(c)}>Chọn</Button>
                                </td>
                            </tr>)}
                            </>: <></>}
                            
                        </tbody>
                    </div>
                    <div class="pagination-container">
                        <div>
                            <button className="pagination-button" onClick={prevPage} disabled={page === 0}>{"<"}</button>
                            {[...Array(totalPages).keys()].map((pageNum) => (
                                <button
                                    className="pagination-button"
                                    key={pageNum}
                                    onClick={() => goToPage(pageNum)}
                                    style={{ fontWeight: pageNum === page ? 'bold' : 'normal' }}
                                >
                                    {pageNum + 1}
                                </button>
                            ))}
                            <button className="pagination-button" onClick={nextPage} disabled={page === totalPages - 1}>{">"}</button>
                        </div>
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