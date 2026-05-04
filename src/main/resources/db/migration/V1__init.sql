CREATE TABLE content_item (
    id          BIGSERIAL PRIMARY KEY,
    url         TEXT NOT NULL,
    title       VARCHAR(500),
    category    VARCHAR(100),
    summary     TEXT,
    image_url   TEXT,
    status      VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    source      VARCHAR(100),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_content_item_status   ON content_item (status);
CREATE INDEX idx_content_item_category ON content_item (category);
