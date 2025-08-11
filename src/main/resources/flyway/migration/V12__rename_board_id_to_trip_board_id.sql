-- accommodation 테이블의 board_id 컬럼을 trip_board_id로 변경
-- AccommodationEntity의 @Column(name = "trip_board_id") 매핑과 일치시키기 위함

ALTER TABLE accommodation 
    RENAME COLUMN board_id TO trip_board_id;

-- 성능 최적화를 위한 인덱스 추가
CREATE INDEX IF NOT EXISTS idx_accommodation_trip_board_id ON accommodation(trip_board_id);

-- trip_board 테이블과의 외래키 제약조건 추가 (데이터 무결성 보장)
ALTER TABLE accommodation 
    ADD CONSTRAINT FK_ACCOMMODATION_ON_TRIP_BOARD_ID FOREIGN KEY (trip_board_id) REFERENCES trip_board (id) ON DELETE CASCADE;