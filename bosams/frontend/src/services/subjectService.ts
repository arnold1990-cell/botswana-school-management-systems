import { AxiosError } from 'axios';
import api from '../api/client';

export type SubjectLevel = 'PRIMARY' | 'JUNIOR_SECONDARY' | 'SENIOR_SECONDARY';

export type Subject = {
  id: number;
  name: string;
  code: string;
  level: SubjectLevel;
  gradeFrom: number;
  gradeTo: number;
  elective: boolean;
};

export type SubjectFilters = {
  level?: SubjectLevel;
  grade?: number;
};

export type SubjectServiceErrorType = 'UNAUTHORIZED' | 'FORBIDDEN' | 'SERVER' | 'NETWORK';

export class SubjectServiceError extends Error {
  type: SubjectServiceErrorType;

  constructor(type: SubjectServiceErrorType, message: string) {
    super(message);
    this.name = 'SubjectServiceError';
    this.type = type;
  }
}

type ApiSubject = {
  id: number;
  name: string;
  code: string;
  level: SubjectLevel;
  gradeFrom: number;
  gradeTo: number;
  elective: boolean;
};

const normalizeSubject = (subject: ApiSubject): Subject => ({
  id: subject.id,
  name: subject.name,
  code: subject.code,
  level: subject.level,
  gradeFrom: subject.gradeFrom,
  gradeTo: subject.gradeTo,
  elective: subject.elective,
});

export const getSubjects = async (filters: SubjectFilters = {}): Promise<Subject[]> => {
  try {
    const response = await api.get<ApiSubject[]>('/subjects', ({
      params: {
        ...(filters.level ? { level: filters.level } : {}),
        ...(typeof filters.grade === 'number' ? { grade: filters.grade } : {}),
      },
    } as any));

    return Array.isArray(response.data) ? response.data.map(normalizeSubject) : [];
  } catch (error) {
    const axiosError = error as AxiosError<{ message?: string }>;
    const status = axiosError.response?.status;

    if (status === 401) {
      throw new SubjectServiceError('UNAUTHORIZED', 'Authentication required. Please sign in again.');
    }

    if (status === 403) {
      throw new SubjectServiceError('FORBIDDEN', 'Access denied.');
    }

    if (!axiosError.response) {
      throw new SubjectServiceError('NETWORK', 'Unable to load subjects right now.');
    }

    throw new SubjectServiceError('SERVER', 'Unable to load subjects right now.');
  }
};
