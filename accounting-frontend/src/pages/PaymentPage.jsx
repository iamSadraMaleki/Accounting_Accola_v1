
import React, { useState } from 'react'; 
import PaymentForm from '../components/PaymentForm.jsx';
import TransferForm from '../components/TransferForm.jsx'; 
import ReceiveForm from '../components/ReceiveForm.jsx';
import './PaymentPage.css';

const PaymentPage = () => {
    
    const [activeMode, setActiveMode] = useState('payment');

   
    const handleSuccess = (type) => {
        console.log(`${type} recorded successfully!`);
        
    };

    return (
        <div className="payment-page-container">
           
            <h2>عملیات پرداخت / انتقال</h2>

            
            <div className="payment-mode-selector">
                <button
                    onClick={() => setActiveMode('payment')}
                    className={activeMode === 'payment' ? 'active' : ''}
                >
                    ثبت فیش / پرداخت
                </button>
                <button
                    onClick={() => setActiveMode('transfer')}
                    className={activeMode === 'transfer' ? 'active' : ''}
                >
                    کارت به کارت
                </button>
                <button
                    onClick={() => setActiveMode('receive')}
                    className={activeMode === 'receive' ? 'active' : ''}
                >
                    ثبت دریافت
                </button>
            </div>

           
            <div className="form-content">
                {activeMode === 'payment' && (
                    <PaymentForm onSuccess={() => handleSuccess('Payment')} />
                )}
                {activeMode === 'transfer' && (
                    <TransferForm onSuccess={() => handleSuccess('Transfer')} />
                )}
                 {activeMode === 'receive' && (
                    <ReceiveForm onSuccess={() => handleSuccess('Receive')} />
                )}
            </div>
        </div>
    );
};

export default PaymentPage;