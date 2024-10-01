import axios from "axios";
import cookie from 'react-cookies'


const BASE_URL = 'http://localhost:8080/';

export const endpoints = {
    'login': '/api/auth/authenticate',
    'current-user': '/api/auth/current-user',
    'register': '/api/auth/register',
    'customer-list': '/api/customer/list',
    'customer-info': (customerId) => `/api/customer/${customerId}`
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