import { useContext, useEffect, useState } from 'react';
import './AddCustomer.css'
import { Button, Form, Modal } from 'react-bootstrap';
import { authApi, endpoints } from '../../configs/APIs';
import { useNavigate } from 'react-router-dom';
import { MyUserContext } from '../../configs/Contexts';

const AddCustomer = () => {

    const [user, setUser] = useState({
        "role": 3,
    });
    const [vehicle, setVehicle] = useState({});
    const [vehicles, setVehicles] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [vehicleTypes, setVehicleTypes] = useState([]);
    const [newVehicleCreate, setNewVehicleCreate] = useState(0);
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    
    const nav = useNavigate();

    const currentUser = useContext(MyUserContext);
    if (currentUser === null){
        nav('/login');
    }

    

    const change = (e, field) => {
        setUser(current => {
            return { ...current, [field]: e.target.value }
        })
    };

    const changeVehicle = (e, field) => {
        setVehicle(current => {
            return { ...current, [field]: e.target.value }
        })
    };

    const handleShowModal = (e) => {
        e.preventDefault();

        setShowModal(true);
    };

    const handleCloseModal = () => setShowModal(false);

    const handleShowSuccessModal = () => {
        setShowSuccessModal(true);
    };

    const handleCloseSuccessModal = () => setShowSuccessModal(false);

    const getTypes = async (e) => {
        try {
            let res = await authApi().get(endpoints['vehicle-types']);
            setVehicleTypes(res.data);  
        } catch (ex) {
            console.error(ex);
        }
    };

    const addVehicle = async (e) => {
        e.preventDefault();
        try {
            
            let url = `${endpoints['vehicle-create']}?name=${vehicle["name"]}&plateLicense=${vehicle["plateLicense"]}&type=${vehicle["type"]}`;
            console.info(url);
            let res = await authApi().post(url);

            setNewVehicleCreate(current => current + 1);
        } catch (ex) {
            console.error(ex);
        }
    };

    const getVehicles = async () => {
        try {
            let res = await authApi().get(endpoints['vehicle-list']);
            setVehicles(res.data);
        } catch (ex) {
            console.error(ex);
        }
    };

    const clearVehicle = async () => {
        try {
            let res = await authApi().get(endpoints['vehicle-clear']);
            setVehicles([]);
        } catch (ex) {
            console.error(ex);
        }
    }

    

    const createUser = async (e) => {
        e.preventDefault();
        let form = new FormData();

        form.append("username", user["username"]);
        form.append("password", user["password"]);
        form.append("name", user["name"]);
        form.append("identityNumber", user["identityNumber"]);
        form.append("birthday", user["birthday"]);
        form.append("phone", user["phone"]);
        form.append("email", user["email"]);
        form.append("address", user["address"]);
        form.append("role", user["role"]);

        try {
            let res = await authApi().post(endpoints['customer-create'], form);

            if (res.status === 201) {
                handleShowSuccessModal();
                setTimeout(() => {
                    handleCloseSuccessModal();
                    clearVehicle();
                    nav('/customer/list');
                }, 2000);
            };
        } catch (ex) {
            console.error(ex);
        }
    };

    useEffect(() => {
        getTypes();
    }, []);

    useEffect(() => {
        getVehicles();
    }, [newVehicleCreate]);

    return (
        <div className="containerForm">
            <h2>Thêm Khách Hàng</h2>
            <form onSubmit={createUser} className="user-form">
                <h1 className=''></h1>
                <div className="form-row">
                    <div className="form-group">
                        <label>Username<span className="required">*</span></label>
                        <input required type="text" className="form-control" value={user["username"]} onChange={(e) => change(e, "username")} placeholder='Tài khoản của bạn' />
                    </div>
                    <div className="form-group">
                        <label>Password<span className="required">*</span></label>
                        <input required type="password" className="form-control" value={user["password"]} onChange={(e) => change(e, "password")} placeholder='Mật khẩu của bạn' />
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label>Họ và tên<span className="required">*</span></label>
                        <input required type="text" className="form-control" value={user["name"]} onChange={(e) => change(e, "name")} placeholder='Họ tên của bạn' />
                    </div>
                    <div className="form-group">
                        <label>CMND/CCCD<span className="required">*</span></label>
                        <input maxLength="12" required type="text" className="form-control" value={user["identityNumber"]} onChange={(e) => change(e, "identityNumber")} placeholder='Mã số CMND' />
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label>Ngày sinh<span className="required">*</span></label>
                        <input required type="date" className="form-control" value={user["birthday"]} onChange={(e) => change(e, "birthday")} />
                    </div>
                    <div className="form-group">
                        <label>Số điện thoại<span className="required">*</span></label>
                        <input required maxLength="10" type="text" className="form-control" value={user["phone"]} onChange={(e) => change(e, "phone")} placeholder='Số điện thoại của bạn' />
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label>Email<span className="required">*</span></label>
                        <input required type="email" className="form-control" value={user["email"]} onChange={(e) => change(e, "email")} placeholder='Email của bạn' />
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group address">
                        <label>Địa chỉ<span className="required">*</span></label>
                        <input required type="text" className="form-control" value={user["address"]} onChange={(e) => change(e, "address")} placeholder='Địa chỉ nhà' />
                    </div>
                </div>

                <div className="table-section mb-5">
                    <h2>Danh sách phương tiện</h2>
                    <table className="data-table">

                        <thead>
                            <tr>
                                <th>Tên phương tiện</th>
                                <th>Loại phương tiện</th>
                                <th>Biển số xe</th>
                            </tr>
                        </thead>
                        <tbody>
                        {vehicles.map(v => <tr key={v.id}>
                                    <th>{v.name}</th>
                                    <th>{v.type.type}</th>
                                    <th>{v.plateLicense}</th>
                                </tr>)}
                        </tbody>
                    </table>
                </div>

                <div className="form-footer mb-3">
                    <button type="button" className="btn btn-secondary modal-btn" onClick={handleShowModal}>Thêm phương tiện</button>
                </div>
                <button type="submit" className="btn btn-primary save-btn">Lưu thông tin</button>
            </form>

            <Modal show={showModal} onHide={handleCloseModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Thêm thông tin phương tiện</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={addVehicle}>
                        <Form.Group controlId="formCarName">
                            <Form.Label>Tên xe</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Nhập tên xe"
                                value={vehicle["name"]}
                                onChange={(e) => changeVehicle(e, "name")}
                                required
                            />
                        </Form.Group>

                        <Form.Group controlId="formCarType" className="mt-3">
                            <Form.Label>Loại xe</Form.Label>
                            <Form.Control
                                as="select"
                                value={vehicle["type"]}
                                onChange={(e) => changeVehicle(e, "type")}
                            >
                                {vehicleTypes.map(t => 
                                <option key={t.id} value={t.id}>{t.type}</option>
                                )}
                            </Form.Control>
                        </Form.Group>

                        <Form.Group controlId="formLicensePlate" className="mt-3">
                            <Form.Label>Biển số xe</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Nhập biển số xe"
                                value={vehicle["plateLicense"]}
                                onChange={(e) => changeVehicle(e, "plateLicense")}
                                required
                            />
                        </Form.Group>

                        <Button variant="success" type="submit" className="mt-4 w-100">
                            Lưu
                        </Button>
                    </Form>
                    <div className="table-section mb-5">
                        <h2>Danh sách phương tiện</h2>
                        <table className="data-table">

                            <thead>
                                <tr>
                                    <th>Tên phương tiện</th>
                                    <th>Loại phương tiện</th>
                                    <th>Biển số xe</th>
                                </tr>
                            </thead>
                            <tbody>
                                {vehicles.length !== 0? <>
                                    {vehicles.map(v => <tr key={v.id}>
                                    <th>{v.name}</th>
                                    <th>{v.type.type}</th>
                                    <th>{v.plateLicense}</th>
                                </tr>)}
                                </>: <>
                                    
                                </>}
                                
                            </tbody>
                        </table>
                    </div>
                </Modal.Body>
            </Modal>
            <Modal show={showSuccessModal} onHide={handleCloseSuccessModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Thông báo</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <h1>Tạo User thành công!
                        Đang chuyển về danh sách khách hàng!
                    </h1>
                </Modal.Body>
            </Modal>
        </div>

    );
};

export default AddCustomer;