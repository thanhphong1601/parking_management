import './AddCustomer.css'

const AddCustomer = () => {
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
    }, {
        label: "Tài khoản",
        type: "text",
        field: "username",
        placeholder: "Tài khoản của bạn"
    }, {
        label: "Mật khẩu",
        type: "password",
        field: "password",
        placeholder: "Mật khẩu của bạn"
    }, {
        label: "Địa chỉ nhà",
        type: "text",
        field: "address",
        placeholder: "Địa chỉ nhà"
    }, {
        label: "Ngày sinh",
        type: "date",
        field: "birthday",
        placeholder: "Ngày sinh"
    }];

    return (
        <div className="containerForm">
            <h2>Thêm Khách Hàng</h2>
            <form className="user-form">
                <div className="form-row">
                    <div className="form-group">
                        <label>Username<span className="required">*</span></label>
                        <input type="text" className="form-control" />
                    </div>
                    <div className="form-group">
                        <label>Password<span className="required">*</span></label>
                        <input type="password" className="form-control" />
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label>Họ và tên<span className="required">*</span></label>
                        <input type="text" className="form-control" />
                    </div>
                    <div className="form-group">
                        <label>CMND/CCCD<span className="required">*</span></label>
                        <input type="text" className="form-control" />
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label>Ngày sinh<span className="required">*</span></label>
                        <input type="date" className="form-control" />
                    </div>
                    <div className="form-group">
                        <label>Số điện thoại<span className="required">*</span></label>
                        <input type="text" className="form-control" />
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label>Email<span className="required">*</span></label>
                        <input type="email" className="form-control" />
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group address">
                        <label>Địa chỉ<span className="required">*</span></label>
                        <input type="text" className="form-control" />
                    </div>
                </div>

                <div className="table-section">
                    <table className="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Address</th>
                                {/* Các cột khác mà bạn muốn thêm */}
                            </tr>
                        </thead>
                        <tbody>
                            {/* Hiển thị dữ liệu người dùng tại đây */}
                        </tbody>
                    </table>
                </div>

                <div className="form-footer">
                    <button type="button" className="btn btn-secondary modal-btn">Mở Modal</button>
                    <button type="submit" className="btn btn-primary save-btn">Lưu thông tin</button>
                </div>
            </form>
        </div>
    );
};

export default AddCustomer;