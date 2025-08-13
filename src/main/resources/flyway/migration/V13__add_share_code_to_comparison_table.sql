-- 비교표에 공유 코드 컬럼 추가 (32자리 UUID: f47ac10b58cc4372a5670e02b2c3d479 형태)
-- NULL을 허용하는 컬럼 추가
ALTER TABLE comparison_table 
ADD COLUMN share_code VARCHAR(32);

-- 기존 데이터에 대해 고유한 공유 코드 생성 (UUID 기반)
UPDATE comparison_table 
SET share_code = REPLACE(gen_random_uuid()::TEXT, '-', '')
WHERE share_code IS NULL;

-- 모든 데이터에 값이 들어간 후 NOT NULL 제약조건 추가
ALTER TABLE comparison_table 
ALTER COLUMN share_code SET NOT NULL;

-- UNIQUE 제약조건 추가 (자동으로 인덱스도 생성)
ALTER TABLE comparison_table 
ADD CONSTRAINT uk_comparison_table_share_code UNIQUE (share_code);