CREATE TABLE users (
       id BIGSERIAL PRIMARY KEY,

       provider TEXT,
       social_id TEXT,
       email TEXT,
       nickname TEXT,
       profile_image TEXT,

       created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
       deleted_at TIMESTAMPTZ,

       CONSTRAINT uq_provider_social_id UNIQUE (provider, social_id)
);
