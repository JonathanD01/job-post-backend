CREATE INDEX IF NOT EXISTS idx_title_description_fulltext
    ON "jobposts" USING GIN(to_tsvector('english', title || ' ' || description));