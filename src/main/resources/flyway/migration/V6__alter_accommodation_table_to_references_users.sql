
ALTER TABLE accommodation
    ADD CONSTRAINT FK_ACCOMMODATION_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

-- ALTER TABLE accommodation
--     DROP COLUMN IF EXISTS table_id;