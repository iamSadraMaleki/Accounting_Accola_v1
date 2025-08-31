

/**
 
 * @param {number | string | null | undefined} amount 
 * @returns {string}
 */
export const formatCurrency = (amount) => {
    const num = parseFloat(amount); 
    if (amount === null || amount === undefined || isNaN(num)) {
        return 'N/A';
    }
   
    return new Intl.NumberFormat('fa-IR').format(num);
};

/**

 * @param {string | Date | null | undefined} dateTimeString 
 * @returns {string} 
 */
export const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return 'N/A';
    try {
        const options = {
            year: 'numeric', month: 'numeric', day: 'numeric',
            hour: '2-digit', minute: '2-digit',
            hour12: false
        };
     
        const dateObj = typeof dateTimeString === 'string' ? new Date(dateTimeString) : dateTimeString;
        if (isNaN(dateObj.getTime())) { 
             return 'تاریخ نامعتبر';
        }
        return new Intl.DateTimeFormat('fa-IR', options).format(dateObj);
    } catch (e) {
        console.error("Error formatting date:", e);
        
        return String(dateTimeString);
    }
};

