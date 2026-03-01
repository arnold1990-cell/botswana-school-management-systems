INSERT INTO users (id, email, password_hash, full_name, role, status, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'admin@bosams.local',
    crypt('Admin@12345', gen_salt('bf')),
    'System Admin',
    'ADMIN',
    'ACTIVE',
    now(),
    now()
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@bosams.local'
);
