-- 기존 unique constraint 제거
ALTER TABLE users DROP CONSTRAINT uq_provider_social_id;

-- 삭제되지 않은 사용자에 대해서만 unique constraint 적용 (partial unique index)
CREATE UNIQUE INDEX uq_provider_social_id_not_deleted 
ON users (provider, social_id) 
WHERE deleted_at IS NULL;
