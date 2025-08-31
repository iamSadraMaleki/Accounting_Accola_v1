
import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom'; 
import apiClient from '../api/axiosConfig'; 
import { useAuth } from '../context/AuthContext'; 
import { formatCurrency, formatDateTime } from '../utils/formatters';
import './HistoryPage.css'; 

const InvoiceDetailPage = () => {
    const { invoiceId } = useParams(); 
    const { user } = useAuth(); 

    const [invoiceData, setInvoiceData] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');

    
    useEffect(() => {
        const fetchInvoiceDetails = async () => {
            if (!invoiceId) return; 
            setIsLoading(true);
            setError('');
            console.log(`[InvoiceDetail] Fetching invoice ID: ${invoiceId}`);
            try {
                const response = await apiClient.get(`/api/invoices/${invoiceId}`);
                console.log("[InvoiceDetail] Invoice data received:", response.data);
                setInvoiceData(response.data);
            } catch (err) {
                console.error(`Error fetching invoice ${invoiceId}:`, err);
                if (err.response && (err.response.status === 404 || err.response.status === 403)) {
                    setError("فاکتور مورد نظر یافت نشد یا شما اجازه دسترسی به آن را ندارید.");
                } else {
                    setError("خطا در دریافت اطلاعات فاکتور.");
                }
                setInvoiceData(null); 
            } finally {
                setIsLoading(false);
            }
        };

        fetchInvoiceDetails();
    }, [invoiceId]); 

   
    const translateStatus = (status) => {
        switch (status) {
            case 'DRAFT': return 'پیش‌نویس';
            case 'ISSUED': return 'صادر شده';
            case 'PAID': return 'پرداخت شده';
            case 'CANCELLED': return 'لغو شده';
            default: return status;
        }
    }


    
    if (isLoading) {
        return <div className="invoice-detail-container"><p>در حال بارگذاری جزئیات فاکتور...</p></div>;
    }

    if (error) {
        return <div className="invoice-detail-container"><p className="error-message">{error}</p></div>;
    }

    if (!invoiceData) {
       
        return <div className="invoice-detail-container"><p>اطلاعات فاکتور یافت نشد.</p></div>;
    }

  
    const amountDue = invoiceData.amountDue !== undefined ? invoiceData.amountDue :
                      (invoiceData.totalAmount || 0) - (invoiceData.amountPaid || 0);

    return (
        <div className="invoice-detail-container">
            
            <div className="breadcrumb">
                <Link to="/invoices">&larr; بازگشت به لیست فاکتورها</Link>
            </div>

            
            <div className="invoice-paper">
                
                <div className="invoice-header">
                    <div className="issuer-details">
                        <h3>{invoiceData.issuerBusinessName || 'نام کسب و کار صادرکننده'}</h3>
                        <p>صادر کننده: {invoiceData.issuerUsername}</p>

                    </div>
                    <div className="invoice-meta">
                        <h2>فاکتور فروش</h2>
                        <p><strong>شماره فاکتور:</strong> {invoiceData.invoiceNumber}</p>
                        <p><strong>تاریخ صدور:</strong> {invoiceData.issueDate ? new Date(invoiceData.issueDate).toLocaleDateString('fa-IR') : '-'}</p>
                        <p><strong>تاریخ سررسید:</strong> {invoiceData.dueDate ? new Date(invoiceData.dueDate).toLocaleDateString('fa-IR') : '-'}</p>
                         <p><strong>وضعیت:</strong>
                             <span className={`status-badge status-${invoiceData.status?.toLowerCase()}`}>
                                {translateStatus(invoiceData.status)}
                            </span>
                         </p>
                    </div>
                </div>

              
                <div className="recipient-details">
                    <h4>مشخصات گیرنده</h4>
                    <p><strong>نام:</strong> {invoiceData.recipientName}</p>
                    <p><strong>تلفن:</strong> {invoiceData.recipientPhone || '-'}</p>
                    <p><strong>کد ملی:</strong> {invoiceData.recipientNationalId || '-'}</p>
                </div>

                
                <div className="invoice-items-table transaction-table-container">
                    <table className="transaction-table">
                        <thead>
                            <tr>
                                <th>ردیف</th>
                                <th>شرح کالا / خدمات</th>
                                <th>تعداد</th>
                                <th>فی واحد (ریال)</th>
                                <th>مبلغ کل (ریال)</th>
                            </tr>
                        </thead>
                        <tbody>
                            {invoiceData.items && invoiceData.items.map((item, index) => (
                                <tr key={item.id || index}>
                                    <td>{index + 1}</td>
                                    <td>{item.description}</td>
                                    <td style={{textAlign: 'center'}}>{item.quantity}</td>
                                    <td className="amount-cell">{formatCurrency(item.unitPrice)}</td>
                                    <td className="amount-cell">{formatCurrency(item.totalPrice)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

               
                <div className="invoice-footer">
                    <div className="invoice-notes">
                        <strong>یادداشت:</strong>
                        <p>{invoiceData.notes || '-'}</p>
                    </div>
                    <div className="invoice-totals">
                        <div className="summary-row"><span>جمع جزء:</span> <span>{formatCurrency(invoiceData.subTotal)} ریال</span></div>
                        <div className="summary-row"><span>مالیات:</span> <span>{formatCurrency(invoiceData.taxAmount)} ریال</span></div>
                        <div className="summary-row total"><span>جمع کل:</span> <span>{formatCurrency(invoiceData.totalAmount)} ریال</span></div>
                        <hr/>
                        <div className="summary-row"><span>پرداخت شده:</span> <span>{formatCurrency(invoiceData.amountPaid)} ریال</span></div>
                        <div className="summary-row due"><span>مانده:</span> <span>{formatCurrency(amountDue)} ریال</span></div>
                    </div>
                </div>

                
                 <div className="invoice-actions">
                 
                 </div>

            </div> 
        </div>
    );
};

export default InvoiceDetailPage;