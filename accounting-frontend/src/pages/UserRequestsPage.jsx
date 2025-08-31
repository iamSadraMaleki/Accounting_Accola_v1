
import React, { useState, useEffect, useCallback } from 'react'; 
import SubmitSellerRequestForm from '../components/SubmitSellerRequestForm.jsx';
import Modal from '../components/common/Modal.jsx';
import apiClient from '../api/axiosConfig.jsx'; 
import { formatDateTime } from '../utils/formatters'; 
import '../pages/AdminRequestPage.css';


const UserRequestsPage = () => {
    const [showRequestForm, setShowRequestForm] = useState(false);
    
    const [myRequests, setMyRequests] = useState([]);
    const [isLoadingHistory, setIsLoadingHistory] = useState(true);
    const [historyError, setHistoryError] = useState('');
   
    const fetchMyRequests = useCallback(async () => {
        setIsLoadingHistory(true);
        setHistoryError('');
        try {
            console.log("[UserRequestsPage] Fetching request history...");
            
            const response = await apiClient.get('/api/requests/my-history');
            console.log("[UserRequestsPage] My requests received:", response.data);
            setMyRequests(response.data || []);
        } catch (err) {
            console.error("Error fetching user request history:", err);
            setHistoryError("خطا در دریافت تاریخچه درخواست‌ها.");
            setMyRequests([]);
        } finally {
            setIsLoadingHistory(false);
        }
    }, []); 

    
    useEffect(() => {
        fetchMyRequests();
    }, [fetchMyRequests]);

    
    const handleRequestSubmitted = () => {
        setShowRequestForm(false);
        fetchMyRequests(); 
       
    };

    
    const renderStatusBadge = (status) => {
        let statusText = status;
        switch (status) {
            case 'PENDING': statusText = 'در حال بررسی'; break;
            case 'APPROVED': statusText = 'تایید شده'; break;
            case 'REJECTED': statusText = 'رد شده'; break;
            default: break;
        }
       
        return (
             <span className={`status-badge status-${status?.toLowerCase()}`}>
                {statusText}
            </span>
        );
    };


    return (
        
        <div className="user-requests-container history-page-container"> 
            <h2>درخواست‌های من</h2>

            <div className="page-actions" style={{ marginBottom: '20px' }}>
                <button onClick={() => setShowRequestForm(true)} className="submit-btn"> 
                    ثبت درخواست جدید (ارتقا به فروشنده)
                </button>
            </div>

           
            <div className="previous-requests transaction-table-container"> 
                <h4>تاریخچه درخواست‌ها</h4>

                {isLoadingHistory && <p>در حال بارگذاری تاریخچه...</p>}
                {historyError && <p className="error-message history-error">{historyError}</p>}

                {!isLoadingHistory && !historyError && (
                     <>
                        {myRequests.length === 0 ? (
                            <p>شما تاکنون هیچ درخواستی ثبت نکرده‌اید.</p>
                        ) : (
                            <table className="transaction-table requests-table"> 
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>زمان درخواست</th>
                                        <th>جزئیات درخواست</th>
                                        <th>وضعیت</th>
                                        <th>پاسخ ادمین</th>
                                        <th>زمان پاسخ</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {myRequests.map(req => (
                                       
                                        <tr key={req.id} className={`status-row-${req.status?.toLowerCase()}`}>
                                            <td>{req.id}</td>
                                            <td>{formatDateTime(req.requestTime)}</td>
                                            <td className="request-details">{req.requestDetails}</td>
                                            <td>{renderStatusBadge(req.status)}</td>
                                            <td>{req.adminNotes || '-'}</td>
                                            <td>{req.resolutionTime ? formatDateTime(req.resolutionTime) : '-'}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        )}
                     </>
                )}
            </div>
           
            <Modal
                isOpen={showRequestForm}
                onClose={() => setShowRequestForm(false)}
                title="ثبت درخواست ارتقا به اکانت فروشنده"
            >
                <SubmitSellerRequestForm
                    onSuccess={handleRequestSubmitted}
                    onCancel={() => setShowRequestForm(false)}
                />
            </Modal>
        </div>
    );
};

export default UserRequestsPage;