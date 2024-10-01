import { useEffect, useState } from 'react';
import './CustomerList.css'
import APIs, { authApi, endpoints } from "../../configs/APIs";
import MySpinner from '../common/MySpinner';
import { Button, Modal } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';


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

    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(false);
    const [users, setUsers] = useState([]);
    const [name, setName] = useState("");
    const [identityNum, setIdentityNum] = useState("");
    const [selectedUser, setSelectedUser] = useState(null);
    const [showInfoModal, setShowInfoModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [editUser, setEditUser] = useState({});
    const [id, setId] = useState();
    const [updated, setUpdated] = useState(0);
    const nav = useNavigate();

    const fields = [{
        label: "Họ tên",
        type: "text",
        field: "name",
        placeholder: "Họ tên của bạn"
    }, {
        label: "Email",
        type: "email",
        field: "email",
        placeholder: "Email của bạn"
    },{
        label: "Địa chỉ nhà",
        type: "text",
        field: "address",
        placeholder: "Địa chỉ nhà"
    }]

    const change = (e, field) => {
        setEditUser(current => {
            return { ...current, [field]: e.target.value }
        })
    }

    const loadCustomers = async () => {
        setLoading(true);
        try {
            let url = `${endpoints['customer-list']}?name=${name}&identityNum=${identityNum}`

            let res = await authApi().get(url);

            setUsers(res.data);
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

    const navAddCustomer = (e) => {
        e.preventDefault();
        nav('/customer/add');
    }

    //modal interaction
    //info
    const handleShowDetails = (e, user) => {
        e.preventDefault();
        setSelectedUser(user);
        setShowInfoModal(true);
    };

    const handleCloseInfo = () => setShowInfoModal(false);

    //edit
    const handleShowEdit = (e, user) => {
        e.preventDefault();
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


    useEffect(() => {
        loadCustomers();
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
                            <Button  onClick={(e) => handleShowEdit(e, u)}>Sửa</Button>
                        </td>
                        <td>
                            <Button>Xóa</Button>
                        </td>
                        <td>
                            <Button onClick={(e) => handleShowDetails(e, u)}>Chi tiết</Button>
                        </td>
                    </tr>)}

                </tbody>
            </table>
            <div class="pagination-container">
                <button class="pagination-button">{"<"}</button>
                <button class="pagination-button">1</button>
                <button class="pagination-button">2</button>
                <button class="pagination-button">3</button>
                <button class="pagination-button">{">"}</button>
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
                            />
                        </div>)}
                        <div className="form-group">
                            <label>CMND</label>
                            <input
                                type="text"
                                className="form-control"
                                value={editUser["identityNumber"]}
                                placeholder={selectedUser? selectedUser.identityNumber: 'Số CMND bạn muốn đổi'}
                                onChange={(e) => change(e, "identityNumber")}
                            />
                        </div>
                        <div className="form-group">
                            <label>Số điện thoại</label>
                            <input
                                type="text"
                                className="form-control"
                                value={editUser["phone"]}
                                placeholder={selectedUser? selectedUser.phone: 'Số điện thoại bạn muốn đổi'}
                                onChange={(e) => change(e, "phone")}
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
        </div>

    </>
}

export default CustomerList;