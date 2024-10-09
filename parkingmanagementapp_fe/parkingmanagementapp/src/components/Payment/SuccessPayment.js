import { useContext, useEffect, useState } from "react";
import { FaCheck } from "react-icons/fa";
import { VscError } from "react-icons/vsc";
import { Link, useSearchParams } from "react-router-dom";
import { authApi, endpoints } from "../../configs/APIs";
import { MyDispatchContext } from "../../configs/Contexts";

const SuccessPayment = () => {
    const [searchParams] = useSearchParams();
    const dispatch = useContext(MyDispatchContext);
    const [ticketId, setTicketId] = useState(null);


    const resultCode = searchParams.get('vnp_ResponseCode');
    const content = {
        title: 'Thất bại',
        icon: () => <VscError color={'#E74033'} size={180} />,
        message: 'Đã có lỗi xãy ra! Vui lòng thử lại',
    };
    if (resultCode === '00') {
        content['title'] = 'Thành công';
        content['icon'] = () => <FaCheck size={180} color="#34A853" />;
        content['message'] = 'Thanh toán của bạn đã hoàn tất';
    }

    const reloadUser = async () => {
        try {
            let u = await authApi().get(endpoints['current-user']);        

            dispatch({
                "type": "login",
                "payload": u.data
            });

            setTicketId(localStorage.getItem("ticketId"));

        } catch (ex) {
            console.error(ex);
        }
    };

    const updateTicketState = async () => {
        let form = new FormData();
        form.append("id", ticketId);
        try {
            let res = await authApi().post(endpoints['payment-success'], form);
            let resReceiptCreate = await authApi().post(endpoints['receipt-create'], form);
        } catch (ex) {
            console.error(ex);
        }
    };

    useEffect(() => {
        reloadUser();
    }, [])

    useEffect(() => {
        updateTicketState();
    }, [ticketId])

    return (
        <div className="container d-flex flex-column align-items-center my-5">
            <div className="">
                <h1 className="title text-uppercase fw-bold ">{content['title']}</h1>
            </div>
            <div className="my-5">{content['icon']()}</div>
            <div>
                <p className="fs-1">{content['message']}</p>
            </div>
            <div className="row">
                <Link to={'/ticket/list'} className="btn btn-primary my-2">
                    Quay về trang vé
                </Link>
            </div>
        </div>
    );
}

export default SuccessPayment;