-- 비교표에 공유 코드 컬럼 추가 (32자리 UUID 형태)
ALTER TABLE comparison_table 
ADD COLUMN share_code VARCHAR(32) UNIQUE NOT NULL ;

-- 기존 데이터에 대해 고유한 공유 코드 생성 (UUID 기반)
UPDATE comparison_table 
SET share_code = REPLACE(gen_random_uuid()::TEXT, '-', '')
WHERE share_code IS NULL;
