
import React from 'react';
import { useNavigate } from 'react-router-dom';
import InvoiceForm from '../components/InvoiceForm.jsx';


const CreateInvoicePage = () => {
    const navigate = useNavigate();

   
    const handleInvoiceCreated = (newInvoice) => {
        console.log('Invoice created successfully:', newInvoice);
        
        navigate('/invoices'); 
    };

    return (
        <div className="create-invoice-container" style={{ padding: '20px' }}> 
            <h2>ایجاد فاکتور جدید</h2>
            <InvoiceForm onSuccess={handleInvoiceCreated} />
        </div>
    );
};

export default CreateInvoicePage;