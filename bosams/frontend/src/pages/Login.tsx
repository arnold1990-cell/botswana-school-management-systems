import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import http, { ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY } from '../api/http';

export const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const onSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await http.post('/auth/login', { email, password });
      localStorage.setItem(ACCESS_TOKEN_KEY, response.data.accessToken);
      localStorage.setItem(REFRESH_TOKEN_KEY, response.data.refreshToken);
      navigate('/dashboard');
    } catch (err) {
      if (import.meta.env.DEV) {
        console.error('Login failed', err);
      }
      setError('Invalid email or password. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className='login-page'>
      <form className='login-form' onSubmit={onSubmit}>
        <h1>Bosams Web</h1>
        <label htmlFor='email'>Email</label>
        <input
          id='email'
          type='email'
          autoComplete='email'
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <label htmlFor='password'>Password</label>
        <input
          id='password'
          type='password'
          autoComplete='current-password'
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button type='submit' disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
        {error && <p className='login-error'>{error}</p>}
      </form>
    </div>
  );
};
