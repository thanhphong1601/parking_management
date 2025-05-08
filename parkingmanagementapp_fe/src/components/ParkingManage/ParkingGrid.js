import React, { useContext, useState } from 'react';
import './ParkingGrid.css';
import PositionModal from './PositionModal';
import { useLocation, useNavigate } from 'react-router-dom';
import { MyUserContext } from '../../configs/Contexts';

const ParkingGrid = ({ floor }) => {
    const currentUser = useContext(MyUserContext);
    const nav = useNavigate();
    const location = useLocation();
    const currentPath = location.pathname + location.search;
    if (currentUser == null) {
        nav(`/login?continue=${encodeURIComponent(currentPath)}`);
    }
    const [selectedPosition, setSelectedPosition] = useState(null);

    //modal position info
    const [showPositionModal, setShowPositionModal] = useState(false);
    const handleClosePositionModal = () => {
        setShowPositionModal(false);
    }

    //process funcs
    const handlePositionClick = (pos) => {
        setSelectedPosition(pos);
        setShowPositionModal(true);
    };

    return (<>
        <div className="parking-floor">
            <h2>Tầng {floor.floorNumber}</h2>
            {floor.lines.map(line => (
                <div key={line.id} className="parking-line">
                    <div className="line-label">Dãy {line.line}</div>
                    <div className="position-row">
                        {line.positions.map(pos => (
                            <div
                                key={pos.id}
                                className={`position-cell ${pos.status.toLowerCase()}`}
                                title={`Vị trí ${pos.position} (${pos.status})`}
                                onClick={() => handlePositionClick(pos)}
                            >
                                {pos.position}
                            </div>
                        ))}
                    </div>
                </div>
            ))}
        </div>
        {selectedPosition && (
            <PositionModal
                show={showPositionModal}
                handleClose={() => handleClosePositionModal()}
                position={selectedPosition}
            />
        )}
    </>
    );
};

function handleClick(position) {
    if (position.status === 'AVAILABLE') {
        alert(`Chọn vị trí ${position.position}`);
        // hoặc gọi callback chọn chỗ / mở modal
    }
}

export default ParkingGrid;
