import { useEffect, useMemo, useState } from 'react';
import { useAuthReady } from '../auth/useAuthReady';
import { getSubjects, Subject, SubjectLevel, SubjectServiceError } from '../services/subjectService';

type LevelFilter = 'ALL' | SubjectLevel;
type ClassFilter = 'ALL' | 'STD_1' | 'STD_2' | 'STD_3' | 'STD_4' | 'STD_5' | 'STD_6' | 'STD_7' | 'FORM_1' | 'FORM_2' | 'FORM_3' | 'FORM_4' | 'FORM_5';

type LoadState = 'loading' | 'success' | 'empty' | 'authError' | 'accessDenied' | 'serverError';

const classOptions: Array<{ value: ClassFilter; label: string; level: SubjectLevel; grade: number }> = [
  { value: 'STD_1', label: 'Standard 1', level: 'PRIMARY', grade: 1 },
  { value: 'STD_2', label: 'Standard 2', level: 'PRIMARY', grade: 2 },
  { value: 'STD_3', label: 'Standard 3', level: 'PRIMARY', grade: 3 },
  { value: 'STD_4', label: 'Standard 4', level: 'PRIMARY', grade: 4 },
  { value: 'STD_5', label: 'Standard 5', level: 'PRIMARY', grade: 5 },
  { value: 'STD_6', label: 'Standard 6', level: 'PRIMARY', grade: 6 },
  { value: 'STD_7', label: 'Standard 7', level: 'PRIMARY', grade: 7 },
  { value: 'FORM_1', label: 'Form 1', level: 'JUNIOR_SECONDARY', grade: 8 },
  { value: 'FORM_2', label: 'Form 2', level: 'JUNIOR_SECONDARY', grade: 9 },
  { value: 'FORM_3', label: 'Form 3', level: 'JUNIOR_SECONDARY', grade: 10 },
  { value: 'FORM_4', label: 'Form 4', level: 'SENIOR_SECONDARY', grade: 11 },
  { value: 'FORM_5', label: 'Form 5', level: 'SENIOR_SECONDARY', grade: 12 },
];

const levelLabel = (level: SubjectLevel) => level.replace(/_/g, ' ');

const classRangeLabel = (subject: Subject) => {
  const formatGrade = (grade: number) => (grade <= 7 ? `Standard ${grade}` : `Form ${grade - 7}`);
  if (subject.gradeFrom === subject.gradeTo) {
    return formatGrade(subject.gradeFrom);
  }
  return `${formatGrade(subject.gradeFrom)} - ${formatGrade(subject.gradeTo)}`;
};

const mapStateFromError = (error: unknown): LoadState => {
  if (error instanceof SubjectServiceError) {
    if (error.type === 'UNAUTHORIZED') return 'authError';
    if (error.type === 'FORBIDDEN') return 'accessDenied';
  }
  return 'serverError';
};

export const SubjectsPage = () => {
  const { authReady, authLoading, isAuthenticated } = useAuthReady();
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [level, setLevel] = useState<LevelFilter>('ALL');
  const [selectedClass, setSelectedClass] = useState<ClassFilter>('ALL');
  const [state, setState] = useState<LoadState>('loading');

  const filteredClassOptions = useMemo(() => {
    if (level === 'ALL') {
      return classOptions;
    }
    return classOptions.filter((option) => option.level === level);
  }, [level]);

  useEffect(() => {
    if (selectedClass === 'ALL') {
      return;
    }

    const selectedOption = classOptions.find((option) => option.value === selectedClass);
    if (!selectedOption) {
      setSelectedClass('ALL');
      return;
    }

    if (level !== 'ALL' && selectedOption.level !== level) {
      setSelectedClass('ALL');
    }
  }, [level, selectedClass]);

  useEffect(() => {
    if (!authReady || !isAuthenticated) return;
    const selectedOption = classOptions.find((option) => option.value === selectedClass);
    const grade = selectedOption?.grade;
    const requestLevel = level !== 'ALL' ? level : selectedOption?.level;

    const loadSubjects = async () => {
      setState('loading');

      try {
        const nextSubjects = await getSubjects({ level: requestLevel, grade });
        setSubjects(nextSubjects);
        setState(nextSubjects.length > 0 ? 'success' : 'empty');
      } catch (error) {
        setSubjects([]);
        setState(mapStateFromError(error));
      }
    };

    loadSubjects();
  }, [authReady, isAuthenticated, level, selectedClass]);

  return <section>
    <h2>Subjects</h2>
    <article className='card'>
      <div className='toolbar'>
        <label>Level
          <select value={level} onChange={(e) => setLevel(e.target.value as LevelFilter)}>
            <option value='ALL'>All</option>
            <option value='PRIMARY'>Primary</option>
            <option value='JUNIOR_SECONDARY'>Junior Secondary</option>
            <option value='SENIOR_SECONDARY'>Senior Secondary</option>
          </select>
        </label>
        <label>Class
          <select value={selectedClass} onChange={(e) => setSelectedClass(e.target.value as ClassFilter)}>
            <option value='ALL'>All</option>
            {filteredClassOptions.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
          </select>
        </label>
      </div>

      {(authLoading || state === 'loading') && <p>Loading subjects...</p>}
      {state === 'authError' && <p className='muted'>Authentication required. Please sign in again.</p>}
      {state === 'accessDenied' && <p className='muted'>Access denied.</p>}
      {state === 'serverError' && <p className='muted'>Unable to load subjects right now.</p>}
      {state === 'empty' && <p>No subjects found for this filter.</p>}

      {(state === 'success' || state === 'loading') && <table className='table'>
        <thead><tr><th>Name</th><th>Code</th><th>Level</th><th>Class Range</th></tr></thead>
        <tbody>{subjects.map((subject) => <tr key={subject.id}><td>{subject.name}</td><td>{subject.code}</td><td>{levelLabel(subject.level)}</td><td>{classRangeLabel(subject)}</td></tr>)}</tbody>
      </table>}
    </article>
  </section>;
};
