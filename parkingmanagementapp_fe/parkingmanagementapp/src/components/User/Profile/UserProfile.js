import { useContext, useEffect } from 'react';
import './UserProfile.css'
import { MyDispatchContext, MyUserContext } from '../../../configs/Contexts';
import { Link, useNavigate } from 'react-router-dom';

const UserProfile = () => {
    const currentUser = useContext(MyUserContext);
    const dispatch = useContext(MyDispatchContext);
    const nav = useNavigate();

    const role = {
        1: "Quản trị viên",
        2: "Nhân viên bảo vệ",
        3: "Khách hàng"
    };


    useEffect(() => {
        if (currentUser == null) {
            console.info("hell");
            nav('/login');
        }
    }, [currentUser]);

    return (<>
        {currentUser && <>
            <div class="container emp-profile">
                <form method="post">
                    <div class="row">
                        <div class="col-md-4">
                            <div class="profile-img">
                                <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS52y5aInsxSm31CvHOFHWujqUx_wWTS9iM6s7BAm21oEN_RiGoog" alt="" />
                                <div class="file btn btn-lg btn-primary">
                                    Đổi ảnh đại diện
                                    <input type="file" name="file" />
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
                                        <a class="nav-link active" id="home-tab" data-toggle="tab" href="#home" role="tab" aria-controls="home" aria-selected="true">Bản thân</a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link" id="profile-tab" data-toggle="tab" href="#profile" role="tab" aria-controls="profile" aria-selected="false">Khác</a>
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
                                <div class="tab-pane fade show active" id="home" role="tabpanel" aria-labelledby="home-tab">
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
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </>}

    </>);
}

export default UserProfile;