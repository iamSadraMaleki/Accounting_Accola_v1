
import React, { useState, useEffect } from 'react';
import apiClient from '../api/axiosConfig'; 
import { formatCurrency, formatDateTime } from '../utils/formatters'; 
import './TransferHistoryPage.css'; 

const TransferHistoryPage = () => {
    const [transfers, setTransfers] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchTransfers = async () => {
            setIsLoading(true);
            setError('');
            try {
                console.log("[TransferHistory] Fetching transfer history...");
                const response = await apiClient.get('/api/transfers'); 
                console.log("[TransferHistory] Transfers received:", response.data);
                setTransfers(response.data || []);
            } catch (err) {
                console.error("Error fetching transfer history:", err);
                setError("خطا در دریافت تاریخچه انتقال وجه.");
                setTransfers([]);
            } finally {
                setIsLoading(false);
            }
        };
        fetchTransfers();
    }, []); 

   
    const renderCardInfo = (maskedNum, bankName) => {
        if (!maskedNum || !bankName) return 'N/A';
        return (
            <>
                {bankName} <br /> ({maskedNum})
            </>
        );
    }

    return (
         <div className="transfer-history-container"> 
            <h2>تاریخچه کارت به کارت</h2>

            {error && <p className="error-message history-error">{error}</p>}
            {isLoading && <p>در حال بارگذاری تاریخچه...</p>}

            {!isLoading && !error && (
                <div className="transaction-table-container">
                    {transfers.length === 0 ? (
                        <p>هیچ تراکنش انتقال وجهی یافت نشد.</p>
                    ) : (
                        <table className="transaction-table"> 
                            <thead>
                                <tr>
                                    <th>زمان انتقال</th>
                                    <th>از کارت</th>
                                    <th>به کارت</th>
                                    <th>مبلغ (ریال)</th>
                                    <th>توضیحات</th>
                                    <th>زمان ثبت</th>
                                </tr>
                            </thead>
                            <tbody>
                                {transfers.map(tx => (
                                    <tr key={tx.id}>
                                        <td>{formatDateTime(tx.transferTime)}</td>
                                        <td className="card-info-cell">{renderCardInfo(tx.sourceMaskedCardNumber, tx.sourceBankName)}</td>
                                        <td className="card-info-cell">{renderCardInfo(tx.destinationMaskedCardNumber, tx.destinationBankName)}</td>
                                        <td className="amount-cell">{formatCurrency(tx.amount)}</td>
                                        <td>{tx.description}</td>
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

export default TransferHistoryPage;