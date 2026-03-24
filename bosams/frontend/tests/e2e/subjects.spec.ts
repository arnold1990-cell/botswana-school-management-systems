import { expect, test } from '@playwright/test';

const teacherUser = {
  id: '2f0f8d0d-8f95-4fd0-9793-0b6f97830dad',
  fullName: 'Teacher Test',
  email: 'teacher@bosams.local',
  role: 'TEACHER',
};

const subjects = [
  { id: 1, name: 'English', code: 'PRIMARY_ENGLISH', level: 'PRIMARY', gradeFrom: 1, gradeTo: 4, elective: false },
  { id: 2, name: 'Mathematics', code: 'JUNIOR_MATHEMATICS', level: 'JUNIOR_SECONDARY', gradeFrom: 8, gradeTo: 10, elective: false },
];

test.beforeEach(async ({ page }) => {
  await page.addInitScript(() => {
    localStorage.setItem('bosams_access_token', 'fake-token');
  });

  await page.route('**/api/me', async (route) => {
    await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(teacherUser) });
  });
});

test('renders subject rows on success', async ({ page }) => {
  await page.route('**/api/subjects*', async (route) => {
    await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(subjects) });
  });

  await page.goto('/subjects');

  await expect(page.getByRole('cell', { name: 'English' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Mathematics' })).toBeVisible();
});

test('shows empty state correctly', async ({ page }) => {
  await page.route('**/api/subjects*', async (route) => {
    await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([]) });
  });

  await page.goto('/subjects');

  await expect(page.getByText('No subjects found for this filter.')).toBeVisible();
  await expect(page.getByText('Unable to load subjects right now.')).toHaveCount(0);
});

test('shows 401 auth error correctly', async ({ page }) => {
  await page.route('**/api/subjects*', async (route) => {
    await route.fulfill({ status: 401, contentType: 'application/json', body: JSON.stringify({ message: 'Authentication required' }) });
  });

  await page.goto('/subjects');

  await expect(page.getByText('Authentication required. Please sign in again.')).toBeVisible();
});

test('shows 403 access denied correctly', async ({ page }) => {
  await page.route('**/api/subjects*', async (route) => {
    await route.fulfill({ status: 403, contentType: 'application/json', body: JSON.stringify({ message: 'Insufficient permissions' }) });
  });

  await page.goto('/subjects');

  await expect(page.getByText('Access denied.')).toBeVisible();
});

test('shows server error correctly', async ({ page }) => {
  await page.route('**/api/subjects*', async (route) => {
    await route.fulfill({ status: 500, contentType: 'application/json', body: JSON.stringify({ message: 'boom' }) });
  });

  await page.goto('/subjects');

  await expect(page.getByText('Unable to load subjects right now.')).toBeVisible();
});

test('refetches when filters change', async ({ page }) => {
  const requestedUrls: string[] = [];

  await page.route('**/api/subjects*', async (route) => {
    requestedUrls.push(route.request().url());

    const url = new URL(route.request().url());
    const level = url.searchParams.get('level');
    const grade = url.searchParams.get('grade');

    let payload = subjects;
    if (level) {
      payload = payload.filter((subject) => subject.level === level);
    }
    if (grade) {
      payload = payload.filter((subject) => Number(grade) >= subject.gradeFrom && Number(grade) <= subject.gradeTo);
    }

    await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(payload) });
  });

  await page.goto('/subjects');

  await page.getByLabel('Level').selectOption('PRIMARY');
  await page.getByLabel('Class').selectOption('STD_2');

  await expect(page.getByRole('cell', { name: 'English' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Mathematics' })).toHaveCount(0);

  expect(requestedUrls.some((url) => url.includes('/api/subjects?level=PRIMARY'))).toBeTruthy();
  expect(requestedUrls.some((url) => url.includes('/api/subjects?level=PRIMARY&grade=2'))).toBeTruthy();
});
