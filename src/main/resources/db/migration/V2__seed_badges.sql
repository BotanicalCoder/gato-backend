INSERT INTO badge (id, code, name, description)
VALUES
  (gen_random_uuid(), 'FIRST_DONE', 'First Completed Todo', 'Awarded for your first completed todo'),
  (gen_random_uuid(), 'STREAK_7', '7-Day Streak', 'Completed at least one todo daily for 7 days');
