-- V11: 초대 URL을 초대 코드로 변경 및 동시성 안전성 강화
-- 1. 유니크 제약 조건 추가 (user_id, trip_board_id)
-- 2. invitation_url 컬럼을 invitation_code로 변경

-- 1. 기존 유니크 제약 조건 제거 (invitation_url)
ALTER TABLE user_trip_board DROP CONSTRAINT IF EXISTS user_trip_board_invitation_url_key;

-- 2. 컬럼명 변경: invitation_url -> invitation_code
ALTER TABLE user_trip_board RENAME COLUMN invitation_url TO invitation_code;

-- 3. 새로운 유니크 제약 조건 추가 (user_id, trip_board_id) - 중복 참여 방지
ALTER TABLE user_trip_board ADD CONSTRAINT uq_user_trip_board UNIQUE (user_id, trip_board_id);

-- 4. invitation_code에 대한 유니크 제약 조건 재생성
ALTER TABLE user_trip_board ADD CONSTRAINT user_trip_board_invitation_code_key UNIQUE (invitation_code);