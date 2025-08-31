
import React from 'react';

import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';


import MainLayout from './layouts/MainLayout.jsx';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import DashboardPage from './pages/DashboardPage.jsx';
import HomePage from './pages/HomePage.jsx';
import CardManagementPage from './pages/CardManagementPage.jsx';
import PaymentPage from './pages/PaymentPage.jsx';
import PaymentHistoryPage from './pages/PaymentHistoryPage.jsx';
import ReportsPage from './pages/ReportsPage.jsx';
import TransferHistoryPage from './pages/TransferHistoryPage.jsx';
import ReceiveHistoryPage from './pages/ReceiveHistoryPage.jsx';
import AdminRoute, { ForbiddenPage } from './components/auth/AdminRoute.jsx'; 
import AdminRequestPage from './pages/AdminRequestPage.jsx';
import UserRequestsPage from './pages/UserRequestsPage.jsx';
import InvoiceListPage from './pages/InvoiceListPage.jsx';
import CreateInvoicePage from './pages/CreateInvoicePage.jsx';
import InvoiceDetailPage from './pages/InvoiceDetailPage.jsx';



import { useAuth } from './context/AuthContext.jsx';
import './App.css';



const ProtectedRoute = ({ children }) => {
  const { isLoggedIn } = useAuth();
  if (!isLoggedIn) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

const PublicRoute = ({ children }) => {
    const { isLoggedIn } = useAuth();
    if (isLoggedIn) {
        return <Navigate to="/" replace />; 
    }
    return children;
};



function App() {
 
  const { isLoading } = useAuth();

  if (isLoading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', backgroundColor: 'var(--dark-bg)', color: 'var(--text-color-light)' }}>
        در حال بارگذاری اولیه...
      </div>
    );
  }

  return (
    <Router>
      <Routes>
        
   
             <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
                <Route path="/register" element={<PublicRoute><RegisterPage /></PublicRoute>} />
                <Route path="/forbidden" element={<ForbiddenPage />} />
                <Route path="/" element={ <ProtectedRoute> <MainLayout><HomePage /></MainLayout> </ProtectedRoute> } />
                <Route path="/dashboard" element={ <ProtectedRoute> <MainLayout><DashboardPage /></MainLayout> </ProtectedRoute> } />
                <Route path="/cards" element={ <ProtectedRoute> <MainLayout><CardManagementPage /></MainLayout> </ProtectedRoute> } />
                <Route path="/payments" element={ <ProtectedRoute> <MainLayout><PaymentPage /></MainLayout> </ProtectedRoute> } />
                <Route path="/accounts" element={ <ProtectedRoute> <MainLayout><h2>صفحه حساب‌ها</h2></MainLayout> </ProtectedRoute> } />
                <Route path="/reports" element={ <ProtectedRoute> <MainLayout><ReportsPage /></MainLayout> </ProtectedRoute> } />
                <Route path="/reports/payments" element={ <ProtectedRoute> <MainLayout><PaymentHistoryPage /></MainLayout> </ProtectedRoute> } />
                <Route path="/reports/transfers" element={ <ProtectedRoute> <MainLayout><TransferHistoryPage /></MainLayout> </ProtectedRoute> } />
                <Route path="/reports/receives" element={ <ProtectedRoute> <MainLayout><ReceiveHistoryPage /></MainLayout> </ProtectedRoute> } />
                 
                 <Route path="/admin/requests" element={ <AdminRoute><MainLayout><AdminRequestPage /></MainLayout></AdminRoute> } />

      

       
        <Route
            path="/"
            element={
                <ProtectedRoute>
                    <MainLayout>
                        <HomePage />
                    </MainLayout>
                </ProtectedRoute>
            }
        />

       
        <Route
            path="/dashboard"
            element={
                <ProtectedRoute>
                    <MainLayout>
                        <DashboardPage />
                    </MainLayout>
                </ProtectedRoute>
            }
        />

         
        <Route
            path="/cards"
            element={
                <ProtectedRoute>
                    <MainLayout>
                        <CardManagementPage />
                    </MainLayout>
                </ProtectedRoute>
            }
        />

         
         <Route
            path="/payments"
            element={
                <ProtectedRoute>
                    <MainLayout>
                        <PaymentPage />
                    </MainLayout>
                </ProtectedRoute>
            }
        />

        <Route
                    path="/reports"
                    element={
                        <ProtectedRoute>
                            <MainLayout>
                                <ReportsPage />
                            </MainLayout>
                        </ProtectedRoute>
                    }
                />
        <Route
            path="/reports/payments" 
            element={
                <ProtectedRoute>
                    <MainLayout>
                        <PaymentHistoryPage />
                    </MainLayout>
                </ProtectedRoute>
            }
        />
        <Route
                    path="/reports/transfers" 
                    element={
                        <ProtectedRoute>
                            <MainLayout>
                                <TransferHistoryPage /> 
                            </MainLayout>
                        </ProtectedRoute>
                    }
                />

        <Route
                    path="/reports/receives" 
                    element={
                        <ProtectedRoute>
                            <MainLayout>
                                <ReceiveHistoryPage /> 
                            </MainLayout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/requests" 
                    element={
                        <AdminRoute> 
                            <MainLayout>
                                <AdminRequestPage />
                            </MainLayout>
                        </AdminRoute>
                    }
                />
                 <Route
                    path="/requests"
                    element={
                        <ProtectedRoute>
                            <MainLayout>
                                <UserRequestsPage />
                            </MainLayout>
                        </ProtectedRoute>
                    }
                />
                 <Route
                    path="/invoices" 
                    element={
                        <ProtectedRoute>
                            <MainLayout>
                                <InvoiceListPage />
                            </MainLayout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/invoices/new" 
                    element={
                        <ProtectedRoute> 
                            <MainLayout>
                                <CreateInvoicePage />
                            </MainLayout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/invoices/:invoiceId" 
                    element={
                        <ProtectedRoute>
                            <MainLayout>
                                <InvoiceDetailPage /> 
                            </MainLayout>
                        </ProtectedRoute>
                    }
                />
       
        <Route path="*" element={
             <div style={{ textAlign: 'center', marginTop: '50px', color: 'var(--text-color-dark)' }}>
                 <h2>404 - صفحه مورد نظر یافت نشد</h2>
             </div>
        } />

      </Routes>
    </Router>
  );
}

export default App;