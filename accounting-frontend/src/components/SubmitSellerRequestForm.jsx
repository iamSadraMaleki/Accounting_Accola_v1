
import React, { useState, useEffect } from 'react';
import apiClient from '../api/axiosConfig'; 
import { useAuth } from '../context/AuthContext';
import './SubmitSellerRequestForm.css'; 

const SubmitSellerRequestForm = ({ onSuccess, onCancel }) => {
    const { user: authUser } = useAuth();
    const [formData, setFormData] = useState({
        businessName: '', guildCode: '', unionType: '', activityType: '',
        businessUnitGrade: '', workAddress: '', workPhone: '', requestDetails: '',
        firstName: '', lastName: ''
    });
    const [licenseFile, setLicenseFile] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    
   
    useEffect(() => {
        const fetchProfileData = async () => {
            
             try {
                 if (authUser) {
                    const profileResponse = await apiClient.get('/api/profile/me');
                    const profile = profileResponse.data;
                    setFormData(prev => ({
                        ...prev,
                        firstName: profile?.firstName || '',
                        lastName: profile?.lastName || '',
                        
                        workAddress: profile?.workAddress || '',
                        workPhone: profile?.workPhone || '',
                    }));
                 }
             } catch (err) { console.error("Could not fetch initial profile data:", err); }
        };
        fetchProfileData();
    }, [authUser]);

    
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setError('');
        setSuccessMessage('');
    };

   
    const handleFileChange = (e) => {
        setError('');
        if (e.target.files && e.target.files[0]) {
            const file = e.target.files[0];
            if (file.size > 5 * 1024 * 1024) { 
                 setError("حجم فایل پروانه کسب نباید بیشتر از 5MB باشد.");
                 setLicenseFile(null); e.target.value = null; return;
             }
            setLicenseFile(file);
        } else { setLicenseFile(null); }
    };

     
     const validateForm = () => {
         if (!formData.businessName.trim() || !formData.workAddress.trim() || !formData.workPhone.trim() || !formData.requestDetails.trim()) {
             setError("لطفاً تمام فیلدهای الزامی ستاره‌دار را پر کنید.");
             return false;
         }
         
         return true;
     };


    
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');
        if (!validateForm()) return;
        setIsSubmitting(true); 

        const submissionData = new FormData();
        if (licenseFile) {
            submissionData.append('licenseFile', licenseFile);
        }
        const requestDto = {
            businessName: formData.businessName, guildCode: formData.guildCode,
            unionType: formData.unionType, activityType: formData.activityType,
            businessUnitGrade: formData.businessUnitGrade, workAddress: formData.workAddress,
            workPhone: formData.workPhone, requestDetails: formData.requestDetails
        };
        submissionData.append('requestData', new Blob([JSON.stringify(requestDto)], { type: 'application/json' }));

        console.log("[SubmitRequestForm] Submitting FormData...");

        try {
            await apiClient.post('/api/requests/seller-upgrade', submissionData, {
                 headers: { 'Content-Type': undefined } 
             });
            setSuccessMessage('درخواست شما با موفقیت ثبت شد.');
            setFormData({ 
                 businessName: '', guildCode: '', unionType: '', activityType: '',
                 businessUnitGrade: '', workAddress: '', workPhone: '', requestDetails: '',
                
                 firstName: formData.firstName, lastName: formData.lastName
            });
            setLicenseFile(null); 
            document.getElementById('licenseFile').value = null; 

            if (onSuccess) {
                 setTimeout(() => { onSuccess(); }, 1500); 
            }

        } catch (err) {
             console.error("[SubmitRequestForm] Error submitting request:", err);
            
             if (err.response && err.response.data) {
                 console.error("[SubmitRequestForm] Backend Error:", JSON.stringify(err.response.data, null, 2));
                 if (typeof err.response.data === 'object' && err.response.data.error) { setError(err.response.data.error); }
                 else if (typeof err.response.data === 'string') { setError(err.response.data); }
                 else { setError('خطا در ثبت درخواست.'); }
             } else if (err.request) { setError('خطا در ارتباط با سرور.'); }
             else { setError('یک خطای پیش‌بینی نشده رخ داد.'); }
        } finally {
            setIsSubmitting(false); 
        }
    };


    return (
       
         <form onSubmit={handleSubmit} className="submit-request-form">
             {error && <p className="error-message">{error}</p>}
             {successMessage && <p className="success-message">{successMessage}</p>}
            
              <div className="form-row"> <div className="form-group"> <label>نام:</label> <input type="text" value={formData.firstName || ''} readOnly disabled /> </div> <div className="form-group"> <label>نام خانوادگی:</label> <input type="text" value={formData.lastName || ''} readOnly disabled /> </div> </div>
              <div className="form-group"> <label htmlFor="businessName">نام کسب و کار: *</label> <input type="text" id="businessName" name="businessName" value={formData.businessName} onChange={handleChange} required /> </div>
              <div className="form-row"> <div className="form-group"> <label htmlFor="guildCode">کد صنف:</label> <input type="text" id="guildCode" name="guildCode" value={formData.guildCode} onChange={handleChange} /> </div> <div className="form-group"> <label htmlFor="unionType">نوع اتحادیه:</label> <input type="text" id="unionType" name="unionType" value={formData.unionType} onChange={handleChange} /> </div> </div>
              <div className="form-row"> <div className="form-group"> <label htmlFor="activityType">نوع فعالیت:</label> <input type="text" id="activityType" name="activityType" value={formData.activityType} onChange={handleChange} /> </div> <div className="form-group"> <label htmlFor="businessUnitGrade">درجه واحد صنفی:</label> <input type="text" id="businessUnitGrade" name="businessUnitGrade" value={formData.businessUnitGrade} onChange={handleChange} /> </div> </div>
              <div className="form-group"> <label htmlFor="workAddress">آدرس محل کار: *</label> <textarea id="workAddress" name="workAddress" value={formData.workAddress} onChange={handleChange} rows="3" required /> </div>
              <div className="form-group"> <label htmlFor="workPhone">تلفن محل کار: *</label> <input type="tel" id="workPhone" name="workPhone" value={formData.workPhone} onChange={handleChange} required /> </div>
              <div className="form-group"> <label htmlFor="licenseFile">تصویر پروانه کسب (حداکثر 5MB):</label> <input type="file" id="licenseFile" name="licenseFile" onChange={handleFileChange} accept="image/jpeg, image/png, application/pdf"/> {licenseFile && <small>فایل انتخاب شده: {licenseFile.name}</small>} </div>
              <div className="form-group"> <label htmlFor="requestDetails">متن درخواست: *</label> <textarea id="requestDetails" name="requestDetails" value={formData.requestDetails} onChange={handleChange} rows="5" required placeholder="لطفا دلیل درخواست خود برای ارتقا به اکانت فروشنده را توضیح دهید..." /> </div>
             <div className="form-actions"> <button type="submit" disabled={isSubmitting} className="submit-btn"> {isSubmitting ? 'در حال ارسال...' : 'ارسال درخواست'} </button> <button type="button" onClick={onCancel} className="cancel-btn" disabled={isSubmitting}>انصراف</button> </div>
         </form>
     );
 };

 export default SubmitSellerRequestForm;