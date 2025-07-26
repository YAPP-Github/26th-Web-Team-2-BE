CREATE TABLE accommodation (
   id BIGSERIAL PRIMARY KEY,

   url TEXT,
   site_name TEXT,
   memo TEXT,
   created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
   created_by BIGINT,
   table_id BIGINT,

   accommodation_name TEXT,
   images JSONB,
   address TEXT,
   latitude DOUBLE PRECISION,
   longitude DOUBLE PRECISION,
   lowest_price INTEGER,
   highest_price INTEGER,
   currency VARCHAR(3),
   review_score DOUBLE PRECISION,
   cleanliness_score DOUBLE PRECISION,
   review_summary TEXT,
   hotel_id BIGINT,

   nearby_attractions JSONB,
   nearby_transportation JSONB,
   amenities JSONB,
   check_in_time JSONB,
   check_out_time JSONB
);
