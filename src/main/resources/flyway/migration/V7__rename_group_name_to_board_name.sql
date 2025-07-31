-- TripBoardEntity의 group_name 컬럼을 board_name으로 변경
ALTER TABLE trip_board RENAME COLUMN group_name TO board_name;