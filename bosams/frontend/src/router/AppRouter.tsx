import { Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from '../auth/AuthContext';
import { AppLayout } from '../layout/AppLayout';
import { Login } from '../pages/Login';
import { DashboardPage, ExamsPage, GradebookPage, ReportsPage, TeacherDashboardPage, TeachersPage } from '../pages/Pages';
import { ProtectedRoute } from '../routes/ProtectedRoute';
import { RoleProtectedRoute } from '../routes/RoleProtectedRoute';

export const AppRouter = () => {
  return (
    <AuthProvider>
      <Routes>
        <Route path='/login' element={<Login />} />

        <Route element={<ProtectedRoute />}>
          <Route path='/' element={<AppLayout />}>
            <Route index element={<Navigate to='/dashboard' replace />} />

            <Route element={<RoleProtectedRoute allowedRoles={['ADMIN', 'PRINCIPAL']} />}>
              <Route path='dashboard' element={<DashboardPage />} />
              <Route path='teachers' element={<TeachersPage />} />
            </Route>

            <Route element={<RoleProtectedRoute allowedRoles={['ADMIN', 'PRINCIPAL', 'TEACHER']} />}>
              <Route path='teacher/dashboard' element={<TeacherDashboardPage />} />
              <Route path='gradebook' element={<GradebookPage />} />
              <Route path='exams' element={<ExamsPage />} />
              <Route path='reports' element={<ReportsPage />} />
            </Route>

            <Route path='*' element={<Navigate to='/dashboard' replace />} />
          </Route>
        </Route>

        <Route path='*' element={<Navigate to='/dashboard' replace />} />
      </Routes>
    </AuthProvider>
  );
};
