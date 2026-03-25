import { FormEvent, useEffect, useState } from 'react';
import { AxiosError } from 'axios';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { SESSION_EXPIRED_MESSAGE_KEY } from '../api/client';

export const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const { login, user, loading: authLoading } = useAuth();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const sessionExpired = params.get('sessionExpired') === '1';
    const storedMessage = sessionStorage.getItem(SESSION_EXPIRED_MESSAGE_KEY);

    if (sessionExpired || storedMessage) {
      setError(storedMessage ?? 'Your session expired. Please sign in again.');
      sessionStorage.removeItem(SESSION_EXPIRED_MESSAGE_KEY);
    }
  }, [location.search]);

  useEffect(() => {
    if (authLoading || !user) {
      return;
    }

    const targetRoute =
      user.role === 'TEACHER' ? '/teacher/dashboard'
        : user.role === 'STUDENT' ? '/student/dashboard'
          : user.role === 'PARENT' ? '/parent/dashboard'
            : user.role === 'ACCOUNTANT' ? '/accountant/dashboard'
              : '/dashboard';
    navigate(targetRoute, { replace: true });
  }, [authLoading, navigate, user]);

  const onSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      const user = await login(email, password);
      const targetRoute =
      user.role === 'TEACHER' ? '/teacher/dashboard'
        : user.role === 'STUDENT' ? '/student/dashboard'
          : user.role === 'PARENT' ? '/parent/dashboard'
            : user.role === 'ACCOUNTANT' ? '/accountant/dashboard'
              : '/dashboard';
      navigate(targetRoute, { replace: true });
    } catch (err) {
      if (import.meta.env.DEV) {
        console.error('Login failed', err);
      }

      const fallbackMessage = 'Invalid email or password. Please try again.';
      const apiMessage =
        err instanceof AxiosError && typeof err.response?.data?.message === 'string'
          ? err.response.data.message
          : null;

      setError(apiMessage ?? fallbackMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className='login-page'>
      <section className='login-panel'>
        <p className='section-eyebrow'>Welcome back</p>
        <h1>Sign in to BOSAMS</h1>
        <p className='muted'>Manage academics, exams, reports, and teachers from one dashboard.</p>

        <form className='login-form' onSubmit={onSubmit}>
          <label htmlFor='email'>Email</label>
          <input
            id='email'
            type='email'
            autoComplete='email'
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder='admin@school.ac.bw'
            required
          />

          <label htmlFor='password'>Password</label>
          <input
            id='password'
            type='password'
            autoComplete='current-password'
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder='Enter your password'
            required
          />

          <button className='btn btn-primary' type='submit' disabled={loading}>
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
          {error && <p className='login-error'>{error}</p>}
          <p className='muted' style={{ marginTop: 12 }}>
            <Link to='/reset-password-request'>Forgot student password?</Link>
          </p>
        </form>
      </section>
    </div>
  );
};
