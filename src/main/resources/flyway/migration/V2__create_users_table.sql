CREATE TABLE users (
       id BIGSERIAL PRIMARY KEY,

       provider TEXT,
       social_id TEXT,
       email TEXT,
       nickname TEXT,
       profile_image TEXT,

       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       deleted_at TIMESTAMP,

       CONSTRAINT uq_provider_social_id UNIQUE (provider, social_id)
);
