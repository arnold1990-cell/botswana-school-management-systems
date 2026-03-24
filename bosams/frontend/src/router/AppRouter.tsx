import { Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from '../auth/AuthContext';
import { AppLayout } from '../layout/AppLayout';
import { Login } from '../pages/Login';
import { AccountantDashboardPage, DashboardPage, ParentDashboardPage, StudentDashboardPage, TeacherDashboardPage } from '../pages/Pages';
import { TeachersPage } from '../pages/TeachersPage';
import { LearnersPage } from '../pages/LearnersPage';
import { SubjectsPage } from '../pages/SubjectsPage';
import { MarksEntryPage } from '../pages/MarksEntryPage';
import { ReportsPage } from '../pages/ReportsPage';
import { AnnouncementsPage, AssignmentsPage, AttendancePage, ExamsPage, FeesPage, HolidaysPage, LibraryPage, MessagesPage, SettingsPage, TimetablePage } from '../pages/BosamsModulePages';
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
              <Route path='learners' element={<LearnersPage />} />
              <Route path='teacher/dashboard' element={<TeacherDashboardPage />} />
              <Route path='subjects' element={<SubjectsPage />} />
              <Route path='marks-entry' element={<MarksEntryPage />} />
              <Route path='reports' element={<ReportsPage />} />
              <Route path='attendance' element={<AttendancePage />} />
              <Route path='announcements' element={<AnnouncementsPage />} />
              <Route path='timetable' element={<TimetablePage />} />
              <Route path='assignments' element={<AssignmentsPage />} />
              <Route path='exams' element={<ExamsPage />} />
              <Route path='fees' element={<FeesPage />} />
              <Route path='library' element={<LibraryPage />} />
              <Route path='holidays' element={<HolidaysPage />} />
              <Route path='messages' element={<MessagesPage />} />
              <Route path='settings' element={<SettingsPage />} />
            </Route>

            <Route element={<RoleProtectedRoute allowedRoles={['STUDENT']} />}>
              <Route path='student/dashboard' element={<StudentDashboardPage />} />
              <Route path='announcements' element={<AnnouncementsPage />} />
              <Route path='assignments' element={<AssignmentsPage />} />
              <Route path='attendance' element={<AttendancePage />} />
              <Route path='timetable' element={<TimetablePage />} />
            </Route>

            <Route element={<RoleProtectedRoute allowedRoles={['PARENT']} />}>
              <Route path='parent/dashboard' element={<ParentDashboardPage />} />
              <Route path='announcements' element={<AnnouncementsPage />} />
              <Route path='attendance' element={<AttendancePage />} />
              <Route path='timetable' element={<TimetablePage />} />
              <Route path='fees' element={<FeesPage />} />
            </Route>

            <Route element={<RoleProtectedRoute allowedRoles={['ACCOUNTANT']} />}>
              <Route path='accountant/dashboard' element={<AccountantDashboardPage />} />
              <Route path='reports' element={<ReportsPage />} />
              <Route path='fees' element={<FeesPage />} />
              <Route path='announcements' element={<AnnouncementsPage />} />
            </Route>

            <Route path='*' element={<Navigate to='/dashboard' replace />} />
          </Route>
        </Route>

        <Route path='*' element={<Navigate to='/dashboard' replace />} />
      </Routes>
    </AuthProvider>
  );
};
