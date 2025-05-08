import { useContext, useEffect, useState } from "react";
import { Button, Container, Form, Table, ToggleButton, ToggleButtonGroup } from "react-bootstrap";
import "./TicketManage.css"
import { authApi, customerEndpoints, endpoints } from "../../../configs/APIs";
import { MyUserContext } from "../../../configs/Contexts";
import { Link, useLocation, useNavigate } from "react-router-dom";

const TicketManage = () => {
    const mockData = [
        {
            floorNumber: 1,
            line: "A",
            position: 1,
            ticketPrice: 15000,
            vehicleName: "Vision",
            vehicleLicensePlate: "IT20BOM",
            startDay: "2024-09-23T17:00:00.000+00:00",
            endDay: "2024-10-24T17:00:00.000+00:00",
            isPaid: true,
            totalPrice: 465000,
        },
        {
            floorNumber: 1,
            line: "A",
            position: 5,
            ticketPrice: 15000,
            vehicleName: "Vision",
            vehicleLicensePlate: "IT20BOM",
            startDay: "2025-03-04T17:00:00.000+00:00",
            endDay: "2025-03-05T17:00:00.000+00:00",
            isPaid: true,
            totalPrice: 15000,
        },
    ];
    const exampleData = {
        "content": [
            {
                "ticketId": 1,
                "userOwnedName": "Phan Văn Hào",
                "floorNumber": 1,
                "line": "A",
                "position": 1,
                "ticketPrice": 15000,
                "vehicleName": "Vision",
                "vehicleLicensePlate": "IT20BOM",
                "startDay": "2024-09-23T17:00:00.000+00:00",
                "endDay": "2024-10-24T17:00:00.000+00:00",
                "isPaid": true,
                "totalPrice": 465000
            },
            {
                "ticketId": 12,
                "userOwnedName": "Phan Văn Hào",
                "floorNumber": 1,
                "line": "A",
                "position": 5,
                "ticketPrice": 15000,
                "vehicleName": "Vision",
                "vehicleLicensePlate": "IT20BOM",
                "startDay": "2025-03-04T17:00:00.000+00:00",
                "endDay": "2025-03-05T17:00:00.000+00:00",
                "isPaid": true,
                "totalPrice": 15000
            },
            {
                "ticketId": 16,
                "userOwnedName": "Phan Văn Hào",
                "floorNumber": 1,
                "line": "A",
                "position": 1,
                "ticketPrice": 15000,
                "vehicleName": "Vision",
                "vehicleLicensePlate": "IT20BOM",
                "startDay": "2024-09-23T17:00:00.000+00:00",
                "endDay": "2024-10-24T17:00:00.000+00:00",
                "isPaid": false,
                "totalPrice": 465000
            }
        ],
        "pageable": {
            "pageNumber": 0,
            "pageSize": 6,
            "sort": {
                "unsorted": true,
                "empty": true,
                "sorted": false
            },
            "offset": 0,
            "unpaged": false,
            "paged": true
        },
        "totalElements": 3,
        "last": true,
        "totalPages": 1,
        "first": true,
        "size": 6,
        "number": 0,
        "sort": {
            "unsorted": true,
            "empty": true,
            "sorted": false
        },
        "numberOfElements": 3,
        "empty": false
    };
    const currentUser = useContext(MyUserContext);
    const [tickets, setTickets] = useState([]);
    const [startFilter, setStartFilter] = useState("");
    const [endFilter, setEndFilter] = useState("");
    const [showPaid, setShowPaid] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const pageSize = 6;
    const [ticketStartDay, setTicketStartDay] = useState(null);
    const [ticketEndDay, setTicketEndDay] = useState(null);
    const [totalPages, setTotalPages] = useState(1);
    const nav = useNavigate();
    const location = useLocation();
    const currentPath = location.pathname + location.search;
    if (currentUser == null) {
        nav(`/login?continue=${encodeURIComponent(currentPath)}`);
    }

    const loadCustomerTickets = async () => {
        try {
            let url = `${customerEndpoints['customer-ticket-list'](currentUser.id)}?page=${currentPage}&startDay=${startFilter}&endDay=${endFilter}&isPaid=${showPaid}`;
            let res = await authApi().get(url);

            setTickets(res.data.content);
            setTotalPages(res.data.totalPages);
        } catch (ex) {
            console.error(ex);
        }
    };

    const nextPage = () => {
        if (currentPage < totalPages - 1) setCurrentPage(currentPage + 1);
    };

    const prevPage = () => {
        if (currentPage > 0) setCurrentPage(currentPage - 1);
    };

    const goToPage = (pageNum) => {
        setCurrentPage(pageNum);
    };

    const createPayment = async (e, t) => {
        e.preventDefault();
        let form = new FormData();

        const info = `Người dùng ${t.userOwnedName} thanh toán vé mang id ${t.ticketId}`;
        form.append("orderInfo", info);
        form.append("price", t.totalPrice);
        form.append("ticketId", t.ticketId);
        try {
            let res = await authApi().post(endpoints['create-payment'], form);
            localStorage.setItem("ticketId", t.ticketId);

            window.location.href = res.data;

        } catch (ex) {
            console.error(ex);
        }
    };

    useEffect(() => {
        loadCustomerTickets();
    }, [currentPage, startFilter, endFilter, showPaid]);

    return (
        <Container className="ticket-container">
            <div className="ticket-header">
                <h2>Danh sách vé xe</h2>
                <Link to={`/customer/${currentUser.id}/ticket/create`} className="btn btn-primary" variant="primary">Đăng ký vé mới</Link>
            </div>

            <div className="ticket-filters">
                <Form.Control type="date" value={startFilter} onChange={(e) => setStartFilter(e.target.value)} />
                <Form.Control type="date" value={endFilter} onChange={(e) => setEndFilter(e.target.value)} />
                <ToggleButtonGroup type="radio" name="paid" defaultValue={null}>
                    <ToggleButton id="t-paid-all"
                        value={null}
                        onClick={() => setShowPaid(null)}
                        variant={showPaid === null ? "primary" : "outline-primary"}
                        active={showPaid === null}
                    >
                        Tất cả
                    </ToggleButton>
                    <ToggleButton id="t-paid-yes"
                        value={true}
                        onClick={() => setShowPaid(true)}
                        variant={showPaid === true ? "success" : "outline-success"}
                        active={showPaid === true}
                    >
                        Đã thanh toán</ToggleButton>
                    <ToggleButton id="t-paid-no"
                        value={false}
                        onClick={() => setShowPaid(false)}
                        variant={showPaid === false ? "danger" : "outline-danger"}
                        active={showPaid === false}
                    >
                        Chưa thanh toán</ToggleButton>
                </ToggleButtonGroup>
            </div>

            <Table striped bordered hover responsive className="ticket-table">
                <thead>
                    <tr>
                        <th>Tầng</th>
                        <th>Dãy</th>
                        <th>Vị trí</th>
                        <th>Phương tiện</th>
                        <th>Biển số</th>
                        <th>Giá vé</th>
                        <th>Thời gian</th>
                        <th>Tổng tiền</th>
                        <th>Trạng thái</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    {tickets.length !== 0 ? tickets.map(t => (
                        <tr key={t.ticketId}>
                            <td>{t.floorNumber}</td>
                            <td>{t.line}</td>
                            <td>{t.position}</td>
                            <td>{t.vehicleName}</td>
                            <td>{t.vehicleLicensePlate}</td>
                            <td>{t.ticketPrice.toLocaleString()}đ</td>
                            <td>
                                {new Date(t.startDay).toLocaleDateString()} - {new Date(t.endDay).toLocaleDateString()}
                            </td>
                            <td>{t.totalPrice.toLocaleString()}đ</td>
                            <td>{t.isPaid ? "✅ Đã thanh toán" : "❌ Chưa thanh toán"}</td>
                            <td>
                                <Button active={t.isPaid !== false}
                                    variant={t.isPaid === true ? "secondary" : "primary"}
                                    onClick={e => createPayment(e, t)}
                                >Thanh toán</Button>
                            </td>
                        </tr>
                    )) :
                        <tr>
                            <td colSpan={10}>
                                <p className="p-empty-info">Chưa có vé nào được đăng ký.</p>

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
    );
};

export default TicketManage;