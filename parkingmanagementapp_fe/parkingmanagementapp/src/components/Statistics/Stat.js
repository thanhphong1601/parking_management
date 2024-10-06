import { useEffect, useState } from "react";
import { authApi } from "../../configs/APIs";
import { Bar } from "react-chartjs-2";

const Stat = () => {
    // const [dayData, setDayData] = useState(null);
    // const [monthData, setMonthData] = useState(null);
    // const [averageParkingDuration, setAverageParkingDuration] = useState(null);

    // useEffect(() => {
    //     // Fetch count by date
    //     authApi().get('/api/count-by-date?date=2024-10-01')
    //         .then(response => {
    //             setDayData({
    //                 labels: ["2024-10-01"],
    //                 datasets: [
    //                     {
    //                         label: 'Số xe ra vào trong ngày',
    //                         data: [response.data.count],
    //                         backgroundColor: 'rgba(75, 192, 192, 0.6)',
    //                     }
    //                 ]
    //             });
    //         }).catch(error => {
    //             console.error("Error fetching day data:", error);
    //             setDayData(null);  // Đảm bảo dữ liệu không bị undefined
    //         });;

    //     // Fetch count by month
    //     authApi().get('/api/count-by-month?month=10&year=2024')
    //         .then(response => {
    //             setMonthData({
    //                 labels: ["Tháng 10"],
    //                 datasets: [
    //                     {
    //                         label: 'Số xe ra vào trong tháng',
    //                         data: [response.data.count],
    //                         backgroundColor: 'rgba(153, 102, 255, 0.6)',
    //                     }
    //                 ]
    //             });
    //         }).catch(error => {
    //             console.error("Error fetching month data:", error);
    //             setMonthData(null);  // Đảm bảo dữ liệu không bị undefined
    //         });;

    //     // Fetch average parking duration
    //     authApi().get('/api/entry-history/average-parking-duration')
    //         .then(response => {
    //             setAverageParkingDuration(response.data.averageDuration);
    //         }).catch(error => {
    //             console.error("Error fetching average parking duration:", error);
    //             setAverageParkingDuration(null);
    //         });;
    // }, []);

    // return (
    //     <div>
    //         <div>
    //             <h3>Thống kê xe ra vào theo ngày</h3>
    //             {dayData ? (
    //                 <Bar data={dayData} options={{ scales: { y: { beginAtZero: true } } }} />
    //             ) : (
    //                 <p>Không có dữ liệu cho ngày này</p>
    //             )}
    //         </div>

    //         <div>
    //             <h3>Thống kê xe ra vào theo tháng</h3>
    //             {monthData ? (
    //                 <Bar data={monthData} options={{ scales: { y: { beginAtZero: true } } }} />
    //             ) : (
    //                 <p>Không có dữ liệu cho tháng này</p>
    //             )}
    //         </div>

    //         <div>
    //             <h3>Thời gian đỗ xe trung bình</h3>
    //             {averageParkingDuration !== null ? (
    //                 <p>{averageParkingDuration ? averageParkingDuration.toFixed(2) : 'N/A'} phút</p>
    //             ) : (
    //                 <p>Đang tải...</p>
    //             )}
    //         </div>
    //     </div>
    // );
    return <>
        <h1>Thống kê</h1>
    </>
}

export default Stat;