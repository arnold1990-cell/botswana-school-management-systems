import { Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from '../auth/AuthContext';
import { AppLayout } from '../layout/AppLayout';
import { Login } from '../pages/Login';
import { DashboardPage, ExamsPage, GradebookPage, ReportsPage, TeachersPage } from '../pages/Pages';
import { ProtectedRoute } from '../routes/ProtectedRoute';

export const AppRouter = () => {
  return (
    <AuthProvider>
      <Routes>
        <Route path='/login' element={<Login />} />

        <Route element={<ProtectedRoute />}>
          <Route path='/' element={<AppLayout />}>
            <Route index element={<Navigate to='/dashboard' replace />} />
            <Route path='dashboard' element={<DashboardPage />} />
            <Route path='gradebook' element={<GradebookPage />} />
            <Route path='exams' element={<ExamsPage />} />
            <Route path='reports' element={<ReportsPage />} />
            <Route path='teachers' element={<TeachersPage />} />
            <Route path='*' element={<Navigate to='/dashboard' replace />} />
          </Route>
        </Route>

        <Route path='*' element={<Navigate to='/dashboard' replace />} />
      </Routes>
    </AuthProvider>
  );
};
