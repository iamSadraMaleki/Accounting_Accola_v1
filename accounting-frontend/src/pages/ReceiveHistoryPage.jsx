
import React, { useState, useEffect, useCallback } from 'react';
import apiClient from '../api/axiosConfig'; 
import { formatCurrency, formatDateTime } from '../utils/formatters'; 
import './ReceiveHistoryPage.css'; 

const getDefaultDateRange = () => {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);
    const formatDate = (date) => date.toISOString().split('T')[0];
    return { start: formatDate(firstDay), end: formatDate(lastDay) };
}

const ReceiveHistoryPage = () => {
    const [receives, setReceives] = useState([]); 
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const defaultDates = getDefaultDateRange();
    const [startDate, setStartDate] = useState(defaultDates.start);
    const [endDate, setEndDate] = useState(defaultDates.end);

    
    const fetchReceives = useCallback(async (start, end) => {
        const startTime = `${start}T00:00:00`;
        const endTime = `${end}T23:59:59`;

        setIsLoading(true);
        setError('');
        console.log(`[ReceiveHistory] Fetching receives between ${startTime} and ${endTime}`);
        try {
            
            const response = await apiClient.get('/api/receives/by-date', {
                params: { start: startTime, end: endTime }
            });
            console.log("[ReceiveHistory] Receives received:", response.data);
            setReceives(response.data || []);
        } catch (err) {
            console.error("Error fetching receive history:", err);
            setError("خطا در دریافت تاریخچه دریافت‌ها.");
            setReceives([]);
        } finally {
            setIsLoading(false);
        }
    }, []); 
    
     useEffect(() => {
         fetchReceives(startDate, endDate);
     }, [fetchReceives]); 
   
    const handleFilter = () => {
         if (startDate && endDate && startDate <= endDate) {
             fetchReceives(startDate, endDate);
         } else {
             setError("لطفاً بازه تاریخی معتبری انتخاب کنید.");
         }
    };

   
     const renderDestinationCardInfo = (maskedNum, bankName) => {
         if (!maskedNum || !bankName) return 'N/A';
         return (
             <>
                 {bankName} <br /> ({maskedNum})
             </>
         );
     }

    return (
       
        <div className="Receive-history-container">
            <h2>تاریخچه دریافت‌ها</h2>

           
            <div className="filter-section">
                <div className="date-filter">
                    <label htmlFor="startDate">از تاریخ:</label>
                    <input type="date" id="startDate" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
                </div>
                <div className="date-filter">
                    <label htmlFor="endDate">تا تاریخ:</label>
                    <input type="date" id="endDate" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
                </div>
                <button onClick={handleFilter} disabled={isLoading} className="filter-button">
                    اعمال فیلتر
                </button>
            </div>

            {error && <p className="error-message history-error">{error}</p>}
            {isLoading && <p>در حال بارگذاری تاریخچه...</p>}

            {!isLoading && !error && (
                <div className="transaction-table-container">
                    {receives.length === 0 ? (
                        <p>تراکنش دریافتی برای نمایش در این بازه زمانی یافت نشد.</p>
                    ) : (
                        <table className="transaction-table">
                            <thead>
                                <tr>
                                    <th>زمان دریافت</th>
                                    <th>از (توضیحات)</th>
                                    <th>مبلغ (ریال)</th>
                                    <th>به کارت</th>
                                    <th>زمان ثبت</th>
                                </tr>
                            </thead>
                            <tbody>
                                {receives.map(rx => ( 
                                    <tr key={rx.id}>
                                        <td>{formatDateTime(rx.receiveTime)}</td>
                                        <td>{rx.description}</td> 
                                        <td className="amount-cell income">{formatCurrency(rx.amount)}</td> 
                                        <td className="card-info-cell">
                                            {renderDestinationCardInfo(rx.destinationMaskedCardNumber, rx.destinationBankName)}
                                        </td>
                                        <td>{formatDateTime(rx.createdAt)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>
            )}
        </div>
    );
};

export default ReceiveHistoryPage;