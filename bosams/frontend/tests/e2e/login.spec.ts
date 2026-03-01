import { expect, test } from '@playwright/test';

test('user can log in and land on dashboard', async ({ page }) => {
  await page.goto('/login');

  await page.locator('input').first().fill('admin@bosams.local');
  await page.locator('input[type="password"]').fill('password');
  await page.getByRole('button', { name: 'Login' }).click();

  await expect(page).toHaveURL(/\/$/);
  await expect(page.getByRole('heading', { name: 'Dashboard' })).toBeVisible();
  await expect(page.getByText('My Profile')).toBeVisible();
});
