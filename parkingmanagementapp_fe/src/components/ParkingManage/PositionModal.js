import { useRef, useState } from "react";
import { Button, Modal, Spinner } from "react-bootstrap";
import { authApi, endpoints } from "../../configs/APIs";
import "./PositionModal.css"

const PositionModal = ({ show, handleClose, position }) => {
    const files = useRef(); //license plate image
    const [image, setImage] = useState(null);
    const [msg, setMsg] = useState("");
    const [showLoading, setShowLoading] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    //loading modal
    const closeLoading = () => {
        setShowLoading(false);
    }


    const handleImageChange = async (e, position) => {
        e.preventDefault();
        let form = new FormData();

        const file = e.target.files[0];
        if (file) {
            setImage(URL.createObjectURL(file));

            form.append("file", file);
        }

        console.info(position);


        if (position.plateImgUrl != null) {
            setMsg("");
            setShowLoading(true);
            setIsLoading(true);
            try {
                let url = `${endpoints['position-plate-check'](position.id)}`;
                let res = await authApi().post(url, form);

                if (res.status == 200) {
                    setMsg(res.data == true ? "Biển số đúng" : "Phát hiện sai biển số đã đăng ký với vị trí hiện tại");
                    setIsLoading(false);
                }
            } catch (ex) {
                if (ex.response.status == 404)
                    console.error(ex.response.data);
                console.error(ex);

            } finally {
                setIsLoading(false);
            }
        };
    };

    const handleCloseModal = () => {
        handleClose();
        setTimeout(() => {
            setImage(null);

        }, 1000);

    };

    return (<>
        <Modal show={show} onHide={handleCloseModal} centered size="lg">
            <Modal.Header closeButton>
                <Modal.Title>Thông tin vị trí #{position.id}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p><strong>Vị trí:</strong> {position.position}</p>
                <p><strong>Trạng thái:</strong> {position.status}</p>

                {/* Vùng hình ảnh */}
                <div
                    className="image-upload-zone"
                    onClick={() => document.getElementById('image-upload').click()}
                    style={{
                        
                        marginTop: '1rem',
                        cursor: 'pointer',
                        border: '2px dashed #ccc',
                        padding: '1rem',
                        borderRadius: '10px',
                        textAlign: 'center',
                        backgroundColor: '#f9f9f9'
                    }}
                >
                    {position.plateImgUrl || image ? (
                        <img
                            src={image !== null ? image : position.plateImgUrl}
                            alt="Hình ảnh biển xe"
                            style={{ width: '100%', maxHeight: '500px', minHeight: '200px', objectFit: 'contain' }}
                        />
                    ) : (
                        <p>Nhấn vào đây để chọn ảnh</p>
                    )}
                    <input
                        type="file"
                        id="image-upload"
                        style={{ display: 'none' }}
                        accept=".png,.jpg"
                        onChange={(e) => handleImageChange(e, position)}
                        ref={files}
                    />
                </div>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>Đóng</Button>
                <Button variant="primary">Lưu</Button>
            </Modal.Footer>
        </Modal>

        <Modal show={showLoading} onHide={closeLoading}>
            <Modal.Body className="text-center">
                {isLoading ? (
                    <>
                        <Spinner animation="border" role="status">
                            <span className="visually-hidden">Đang tải</span>
                        </Spinner>
                        <p>Đang tiến hành phân tích thông tin...</p>
                    </>
                ) : <>
                    <div className="modal-title">
                        <div className="modal-title-dash" />
                        <h1><strong>Kết quả</strong></h1>
                        <div className="modal-title-dash" />
                    </div>
                    <h2>{msg}</h2>
                </>}
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={closeLoading}>
                    Đóng
                </Button>
            </Modal.Footer>
        </Modal>
    </>
    );

}

export default PositionModal;
