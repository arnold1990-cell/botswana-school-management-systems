import { createContext, useContext, useEffect, useState } from 'react';
import api from '../api/client';
import { User } from '../types/auth';

type Ctx = { user?: User; login: (email: string, password: string) => Promise<void>; logout: () => void };
const AuthContext = createContext<Ctx>({ login: async()=>{}, logout: ()=>{} });
export const useAuth = ()=>useContext(AuthContext);
export const AuthProvider = ({children}:{children:React.ReactNode})=>{
  const [user,setUser]=useState<User>();
  const load = async()=>{ try { const r=await api.get('/users/me'); setUser(r.data); } catch { setUser(undefined); } };
  useEffect(()=>{ if(localStorage.getItem('accessToken')) load();},[]);
  const login = async(email:string,password:string)=>{ const r=await api.post('/auth/login',{email,password}); localStorage.setItem('accessToken',r.data.accessToken); localStorage.setItem('refreshToken',r.data.refreshToken); await load(); };
  const logout = ()=>{ localStorage.clear(); setUser(undefined); };
  return <AuthContext.Provider value={{user,login,logout}}>{children}</AuthContext.Provider>
}
