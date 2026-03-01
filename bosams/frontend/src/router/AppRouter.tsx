import { Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from '../auth/AuthContext';
import { AppLayout } from '../layout/AppLayout';
import { Login } from '../pages/Login';
import { DashboardPage, TeacherDashboardPage, TeachersPage } from '../pages/Pages';
import { LearnersPage } from '../pages/LearnersPage';
import { SubjectsPage } from '../pages/SubjectsPage';
import { MarksEntryPage } from '../pages/MarksEntryPage';
import { ReportsPage } from '../pages/ReportsPage';
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
              <Route path='learners' element={<LearnersPage />} />
            </Route>

            <Route element={<RoleProtectedRoute allowedRoles={['ADMIN', 'PRINCIPAL', 'TEACHER']} />}>
              <Route path='teacher/dashboard' element={<TeacherDashboardPage />} />
              <Route path='subjects' element={<SubjectsPage />} />
              <Route path='marks-entry' element={<MarksEntryPage />} />
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
