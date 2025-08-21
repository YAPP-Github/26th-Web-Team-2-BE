-- 여행보드 테이블에 다음 비교표 번호 필드 추가
ALTER TABLE trip_board 
ADD COLUMN next_comparison_table_number INTEGER NOT NULL DEFAULT 1;