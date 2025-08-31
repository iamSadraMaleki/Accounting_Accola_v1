
import React, { useState, useEffect, useCallback } from 'react'; 
import apiClient from '../api/axiosConfig.jsx';
import Modal from '../components/common/Modal.jsx'; 
import { formatDateTime } from '../utils/formatters.jsx'; 
import './AdminRequestPage.css';

const AdminRequestPage = () => {
    const [requests, setRequests] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const [filter, setFilter] = useState('PENDING'); 

    
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedRequest, setSelectedRequest] = useState(null); 
    const [actionType, setActionType] = useState(null); 
    const [adminNotes, setAdminNotes] = useState(''); 
    const [isSubmittingAction, setIsSubmittingAction] = useState(false); 
    const [modalError, setModalError] = useState(''); 
   
   
    const fetchRequests = useCallback(async (status = 'PENDING') => {
        setIsLoading(true);
        setError('');
        const url = status === 'ALL' ? '/api/admin/requests/all' : '/api/admin/requests/pending';
        console.log(`[AdminRequests] Fetching requests from ${url}`);
        try {
            const response = await apiClient.get(url);
            setRequests(response.data || []);
        } catch (err) {
            console.error("Error fetching admin requests:", err);
            setError("خطا در دریافت لیست درخواست‌ها.");
            setRequests([]);
        } finally {
            setIsLoading(false);
        }
    }, []);
    
    useEffect(() => {
        fetchRequests('PENDING');
    }, [fetchRequests]);

  
    const openApproveModal = (request) => {
        setSelectedRequest(request);
        setActionType('approve');
        setAdminNotes(request.adminNotes || ''); 
        setModalError('');
        setIsModalOpen(true);
    };

    const openRejectModal = (request) => {
        setSelectedRequest(request);
        setActionType('reject');
        setAdminNotes(request.adminNotes || '');
        setModalError('');
        setIsModalOpen(true);
    };
    
    const handleConfirmAction = async () => {
        if (!selectedRequest || !actionType) return;

        setIsSubmittingAction(true);
        setModalError('');
        const url = `/api/admin/requests/${selectedRequest.id}/${actionType}`; 
        const payload = { adminNotes: adminNotes };
        console.log(`[AdminRequests] Submitting ${actionType} for request ${selectedRequest.id} to ${url}`);

        try {
            await apiClient.put(url, payload);
            setIsModalOpen(false); 
            setAdminNotes('');     
            setSelectedRequest(null);
            setActionType(null);
            
            fetchRequests(filter); 
            
        } catch (err) {
            console.error(`Error ${actionType}ing request:`, err);
             if (err.response && err.response.data) {
                 setModalError(err.response.data.error || "خطا در انجام عملیات.");
             } else {
                 setModalError("خطا در ارتباط با سرور.");
             }
        } finally {
             setIsSubmittingAction(false);
        }
    };
   
    const handleCloseModal = () => {
        setIsModalOpen(false);
        setAdminNotes('');
        setSelectedRequest(null);
        setActionType(null);
        setModalError('');
    };

   
    const handleFilterChange = (newFilter) => {
        setFilter(newFilter);
        fetchRequests(newFilter);
    };


    return (
        <div className="admin-request-page">
            <h2>مدیریت درخواست‌های فروشندگی</h2>

           
            <div className="filters request-filters">
                 <button onClick={() => handleFilterChange('PENDING')} className={filter === 'PENDING' ? 'active' : ''}>در حال بررسی</button>
                 <button onClick={() => handleFilterChange('ALL')} className={filter === 'ALL' ? 'active' : ''}>همه درخواست‌ها</button>
            </div>

            {isLoading && <p>در حال بارگذاری درخواست‌ها...</p>}
            {error && <p className="error-message history-error">{error}</p>}

            {!isLoading && !error && (
                <div className="transaction-table-container"> 
                    <table className="requests-table transaction-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>کاربر</th>
                                <th>زمان درخواست</th>
                                <th>جزئیات درخواست</th>
                                <th>وضعیت</th>
                                <th>عملیات / پاسخ</th>
                            </tr>
                        </thead>
                        <tbody>
                            {requests.length === 0 ? (
                                <tr><td colSpan="6">هیچ درخواستی برای نمایش ({filter === 'PENDING' ? 'در حال بررسی' : 'کل'}) وجود ندارد.</td></tr>
                            ) : (
                                requests.map(req => (
                                   
                                    <tr key={req.id} className={`status-row-${req.status?.toLowerCase()}`}>
                                        <td>{req.id}</td>
                                        <td>{req.requestingUsername} <small>(ID: {req.requestingUserId})</small></td>
                                        <td>{formatDateTime(req.requestTime)}</td> 
                                        <td className="request-details">{req.requestDetails}</td>
                                        <td>
                                            <span className={`status-badge status-${req.status?.toLowerCase()}`}>
                                                {req.status === 'PENDING' ? 'در حال بررسی' : req.status === 'APPROVED' ? 'تایید شده' : 'رد شده'}
                                            </span>
                                        </td>
                                        <td>
                                            {req.status === 'PENDING' ? (
                                                <div className='action-buttons'>
                                                    
                                                    <button onClick={() => openApproveModal(req)} className="action-btn approve">تایید</button>
                                                    <button onClick={() => openRejectModal(req)} className="action-btn reject">رد</button>
                                                </div>
                                            ) : (
                                                <div className='resolution-details'>
                                                    <span>{req.status === 'APPROVED' ? 'تایید توسط:' : 'رد توسط:'} {req.resolverUsername || '-'}</span>
                                                    <br/>
                                                    <small>زمان: {formatDateTime(req.resolutionTime)}</small>
                                                    <br/>
                                                    <small title={req.adminNotes || ''}>یادداشت: {req.adminNotes ? req.adminNotes.substring(0,30)+'...' : '-'}</small>
                                                 </div>
                                             )}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            )}

             
            <Modal
                isOpen={isModalOpen}
                onClose={handleCloseModal}
                title={actionType === 'approve' ? `تایید درخواست #${selectedRequest?.id}` : `رد درخواست #${selectedRequest?.id}`}
            >
                
                {selectedRequest && (
                    <div className="request-modal-content">
                        <p><strong>کاربر:</strong> {selectedRequest.requestingUsername}</p>
                        <p><strong>جزئیات درخواست:</strong></p>
                        <p className="request-details-modal">{selectedRequest.requestDetails}</p>
                        <hr />
                        <div className="form-group">
                            <label htmlFor="adminNotes">یادداشت / پاسخ ادمین (اختیاری):</label>
                            <textarea
                                id="adminNotes"
                                value={adminNotes}
                                onChange={(e) => setAdminNotes(e.target.value)}
                                rows="4"
                                placeholder="دلیل رد یا پیام برای کاربر..."
                            />
                        </div>
                        {modalError && <p className="error-message">{modalError}</p>}
                        <div className="modal-actions">
                            <button onClick={handleConfirmAction} disabled={isSubmittingAction} className={`action-btn ${actionType}`}>
                                {isSubmittingAction ? 'در حال ثبت...' : (actionType === 'approve' ? 'تایید نهایی' : 'رد نهایی')}
                            </button>
                            <button onClick={handleCloseModal} className="cancel-btn">انصراف</button>
                        </div>
                    </div>
                )}
            </Modal>
            

        </div>
    );
};

export default AdminRequestPage;