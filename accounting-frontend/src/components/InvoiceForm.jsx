
import React, { useState, useEffect, useMemo } from 'react';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import { formatCurrency } from '../utils/formatters'; 
import './InvoiceForm.css'; 

const InvoiceForm = ({ onSuccess, initialData = null }) => { 
    const { user } = useAuth(); 


    const [recipientName, setRecipientName] = useState('');
    const [recipientPhone, setRecipientPhone] = useState('');
    const [recipientNationalId, setRecipientNationalId] = useState('');
    const [issueDate, setIssueDate] = useState(new Date().toISOString().split('T')[0]); 
    const [dueDate, setDueDate] = useState('');
    const [notes, setNotes] = useState('');
    const [items, setItems] = useState([
        { id: Date.now(), description: '', quantity: 1, unitPrice: '' } 
    ]);
    const [amountPaid, setAmountPaid] = useState('0'); 
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState('');
    

    
    const { subTotal, totalAmount, amountDue } = useMemo(() => {
        let currentSubTotal = 0;
        items.forEach(item => {
            const quantity = parseInt(item.quantity, 10) || 0;
            const price = parseFloat(item.unitPrice) || 0;
            currentSubTotal += quantity * price;
        });
        
        const currentTax = 0;
        const currentTotal = currentSubTotal + currentTax;
        const paid = parseFloat(amountPaid) || 0;
        const due = currentTotal - paid;
        return {
            subTotal: currentSubTotal,
            totalAmount: currentTotal,
            amountDue: due
        };
    }, [items, amountPaid]); 

    
    const handleItemChange = (index, field, value) => {
        const newItems = [...items];
        
        if (field === 'quantity') {
            newItems[index][field] = parseInt(value, 10) || 0;
        } else if (field === 'unitPrice') {
             newItems[index][field] = value; 
        }
         else {
            newItems[index][field] = value;
        }
        setItems(newItems);
    };

    const handleAddItem = () => {
        setItems([
            ...items,
            { id: Date.now(), description: '', quantity: 1, unitPrice: '' } 
        ]);
    };

    const handleRemoveItem = (index) => {
        if (items.length <= 1) return; 
        const newItems = items.filter((_, i) => i !== index);
        setItems(newItems);
    };

   
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        
        if (!recipientName.trim()) { setError("نام گیرنده الزامی است."); return; }
        if (items.length === 0) { setError("فاکتور باید حداقل یک آیتم داشته باشد."); return; }
        let itemsValid = true;
        items.forEach((item, index) => {
            if (!item.description.trim()) { setError(`شرح کالا در ردیف ${index + 1} الزامی است.`); itemsValid = false; }
            if (item.quantity <= 0) { setError(`تعداد کالا در ردیف ${index + 1} باید مثبت باشد.`); itemsValid = false; }
             const price = parseFloat(item.unitPrice);
            if (isNaN(price) || price < 0) { setError(`فی واحد در ردیف ${index + 1} نامعتبر است.`); itemsValid = false; }
        });
        if (!itemsValid) return;
        // ----------------------

        setIsSaving(true);

        const requestData = {
            recipientName,
            recipientPhone,
            recipientNationalId,
            issueDate: issueDate || null,
            dueDate: dueDate || null,
            notes,
            
            items: items.map(item => ({
                description: item.description,
                quantity: item.quantity,
                unitPrice: parseFloat(item.unitPrice) || 0 
            }))
          
        };

        console.log("[InvoiceForm] Sending Invoice Data:", JSON.stringify(requestData, null, 2));

        try {
            
            const response = await apiClient.post('/api/invoices', requestData);
            console.log("[InvoiceForm] Invoice created:", response.data);
            if (onSuccess) {
                onSuccess(response.data); 
            }
        } catch (err) {
            console.error("[InvoiceForm] Error creating invoice:", err);
            setIsSaving(false);
             if (err.response && err.response.data) {
                 console.error("[InvoiceForm] Backend Error:", JSON.stringify(err.response.data, null, 2));
                 if (typeof err.response.data === 'object' && err.response.data.error) { setError(err.response.data.error); }
                 else if (typeof err.response.data === 'string') { setError(err.response.data); }
                 else { setError('خطا در ایجاد فاکتور.'); }
             } else if (err.request) { setError('خطا در ارتباط با سرور.'); }
             else { setError('یک خطای پیش‌بینی نشده رخ داد.'); }
        } finally {
            // setIsSaving(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="invoice-form">
            {error && <p className="error-message">{error}</p>}

           
            <fieldset className="issuer-info">
                <legend>اطلاعات صادر کننده</legend>
                <p><strong>نام:</strong> {user?.fullName || user?.username || 'نامشخص'}</p>
                <p><strong>نام کسب و کار:</strong> {user?.businessName || '(ثبت نشده)'}</p>
              
            </fieldset>

           
            <fieldset>
                <legend>اطلاعات گیرنده</legend>
                <div className="form-group">
                    <label htmlFor="recipientName">نام و نام خانوادگی: *</label>
                    <input type="text" id="recipientName" value={recipientName} onChange={(e) => setRecipientName(e.target.value)} required />
                </div>
                 <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="recipientPhone">شماره تلفن:</label>
                        <input type="tel" id="recipientPhone" value={recipientPhone} onChange={(e) => setRecipientPhone(e.target.value)} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="recipientNationalId">کد ملی:</label>
                        <input type="text" id="recipientNationalId" value={recipientNationalId} onChange={(e) => setRecipientNationalId(e.target.value)} maxLength="10" pattern="\d{10}" />
                    </div>
                 </div>
            </fieldset>

            
             <fieldset>
                <legend>جزئیات فاکتور</legend>
                 <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="issueDate">تاریخ صدور:</label>
                        <input type="date" id="issueDate" value={issueDate} onChange={(e) => setIssueDate(e.target.value)} />
                    </div>
                     <div className="form-group">
                        <label htmlFor="dueDate">تاریخ سررسید:</label>
                        <input type="date" id="dueDate" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
                    </div>
                 </div>
                 <div className="form-group">
                        <label htmlFor="notes">یادداشت:</label>
                        <textarea id="notes" value={notes} onChange={(e) => setNotes(e.target.value)} rows="2"></textarea>
                 </div>
            </fieldset>


           
            <fieldset className="invoice-items">
                <legend>ردیف‌های فاکتور *</legend>
                <table>
                    <thead>
                        <tr>
                            <th>ردیف</th>
                            <th>شرح کالا / خدمات *</th>
                            <th>تعداد *</th>
                            <th>فی واحد (ریال) *</th>
                            <th>مبلغ کل (ریال)</th>
                            <th></th> 
                        </tr>
                    </thead>
                    <tbody>
                        {items.map((item, index) => (
                            <tr key={item.id}>
                                <td>{index + 1}</td>
                                <td><input type="text" value={item.description} onChange={(e) => handleItemChange(index, 'description', e.target.value)} required /></td>
                                <td><input type="number" value={item.quantity} onChange={(e) => handleItemChange(index, 'quantity', e.target.value)} min="1" required style={{width: '70px'}}/></td>
                                <td><input type="number" value={item.unitPrice} onChange={(e) => handleItemChange(index, 'unitPrice', e.target.value)} min="0" step="any" required /></td>
                                <td>{formatCurrency((parseInt(item.quantity, 10) || 0) * (parseFloat(item.unitPrice) || 0))}</td>
                                <td>
                                    {items.length > 1 && ( 
                                        <button type="button" onClick={() => handleRemoveItem(index)} className="remove-item-btn">&times;</button>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                <button type="button" onClick={handleAddItem} className="add-item-btn">+ افزودن ردیف</button>
            </fieldset>

           
             <fieldset className="invoice-summary">
                <legend>خلاصه مبالغ</legend>
                <div className="summary-row"><span>جمع کل آیتم‌ها:</span> <span>{formatCurrency(subTotal)} ریال</span></div>
                <div className="summary-row total"><span>مبلغ نهایی فاکتور:</span> <span>{formatCurrency(totalAmount)} ریال</span></div>
                <hr/>
                 <div className="summary-row">
                    <label htmlFor="amountPaid">مبلغ پرداخت شده:</label>
                    <input type="number" id="amountPaid" value={amountPaid} onChange={(e)=> setAmountPaid(e.target.value)} min="0" step="any" />
                 </div>
                 <div className="summary-row due"><span>مبلغ باقی مانده:</span> <span>{formatCurrency(amountDue)} ریال</span></div>
            </fieldset>


           
            <div className="form-actions">
                <button type="submit" disabled={isSaving} className="submit-btn">
                    {isSaving ? 'در حال ایجاد فاکتور...' : 'ایجاد و ذخیره فاکتور'}
                </button>
                
            </div>
        </form>
    );
};

export default InvoiceForm;