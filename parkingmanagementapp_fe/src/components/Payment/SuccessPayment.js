import { useContext, useEffect, useRef, useState } from "react";
import { FaCheck } from "react-icons/fa";
import { VscError } from "react-icons/vsc";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import APIs, { authApi, endpoints } from "../../configs/APIs";
import { MyDispatchContext, MyUserContext } from "../../configs/Contexts";
import "./SuccessPayment.css"

const SuccessPayment = () => {
    const [searchParams] = useSearchParams();
    const dispatch = useContext(MyDispatchContext);
    const [ticketId, setTicketId] = useState(null);
    const currentUser = useContext(MyUserContext);

    const navigate = useNavigate();

    const status = searchParams.get("vnp_ResponseCode");
    const success = status === "00";

    const info = {
        amount: Number(searchParams.get("vnp_Amount") || 0) / 100,
        bankCode: searchParams.get("vnp_BankCode"),
        bankTranNo: searchParams.get("vnp_BankTranNo"),
        cardType: searchParams.get("vnp_CardType"),
        orderInfo: decodeURIComponent(searchParams.get("vnp_OrderInfo") || "").replace(/\+/g, " "),
        payDate: searchParams.get("vnp_PayDate"),
        transactionNo: searchParams.get("vnp_TransactionNo"),
        ticketId: searchParams.get("ticketId"),
    };

    const reloadUser = async () => {
        try {
            let u = await authApi().get(endpoints['current-user']);

            dispatch({
                "type": "login",
                "payload": u.data
            });

        } catch (ex) {
            console.error(ex);
        }
    };

    const updateTicketState = async () => {
        let form = new FormData();
        form.append("id", info.ticketId);
        form.append("transactionDate", formatPayDate(info.payDate));
        form.append("transactionNumber", info.transactionNo);
        form.append("content", info.orderInfo);

        for (let [key, value] of form.entries()) {
            console.info(`${key}: ${value}`);
        }

        try {
            let res = await APIs.post(endpoints['payment-success'], form);
            let resReceiptCreate = await APIs.post(endpoints['receipt-create'], form);
        } catch (ex) {
            console.error(ex);
        }
    };

    const formatPayDate = (raw) => {
        if (!raw || raw.length !== 14) return raw;
        return `${raw.slice(6, 8)}/${raw.slice(4, 6)}/${raw.slice(0, 4)} ${raw.slice(8, 10)}:${raw.slice(10, 12)}:${raw.slice(12, 14)}`;
    };

    // useEffect(() => {
    //     if (success) {
    //         updateTicketState();
    //     }
    // }, [success]);

    const hasUpdated = useRef(false);

    useEffect(() => {
        if (success && !hasUpdated.current) {
            updateTicketState();
            hasUpdated.current = true;
        }
    }, [success]);

    useEffect(() => {
        const timer = setTimeout(() => {
            reloadUser();
        }, 200);

        return () => clearTimeout(timer);
    }, []);

    return (
        <div className="payment-container">
            <div className={`payment-status ${success ? "success" : "failed"}`}>
                <h2>{success ? "🎉 Thanh toán thành công!" : "❌ Thanh toán thất bại!"}</h2>
                <p>
                    {success
                        ? "Cảm ơn bạn đã sử dụng dịch vụ. Thông tin thanh toán của bạn được hiển thị bên dưới."
                        : "Giao dịch không thành công. Vui lòng thử lại hoặc liên hệ hỗ trợ nếu cần thiết."}
                </p>
            </div>

            <div className="payment-bill">
                <h3>Chi tiết giao dịch</h3>
                <div className="bill-item"><span>💳 Số tiền:</span> <strong>{info.amount.toLocaleString()} VNĐ</strong></div>
                <div className="bill-item"><span>🏦 Ngân hàng:</span> {info.bankCode}</div>
                <div className="bill-item"><span>🔐 Loại thẻ:</span> {info.cardType}</div>
                <div className="bill-item"><span>🧾 Mã giao dịch:</span> {info.transactionNo}</div>
                <div className="bill-item"><span>📄 Mã ngân hàng:</span> {info.bankTranNo}</div>
                <div className="bill-item"><span>🕓 Thời gian:</span> {formatPayDate(info.payDate)}</div>
                <div className="bill-item"><span>📝 Nội dung:</span> {info.orderInfo}</div>
            </div>

            <div className="payment-action">
                <button onClick={() => navigate(`/customer/${currentUser.id}/ticket/list`)}>Quay lại trang chính</button>
            </div>
        </div>
    );
}

export default SuccessPayment;