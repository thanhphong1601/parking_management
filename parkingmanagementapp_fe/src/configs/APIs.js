import axios from "axios";
import cookie from 'react-cookies'


const BASE_URL = 'http://localhost:8080/';

export const endpoints = {
    'analyze-plate': "/api/analyzePlate",
    'login': '/api/auth/authenticate',
    'google-login-success': '/api/auth/oauth2/success',
    'current-user': '/api/auth/current-user',
    'register': '/api/auth/register',
    'account-active-check': '/api/auth/account/active/check',
    'verify-mail': (email) => `/api/verifyMail/${email}`,
    'forget-pass-verify-mail': (email) => `/api/forgetPassword/verifyMail/${email}`,
    'verify-otp': (otp, email) => `/api/verifyOtp/${otp}/${email}`,
    'active-user': (email) => `/api/auth/user/${email}/activate`,
    'check-mail-and-identity-number': (email, identityNumber, username) => `/api/auth/user/check/${email}/${identityNumber}/${username}`,
    'check-mail-valid': '/api/auth/user/email/check',
    'forget-password': '/api/auth/forgetPassword',
    'password-check': (userId) => `/api/auth/account/${userId}/password/check`,
    'user-info-update': (userId) => `/api/auth/account/${userId}/info/change`,
    'user-avatar-change': '/api/auth/account/avatar/change',
    'customer-list': '/api/customer/list',
    'customer-list-all': '/api/customer/list/all',
    'security-list': '/api/security/list',
    'customer-info': (customerId) => `/api/customer/${customerId}`,
    'customer-deactivate': (customerId) => `/api/customer/${customerId}/deactivate`,
    'customer-create': '/api/customer/create',
    'vehicle-list-user': (customerId) => `/api/user/${customerId}/vehicle/list`,
    'vehicle-types': '/api/vehicle/types',
    'vehicle-create': '/api/vehicle/create',
    'vehicle-create-with-user': (uid) => `/api/vehicle/${uid}/create`,
    'vehicle-clear': '/api/vehicle/blank/clear',
    'vehicle-list': '/api/vehicle/list/blank',
    'vehicle-list-choice': '/api/vehicle/list',
    'ticket-list': '/api/ticket/list',
    'floor-list': '/api/floor/list',
    'floor-dto-list': '/api/floorDTO/list',
    'line-list': '/api/line/list',
    'line-list-all': '/api/line/list/all',
    'position-list': '/api/position/list',
    'position-list-all': '/api/position/list/all',
    'position-info-find-by-ticketId': (ticketId) => `/api/ticket/${ticketId}/position/get`,
    'position-state-update-vehicle-in': (positionId) => `/api/position/${positionId}/updateState/in`,
    'position-state-update-vehicle-out': (positionId) => `/api/position/${positionId}/updateState/out`,
    'position-plate-check': (positionId) => `/api/position/${positionId}/plate/check`,
    'ticket-price-list': '/api/ticket/price/list',
    'ticket-price-getPrice': '/api/ticket/price/totalPrice',
    'ticket-type-list': '/api/ticket/type/list',
    'ticket-create': '/api/ticket/create',
    'ticket-delete': (ticketId) => `/api/ticket/${ticketId}/delete`,
    'ticket-create-anonymous': '/api/ticket/create/anonymous',
    'ticket-get-newest': '/api/ticket/new/get',
    'ticket-fast-pay': '/api/security/ticket/paid',
    'admin-ticket-status-update-all': '/api/ticket/get/all/active/update',
    'customer-ticket-status-update-all': (customerId) => `/api/customer/${customerId}/ticket/get/all/active/update`,
    'recognize-in': '/api/recognize/in/get',
    'recognize-in-save': '/api/recognize/in',
    'entry-in-list': '/api/recognize/out/list',
    'entry-in-list-pageable': '/api/entry/list',
    'entry-all-list-pageable': '/api/entry/all/list',
    'ticketId-get-by-entry': (entryId) => `/api/entry/${entryId}/ticket/getTicketId`,
    'recognize-out': '/api/recognize/out/get',
    'recognize-out-save': '/api/recognize/out',
    'entry-ticket-process': '/api/entry/ticket/process',
    'entry-record-in': '/api/recognize/in/record',
    'entry-ticket-process-with-entry-info': '/api/entry/ticket/get/all',
    'entry-record-out': '/api/recognize/out/record',
    'entry-ticket-image-text-extract': '/api/entry/image/ticket/get',
    'count-by-date': '/api/count-by-date',
    'count-by-month': '/api/count-by-month',
    'avarage-parking-duration': '/api/average-parking-duration',
    'create-payment': '/api/payment/createPayment',
    'payment-success': '/api/successPayment',
    'receipt-create': '/api/receipt/create',
    'receipt-list': '/api/receipt/list',
    'receipt-customer-list': (customerId) => `/api/customer/${customerId}/receipt/list`,
    'fast-payment': '/api/pay/fast-payment'
};

export const customerEndpoints = {
    'customer-ticket-list': (userId) => `/api/customerr/ticket/${userId}/list`,
};


// console.info(cookie.load('token'))

// export const authApi = () => {
//     return axios.create({
//         baseURL: BASE_URL,
//         headers: {
//             'Authorization': `Bearer ${cookie.load('token')}`
//         }
//     })
// }

export const authApi = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`
        }
    })
}

export default axios.create({
    baseURL: BASE_URL
});