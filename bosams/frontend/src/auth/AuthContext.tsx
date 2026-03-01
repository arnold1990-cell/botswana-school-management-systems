import { createContext, useContext, useEffect, useState } from 'react';
import http, { ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY } from '../api/http';
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
      const r=await http.get('/users/me');
      setUser(r.data);
    } catch {
      setUser(undefined);
    } finally {
      setLoading(false);
    }
  };
  useEffect(()=>{
    if(localStorage.getItem(ACCESS_TOKEN_KEY)) {
      load();
      return;
    }
    setLoading(false);
  },[]);
  const login = async(email:string,password:string)=>{ const r=await http.post('/auth/login',{email,password}); localStorage.setItem(ACCESS_TOKEN_KEY,r.data.accessToken); localStorage.setItem(REFRESH_TOKEN_KEY,r.data.refreshToken); await load(); };
  const logout = ()=>{ localStorage.removeItem(ACCESS_TOKEN_KEY); localStorage.removeItem(REFRESH_TOKEN_KEY); setUser(undefined); };
  return <AuthContext.Provider value={{user,login,logout,loading}}>{children}</AuthContext.Provider>
}
