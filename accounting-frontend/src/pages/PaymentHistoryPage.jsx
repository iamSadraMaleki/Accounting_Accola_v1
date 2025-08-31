
import React, { useState, useEffect, useCallback } from 'react';
import apiClient from '../api/axiosConfig'; 
import './PaymentHistoryPage.css'; 


const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return 'N/A';
    try {
        
        const options = {
            year: 'numeric', month: 'numeric', day: 'numeric',
            hour: '2-digit', minute: '2-digit',
            hour12: false 
        };
        return new Intl.DateTimeFormat('fa-IR', options).format(new Date(dateTimeString));
    } catch (e) {
        console.error("Error formatting date:", e);
        return dateTimeString; 
    }
}


const formatCurrency = (amount) => {
    if (amount === null || amount === undefined) return 'N/A';
    return new Intl.NumberFormat('fa-IR').format(amount);
}


const getDefaultDateRange = () => {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0); 

    
    const formatDate = (date) => date.toISOString().split('T')[0];

    return {
        start: formatDate(firstDay),
        end: formatDate(lastDay)
    };
}


const PaymentHistoryPage = () => {
    const [transactions, setTransactions] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

  
    const defaultDates = getDefaultDateRange();
    const [startDate, setStartDate] = useState(defaultDates.start);
    const [endDate, setEndDate] = useState(defaultDates.end);

    
    const fetchTransactions = useCallback(async (start, end) => {
        
        const startTime = `${start}T00:00:00`;
        const endTime = `${end}T23:59:59`;

        setIsLoading(true);
        setError('');
        console.log(`[PaymentHistory] Fetching transactions between ${startTime} and ${endTime}`);
        try {
            const response = await apiClient.get('/api/payments/by-date', {
                params: { 
                    start: startTime,
                    end: endTime
                }
            });
            console.log("[PaymentHistory] Transactions received:", response.data);
            setTransactions(response.data || []);
        } catch (err) {
            console.error("Error fetching payment history:", err);
            setError("خطا در دریافت تاریخچه پرداخت‌ها.");
            setTransactions([]); 
        } finally {
            setIsLoading(false);
        }
    }, []); 
     useEffect(() => {
         fetchTransactions(startDate, endDate);
     }, [fetchTransactions]); 


    
    const handleFilter = () => {
         if (startDate && endDate && startDate <= endDate) {
             fetchTransactions(startDate, endDate);
         } else {
             setError("لطفاً بازه تاریخی معتبری انتخاب کنید.");
         }
    };

    return (
        <div className="payment-history-container">
            <h2>تاریخچه پرداخت‌ها</h2>

            <div className="filter-section">
                <div className="date-filter">
                    <label htmlFor="startDate">از تاریخ:</label>
                    <input
                        type="date"
                        id="startDate"
                        value={startDate}
                        onChange={(e) => setStartDate(e.target.value)}
                    />
                </div>
                <div className="date-filter">
                    <label htmlFor="endDate">تا تاریخ:</label>
                    <input
                        type="date"
                        id="endDate"
                        value={endDate}
                        onChange={(e) => setEndDate(e.target.value)}
                    />
                </div>
                <button onClick={handleFilter} disabled={isLoading} className="filter-button">
                    اعمال فیلتر
                </button>
            </div>

           
            {error && <p className="error-message history-error">{error}</p>}

           
            {isLoading && <p>در حال بارگذاری تاریخچه...</p>}

           
            {!isLoading && !error && (
                <div className="transaction-table-container">
                    {transactions.length === 0 ? (
                        <p>تراکنشی برای نمایش در این بازه زمانی یافت نشد.</p>
                    ) : (
                        <table className="transaction-table">
                            <thead>
                                <tr>
                                    <th>تاریخ و زمان</th>
                                    <th>فروشگاه/گیرنده</th>
                                    <th>مبلغ (ریال)</th>
                                    <th>کارت مورد استفاده</th>
                                    <th>شماره پیگیری</th>
                                    <th>زمان ثبت</th>
                                </tr>
                            </thead>
                            <tbody>
                                {transactions.map(tx => (
                                    <tr key={tx.id}>
                                        <td>{formatDateTime(tx.transactionTime)}</td>
                                        <td>{tx.shopName}</td>
                                        <td className="amount-cell">{formatCurrency(tx.amount)}</td>
                                        <td className="card-info-cell">
                                            {tx.bankName} <br /> ({tx.maskedCardNumber})
                                        </td>
                                        <td>{tx.trackingNumber}</td>
                                        <td>{formatDateTime(tx.createdAt)}</td>
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

export default PaymentHistoryPage;