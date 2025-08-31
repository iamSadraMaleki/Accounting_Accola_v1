
import React from 'react'; 
import './BalanceStatusComponent.css'; 


const formatCurrency = (amount) => {
    if (amount === null || amount === undefined) return 'N/A';
    return new Intl.NumberFormat('fa-IR').format(amount);
}


const BalanceStatusComponent = ({ balances }) => {


   
    if (!balances) {
        return null;
    }

    return (
         <div className="balance-status-container">
            <h4>وضعیت موجودی کارت‌ها</h4>
            
             {balances.length === 0 ? (
                <p>هنوز کارتی برای نمایش موجودی ثبت نشده است.</p>
            ) : (
                <table className="balance-table">
                    <thead>
                        <tr>
                            <th>نام بانک</th>
                            <th>شماره کارت</th>
                            <th>موجودی (ریال)</th>
                        </tr>
                    </thead>
                    <tbody>
                        
                        {balances.map((item, index) => (
                           
                            <tr key={item.maskedCardNumber || index}> 
                                <td>{item.bankName}</td>
                                <td className="card-number-cell">{item.maskedCardNumber}</td>
                                <td className="balance-cell">{formatCurrency(item.balance)}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
             )}
        </div>
    );
};

export default BalanceStatusComponent;