import { expect, test } from '@playwright/test';

const teacherUser = {
  id: '2f0f8d0d-8f95-4fd0-9793-0b6f97830dad',
  fullName: 'Teacher Test',
  email: 'teacher@bosams.local',
  role: 'TEACHER',
};

const subjects = [
  { id: 1, name: 'English', code: 'PRIMARY_ENGLISH', schoolLevel: 'PRIMARY', gradeFrom: 1, gradeTo: 4 },
  { id: 2, name: 'Mathematics', code: 'JUNIOR_MATHEMATICS', schoolLevel: 'JUNIOR_SECONDARY', gradeFrom: 8, gradeTo: 10 },
];

test.beforeEach(async ({ page }) => {
  await page.addInitScript(() => {
    localStorage.setItem('bosams_access_token', 'fake-token');
  });

  await page.route('**/api/me', async (route) => {
    await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(teacherUser) });
  });
});

test('subjects page renders rows from api data and filters by level', async ({ page }) => {
  let sawAuthHeader = false;
  await page.route('**/api/subjects*', async (route) => {
    sawAuthHeader = route.request().headers()['authorization'] === 'Bearer fake-token';
    const url = new URL(route.request().url());
    const level = url.searchParams.get('level');
    const payload = level ? subjects.filter((subject) => subject.schoolLevel === level) : subjects;
    await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(payload) });
  });

  await page.goto('/subjects');

  await expect(page.getByRole('cell', { name: 'English' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Mathematics' })).toBeVisible();

  await page.getByLabel('Level').selectOption('PRIMARY');

  await expect(page.getByRole('cell', { name: 'English' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Mathematics' })).toHaveCount(0);
  expect(sawAuthHeader).toBeTruthy();
});

test('subjects page shows empty state only for successful no-match filter', async ({ page }) => {
  await page.route('**/api/subjects*', async (route) => {
    await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([]) });
  });

  await page.goto('/subjects');

  await expect(page.getByText('No subjects found for this filter.')).toBeVisible();
  await expect(page.getByText('Unable to load subjects')).toHaveCount(0);
});

test('subjects page shows error state and retry button when api fails', async ({ page }) => {
  let shouldFail = true;

  await page.route('**/api/subjects*', async (route) => {
    if (shouldFail) {
      await route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'Subject service unavailable' }),
      });
      return;
    }

    await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(subjects) });
  });

  await page.goto('/subjects');

  await expect(page.getByText('Subject service unavailable')).toBeVisible();
  await expect(page.getByRole('button', { name: 'Retry' })).toBeVisible();
  await expect(page.getByText('No subjects found for this filter.')).toHaveCount(0);

  shouldFail = false;
  await page.getByRole('button', { name: 'Retry' }).click();

  await expect(page.getByRole('cell', { name: 'English' })).toBeVisible();
});

test('subjects page maps 401 to authentication required message', async ({ page }) => {
  await page.route('**/api/subjects*', async (route) => {
    await route.fulfill({
      status: 401,
      contentType: 'application/json',
      body: JSON.stringify({ message: 'Authentication required' }),
    });
  });

  await page.goto('/subjects');

  await expect(page.getByText('Authentication required. Please sign in again.')).toBeVisible();
});

test('subjects page maps 403 to access denied message', async ({ page }) => {
  await page.route('**/api/subjects*', async (route) => {
    await route.fulfill({
      status: 403,
      contentType: 'application/json',
      body: JSON.stringify({ message: 'Insufficient permissions' }),
    });
  });

  await page.goto('/subjects');

  await expect(page.getByText('Access denied. You do not have permission to view subjects.')).toBeVisible();
});
