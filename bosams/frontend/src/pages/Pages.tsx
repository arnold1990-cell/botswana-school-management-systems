import { FormEvent, useState } from 'react';
import { useAuth } from '../auth/AuthContext';

export const LoginPage=()=>{ const {login}=useAuth(); const [email,setEmail]=useState('admin@bosams.local'); const [password,setPassword]=useState('password');
 const submit=async(e:FormEvent)=>{e.preventDefault(); await login(email,password); window.location.href='/';};
 return <form onSubmit={submit}><h1>Bosams Web</h1><input value={email} onChange={e=>setEmail(e.target.value)}/><input type='password' value={password} onChange={e=>setPassword(e.target.value)}/><button>Login</button></form> }
export const DashboardPage=()=> <div><h2>Dashboard</h2><div className='cards'><div>Calendar</div><div>Leaves (placeholder)</div><div>My Profile</div><div>News</div></div></div>;
export const AcademicsPage=()=> <div><h2>Manage Gradebook</h2><p>Standards, Streams, Subjects, Students tables and forms.</p></div>;
export const ExamsPage=()=> <div><h2>Exams</h2><p>Year plan, schedule management, marks entry.</p></div>;
export const ReportsPage=()=> <div><h2>Reports</h2><p>Student, Subject, Consolidated + PDF export controls.</p></div>;
export const TeachersPage=()=> <div><h2>Teacher Management</h2><p>Assign stream + subject for active year.</p></div>;
