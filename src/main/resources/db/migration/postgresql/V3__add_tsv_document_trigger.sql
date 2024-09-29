CREATE OR REPLACE FUNCTION update_tsv_document()
RETURNS TRIGGER AS $$
BEGIN
    NEW.tsv_document := to_tsvector('english', NEW.title || ' ' || NEW.description);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_tsv_document
BEFORE INSERT OR UPDATE ON jobposts
FOR EACH ROW EXECUTE FUNCTION update_tsv_document();