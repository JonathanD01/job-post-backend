DROP INDEX IF EXISTS idx_title_description_fulltext;

ALTER TABLE jobposts ADD COLUMN IF NOT EXISTS tsv_document tsvector;

UPDATE jobposts SET tsv_document = to_tsvector(title || ' ' || description);

CREATE index tsv_document_idx ON jobposts USING gin (tsv_document);