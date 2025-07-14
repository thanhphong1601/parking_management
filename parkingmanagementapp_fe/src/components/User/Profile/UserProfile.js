import { useContext, useEffect, useRef, useState } from 'react';
import './UserProfile.css'
import { MyDispatchContext, MyUserContext } from '../../../configs/Contexts';
import { Link, useNavigate } from 'react-router-dom';
import { Alert, Button, Form, Modal } from 'react-bootstrap';
import { authApi, endpoints } from '../../../configs/APIs';

const UserProfile = () => {
    const currentUser = useContext(MyUserContext);
    const dispatch = useContext(MyDispatchContext);
    const nav = useNavigate();

    const role = {
        1: "Quản trị viên",
        2: "Nhân viên bảo vệ",
        3: "Khách hàng"
    };

    const [activeTab, setActiveTab] = useState('home');
    const [name, setName] = useState("");
    const [phone, setPhone] = useState("");
    const [address, setAddress] = useState("");
    const [confirm, setConfirm] = useState("");
    const [identityNumber, setIdentityNumber] = useState("");
    const [msg, setMsg] = useState("");
    const [infoMsg, setInfoMsg] = useState("");
    const [vehicleList, setVehicleList] = useState([]);
    const [inform, setInform] = useState("");


    let currentUserAvatarUrl;

    const loadAvatar = () => {
        if (currentUser && currentUser.avatar) {
            return currentUser.avatar;
        } else {
            return 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS52y5aInsxSm31CvHOFHWujqUx_wWTS9iM6s7BAm21oEN_RiGoog';
        }
    };

    const loadVehicle = async () => {
        try {
            let url = `${endpoints['vehicle-list-user'](currentUser.id)}`;
            let res = await authApi().get(url);

            if (res.status === 200) {
                setVehicleList(res.data);
            }
        } catch (ex) {
            console.error(ex);
        }
    }
    const files = useRef();
    const [userAvtUrl, setUserAvtUrl] = useState();

    const handleInPersonImgChange = async (event) => {
        event.preventDefault();
        setMsg("");
        setInform("Đã Upload thành công");


        const file = event.target.files[0];
        if (file) {
            const imageUrl = URL.createObjectURL(file);
            setUserAvtUrl(imageUrl);

            try {
                let form = new FormData();
                form.append("file1", file);
                let url = `${endpoints['user-avatar-change']}?userId=${currentUser.id}`;

                let res = await authApi().post(url, form);

                if (res.status === 200){
                    loadAvatar();
                    showSuccess();

                    setTimeout(() => {
                        closeSuccess();
                    }, 2000);
                }
            } catch (ex) {
                if (ex.response.status === 404){
                    setInform(ex.response.data);

                    showSuccess();

                    setTimeout(() => {
                        closeSuccess();
                    }, 2000);
                }
                console.error(ex);
            }
        }
    };

    //success modal
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    const showSuccess = () => {
        setShowSuccessModal(true);
    };

    const closeSuccess = () => {
        setShowSuccessModal(false);
    };

    const changeAvatar = async () => {

    };

    const submitChange = async (e) => {
        e.preventDefault();
        setMsg("");
        try {
            let url = `${endpoints['password-check'](currentUser.id)}`;
            let form = new FormData();
            form.append("password", confirm);
            let res = await authApi().post(url, form);
            if (res.status === 200) {
                let form2 = new FormData();
                form2.append("phone", phone);
                form2.append("name", name);
                form2.append("address", address);
                form2.append("identityNumber", identityNumber);

                url = `${endpoints['user-info-update'](currentUser.id)}`;
                res = await authApi().put(url, form2);
                if (res.status == 200) {
                    setInfoMsg("Cập nhật thành công");

                    setTimeout(() => {
                        nav(`/profile/${currentUser.id}`);
                    }, 3000);
                }
            }
        } catch (ex) {
            if (ex.response.status === 401) {
                setMsg(ex.response.data);
            }
            console.error(ex);
        }
    }

    useEffect(() => {
        if (currentUser == null) {
            nav('/login');
        }

        currentUserAvatarUrl = loadAvatar();

    }, [currentUser, files]);

    useEffect(() => {
        loadVehicle();
    }, [])

    return (<>
        {currentUser && <>
            <div class="container emp-profile">
                <form method="post">
                    <div class="row">
                        <div class="col-md-4">
                            <div class="profile-img">
                                <img src={loadAvatar()}
                                    alt="Hình ảnh avatar"
                                    className="img-fluid"
                                    style={{
                                        minHeight: '150px',
                                        maxHeight: '150px',
                                        objectFit: 'contain',
                                        width: '100%',
                                    }} />
                                <div class="file btn btn-lg btn-primary">
                                    Đổi ảnh đại diện
                                    <input type="file" accept=".png,.jpg" ref={files} onChange={e => handleInPersonImgChange(e)} />
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="profile-head">
                                <h5>
                                    {currentUser.name}
                                </h5>
                                <h6>
                                    Thông tin cá nhân
                                </h6>
                                <p class="proile-rating"><span>Mô tả profile...</span></p>

                                <ul class="nav nav-tabs" id="myTab" role="tablist">
                                    <li class="nav-item">
                                        <a className={`nav-link ${activeTab === 'home' ? 'active' : ''}`}
                                            id="home-tab"
                                            data-toggle="tab"
                                            role="tab"
                                            aria-controls="home"
                                            aria-selected="true"
                                            onClick={() => setActiveTab('home')}
                                        >Bản thân</a>
                                    </li>
                                    <li class="nav-item">
                                        <a
                                            className={`nav-link ${activeTab === 'edit' ? 'active' : ''}`}
                                            id="edit-tab"
                                            onClick={() => setActiveTab('edit')}
                                            data-toggle="tab"
                                            href="#edit"
                                            role="tab"
                                            aria-controls="edit"
                                            aria-selected="false">Chỉnh sửa</a>
                                    </li>
                                </ul>

                            </div>
                        </div>
                        <div class="col-md-2">
                            <input class="profile-edit-btn" name="btnAddMore" value="Thay đổi profile" />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-4">
                            <div class="profile-work">
                                <p>TÁC VỤ</p>
                                <a href="#">Bảo mật</a><br />
                                <Link onClick={() => dispatch({
                                    "type": "logout"
                                })}>Đăng xuất</Link><br />
                            </div>
                        </div>
                        <div class="col-md-8">
                            <div class="tab-content profile-tab" id="myTabContent">
                                <div className={`tab-pane fade ${activeTab === 'home' ? 'show active' : ''}`} id="home" role="tabpanel" aria-labelledby="home-tab">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <label>Tên người dùng</label>
                                        </div>
                                        <div class="col-md-6">
                                            <p>{currentUser.name}</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <label>Tài khoản người dùng</label>
                                        </div>
                                        <div class="col-md-6">
                                            <p>{currentUser.username}</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <label>Email</label>
                                        </div>
                                        <div class="col-md-6">
                                            <p>{currentUser.email}</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <label>Điện thoại</label>
                                        </div>
                                        <div class="col-md-6">
                                            <p>{currentUser.phone}</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <label>Địa chỉ người dùng</label>
                                        </div>
                                        <div class="col-md-6">
                                            <p>{currentUser.address}</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <label>Số CMND</label>
                                        </div>
                                        <div class="col-md-6">
                                            <p>{currentUser.identityNumber}</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <label>Vai trò</label>
                                        </div>
                                        <div class="col-md-6">
                                            <p>{role[currentUser.role.id]}</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="tab-pane fade" id="profile" role="tabpanel" aria-labelledby="profile-tab">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <label>Experience</label>
                                        </div>
                                        <div class="col-md-6">
                                            <p>Expert</p>
                                        </div>
                                    </div>

                                </div>

                                <div className={`tab-pane fade ${activeTab === 'edit' ? 'show active' : ''}`} id="edit" role="tabpanel" aria-labelledby="edit-tab">
                                    {msg && (
                                        <Alert variant="danger" className="alert-danger">
                                            <div>{msg}</div>
                                        </Alert>
                                    )}
                                    {infoMsg && (
                                        <Alert variant="success" className="alert-success">
                                            <div>{infoMsg}</div>
                                        </Alert>
                                    )}
                                    <form>

                                        <div className="col mb-3">
                                            <div className="col-md-12">
                                                <label>Tên người dùng</label>
                                                <input type="text" value={name} onChange={(e) => setName(e.target.value)} className="form-control" />
                                            </div>
                                            <div className="col-md-12">
                                                <label>Điện thoại</label>
                                                <input type="text" value={phone} onChange={(e) => setPhone(e.target.value)} className="form-control" />
                                            </div>
                                            <div className="col-md-12">
                                                <label>Địa chỉ</label>
                                                <input type="text" value={address} onChange={(e) => setAddress(e.target.value)} className="form-control" />
                                            </div>
                                            <div className="col-md-12">
                                                <label>Căn cước công dân</label>
                                                <input type="text" value={identityNumber} onChange={(e) => setIdentityNumber(e.target.value)} className="form-control" />
                                            </div>
                                        </div>
                                        <div className="row mb-4">
                                            <div className="col-md-12">
                                                <label>Mật khẩu xác nhận</label>
                                                <input type="password" value={confirm} onChange={(e) => setConfirm(e.target.value)} className="form-control" required />
                                            </div>
                                        </div>
                                        <div className="text-center">
                                            <button type="submit" onClick={(e) => submitChange(e)} className="btn btn-primary">Xác nhận thay đổi</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                            <h4 className="mb-4">🚗 Phương Tiện Đăng Ký</h4>
                            <div className="table-responsive">
                                <table className="table table-striped table-hover text-center align-middle">
                                    <thead className="table-primary">
                                        <tr>
                                            <th>Tên phương tiện</th>
                                            <th>Biển số xe</th>
                                            <th>Loại phương tiện</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {vehicleList.length > 0 ? (
                                            vehicleList.map((vehicle, index) => (
                                                <tr key={index}>
                                                    <td>{vehicle.name}</td>
                                                    <td>{vehicle.plateLicense}</td>
                                                    <td>
                                                        {vehicle.type.id === 1
                                                            ? '4 bánh'
                                                            : vehicle.type.id === 2
                                                                ? '2 bánh'
                                                                : 'Không xác định'}
                                                    </td>
                                                </tr>
                                            ))
                                        ) : (
                                            <tr>
                                                <td colSpan="3" className="text-muted">Chưa có phương tiện nào.</td>
                                            </tr>
                                        )}
                                    </tbody>
                                </table>
                            </div>


                        </div>
                    </div>
                </form>
            </div>
        </>}

        <Modal show={showSuccessModal} onHide={closeSuccess}>
            <Modal.Body className="text-center">
                <h2>{inform}</h2>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={closeSuccess}>
                    Đóng
                </Button>
            </Modal.Footer>
        </Modal>
    </>);
}

export default UserProfile;