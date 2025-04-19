import { useContext, useEffect, useState } from 'react';
import './CustomerList.css'
import APIs, { authApi, endpoints } from "../../configs/APIs";
import MySpinner from '../common/MySpinner';
import { Button, Modal, Spinner } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { MyUserContext } from '../../configs/Contexts';


const CustomerList = () => {
    // const users = [{
    //     "id": 12,
    //     "name": "Phan Văn Hào",
    //     "identityNumber": "1",
    //     "phone": "1",
    //     "address": "testAdress",
    //     "birthday": "2024-09-16T17:00:00.000+00:00",
    //     "email": "pvh@gmail.com",
    //     "username": "khachhang1",
    //     "password": "$2a$10$.7DzCoyRXN4uUwZM2BbsaOJHSzoRbdV7XrhHeo9PTqzgJ0HgNqOQi",
    //     "role": {
    //         "id": 3,
    //         "role": "ROLE_CUSTOMER"
    //     },
    //     "avatar": "",
    //     "file": null,
    //     "active": true,
    //     "enabled": true,
    //     "accountNonExpired": true,
    //     "credentialsNonExpired": true,
    //     "accountNonLocked": true
    // },
    // {
    //     "id": 14,
    //     "name": "Trương Văn Huy",
    //     "identityNumber": "2",
    //     "phone": "1",
    //     "address": "testAdress",
    //     "birthday": "2024-09-10T17:00:00.000+00:00",
    //     "email": "tvh@gmail.com",
    //     "username": "khachhang2",
    //     "password": "$2a$10$iAk0IoIvz3c5b9cZGKTSR.ECHXdtB.5KITo7.fK929BnHWwDTsMxu",
    //     "role": {
    //         "id": 3,
    //         "role": "ROLE_CUSTOMER"
    //     },
    //     "avatar": "",
    //     "file": null,
    //     "active": true,
    //     "enabled": true,
    //     "accountNonExpired": true,
    //     "credentialsNonExpired": true,
    //     "accountNonLocked": true
    // }];

    const [loading, setLoading] = useState(false);
    const [users, setUsers] = useState([]);
    const [name, setName] = useState("");
    const [identityNum, setIdentityNum] = useState("");
    const [selectedUser, setSelectedUser] = useState({});
    const [showInfoModal, setShowInfoModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showConfirmDelete, setShowConfirmDelete] = useState(false);
    const [editUser, setEditUser] = useState({});
    const [id, setId] = useState();
    const [updated, setUpdated] = useState(0);
    const nav = useNavigate();

    //pagination
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [isDeleting, setIsDeleting] = useState(true);
    const [deleteSuccess, setDeleteSuccess] = useState(false);
    const [showDeleteSpinner, setShowDeleteSpinner] = useState(false);
    const [userInfo, setUserInfo] = useState(null);

    const currentUser = useContext(MyUserContext);
    if (currentUser === null){
        nav('/login');
    }

    const fields = [{
        label: "Họ tên",
        type: "text",
        field: "name",
        placeholder: selectedUser ? selectedUser["name"] : "Họ tên"
    }, {
        label: "Email",
        type: "email",
        field: "email",
        placeholder: selectedUser ? selectedUser["email"] : "Email"
    }, {
        label: "Địa chỉ nhà",
        type: "text",
        field: "address",
        placeholder: selectedUser ? selectedUser["address"] : "Địa chỉ"
    }]

    const change = (e, field) => {
        setEditUser(current => {
            return { ...current, [field]: e.target.value }
        })
    }

    const loadCustomers = async () => {
        setLoading(true);
        try {
            let url = `${endpoints['customer-list']}?name=${name}&identityNum=${identityNum}&page=${page}`

            let res = await authApi().get(url);

            setUsers(res.data.content);
            setTotalPages(res.data.totalPages);

        } catch (ex) {
            console.error(ex);
        } finally {
            setLoading(false);
        }
    }

    const formatDate = (isoDateString) => {
        const dateObj = new Date(isoDateString);
        return dateObj.toLocaleDateString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    const dataUserFetch =
    {
        "tickets": [
            {
                "id": 1,
                "floor": {
                    "id": 1,
                    "floorNumber": 1
                },
                "line": {
                    "id": 1,
                    "line": "A",
                    "floor": {
                        "id": 1,
                        "floorNumber": 1
                    }
                },
                "position": {
                    "id": 1,
                    "position": 1,
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
                    "accountNonExpired": true,
                    "credentialsNonExpired": true,
                    "accountNonLocked": true
                },
                "userOwned": {
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
                    "accountNonExpired": true,
                    "credentialsNonExpired": true,
                    "accountNonLocked": true
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
                        "accountNonExpired": true,
                        "credentialsNonExpired": true,
                        "accountNonLocked": true
                    }
                },
                "startDay": "2024-09-23T17:00:00.000+00:00",
                "endDay": "2024-10-24T17:00:00.000+00:00",
                "totalPrice": 450000,
                "isPaid": false
            }
        ],
        "vehicles": [
            {
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
                    "accountNonExpired": true,
                    "credentialsNonExpired": true,
                    "accountNonLocked": true
                }
            }
        ],
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
            "accountNonExpired": true,
            "credentialsNonExpired": true,
            "accountNonLocked": true
        }
    };

    //pagination funcs
    const nextPage = () => {
        if (page < totalPages - 1) setPage(page + 1);
    };

    const prevPage = () => {
        if (page > 0) setPage(page - 1);
    };

    const goToPage = (pageNum) => {
        setPage(pageNum);
    };

    const navAddCustomer = (e) => {
        e.preventDefault();
        nav('/customer/add');
    }

    //modal interaction
    //info
    const handleShowDetails = (user) => {
        setSelectedUser(user);
        getUserInfo(user.id);
        setShowInfoModal(true);
    };

    const handleCloseInfo = () => setShowInfoModal(false);

    //edit
    const handleShowEdit = (user) => {
        setSelectedUser(user);
        setId(user.id);
        setShowEditModal(true);
    };

    const handleCloseEdit = () => {
        setId();
        setEditUser([]);
        setShowEditModal(false);
    }

    const handleSaveEdit = async (e) => {
        e.preventDefault();

        let form = new FormData();
        for (let key in editUser)
            form.append(key, editUser[key]);

        try {
            let url = `${endpoints['customer-info'](id)}`;

            let res = await authApi().post(url, form);
            setUpdated(current => current + 1);
            handleCloseEdit();
        } catch (ex) {
            console.error(ex);
        }

    }

    //delete user
    const handleShowDelete = (user) => {
        setSelectedUser(user);
        getUserInfo(user.id);
        setShowDeleteModal(true);
    };

    const handleCloseDelete = () => {
        setShowDeleteModal(false);
    };

    const confirmDelete = () => {
        setShowConfirmDelete(true);
    };

    const cancelConfirmDelete = () => {
        setShowConfirmDelete(false);
    };

    const getUserInfo = async (id) => {
        try {
            let res = await authApi().get(endpoints['customer-info'](id));

            setUserInfo(res.data);
        } catch (ex) {
            console.info(ex);
        }
    };

    const showDeleting = () => {
        setShowDeleteSpinner(true);
    };

    const cancelDeleting = () => {
        setShowDeleteSpinner(false);
    };

    const deleteUser = async () => {
        cancelConfirmDelete();
        showDeleting();
        setIsDeleting(true);
        setDeleteSuccess(false);

        try {
            let res = await authApi().post(endpoints['customer-deactivate'](selectedUser.id));

            if (res.status === 204) {
                setTimeout(() => {
                    setIsDeleting(false);
                    setDeleteSuccess(true);

                    setUpdated(current => current + 1);
                    handleCloseDelete();
                }, 2000);
            }


        } catch (error) {
            console.error("Lỗi khi xóa:", error);
            setIsDeleting(false);
            setDeleteSuccess(false); // Xóa thất bại, bạn có thể hiển thị thêm lỗi
        }
    };

    useEffect(() => {
        loadCustomers();
        getUserInfo(12);
    }, [page, name, identityNum, updated]);

    return <>
        <div class="customer-list-container">
            <h1>Danh sách khách hàng</h1>

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
            <button onClick={(e) => navAddCustomer(e)} class="add-button" >Thêm khách hàng</button>

            <table class="customer-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Họ Tên</th>
                        <th>CMND</th>
                        <th>Số điện thoại</th>
                        <th>Địa chỉ</th>
                        <th>Ngày sinh</th>
                        <th>Email</th>
                        <th></th>
                        <th></th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>

                    {users.map(u => <tr key={u.id}>
                        <td>{u.id}</td>
                        <td>{u.name}</td>
                        <td>{u.identityNumber}</td>
                        <td>{u.phone}</td>
                        <td>{u.address}</td>
                        <td>{formatDate(u.birthday)}</td>
                        <td>{u.email}</td>
                        <td>
                            <Button onClick={(e) => handleShowEdit(u)}>Sửa</Button>
                        </td>
                        <td>
                            <Button onClick={(e) => handleShowDelete(u)}>Xóa</Button>
                        </td>
                        <td>
                            <Button onClick={(e) => handleShowDetails(u)}>Chi tiết</Button>
                        </td>
                    </tr>)}

                </tbody>
            </table>
            <div class="pagination-container">
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

            {selectedUser && (
                <Modal show={showInfoModal} onHide={handleCloseInfo} size="lg" className="custom-modal">
                    <Modal.Header closeButton>
                        <Modal.Title className="modal-title-custom">Thông tin chi tiết</Modal.Title>
                    </Modal.Header>
                    <Modal.Body className="modal-body-custom">
                        <h4>Họ và Tên: {selectedUser.name}</h4>
                        <p><strong>CMND:</strong> {selectedUser.identityNumber}</p>
                        <p><strong>Số điện thoại:</strong> {selectedUser.phone}</p>
                        <p><strong>Địa chỉ:</strong> {selectedUser.address}</p>
                        <p><strong>Ngày sinh:</strong> {formatDate(selectedUser.birthday)}</p>
                        <p><strong>Email:</strong> {selectedUser.email}</p>

                        <div className='mt-4'>
                            <h4>Danh sách vé</h4>
                            <table class="customer-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Nơi đỗ</th>
                                        <th>Hiệu lực</th>
                                        <th>Giá vé</th>
                                        <th>Thuộc xe</th>
                                        <th>Biển số</th>
                                        <th>Thanh toán</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {userInfo !== null ? <>{userInfo.tickets.map(t => <tr key={t.id}>
                                        <td>{t.id}</td>
                                        <td>Vị trí {t.position.position} dãy {t.line.line} tầng {t.floor.floorNumber}</td>
                                        <td>{formatDate(t.startDay)} đến {formatDate(t.endDay)}</td>
                                        <td>{t.totalPrice} VNĐ</td>
                                        <td>{t.vehicle.name}</td>
                                        <td>{t.vehicle.plateLicense}</td>
                                        <td>{t.isPaid ? "Đã thanh toán" : "Chưa thanh toán"}</td>
                                    </tr>)}</> : <></>}

                                </tbody>
                            </table>
                        </div>

                        <div className='mt-2'>
                            <h4>Danh sách phương tiện</h4>
                            <table class="customer-table">
                                <thead>
                                    <tr>
                                        <th>Tên xe</th>
                                        <th>Loại xe</th>
                                        <th>Biển số</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {userInfo ? <>
                                        {userInfo.vehicles.map(v => <tr key={v.id}>
                                            <td>{v.name}</td>
                                            <td>{v.type.type}</td>
                                            <td>{v.plateLicense}</td>
                                        </tr>)}
                                    </> : <></>}
                                </tbody>
                            </table>
                        </div>

                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={handleCloseInfo}>
                            Đóng
                        </Button>
                    </Modal.Footer>
                </Modal>
            )}

            <Modal show={showEditModal} onHide={handleCloseEdit}>
                <Modal.Header closeButton>
                    <Modal.Title>Sửa thông tin</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <form>
                        {fields.map(f => <div className="form-group" key={f.id}>
                            <label>{f.label}</label>
                            <input
                                type={f.type}
                                className="form-control"
                                value={editUser[f.field]}
                                onChange={(e) => change(e, f.field)}
                                placeholder={f.placeholder}
                            />
                        </div>)}
                        <div className="form-group">
                            <label>CMND</label>
                            <input
                                type="text"
                                className="form-control"
                                value={editUser["identityNumber"]}
                                placeholder={selectedUser ? selectedUser["identityNumber"] : 'Số CMND bạn muốn đổi'}
                                onChange={(e) => change(e, "identityNumber")}
                                maxLength="12"
                            />
                        </div>
                        <div className="form-group">
                            <label>Số điện thoại</label>
                            <input
                                type="text"
                                className="form-control"
                                value={editUser["phone"]}
                                placeholder={selectedUser ? selectedUser["phone"] : 'Số điện thoại bạn muốn đổi'}
                                onChange={(e) => change(e, "phone")}
                                maxLength="10"
                            />
                        </div>
                    </form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseEdit}>
                        Hủy
                    </Button>
                    <Button variant="primary" onClick={handleSaveEdit}>
                        Lưu
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={showDeleteModal} onHide={handleCloseDelete} size="lg" className="custom-modal">
                <Modal.Header closeButton>
                    <Modal.Title>Xác nhận xóa thông tin</Modal.Title>
                </Modal.Header>
                <Modal.Body className="modal-body-custom">
                    <h4>Họ và Tên: {selectedUser.name}</h4>
                    <p><strong>CMND:</strong> {selectedUser.identityNumber}</p>
                    <p><strong>Số điện thoại:</strong> {selectedUser.phone}</p>
                    <p><strong>Địa chỉ:</strong> {selectedUser.address}</p>
                    <p><strong>Ngày sinh:</strong> {formatDate(selectedUser.birthday)}</p>
                    <p><strong>Email:</strong> {selectedUser.email}</p>

                    <h3 className='mt-2 mb-2 text-center text-danger'>CÁC THÔNG TIN SAU SẼ BỊ ẢNH HƯỞNG</h3>


                    <div className='mt-4'>
                        <h4>Danh sách vé</h4>
                        <table class="customer-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nơi đỗ</th>
                                    <th>Hiệu lực</th>
                                    <th>Giá vé</th>
                                    <th>Thuộc xe</th>
                                    <th>Biển số</th>
                                    <th>Thanh toán</th>
                                </tr>
                            </thead>
                            <tbody>
                                {userInfo !== null ? <>{userInfo.tickets.map(t => <tr key={t.id}>
                                    <td>{t.id}</td>
                                    <td>Vị trí {t.position.position} dãy {t.line.line} tầng {t.floor.floorNumber}</td>
                                    <td>{formatDate(t.startDay)} đến {formatDate(t.endDay)}</td>
                                    <td>{t.totalPrice} VNĐ</td>
                                    <td>{t.vehicle.name}</td>
                                    <td>{t.vehicle.plateLicense}</td>
                                    <td>{t.isPaid ? "Đã thanh toán" : "Chưa thanh toán"}</td>
                                </tr>)}</> : <></>}


                            </tbody>
                        </table>
                    </div>

                    <div className='mt-2'>
                        <h4>Danh sách phương tiện</h4>
                        <table class="customer-table">
                            <thead>
                                <tr>
                                    <th>Tên xe</th>
                                    <th>Loại xe</th>
                                    <th>Biển số</th>
                                </tr>
                            </thead>
                            <tbody>
                                {userInfo ? <>
                                    {userInfo.vehicles.map(v => <tr key={v.id}>
                                        <td>{v.name}</td>
                                        <td>{v.type.type}</td>
                                        <td>{v.plateLicense}</td>
                                    </tr>)}
                                </> : <></>}
                            </tbody>
                        </table>
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="danger" onClick={confirmDelete}>
                        Xóa
                    </Button>
                    <Button variant="secondary" onClick={handleCloseDelete}>
                        Không
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={showConfirmDelete} onHide={cancelConfirmDelete} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>Xác nhận xóa thông tin</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <h1 className='fw-bold text-center text-danger'>Bạn có chắc chắn xóa thông tin người dùng này?</h1>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="danger" onClick={deleteUser}>
                        CHẮC CHẮN
                    </Button>
                    <Button variant="secondary" onClick={cancelConfirmDelete}>
                        SUY NGHĨ THÊM
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>

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

    </>
}

export default CustomerList;