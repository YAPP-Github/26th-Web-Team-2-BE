-- TripBoardEntity에 여행지와 여행 기간 컬럼 추가
ALTER TABLE trip_board 
ADD COLUMN destination VARCHAR(20) NOT NULL DEFAULT '',
ADD COLUMN start_date DATE NOT NULL DEFAULT CURRENT_DATE,
ADD COLUMN end_date DATE NOT NULL DEFAULT CURRENT_DATE;

-- 기본값 제거 (새로운 레코드에는 명시적으로 값을 제공해야 함)
ALTER TABLE trip_board 
ALTER COLUMN destination DROP DEFAULT,
ALTER COLUMN start_date DROP DEFAULT,
ALTER COLUMN end_date DROP DEFAULT;