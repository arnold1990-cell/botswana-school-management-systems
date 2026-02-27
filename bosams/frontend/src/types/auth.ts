export type Role = 'ADMIN' | 'PRINCIPAL' | 'TEACHER';
export type User = { id: number; fullName: string; email: string; role: Role };
