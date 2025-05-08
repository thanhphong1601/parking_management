import React, { useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import APIs, { authApi, endpoints } from '../../../configs/APIs';
import { MyDispatchContext } from '../../../configs/Contexts';

const GoogleLoginSuccess = () => {
  const navigate = useNavigate();
  const dispatch = useContext(MyDispatchContext);
  const nav = useNavigate();


  useEffect(() => {
    const fetchToken = async () => {
      try {
        let res = await APIs.get(endpoints['google-login-success'], {
          withCredentials: true, // nếu cần cookie/session
        });

        const token = res.data.token;

        if (token) {
          localStorage.setItem("token", token);

          let u = await authApi().get(endpoints['current-user']);
          if (u.status == 200) {
            dispatch({
              "type": "login",
              "payload": u.data
            })

            if (u.data.role.id == 3) {
              res = await authApi().put(endpoints['customer-ticket-status-update-all'](u.data.id));
            } else {
              res = await authApi().put(endpoints['admin-ticket-status-update-all']);
            }
            nav("/");


          }
        } else {
          throw new Error('Không nhận được token');
        }
      } catch (error) {
        console.error('Lỗi khi nhận JWT:', error);
        navigate('/login');
      }
    };

    fetchToken();
  }, [navigate]);

  return <div>Đang xác thực Google... vui lòng chờ...</div>;
};

export default GoogleLoginSuccess;
