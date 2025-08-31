
import React, { useState, useEffect } from 'react';
import apiClient from '../api/axiosConfig'; 
import './UserProfileForm.css'; 

const UserProfileForm = () => {
    const [profileData, setProfileData] = useState({
        firstName: '', lastName: '', birthDate: '', nationalId: '', fatherName: '',
        mobilePhone: '', landlinePhone: '', province: '', city: '', street: '',
        alley: '', houseNumber: '', educationLevel: '', fieldOfStudy: '', occupation: '',
       
        username: '', email: '', lastUpdatedAt: null
    });
    const [isLoading, setIsLoading] = useState(true); 
    const [isSaving, setIsSaving] = useState(false); 
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

   
    useEffect(() => {
        const fetchProfile = async () => {
            setIsLoading(true);
            setError('');
            try {
                
                const response = await apiClient.get('/api/profile/me');
                
                const data = response.data;
                if (data.birthDate) {
                    data.birthDate = data.birthDate.split('T')[0]; 
                }
                setProfileData({
                    ...profileData,
                    ...data        
                 });
            } catch (err) {
                console.error("Error fetching profile:", err);
                setError("خطا در دریافت اطلاعات پروفایل.");
               
            } finally {
                setIsLoading(false);
            }
        };

        fetchProfile();
        
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setProfileData(prevData => ({
            ...prevData,
            [name]: value
        }));
        setError(''); 
        setSuccessMessage('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSaving(true);
        setError('');
        setSuccessMessage('');

       
        const updateData = {
            firstName: profileData.firstName,
            lastName: profileData.lastName,
            birthDate: profileData.birthDate || null, 
            nationalId: profileData.nationalId,
            fatherName: profileData.fatherName,
            mobilePhone: profileData.mobilePhone,
            landlinePhone: profileData.landlinePhone,
            province: profileData.province,
            city: profileData.city,
            street: profileData.street,
            alley: profileData.alley,
            houseNumber: profileData.houseNumber,
            educationLevel: profileData.educationLevel,
            fieldOfStudy: profileData.fieldOfStudy,
            occupation: profileData.occupation
        };


        try {
           
            const response = await apiClient.put('/api/profile/me', updateData);
            
             const updatedData = response.data;
                if (updatedData.birthDate) {
                    updatedData.birthDate = updatedData.birthDate.split('T')[0];
                }
            setProfileData(prev => ({...prev, ...updatedData}));

            setSuccessMessage('اطلاعات پروفایل با موفقیت ذخیره شد.');

        } catch (err) {
            console.error("Error updating profile:", err);
             if (err.response && err.response.data) {
                 
                 if (typeof err.response.data === 'object') {
                 
                     setError("خطا در اعتبارسنجی داده‌ها. لطفاً ورودی‌ها را بررسی کنید.");
                 } else {
                      setError(err.response.data || "خطا در ذخیره اطلاعات.");
                 }

             } else {
                  setError("خطا در ذخیره اطلاعات.");
             }
        } finally {
            setIsSaving(false);
        }
    };

   
    if (isLoading) {
        return <p>در حال بارگذاری اطلاعات پروفایل...</p>;
    }

    return (
        <form onSubmit={handleSubmit} className="profile-form">
            <h3>مدیریت اطلاعات هویتی</h3>

            {error && <p className="error-message">{error}</p>}
            {successMessage && <p className="success-message">{successMessage}</p>}

            
            <fieldset>
                <legend>اطلاعات شناسنامه</legend>
                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="firstName">نام:</label>
                        <input type="text" id="firstName" name="firstName" value={profileData.firstName || ''} onChange={handleChange} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="lastName">نام خانوادگی:</label>
                        <input type="text" id="lastName" name="lastName" value={profileData.lastName || ''} onChange={handleChange} />
                    </div>
                </div>
                 <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="birthDate">تاریخ تولد:</label>
                        <input type="date" id="birthDate" name="birthDate" value={profileData.birthDate || ''} onChange={handleChange} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="nationalId">کد ملی:</label>
                        <input type="text" id="nationalId" name="nationalId" value={profileData.nationalId || ''} onChange={handleChange} maxLength="10" />
                    </div>
                     <div className="form-group">
                        <label htmlFor="fatherName">نام پدر:</label>
                        <input type="text" id="fatherName" name="fatherName" value={profileData.fatherName || ''} onChange={handleChange} />
                    </div>
                 </div>
            </fieldset>

           
            <fieldset>
                 <legend>اطلاعات تماس</legend>
                  <div className="form-row">
                     <div className="form-group">
                        <label>ایمیل (ثبت شده):</label>
                        <input type="email" value={profileData.email || ''} readOnly disabled />
                    </div>
                    <div className="form-group">
                        <label htmlFor="mobilePhone">تلفن همراه:</label>
                        <input type="tel" id="mobilePhone" name="mobilePhone" value={profileData.mobilePhone || ''} onChange={handleChange} />
                    </div>
                     <div className="form-group">
                        <label htmlFor="landlinePhone">تلفن ثابت:</label>
                        <input type="tel" id="landlinePhone" name="landlinePhone" value={profileData.landlinePhone || ''} onChange={handleChange} />
                    </div>
                 </div>
                 <div className="form-row">
                      <div className="form-group">
                        <label htmlFor="province">استان:</label>
                        <input type="text" id="province" name="province" value={profileData.province || ''} onChange={handleChange} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="city">شهر:</label>
                        <input type="text" id="city" name="city" value={profileData.city || ''} onChange={handleChange} />
                    </div>
                 </div>
                 <div className="form-group">
                     <label htmlFor="street">آدرس (خیابان اصلی):</label>
                     <input type="text" id="street" name="street" value={profileData.street || ''} onChange={handleChange} />
                 </div>
                  <div className="form-row">
                     <div className="form-group">
                        <label htmlFor="alley">کوچه:</label>
                        <input type="text" id="alley" name="alley" value={profileData.alley || ''} onChange={handleChange} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="houseNumber">پلاک:</label>
                        <input type="text" id="houseNumber" name="houseNumber" value={profileData.houseNumber || ''} onChange={handleChange} />
                    </div>
                 </div>
            </fieldset>

             
            <fieldset>
                 <legend>اطلاعات فرهنگی/شغلی</legend>
                  <div className="form-row">
                     <div className="form-group">
                        <label htmlFor="educationLevel">تحصیلات:</label>
                        <input type="text" id="educationLevel" name="educationLevel" value={profileData.educationLevel || ''} onChange={handleChange} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="fieldOfStudy">رشته تحصیلی:</label>
                        <input type="text" id="fieldOfStudy" name="fieldOfStudy" value={profileData.fieldOfStudy || ''} onChange={handleChange} />
                    </div>
                     <div className="form-group">
                        <label htmlFor="occupation">شغل:</label>
                        <input type="text" id="occupation" name="occupation" value={profileData.occupation || ''} onChange={handleChange} />
                    </div>
                 </div>
            </fieldset>

            
            <fieldset>
                <legend>آپلود مدارک</legend>
                 <div className="form-row">
                     <div className="form-group">
                        <label htmlFor="idCardPhoto">عکس شناسنامه:</label>
                        <input type="file" id="idCardPhoto" name="idCardPhoto" onChange={(e) => console.log("ID File selected:", e.target.files[0])} />
                        
                         {profileData.idCardPhotoPath && <span>فایل قبلی: {profileData.idCardPhotoPath}</span>}
                     </div>
                    <div className="form-group">
                        <label htmlFor="nationalIdPhoto">عکس کارت ملی:</label>
                        <input type="file" id="nationalIdPhoto" name="nationalIdPhoto" onChange={(e) => console.log("NID File selected:", e.target.files[0])} />
                       
                         {profileData.nationalIdPhotoPath && <span>فایل قبلی: {profileData.nationalIdPhotoPath}</span>}
                     </div>
                 </div>
                
            </fieldset>


            <div className="form-actions">
                 {profileData.lastUpdatedAt && (
                    <span className="last-updated">آخرین ویرایش: {new Date(profileData.lastUpdatedAt).toLocaleString('fa-IR')}</span>
                 )}
                <button type="submit" disabled={isSaving} className="save-button">
                    {isSaving ? 'در حال ذخیره...' : 'ذخیره تغییرات پروفایل'}
                </button>
            </div>
        </form>
    );
};

export default UserProfileForm;