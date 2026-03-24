import { FormEvent, useEffect, useState } from 'react';
import api from '../api/client';

type StudentCategory = {
  id: number;
  name: string;
};

export const StudentCategoriesPage = () => {
  const [categories, setCategories] = useState<StudentCategory[]>([]);
  const [name, setName] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const load = async () => {
    try {
      setError('');
      const response = await api.get<StudentCategory[]>('/student-categories', { params: { activeOnly: false } });
      setCategories(response.data ?? []);
    } catch {
      setCategories([]);
      setError('Failed to load categories.');
    }
  };

  useEffect(() => { load(); }, []);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    if (!name.trim()) return;
    try {
      setError('');
      if (editingId) {
        await api.put(`/student-categories/${editingId}`, { name: name.trim() });
        setMessage('Category updated successfully.');
      } else {
        await api.post('/student-categories', { name: name.trim() });
        setMessage('Category created successfully.');
      }
      setName('');
      setEditingId(null);
      await load();
    } catch {
      setError('Unable to save category. Ensure it is unique.');
    }
  };

  const remove = async (id: number) => {
    try {
      setError('');
      await api.delete(`/student-categories/${id}`);
      setMessage('Category deleted.');
      await load();
    } catch {
      setError('Unable to delete category.');
    }
  };

  return (
    <section>
      <div className='page-header'>
        <div>
          <p className='section-eyebrow'>Students</p>
          <h2>Student Category</h2>
        </div>
      </div>
      {message && <p className='muted'>{message}</p>}
      {error && <p className='muted'>{error}</p>}

      <form className='card form-grid form-grid-3' onSubmit={submit}>
        <input placeholder='Category name' value={name} onChange={(e) => setName(e.target.value)} required />
        <button className='btn btn-primary' type='submit'>{editingId ? 'Update Category' : 'Create Category'}</button>
        {editingId && <button className='btn btn-secondary' type='button' onClick={() => { setEditingId(null); setName(''); }}>Cancel</button>}
      </form>

      <article className='card'>
        <h3>Categories</h3>
        <table className='table'>
          <thead><tr><th>Name</th><th>Actions</th></tr></thead>
          <tbody>
            {categories.map((category) => (
              <tr key={category.id}>
                <td>{category.name}</td>
                <td>
                  <button className='btn btn-secondary' onClick={() => { setEditingId(category.id); setName(category.name); }}>Edit</button>{' '}
                  <button className='btn btn-secondary' onClick={() => remove(category.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </article>
    </section>
  );
};
