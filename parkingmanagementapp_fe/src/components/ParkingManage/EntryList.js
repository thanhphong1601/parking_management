import { Button, Modal, OverlayTrigger, Tooltip } from "react-bootstrap";
import "./EntryList.css"
import { useContext, useEffect, useState } from "react";
import { authApi, endpoints } from "../../configs/APIs";
import { format } from "date-fns";
import 'bootstrap-icons/font/bootstrap-icons.css';
import { useLocation, useNavigate } from "react-router-dom";
import { MyUserContext } from "../../configs/Contexts";


const EntryList = () => {
    const currentUser = useContext(MyUserContext);
    const nav = useNavigate();
    const location = useLocation();
    const currentPath = location.pathname + location.search;
    if (currentUser == null) {
        nav(`/login?continue=${encodeURIComponent(currentPath)}`);
    }
    const [entries, setEntries] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [currentPage, setCurrentPage] = useState(0);
    const [plateLicense, setPlateLicense] = useState("");
    const [page, setPage] = useState(0);


    //modal
    const [plateUrl, setPlateUrl] = useState("https://placehold.co/300x250/png");
    const [faceUrl, setFaceUrl] = useState("https://placehold.co/300x250/png");

    const [viewing, setViewing] = useState(null); // 'in' or 'out'
    const getTitle = () => viewing === 'in' ? 'Ảnh vào bãi' : 'Ảnh ra bãi';
    const [show, setShow] = useState();

    const handleShow = (type, entry) => {
        setViewing(type);
        if (type === 'in') {
            setPlateUrl(entry.plateImgIn);
            setFaceUrl(entry.personImgIn);
        }
        else {
            setPlateUrl(entry.plateImgOut);
            setFaceUrl(entry.personImgOut);
        }
        setShow(true);
    };

    const handleClose = () => {
        setViewing("");
        setPlateUrl("https://placehold.co/300x250/png");
        setFaceUrl("https://placehold.co/300x250/png");
        setShow(false);
    }


    //other utils funcs
    const formatDateWithHour = (dateStr) => {
        const normalizedDateStr = dateStr.replace('ICT', 'GMT+0700');
        const date = new Date(normalizedDateStr);
        if (isNaN(date)) {
            return 'Invalid date'; // Xử lý lỗi nếu có
        }
        return format(date, 'dd/MM/yyyy HH:mm:ss');
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

    const loadEntries = async () => {
        try {
            let url = `${endpoints['entry-all-list-pageable']}?licenseNumber=${plateLicense}&page=${page}`;

            let res = await authApi().get(url);
            setEntries(res.data.content);
            setTotalPages(res.data.totalPages);
        } catch (ex) {
            console.error(ex);
        }
    };

    useEffect(() => {
        loadEntries();
    }, [plateLicense, page])

    return (<>
        <div class="customer-list-container">
            <h1>Lịch sử ra vào</h1>

            <div class="search-container">
                <input type="text" placeholder="Tìm kiếm theo biển số..." class="search-input" onChange={(e) => setPlateLicense(e.target.value)} />
            </div>

            <table class="customer-table">
                <thead>
                    <tr>
                        <th>Khách hàng</th>
                        <th>Biển số xe</th>
                        <th>Thời gian vào</th>
                        <th>Thời gian ra</th>
                        <th colSpan="2" class="text-center">Hình ảnh</th>

                    </tr>
                </thead>
                <tbody>
                    {entries.length > 0 ? entries.map((entry) =>
                        <tr key={entry.id}>
                            <td>{entry.owner.name}</td>
                            <td>{entry.licenseNumber == 0 || entry.licenseNumber === null ? "Không có" : entry.licenseNumber.toUpperCase()}</td>
                            <td>{formatDateWithHour(entry.timeIn)}</td>
                            <td>{entry.timeOut !== null || entry.timeOut === "" ? formatDateWithHour(entry.timeOut) : "Chưa ghi nhận"}</td>
                            <td>
                                <OverlayTrigger placement="top" overlay={<Tooltip>Xem ảnh vào</Tooltip>}>
                                    <Button variant="outline-primary" size="sm" onClick={() => handleShow("in", entry)}>
                                        <i className="bi bi-door-open-fill"></i>
                                    </Button>
                                </OverlayTrigger>
                            </td>
                            <td>
                                <OverlayTrigger placement="top" overlay={<Tooltip>Xem ảnh ra</Tooltip>}>
                                    <Button variant="outline-success" size="sm" onClick={() => handleShow("out", entry)}>
                                        <i className="bi bi-door-closed-fill"></i>
                                    </Button>
                                </OverlayTrigger>
                            </td>
                        </tr>
                    ) : (
                        <tr>
                            <td colSpan="6" style={{ textAlign: "center", padding: "1rem" }}>
                                Không có dữ liệu.
                            </td>
                        </tr>
                    )}


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

        <Modal show={show} onHide={handleClose} centered size="lg">
            <Modal.Header closeButton>
                <Modal.Title>{getTitle()}</Modal.Title>
            </Modal.Header>
            <Modal.Body className="text-center">
                <p className="text-muted text-center mb-4">Hình ảnh từ camera giám sát</p>

                <div className="row g-3 justify-content-center">
                    <div className="col-md-6 text-center">
                        <h6 className="text-secondary mb-2">Biển xe</h6>
                        <div className="border rounded shadow-sm p-2">
                            <img
                                src={plateUrl}
                                alt="Ảnh vào"
                                className="img-fluid"
                                style={{
                                    minHeight: '300px',
                                    maxHeight: '300px',
                                    objectFit: 'contain',
                                    width: '100%',
                                }}
                            />
                        </div>
                    </div>

                    <div className="col-md-6 text-center">
                        <h6 className="text-secondary mb-2">Khách hàng</h6>
                        <div className="border rounded shadow-sm p-2">
                            <img
                                src={faceUrl}
                                alt="Ảnh ra"
                                className="img-fluid"
                                style={{
                                    minHeight: '300px',
                                    maxHeight: '300px',
                                    objectFit: 'contain',
                                    width: '100%',
                                }}
                            />
                        </div>
                    </div>
                </div>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                    Đóng
                </Button>
            </Modal.Footer>
        </Modal>
    </>);
}

export default EntryList;