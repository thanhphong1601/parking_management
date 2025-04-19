import { useContext, useEffect, useState } from "react";
import { Button, Modal, Spinner } from "react-bootstrap";
import { authApi, endpoints } from "../../configs/APIs";
import './TicketList.css'
import { useNavigate } from "react-router-dom";
import MySpinner from "../common/MySpinner";
import { MyUserContext } from "../../configs/Contexts";
import {QRCodeSVG} from 'qrcode.react';


const TicketList = () => {
    const nav = useNavigate();
    const currentUser = useContext(MyUserContext);
    if (currentUser === null) {
        nav('/login');
    };

    const data = [
        {
            "id": 2,
            "floor": {
                "id": 2,
                "floorNumber": 2
            },
            "line": {
                "id": 2,
                "line": "B",
                "floor": {
                    "id": 1,
                    "floorNumber": 1
                }
            },
            "position": {
                "id": 2,
                "position": 2,
                "line": {
                    "id": 1,
                    "line": "A",
                    "floor": {
                        "id": 1,
                        "floorNumber": 1
                    }
                }
            },
            "price": {
                "id": 1,
                "price": 15000
            },
            "userCreate": {
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
                "credentialsNonExpired": true,
                "accountNonExpired": true,
                "accountNonLocked": true
            },
            "userOwned": {
                "id": 14,
                "name": "Trương Văn Huy",
                "identityNumber": "2",
                "phone": "1",
                "address": "testAdress",
                "birthday": "2024-09-10T17:00:00.000+00:00",
                "email": "tvh@gmail.com",
                "username": "khachhang2",
                "password": "$2a$10$iAk0IoIvz3c5b9cZGKTSR.ECHXdtB.5KITo7.fK929BnHWwDTsMxu",
                "role": {
                    "id": 3,
                    "role": "ROLE_CUSTOMER"
                },
                "avatar": "",
                "file": null,
                "active": true,
                "enabled": true,
                "credentialsNonExpired": true,
                "accountNonExpired": true,
                "accountNonLocked": true
            },
            "vehicle": {
                "id": 14,
                "name": "Vario",
                "type": {
                    "id": 2,
                    "type": "Xe máy"
                },
                "plateLicense": "11112222",
                "user": {
                    "id": 14,
                    "name": "Trương Văn Huy",
                    "identityNumber": "2",
                    "phone": "1",
                    "address": "testAdress",
                    "birthday": "2024-09-10T17:00:00.000+00:00",
                    "email": "tvh@gmail.com",
                    "username": "khachhang2",
                    "password": "$2a$10$iAk0IoIvz3c5b9cZGKTSR.ECHXdtB.5KITo7.fK929BnHWwDTsMxu",
                    "role": {
                        "id": 3,
                        "role": "ROLE_CUSTOMER"
                    },
                    "avatar": "",
                    "file": null,
                    "active": true,
                    "enabled": true,
                    "credentialsNonExpired": true,
                    "accountNonExpired": true,
                    "accountNonLocked": true
                }
            },
            "startDay": "2024-09-23T17:00:00.000+00:00",
            "endDay": "2024-10-26T17:00:00.000+00:00",
            "totalPrice": 495000,
            "isPaid": false
        }
    ];

    const fields = [{
        label: "Ngày bắt đầu",
        type: "date",
        field: "startDay",
        placeholder: "Ngày bắt đầu"
    }, {
        label: "Ngày kết thúc",
        type: "date",
        field: "endDay",
        placeholder: "Ngày kết thúc"
    }];


    const [name, setName] = useState("");
    const [identityNum, setIdentityNum] = useState("");
    const [loading, setLoading] = useState(false);
    const [tickets, setTickets] = useState([]);
    const [ticket, setTicket] = useState({});
    const [floor, setFloor] = useState(null);
    const [line, setLine] = useState(null);
    const [position, setPosition] = useState(null);

    const [updated, setUpdated] = useState(0);

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);


    //list resource
    const [floors, setFloors] = useState([]);
    const [lines, setLines] = useState([]);
    const [positions, setPositions] = useState([]);

    //modal
    const [selectedTicket, setSelectedTicket] = useState(null);
    const [showTicketInfo, setShowTicketInfo] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showConfirmDelete, setShowConfirmDelete] = useState(false);
    const [showDeleteSpinner, setShowDeleteSpinner] = useState(false);
    const [showQRCode, setShowQRCode] = useState(false);

    const [isDeleting, setIsDeleting] = useState(true);
    const [deleteSuccess, setDeleteSuccess] = useState(false);

    const [qrCode, setQRCode] = useState("");


    // functions
    const formatDate = (isoDateString) => {
        const dateObj = new Date(isoDateString);
        return dateObj.toLocaleDateString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    const loadTicket = async () => {
        setLoading(true);

        try {
            let url = `${endpoints['ticket-list']}?name=${name}&identityNum=${identityNum}&page=${page}`;


            let res = await authApi().get(url);
            setTickets(res.data.content);
            setTotalPages(res.data.totalPages);

        } catch (ex) {
            console.error(ex);
        } finally {
            setLoading(false);
        }
    };

    const deleteTicket = async () => {
        cancelConfirmDelete();
        showDeleting();
        setIsDeleting(true);
        setDeleteSuccess(false);

        try {
            let res = await authApi().delete(endpoints['ticket-delete'](selectedTicket.id));


            if (res.status === 204) {
                setTimeout(() => {
                    setIsDeleting(false);
                    setDeleteSuccess(true);

                    setUpdated(current => current + 1);
                    closeDelete();
                }, 2000);
            }


        } catch (error) {
            console.error("Lỗi khi xóa:", error);
            setIsDeleting(false);
            setDeleteSuccess(false); // Xóa thất bại, bạn có thể hiển thị thêm lỗi
        }
    };

    const createPayment = async (e, t) => {
        e.preventDefault();
        let form = new FormData();

        const info = `Người dùng: ${t.userOwned.name} thanh toán vé id: ${t.id}`;
        form.append("orderInfo", info);
        form.append("price", t.totalPrice);
        try {
            let res = await authApi().post(endpoints['create-payment'], form);
            localStorage.setItem("ticketId", t.id);

            window.location.href = res.data;

        } catch (ex) {
            console.error(ex);
        }
    };

    const createPaymentUrl = async (e, t) => {
        e.preventDefault();
        let form = new FormData();

        const info = `Người dùng: ${t.userOwned.name} thanh toán vé id: ${t.id}`;
        form.append("orderInfo", info);
        form.append("price", t.totalPrice);
        try {
            let res = await authApi().post(endpoints['create-payment'], form);
            setQRCode(res.data);

            showQRCodeModal();
        } catch (ex) {
            console.error(ex);
        }
    }

    //modals interaction funcs
    const showInfo = (e, ticket) => {
        e.preventDefault();
        setSelectedTicket(ticket);
        setShowTicketInfo(true);
    };

    const closeInfo = () => {
        setShowTicketInfo(false);
    };

    const showDelete = (e, ticket) => {
        e.preventDefault();

        setSelectedTicket(ticket);

        setShowDeleteModal(true);
    };

    const closeDelete = () => {
        setSelectedTicket(null);
        setShowDeleteModal(false);
    }

    const confirmDelete = () => {
        setShowConfirmDelete(true);
    };

    const cancelConfirmDelete = () => {
        setShowConfirmDelete(false);
    };

    const showDeleting = () => {
        setShowDeleteSpinner(true);
    };

    const cancelDeleting = () => {
        setShowDeleteSpinner(false);
    };

    const showQRCodeModal = () => {
        setShowQRCode(true);
    }

    const cancelQRCodeModal = () => {
        setShowQRCode(false);
    }

    //load resources func
    const nextPage = () => {
        if (page < totalPages - 1) setPage(page + 1);
    };

    const prevPage = () => {
        if (page > 0) setPage(page - 1);
    };

    const goToPage = (pageNum) => {
        setPage(pageNum);
    };



    //useeffect funcs
    useEffect(() => {
        loadTicket();
    }, [name, identityNum, updated, page]);


    return <>
        <div class="customer-list-container">
            <h1>Danh sách vé</h1>

            <div class="search-container">
                <input type="text" placeholder="Tìm kiếm theo tên..." class="search-input"
                    value={name}
                    onChange={e => setName(e.target.value)} />
            </div>
            <div class="search-container">
                <input type="text" placeholder="Tìm kiếm theo CMND..." class="search-input"
                    value={identityNum}
                    onChange={e => setIdentityNum(e.target.value)} />
            </div>

            <button onClick={(e) => nav('/ticket/add')} class="add-button" >Thêm vé mới</button>

            <table class="customer-table">
                <thead>
                    <tr>
                        <th>Mã vé</th>
                        <th>Vị trí</th>
                        <th>Họ tên chủ sở hữu</th>
                        <th>CMND</th>
                        <th>Người tạo</th>
                        <th>Giá vé</th>
                        <th>Thời hạn</th>
                        <th>Trạng thái</th>
                        <th></th>
                        <th></th>
                        <th></th>
                        <th></th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>

                    {tickets.map(t => <tr key={t.id}>
                        <td>{t.id}</td>
                        <td>Vị trí {t.position ? t.position.position : "-"} dãy {t.line ? t.line.line : "-"} tầng {t.floor ? t.floor.floorNumber : "-"}</td>
                        <td>{t.userOwned.name}</td>
                        <td>{t.userOwned.identityNumber}</td>
                        <td>{t.userCreate.name}</td>
                        <td>{t.totalPrice} VNĐ</td>
                        <td>{formatDate(t.startDay)} - {formatDate(t.endDay)}</td>
                        <td>{t.isPaid === false ? <span className="text-warning bg-dark p-2 rounded border-warning">Chưa thanh toán</span>
                            :
                            <span className="text-white bg-success p-2 rounded">Đã thanh toán</span>}</td>
                        <td>
                            <Button onClick={(e) => showInfo(e, t)} >Chi tiết</Button>
                        </td>
                        <td>
                            {t.isPaid === false ?
                                <Button onClick={(e) => createPayment(e, t)} >Thanh toán</Button>
                                :
                                <Button className="btn-dark" disabled >Đã thanh toán</Button>
                            }
                        </td>
                        <td>
                            <Button className="btn-danger" onClick={(e) => showDelete(e, t)} >Xóa</Button>
                        </td>
                        <td>
                            <Button className="btn-info" onClick={(e) => createPaymentUrl(e, t)} >QR</Button>

                        </td>
                    </tr>)}

                </tbody>
            </table>
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

        {selectedTicket && <Modal show={showTicketInfo} onHide={closeInfo} size="lg" className="custom-modal">
            <Modal.Header closeButton>
                <Modal.Title className="modal-title-custom">Thông tin chi tiết</Modal.Title>
            </Modal.Header>
            <Modal.Body className="modal-body-custom">
                <p><strong>Mã vé:</strong> {selectedTicket.id}</p>
                <p>  <strong>Vị trí:</strong> Vị trí {selectedTicket?.position?.position || "-"} dãy {selectedTicket?.line?.line || "-"} tầng {selectedTicket?.floor?.floorNumber || "-"}
                </p>
                <p><strong>Người sở hữu:</strong> {selectedTicket.userOwned.name}</p>
                <p><strong>Email:</strong> {selectedTicket.userOwned.email}</p>
                <p><strong>CMND:</strong> {selectedTicket.userOwned.identityNumber}</p>
                <p><strong>Ngày sinh:</strong> {formatDate(selectedTicket.userOwned.birthday)}</p>
                <p><strong>Nhân viên tạo vé:</strong> {selectedTicket.userCreate.name}</p>
                <p><strong>Mã nhân viên:</strong> {selectedTicket.userCreate.name}</p>
                <p><strong>CMND nhân viên:</strong> {selectedTicket.userCreate.identityNumber}</p>

                <p><strong>Vé có hiệu lực từ:</strong> {formatDate(selectedTicket.startDay)}</p>
                <p><strong>Đến:              </strong> {formatDate(selectedTicket.endDay)}</p>
                <p><strong>Trạng thái thanh toán:</strong> {selectedTicket.isPaid === false ? "Chưa thanh toán" : "Đã thanh toán"}</p>

            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={closeInfo}>
                    Đóng
                </Button>
            </Modal.Footer>

        </Modal>}

        {/* <Modal show={showEditModal} onHide={closeEdit}>
            <Modal.Header closeButton>
                <Modal.Title>Sửa thông tin</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {selectedTicket? <>
                    <form>
                    <div className="form-group">
                        <label>Tầng</label>
                        <select
                            className="form-control"
                            value={floor}
                            onChange={(e) => changeFloor(e)}>
                            {floors === null ? <MySpinner /> : <>
                                {selectedTicket !== null ? <>
                                    {floors.map(f => <option key={f.id} value={f.id} selected={f.id === selectedTicket.floor.id ? true : false}>
                                        {f.floorNumber}
                                    </option>)}
                                </>: <></>}
                            </>}
                        </select>
                    </div>
                    <div className="form-group">
                        <label>Dãy</label>
                        <select
                            className="form-control"
                            value={line}
                            onChange={(e) => setLine(e.target.value)}>
                                {lines.length > 0 ? <>
                                    {lines.map(l => <option key={l.id} value={l.id} selected={l.id === selectedTicket.line.id ? true : false}>
                                        {l.line}
                                    </option>)}
                                </>: <></>}
                        </select>
                    </div>
                   
                </form>
                </>: <></>}
                
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={closeEdit}>
                    Hủy
                </Button>
                <Button variant="primary">
                    Lưu
                </Button>
            </Modal.Footer>
        </Modal> */}
        {selectedTicket && <Modal show={showDeleteModal} onHide={closeDelete} size="lg" className="custom-modal">
            <Modal.Header closeButton className="modal-header-danger">
                <Modal.Title>Xác nhận xóa thông tin</Modal.Title>
            </Modal.Header>
            <Modal.Body className="modal-body-custom">
                <p><strong>Mã vé:</strong> {selectedTicket.id}</p>
                <p><strong>Vị trí:</strong> Vị trí {selectedTicket?.position?.position || "-"} dãy {selectedTicket?.line?.line || "-"} tầng {selectedTicket?.floor?.floorNumber || "-"}</p>
                <p><strong>Người sở hữu:</strong> {selectedTicket.userOwned.name}</p>
                <p><strong>Email:</strong> {selectedTicket.userOwned.email}</p>
                <p><strong>CMND:</strong> {selectedTicket.userOwned.identityNumber}</p>
                {/* <p><strong>Ngày sinh:</strong> {formatDate(selectedTicket.userOwned.birthday)}</p> */}
                <p><strong>Nhân viên tạo vé:</strong> {selectedTicket.userCreate.name}</p>
                <p><strong>Mã nhân viên:</strong> {selectedTicket.userCreate.name}</p>
                <p><strong>CMND nhân viên:</strong> {selectedTicket.userCreate.identityNumber}</p>

                <p><strong>Vé có hiệu lực từ:</strong> {formatDate(selectedTicket.startDay)}</p>
                <p><strong>Đến:              </strong> {formatDate(selectedTicket.endDay)}</p>
                <p><strong>Trạng thái thanh toán:</strong> {selectedTicket.isPaid === false ? "Chưa thanh toán" : "Đã thanh toán"}</p>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="danger" onClick={confirmDelete}>
                    Xóa
                </Button>
                <Button variant="secondary" onClick={closeDelete}>
                    Không
                </Button>
            </Modal.Footer>
        </Modal>}

        <Modal show={showConfirmDelete} onHide={cancelConfirmDelete} size="lg" className="custom-modal">
            <Modal.Header closeButton className="modal-header modal-header-danger">
                <Modal.Title>Xác nhận xóa thông tin</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <h1 className='fw-bold text-center text-danger'>Bạn có chắc chắn xóa thông tin vé xe này?</h1>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="danger" onClick={deleteTicket}>
                    CHẮC CHẮN
                </Button>
                <Button variant="secondary" onClick={cancelConfirmDelete}>
                    SUY NGHĨ THÊM
                </Button>
            </Modal.Footer>
        </Modal>

        <Modal show={showDeleteSpinner} onHide={cancelDeleting}>
            <Modal.Body className="text-center">
                {isDeleting ? (
                    <>
                        <Spinner animation="border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </Spinner>
                        <p>Đang tiến hành xóa thông tin...</p>
                    </>
                ) : (
                    <p>{deleteSuccess ? "Xóa thành công!" : "Có lỗi xảy ra khi xóa!"}</p>
                )}
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={cancelDeleting}>
                    Đóng
                </Button>
            </Modal.Footer>
        </Modal>

        <Modal show={showQRCode} onHide={cancelQRCodeModal}>
            <Modal.Header closeButton>
                <Modal.Title>Quét QR Code để thanh toán</Modal.Title>
            </Modal.Header>
            <Modal.Body className="d-flex justify-content-center">
                {/* <QRCode value={qrCode} size={256} /> */}
                <QRCodeSVG value={qrCode} size={256} />
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={cancelQRCodeModal}>
                    Đóng
                </Button>
            </Modal.Footer>
        </Modal>
    </>
}

export default TicketList;