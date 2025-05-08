import { useContext, useEffect, useState } from "react";
import { MyUserContext } from "../../../configs/Contexts";
import { useLocation, useNavigate } from "react-router-dom";
import { authApi, endpoints } from "../../../configs/APIs";
import { Button, Modal } from "react-bootstrap";

const AddVehicle = () => {
    const currentUser = useContext(MyUserContext);
    const nav = useNavigate();

    const location = useLocation();
    const currentPath = location.pathname + location.search;
    if (currentUser == null) {
        nav(`/login?continue=${encodeURIComponent(currentPath)}`);
    }

    const [vehicleList, setVehicleList] = useState([]);
    const [types, setTypes] = useState([]);
    const [vehicleName, setVehicleName] = useState("");
    const [plateLicense, setPlateLicense] = useState("");
    const [type, setType] = useState(null);
    const [updateVehicles, setUpdateVehicles] = useState(0);

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

    const loadTypes = async () => {
        try {

            let res = await authApi().get(endpoints['vehicle-types']);

            if (res.status === 200) {
                setTypes(res.data);
            }
        } catch (ex) {
            console.error(ex);
        }
    }

    const createVehicle = async () => {

        let form = new FormData();
        form.append("name", vehicleName);
        form.append("plateLicense", plateLicense);
        form.append("type", type);

        try {
            let url = `${endpoints['vehicle-create-with-user'](currentUser.id)}`;
            let res = await authApi().post(url, form);

            if (res.status === 200) {
                setUpdateVehicles(current => current + 1);
                cancelConfirmModal();
            }
        } catch (ex) {
            console.error(ex);
        }
    }

    //modal confirm
    const [showConfirm, setShowConfirm] = useState(false);
    const showConfirmModal = () => {
        setShowConfirm(true);
    }
    const cancelConfirmModal = () => {
        setShowConfirm(false);
    }

    useEffect(() => {
        loadVehicle();
    }, [updateVehicles])

    useEffect(() => {
        loadTypes();
    }, [])


    return (<>
        <div class="container mt-5">
            <h4 class="mb-4">📝 Đăng ký phương tiện mới</h4>
            <div class="table-responsive mb-5">
                <table class="table table-striped table-hover text-center align-middle">
                    <thead class="table-primary">
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
                                    <td>{vehicle.type.id === 1
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

            <form className="p-4 rounded shadow-sm bg-light">
                <div className="row g-3">
                    <div className="col-md-6">
                        <label className="form-label">Tên phương tiện</label>
                        <input type="text" className="form-control" value={vehicleName} onChange={(e) => setVehicleName(e.target.value)} required />
                    </div>

                    <div className="col-md-6">
                        <label className="form-label">Biển số xe</label>
                        <input type="text" className="form-control" value={plateLicense} onChange={(e) => setPlateLicense(e.target.value)} required />
                    </div>

                    <div className="col-md-6">
                        <label className="form-label">Loại phương tiện</label>
                        <select className="form-select" value={type} onChange={(e) => setType(e.target.value)} required>
                            <option value="">-- Chọn loại --</option>
                            {types.map((type) => (
                                <option key={type.id} value={type.id}>{type.type}</option>
                            ))}
                        </select>
                    </div>

                    <div className="col-12 text-center">
                        <button type="button" onClick={() => showConfirmModal()} className="btn btn-success mt-3 w-50">Đăng ký phương tiện</button>
                    </div>
                </div>
            </form>
        </div>

        <Modal show={showConfirm} onHide={cancelConfirmModal} size="lg" className="custom-modal">
            <Modal.Header closeButton className="modal-header modal-header-info">
                <Modal.Title>Xác nhận tạo phương tiện</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <h1 className='fw-bold text-center'>Bạn có chắc chắn muốn tạo phương tiện này?</h1>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="info" onClick={createVehicle}>
                    CHẮC CHẮN
                </Button>
                <Button variant="secondary" onClick={cancelConfirmModal}>
                    SUY NGHĨ THÊM
                </Button>
            </Modal.Footer>
        </Modal>



    </>);
}

export default AddVehicle;