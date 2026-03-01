export type Role = 'ADMIN' | 'PRINCIPAL' | 'TEACHER';
export type User = { id: string; fullName: string; email: string; role: Role };
