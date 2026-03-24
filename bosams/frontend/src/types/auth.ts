export type Role = 'ADMIN' | 'PRINCIPAL' | 'TEACHER' | 'STUDENT' | 'PARENT' | 'ACCOUNTANT';
export type User = { id: string; fullName: string; email: string; role: Role };
