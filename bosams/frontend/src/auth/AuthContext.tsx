import { createContext, useContext, useEffect, useState } from 'react';
import api from '../api/client';
import { User } from '../types/auth';

type Ctx = { user?: User; login: (email: string, password: string) => Promise<void>; logout: () => void };
type AuthContextValue = Ctx & { loading: boolean };
const AuthContext = createContext<AuthContextValue>({ login: async()=>{}, logout: ()=>{}, loading: true });
export const useAuth = ()=>useContext(AuthContext);
export const AuthProvider = ({children}:{children:React.ReactNode})=>{
  const [user,setUser]=useState<User>();
  const [loading, setLoading] = useState(true);
  const load = async()=>{
    try {
      const r=await api.get('/users/me');
      setUser(r.data);
    } catch {
      setUser(undefined);
    } finally {
      setLoading(false);
    }
  };
  useEffect(()=>{
    if(localStorage.getItem('accessToken')) {
      load();
      return;
    }
    setLoading(false);
  },[]);
  const login = async(email:string,password:string)=>{ const r=await api.post('/auth/login',{email,password}); localStorage.setItem('accessToken',r.data.accessToken); localStorage.setItem('refreshToken',r.data.refreshToken); await load(); };
  const logout = ()=>{ localStorage.clear(); setUser(undefined); };
  return <AuthContext.Provider value={{user,login,logout,loading}}>{children}</AuthContext.Provider>
}
