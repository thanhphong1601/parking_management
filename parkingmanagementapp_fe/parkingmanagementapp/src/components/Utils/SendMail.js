import APIs, { endpoints } from "../../configs/APIs";


export const sendMail = async (email) => {
    let res = await APIs.post(endpoints['verify-mail', email]);
};