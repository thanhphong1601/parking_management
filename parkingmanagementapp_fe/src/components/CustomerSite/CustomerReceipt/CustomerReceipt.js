import { useContext, useEffect, useState } from "react";
import { Alert, Button, Container, Form, Modal, Spinner, Table, ToggleButton, ToggleButtonGroup } from "react-bootstrap";
import { formatDate, formatDateWithHour } from "../../Utils/FormatDate";
import { authApi, endpoints } from "../../../configs/APIs";
import { MyUserContext } from "../../../configs/Contexts";
import './CustomerReceipt.css'
import { useLocation, useNavigate } from "react-router-dom";

const CustomerReceipt = () => {
    const currentUser = useContext(MyUserContext);
    const exampleData = {
        "content": [
            {
                "transactionNumber": "5",
                "transactionDate": "2025-04-02T09:12:11.122+00:00",
                "ticketId": 16,
                "ticketType": "VIP",
                "isPaid": true,
                "totalPrice": 465000
            },
            {
                "transactionNumber": "3",
                "transactionDate": "2025-03-19T16:29:18.969+00:00",
                "ticketId": 12,
                "ticketType": "VIP",
                "isPaid": true,
                "totalPrice": 15000
            },
            {
                "transactionNumber": "1",
                "transactionDate": "2024-10-08T15:44:06.013+00:00",
                "ticketId": 1,
                "ticketType": "VIP",
                "isPaid": true,
                "totalPrice": 465000
            }
        ],
        "pageable": {
            "pageNumber": 0,
            "pageSize": 6,
            "sort": {
                "empty": true,
                "sorted": false,
                "unsorted": true
            },
            "offset": 0,
            "paged": true,
            "unpaged": false
        },
        "totalPages": 1,
        "totalElements": 3,
        "last": true,
        "numberOfElements": 3,
        "size": 6,
        "number": 0,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "first": true,
        "empty": false
    };

    const [name, setName] = useState("");
    const [ticketId, setTicketId] = useState("");
    const [receipts, setReceipts] = useState([]);
    const [startFilter, setStartFilter] = useState("");
    const [endFilter, setEndFilter] = useState("");
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [listStateString, setListStateString] = useState("");
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();
    const location = useLocation();
    const currentPath = location.pathname + location.search;


    if (currentUser == null) {
        nav(`/login?continue=${encodeURIComponent(currentPath)}`);
    }

    //page move funcs
    const nextPage = () => {
        if (currentPage < totalPages - 1) setCurrentPage(currentPage + 1);
    };

    const prevPage = () => {
        if (currentPage > 0) setCurrentPage(currentPage - 1);
    };

    const goToPage = (pageNum) => {
        setCurrentPage(pageNum);
    };

    //load data funcs
    const loadReceipt = async () => {
        setListStateString("");
        setLoading(true);

        const start = new Date(startFilter);
        const end = new Date(endFilter);

        if (start > end) {
            setListStateString("Thời gian không hợp lệ.");
        } else {
            setListStateString("");
            if (currentUser.role.id !== 3) {
                try {
                    let url = `${endpoints['receipt-list']}?page=${currentPage}&ticketId=${ticketId}&name=${name}`;
                    let res = await authApi().get(url);
                    
                    if (res.status == 200) {
                        setReceipts(res.data.content);
                        setTotalPages(res.data.totalPages);
                    }
                } catch (ex) {
                    console.error(ex);
                } finally {
                    setLoading(false);
                }
            } else {
                try {
                    let url = `${endpoints['receipt-customer-list'](currentUser.id)}?page=${currentPage}&startDay=${startFilter}&endDay=${endFilter}`;
                    let res = await authApi().get(url);

                    if (res.status == 200) {
                        setReceipts(res.data.content);
                        setTotalPages(res.data.totalPages);
                    }
                } catch (ex) {
                    console.error(ex);
                } finally {
                    setLoading(false);
                }
            }

        }


        setLoading(false);

    };

    //open/close modal
    const [openModal, setOpenModal] = useState(false);
    const processOpenModal = () => {
        setOpenModal(true);
    };

    const processCloseModal = () => {
        setOpenModal(false);
    };



    useEffect(() => {
        loadReceipt();
    }, [currentPage, startFilter, endFilter, name, ticketId]);

    return (<>
        <Container className="ticket-container">
            <div className="ticket-header">
                <h2>Lịch sử giao dịch</h2>
            </div>
            <div>
                {listStateString !== "" ?
                    <Alert className="alert alert-danger">{listStateString}</Alert>
                    :
                    <></>}
            </div>

            <div className="ticket-filters">
                {currentUser ? currentUser.role.id == 3 ? <>
                    <div className="container-filter">
                        <Form.Label>Từ ngày</Form.Label>
                        <Form.Control name="startDay" type="date" value={startFilter} onChange={(e) => setStartFilter(e.target.value)} />
                    </div>
                    <div className="container-filter">
                        <Form.Label>đến</Form.Label>
                        <Form.Control type="date" value={endFilter} onChange={(e) => setEndFilter(e.target.value)} />
                    </div>
                </> : <>
                    <div className="container-filter">
                        <Form.Label>Tên khách hàng</Form.Label>
                        <Form.Control type="text" value={name} onChange={(e) => setName(e.target.value)} placeholder="Tên khách hàng" />
                    </div>
                    <div className="container-filter">
                        <Form.Label>Mã vé</Form.Label>
                        <Form.Control type="text" value={ticketId} onChange={(e) => setTicketId(e.target.value)} placeholder="Mã vé" />
                    </div>
                </> : <></>}


            </div>


            <Table striped bordered hover responsive className="ticket-table">
                <thead>
                    <tr>
                        <th>Mã giao dịch</th>
                        <th>Ngày giao dịch</th>
                        <th>Khách hàng</th>
                        <th>Mã vé thanh toán</th>
                        <th>Tổng giá</th>
                        {/* <th></th> */}
                    </tr>
                </thead>
                <tbody>
                    {loading === true ? <Spinner /> : receipts.length !== 0 ? receipts.map(r => (
                        <tr key={r.ticketId}>
                            <td>{r.transactionNumber}</td>
                            <td>{formatDateWithHour(r.transactionDate)}</td>
                            <td>{r.username}</td>
                            <td>{r.ticketId}</td>
                            <td>{r.totalPrice}</td>
                            {/* <td>
                                <Button onClick={processOpenModal}>Chi tiết</Button>
                            </td> */}
                        </tr>
                    )) :
                        <tr>
                            <td colSpan={5}>
                                <p className="p-empty-info">Lịch sử trống.</p>
                            </td>
                        </tr>}
                </tbody>
            </Table>

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
        </Container>

        <Modal show={openModal} onHide={processCloseModal} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>Thông tin giao dịch</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <h1 className='fw-bold text-center text-danger'>Bạn có chắc chắn xóa thông tin người dùng này?</h1>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={processCloseModal}>
                        OK
                    </Button>
                </Modal.Footer>
            </Modal>
    </>);
};

export default CustomerReceipt;