
import React from 'react';
import { Link } from 'react-router-dom'; 
import './ReportsPage.css'; 

const ReportsPage = () => {
    return (
        <div className="reports-page-container">
            <h2>بخش گزارشات</h2>
            <p>از این بخش می‌توانید گزارش‌های مختلف سیستم را مشاهده کنید.</p>

            <div className="report-options">
               
                <div className="report-option">
                    <h3>تاریخچه پرداخت‌ها</h3>
                    <p>مشاهده و فیلتر کردن تمام پرداخت‌های ثبت شده.</p>
                    <Link to="/reports/payments" className="report-link-button">
                        مشاهده تاریخچه پرداخت
                    </Link>
                </div>
    
                <div className="report-option">
                    <h3>تاریخچه کارت به کارت</h3>
                    <p>مشاهده تمام انتقال وجه‌های بین کارت‌های شما.</p>
                    <Link to="/reports/transfers" className="report-link-button">
                        مشاهده تاریخچه انتقال
                    </Link>
                </div>
                <div className="report-option">
                    <h3>تاریخچه دریافت‌ها</h3>
                    <p>مشاهده تمام دریافت‌های ثبت شده به کارت‌های شما.</p>
                    <Link to="/reports/receives" className="report-link-button">
                        مشاهده تاریخچه دریافت
                    </Link>
                </div>

            </div>
        </div>
    );
};

export default ReportsPage;