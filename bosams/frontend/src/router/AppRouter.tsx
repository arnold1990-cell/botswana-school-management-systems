import { Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from '../auth/AuthContext';
import { Layout } from '../components/Layout';
import { DashboardPage, LoginPage, AcademicsPage, ExamsPage, ReportsPage, TeachersPage } from '../pages/Pages';

const Guard = ({children,roles}:{children:JSX.Element,roles?:string[]})=>{ const {user}=useAuth(); if(!user) return <Navigate to='/login'/>; if(roles && !roles.includes(user.role)) return <div>Not authorized</div>; return children; };

export const AppRouter = ()=> <AuthProvider><Routes>
  <Route path='/login' element={<LoginPage/>}/>
  <Route path='/' element={<Guard><Layout/></Guard>}>
    <Route index element={<DashboardPage/>}/>
    <Route path='academics' element={<AcademicsPage/>}/>
    <Route path='exams' element={<ExamsPage/>}/>
    <Route path='reports' element={<ReportsPage/>}/>
    <Route path='teachers' element={<Guard roles={['ADMIN','PRINCIPAL']}><TeachersPage/></Guard>}/>
  </Route>
</Routes></AuthProvider>;
