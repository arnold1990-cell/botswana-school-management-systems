INSERT INTO users (id, email, password_hash, full_name, role, status, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'teacher@bosams.local',
    crypt('password', gen_salt('bf')),
    'Demo Teacher',
    'TEACHER',
    'ACTIVE',
    now(),
    now()
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'teacher@bosams.local'
);

INSERT INTO users (id, email, password_hash, full_name, role, status, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'principal@bosams.local',
    crypt('password', gen_salt('bf')),
    'Demo Principal',
    'PRINCIPAL',
    'ACTIVE',
    now(),
    now()
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'principal@bosams.local'
);

UPDATE users
SET password_hash = crypt('password', gen_salt('bf')),
    full_name = 'Demo Teacher',
    role = 'TEACHER',
    status = 'ACTIVE',
    updated_at = now()
WHERE email = 'teacher@bosams.local';

UPDATE users
SET password_hash = crypt('password', gen_salt('bf')),
    full_name = 'Demo Principal',
    role = 'PRINCIPAL',
    status = 'ACTIVE',
    updated_at = now()
WHERE email = 'principal@bosams.local';
