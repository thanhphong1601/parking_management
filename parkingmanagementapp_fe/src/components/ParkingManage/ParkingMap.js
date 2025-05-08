import { useContext, useEffect, useRef, useState } from "react";
import { authApi, endpoints } from "../../configs/APIs";
import { Button, Modal, Spinner } from "react-bootstrap";
import ParkingGrid from "./ParkingGrid";
import "./ParkingMap.css"
import PositionModal from "./PositionModal";
import { useLocation, useNavigate } from "react-router-dom";
import { MyUserContext } from "../../configs/Contexts";

const ParkingMap = () => {
    const currentUser = useContext(MyUserContext);
    const nav = useNavigate();
    const location = useLocation();
    const currentPath = location.pathname + location.search;
    if (currentUser == null) {
        nav(`/login?continue=${encodeURIComponent(currentPath)}`);
    }
    const [floors, setFloors] = useState([]);


    //load data funcs
    const loadFloors = async () => {
        try {
            let res = await authApi().get(endpoints['floor-dto-list']);

            if (res.status == 200) {
                setFloors(res.data);
            }
        } catch (ex) {
            console.error(ex);
        }
    };


    useEffect(() => {
        loadFloors();
    }, []);

    return (<>
        <div className="parking-layout-wrapper">
            <h1 className="layout-title">Sơ đồ bãi đậu xe</h1>

            <div className="floor-list">
                {floors.map(floor => (
                    <div key={floor.id} className="floor-section">
                        <ParkingGrid floor={floor} />
                    </div>
                ))}
            </div>
        </div>


    </>
    );
};

export default ParkingMap;