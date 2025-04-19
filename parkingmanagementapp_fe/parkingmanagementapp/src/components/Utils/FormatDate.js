import { format } from "date-fns";

export const formatDate = (isoDateString) => {
    const dateObj = new Date(isoDateString);
    return dateObj.toLocaleDateString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
};

export const formatDateWithHour = (dateStr) => {
    const normalizedDateStr = dateStr.replace('ICT', 'GMT+0700');
    const date = new Date(normalizedDateStr);
    if (isNaN(date)) {
        return 'Invalid date'; // Xử lý lỗi nếu có
    }
    return format(date, 'dd/MM/yyyy HH:mm:ss');
};

//dd/MM/yyyy HH:mm:ss
export const getDayNowWithFormat = () => {
    const now = new Date();
    const formatted = format(now, 'dd/MM/yyyy HH:mm:ss');

    return formatted;
}