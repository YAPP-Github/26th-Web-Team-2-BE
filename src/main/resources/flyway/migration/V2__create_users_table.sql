CREATE TABLE users (
       id BIGSERIAL PRIMARY KEY,

       provider TEXT NOT NULL,
       social_id TEXT NOT NULL,
       email TEXT,
       nickname TEXT NOT NULL,
       profile_image TEXT,

       created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
       deleted_at TIMESTAMPTZ,

       CONSTRAINT uq_provider_social_id UNIQUE (provider, social_id)
);
