import { FormEvent, useEffect, useState } from 'react';
import { AxiosError } from 'axios';
import { useNavigate } from 'react-router-dom';
import http, { ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY } from '../api/http';

export const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    if (localStorage.getItem(ACCESS_TOKEN_KEY)) {
      navigate('/dashboard', { replace: true });
    }
  }, [navigate]);

  const onSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await http.post('/auth/login', { email, password });
      localStorage.setItem(ACCESS_TOKEN_KEY, response.data.accessToken);
      localStorage.setItem(REFRESH_TOKEN_KEY, response.data.refreshToken);
      navigate('/dashboard', { replace: true });
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
        </form>
      </section>
    </div>
  );
};
