UPDATE users
SET password_hash = crypt('password', gen_salt('bf')),
    updated_at = now()
WHERE email = 'admin@bosams.local';
