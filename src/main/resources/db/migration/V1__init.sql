CREATE TABLE app_user (
  id            UUID PRIMARY KEY,
  email         VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255)       NOT NULL,
  display_name  VARCHAR(100)       NOT NULL,
  points        INT                NOT NULL DEFAULT 0,
  streak_count  INT                NOT NULL DEFAULT 0,
  last_done_on  DATE,
  created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE todo (
  id          UUID PRIMARY KEY,
  user_id     UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
  title       VARCHAR(255) NOT NULL,
  notes       TEXT,
  due_on      DATE,
  is_done     BOOLEAN NOT NULL DEFAULT FALSE,
  done_at     TIMESTAMP,
  created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE badge (
  id          UUID PRIMARY KEY,
  code        VARCHAR(50) UNIQUE NOT NULL,
  name        VARCHAR(100) NOT NULL,
  description VARCHAR(255)
);

CREATE TABLE user_badge (
  user_id  UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
  badge_id UUID NOT NULL REFERENCES badge(id) ON DELETE CASCADE,
  awarded_at TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (user_id, badge_id)
);

CREATE TABLE password_reset_token (
  id         UUID PRIMARY KEY,
  user_id    UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
  token      VARCHAR(64) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used       BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
