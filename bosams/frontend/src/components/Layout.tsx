import { Link, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export const Layout = ()=>{
  const {user}=useAuth();
  const role=user?.role;
  return <div className='app'><aside><h2>Bosams</h2><Link to='/'>Dashboard</Link><Link to='/academics'>Gradebook</Link><Link to='/exams'>Exams</Link><Link to='/reports'>Reports</Link>{role!=='TEACHER'&&<Link to='/teachers'>Teacher Mgmt</Link>}</aside><main><header>Logged in as {user?.fullName} ({role})</header><Outlet/></main></div>
}
