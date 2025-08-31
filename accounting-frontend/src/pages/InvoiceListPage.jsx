
import React, { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom'; 
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext'; 
import { formatCurrency, formatDateTime } from '../utils/formatters'; 

import './InvoiceListPage.css';
const InvoiceListPage = () => {
    const { user } = useAuth();
    const isBusinessUser = user?.role === 'BUSINESS';

    const [invoices, setInvoices] = useState([]);
    const [isLoading, setIsLoading] = useState(true); 
    const [error, setError] = useState('');
    
    const [pageTitle, setPageTitle] = useState('بارگذاری...');
    
    const fetchData = useCallback(async () => {
        setIsLoading(true);
        setError('');
        setInvoices([]); 

        let url = '';
        let title = 'فاکتورها'; 

        if (isBusinessUser) {
            url = '/api/invoices'; 
            title = "فاکتورهای صادر شده توسط شما";
        } else if (user?.role === 'USER') {
            url = '/api/invoices/received'; 
            title = "فاکتورهای دریافتی شما";
        } else {
            setError("نقش کاربری شما برای مشاهده فاکتورها مشخص نیست.");
            setIsLoading(false);
            setPageTitle("خطا در دسترسی"); 
            return;
        }

        setPageTitle(title); 

        console.log(`[InvoiceList] Fetching invoices from ${url} for role: ${user?.role}`);
        try {
            
            const response = await apiClient.get(url);
            console.log("[InvoiceList] Invoices received:", response.data);
            setInvoices(response.data || []);
        } catch (err) {
            console.error("Error fetching invoices:", err);
             if (err.response && err.response.status === 403) {
                 setError("شما اجازه دسترسی به این لیست فاکتور را ندارید.");
             } else {
                setError("خطا در دریافت لیست فاکتورها.");
             }
            setInvoices([]);
        } finally {
            setIsLoading(false);
        }
    }, [user, isBusinessUser]); 
    useEffect(() => {
        if (user) {
             fetchData();
        }
    }, [fetchData, user]); 
    return (
         <div className="history-page-container">
            <h2>{pageTitle}</h2> 

           
            {isBusinessUser && (
                <div className="page-actions" style={{ marginBottom: '20px' }}>
                    <Link to="/invoices/new" className="submit-btn">
                        + ایجاد فاکتور جدید
                    </Link>
                </div>
            )}

            {isLoading && <p>در حال بارگذاری فاکتورها...</p>}
            {error && <p className="error-message history-error">{error}</p>}

            {!isLoading && !error && (
                 <>
                 
                 {invoices.length === 0 ? (
                    <p>هیچ فاکتوری برای نمایش یافت نشد.</p> 
                 ) : (
                    <div className="transaction-table-container">
                        <table className="transaction-table invoice-table">
                            <thead>
                                <tr>
                                    <th>شماره فاکتور</th>
                                    
                                    <th>{isBusinessUser ? 'گیرنده' : 'صادر کننده'}</th>
                                    <th>تاریخ صدور</th>
                                    <th>مبلغ کل (ریال)</th>
                                    <th>وضعیت</th>
                                    <th>عملیات</th>
                                </tr>
                            </thead>
                            <tbody>
                                {invoices.map(inv => (
                                    <tr key={inv.id}>
                                        <td>{inv.invoiceNumber}</td>
                                      
                                        <td>{isBusinessUser ? inv.recipientName : (inv.issuerBusinessName || inv.issuerUsername)}</td>
                                        <td>{inv.issueDate ? new Date(inv.issueDate).toLocaleDateString('fa-IR') : '-'}</td>
                                        <td className="amount-cell">{formatCurrency(inv.totalAmount)}</td>
                                        <td>
                                            <span className={`status-badge status-${inv.status?.toLowerCase()}`}>
                                               
                                                {inv.status}
                                            </span>
                                        </td>
                                        <td>
                                            <Link to={`/invoices/${inv.id}`} className="action-btn view">مشاهده</Link>
                                          
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                  )}
                 </>
            )}
        </div>
    );
};

export default InvoiceListPage;