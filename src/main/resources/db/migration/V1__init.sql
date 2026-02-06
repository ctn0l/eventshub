CREATE TABLE users (
   id          BIGSERIAL PRIMARY KEY,
   email       VARCHAR(255) NOT NULL UNIQUE,
   password    VARCHAR(255) NOT NULL,
   role        VARCHAR(50)  NOT NULL,
   created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
   updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE events (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    starts_at   TIMESTAMP    NOT NULL,
    ends_at     TIMESTAMP,
    location    VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE tickets (
     id          BIGSERIAL PRIMARY KEY,
     event_id    BIGINT       NOT NULL REFERENCES events(id) ON DELETE CASCADE,
     user_id     BIGINT       REFERENCES users(id) ON DELETE SET NULL,
     price_cents INTEGER      NOT NULL,
     status      VARCHAR(50)  NOT NULL,
     purchased_at TIMESTAMP   NOT NULL DEFAULT NOW()
);