import axios from "axios";
import cookie from 'react-cookies'


const BASE_URL = 'http://localhost:8080/';

export const endpoints = {
    'login': '/api/auth/authenticate',
    'current-user': '/api/auth/current-user',
    'register': '/api/auth/register',
    'customer-list': '/api/customer/list',
    'security-list': '/api/security/list',
    'customer-info': (customerId) => `/api/customer/${customerId}`,
    'customer-deactivate': (customerId) => `/api/customer/${customerId}/deactivate`,
    'customer-create': '/api/customer/create',
    'vehicle-list-user': (customerId) => `/api/user/${customerId}/vehicle/list`,
    'vehicle-types': '/api/vehicle/types',
    'vehicle-create': '/api/vehicle/create',
    'vehicle-clear': '/api/vehicle/blank/clear',
    'vehicle-list': '/api/vehicle/list/blank',
    'vehicle-list-choice': '/api/vehicle/list',
    'ticket-list': '/api/ticket/list',
    'floor-list': '/api/floor/list',
    'line-list': '/api/line/list',
    'line-list-all': '/api/line/list/all',
    'position-list': '/api/position/list',
    'position-list-all': '/api/position/list/all',
    'ticket-price-list': '/api/ticket/price/list',
    'ticket-price-getPrice': '/api/ticket/price/totalPrice',
    'ticket-create': '/api/ticket/create',
    'ticket-delete': (ticketId) => `/api/ticket/${ticketId}/delete`,
    'recognize-in': '/api/recognize/in/get',
    'recognize-in-save': '/api/recognize/in',
    'entry-in-list': '/api/recognize/out/list',
    'recognize-out': '/api/recognize/out/get',
    'recognize-out-save': '/api/recognize/out',
    'count-by-date': '/api/count-by-date',
    'count-by-month': '/api/count-by-month',
    'avarage-parking-duration': '/api/average-parking-duration'
}

// console.info(cookie.load('token'))

export const authApi = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            'Authorization': `Bearer ${cookie.load('token')}`
        }
    })
}

export default axios.create({
    baseURL: BASE_URL
});