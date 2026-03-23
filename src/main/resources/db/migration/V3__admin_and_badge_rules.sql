ALTER TABLE app_user
    ADD COLUMN is_admin BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE badge
    ADD COLUMN min_points INT,
    ADD COLUMN min_streak_count INT,
    ADD COLUMN min_completed_todos INT;

UPDATE badge
SET min_completed_todos = 1
WHERE code = 'FIRST_DONE' AND min_completed_todos IS NULL;

UPDATE badge
SET min_streak_count = 7
WHERE code = 'STREAK_7' AND min_streak_count IS NULL;
