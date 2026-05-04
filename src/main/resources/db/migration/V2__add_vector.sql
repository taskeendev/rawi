CREATE EXTENSION IF NOT EXISTS vector;

ALTER TABLE content_item ADD COLUMN embedding vector(768);

CREATE INDEX idx_content_item_embedding ON content_item USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
