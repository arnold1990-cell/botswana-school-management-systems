import { FormEvent, useEffect, useState } from 'react';
import { AxiosError } from 'axios';
import api from '../api/client';
import { useAuth } from '../auth/AuthContext';

type ResetRequest = {
  id: number;
  admissionNo: string;
  studentName: string;
  guardianEmail?: string;
  guardianPhone?: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  adminNote?: string;
  createdAt: string;
};

export const StudentResetPasswordPage = () => {
  const { user } = useAuth();
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [requests, setRequests] = useState<ResetRequest[]>([]);
  const [adminNote, setAdminNote] = useState<Record<number, string>>({});
  const [form, setForm] = useState({
    admissionNo: '',
    studentName: '',
    guardianEmail: '',
    guardianPhone: '',
    reason: '',
  });

  const canManage = user?.role === 'ADMIN' || user?.role === 'PRINCIPAL';

  const load = async () => {
    if (!user) {
      return;
    }
    const response = await api.get<ResetRequest[]>('/password-reset-requests');
    setRequests(response.data);
  };

  useEffect(() => {
    if (canManage || user?.role === 'TEACHER') {
      load();
    }
  }, [canManage, user?.role]);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError('');
    setMessage('');

    try {
      await api.post('/password-reset-requests', form);
      setMessage('Reset request submitted. The school admin will review it shortly.');
      setForm({ admissionNo: '', studentName: '', guardianEmail: '', guardianPhone: '', reason: '' });
    } catch (err) {
      const fallbackMessage = 'Could not submit reset request.';
      const apiMessage = err instanceof AxiosError && typeof err.response?.data?.message === 'string'
        ? err.response.data.message
        : null;
      setError(apiMessage ?? fallbackMessage);
    } finally {
      setLoading(false);
    }
  };

  const updateStatus = async (id: number, status: 'APPROVED' | 'REJECTED') => {
    await api.patch(`/password-reset-requests/${id}`, {
      status,
      adminNote: adminNote[id] ?? '',
    });
    setMessage(`Request #${id} ${status.toLowerCase()}.`);
    await load();
  };

  return (
    <section>
      <div className='page-header'>
        <div>
          <p className='section-eyebrow'>Students</p>
          <h2>Students Reset Password</h2>
          <p className='muted'>Submit and manage student password reset requests.</p>
        </div>
      </div>

      {message && <p className='muted'>{message}</p>}
      {error && <p className='login-error'>{error}</p>}

      <article className='card'>
        <h3>Request Password Reset</h3>
        <form className='form-grid form-grid-3' onSubmit={submit}>
          <input placeholder='Admission No' value={form.admissionNo} onChange={(e) => setForm({ ...form, admissionNo: e.target.value })} required />
          <input placeholder='Student Name' value={form.studentName} onChange={(e) => setForm({ ...form, studentName: e.target.value })} required />
          <input placeholder='Guardian Email (optional)' type='email' value={form.guardianEmail} onChange={(e) => setForm({ ...form, guardianEmail: e.target.value })} />
          <input placeholder='Guardian Phone (optional)' value={form.guardianPhone} onChange={(e) => setForm({ ...form, guardianPhone: e.target.value })} />
          <input style={{ gridColumn: '1 / -1' }} placeholder='Reason for reset' value={form.reason} onChange={(e) => setForm({ ...form, reason: e.target.value })} required />
          <button className='btn btn-primary' type='submit' disabled={loading}>{loading ? 'Submitting…' : 'Submit request'}</button>
        </form>
      </article>

      {(canManage || user?.role === 'TEACHER') && (
        <article className='card' style={{ marginTop: 12 }}>
          <h3>Reset Requests Queue</h3>
          <table className='table'>
            <thead><tr><th>Admission</th><th>Student</th><th>Contact</th><th>Reason</th><th>Status</th><th>Action</th></tr></thead>
            <tbody>
              {requests.map((request) => (
                <tr key={request.id}>
                  <td>{request.admissionNo}</td>
                  <td>{request.studentName}</td>
                  <td>{request.guardianEmail || '-'}<br />{request.guardianPhone || ''}</td>
                  <td>{request.reason}</td>
                  <td><span className='badge'>{request.status}</span></td>
                  <td>
                    {canManage && request.status === 'PENDING' ? (
                      <>
                        <input
                          placeholder='Admin note'
                          value={adminNote[request.id] ?? ''}
                          onChange={(e) => setAdminNote((prev) => ({ ...prev, [request.id]: e.target.value }))}
                        />
                        <div style={{ display: 'flex', gap: 8, marginTop: 8 }}>
                          <button className='btn btn-primary' onClick={() => updateStatus(request.id, 'APPROVED')}>Approve</button>
                          <button className='btn btn-secondary' onClick={() => updateStatus(request.id, 'REJECTED')}>Reject</button>
                        </div>
                      </>
                    ) : request.adminNote || '-'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </article>
      )}
    </section>
  );
};
