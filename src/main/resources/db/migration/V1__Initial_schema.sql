-- Users & Authentication
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Media Management
CREATE TABLE media (
    id BIGSERIAL PRIMARY KEY,
    storage_key VARCHAR(500) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    mime_type VARCHAR(100),
    size BIGINT,
    width INTEGER,
    height INTEGER,
    owner_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    is_public BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories (Hierarchical + Multi-language)
CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT REFERENCES category(id) ON DELETE SET NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE category_translation (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES category(id) ON DELETE CASCADE,
    lang VARCHAR(5) NOT NULL,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    CONSTRAINT uk_category_translation_category_lang UNIQUE (category_id, lang),
    CONSTRAINT uk_category_translation_slug_lang UNIQUE (slug, lang)
);

-- Tags
CREATE TABLE tag (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- News Articles
CREATE TABLE news (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    category_id BIGINT REFERENCES category(id) ON DELETE SET NULL,
    cover_media_id BIGINT REFERENCES media(id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'REVIEW', 'PUBLISHED', 'UNPUBLISHED', 'ARCHIVED')),
    is_featured BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    publish_at TIMESTAMP,
    unpublish_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE news_translation (
    id BIGSERIAL PRIMARY KEY,
    news_id BIGINT NOT NULL REFERENCES news(id) ON DELETE CASCADE,
    lang VARCHAR(5) NOT NULL,
    title VARCHAR(500) NOT NULL,
    slug VARCHAR(500) NOT NULL,
    summary TEXT,
    content TEXT,
    meta_title VARCHAR(255),
    meta_description TEXT,
    CONSTRAINT uk_news_translation_news_lang UNIQUE (news_id, lang),
    CONSTRAINT uk_news_translation_slug_lang UNIQUE (slug, lang)
);

CREATE TABLE news_tag (
    news_id BIGINT NOT NULL REFERENCES news(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tag(id) ON DELETE CASCADE,
    PRIMARY KEY (news_id, tag_id)
);

CREATE TABLE news_history (
    id BIGSERIAL PRIMARY KEY,
    news_id BIGINT NOT NULL REFERENCES news(id) ON DELETE CASCADE,
    changed_by BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    from_status VARCHAR(20),
    to_status VARCHAR(20) NOT NULL,
    diff_json JSONB,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Advertising System
CREATE TABLE ads_placement (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ads_campaign (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    advertiser VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'ACTIVE', 'PAUSED', 'ENDED')),
    start_at TIMESTAMP,
    end_at TIMESTAMP,
    daily_cap_impressions INTEGER,
    daily_cap_clicks INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ads_creative (
    id BIGSERIAL PRIMARY KEY,
    campaign_id BIGINT NOT NULL REFERENCES ads_campaign(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('IMAGE', 'HTML')),
    landing_url VARCHAR(1000),
    image_media_id BIGINT REFERENCES media(id) ON DELETE SET NULL,
    html_snippet TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ads_creative_translation (
    id BIGSERIAL PRIMARY KEY,
    creative_id BIGINT NOT NULL REFERENCES ads_creative(id) ON DELETE CASCADE,
    lang VARCHAR(5) NOT NULL,
    title VARCHAR(255),
    alt_text VARCHAR(255),
    CONSTRAINT uk_ads_creative_translation_creative_lang UNIQUE (creative_id, lang)
);

CREATE TABLE ads_assignment (
    id BIGSERIAL PRIMARY KEY,
    placement_id BIGINT NOT NULL REFERENCES ads_placement(id) ON DELETE CASCADE,
    campaign_id BIGINT NOT NULL REFERENCES ads_campaign(id) ON DELETE CASCADE,
    creative_id BIGINT NOT NULL REFERENCES ads_creative(id) ON DELETE CASCADE,
    weight INTEGER DEFAULT 100,
    lang_filter JSONB,
    category_filter JSONB,
    start_at TIMESTAMP,
    end_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for Performance
CREATE INDEX idx_news_status_publish ON news(status, publish_at DESC) WHERE is_deleted = FALSE;
CREATE INDEX idx_news_translation_slug_lang ON news_translation(slug, lang);
CREATE INDEX idx_news_translation_fts ON news_translation USING GIN(to_tsvector('english', title || ' ' || COALESCE(summary, '') || ' ' || COALESCE(content, '')));
CREATE INDEX idx_category_translation_slug_lang ON category_translation(slug, lang);
CREATE INDEX idx_ads_assignment_placement ON ads_assignment(placement_id, is_active) WHERE is_active = TRUE;
CREATE INDEX idx_news_author ON news(author_id);
CREATE INDEX idx_news_category ON news(category_id);
CREATE INDEX idx_news_created_at ON news(created_at DESC);
CREATE INDEX idx_news_history_news ON news_history(news_id);
CREATE INDEX idx_ads_campaign_status ON ads_campaign(status) WHERE status = 'ACTIVE';
CREATE INDEX idx_ads_assignment_campaign ON ads_assignment(campaign_id, is_active);

-- Insert default roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('EDITOR'), ('AUTHOR');

-- Create default admin user (password: admin123 - should be changed in production)
-- NOTE: This hash is a placeholder. In production, generate a proper Argon2 hash
-- You can generate it using the application's PasswordEncoder or change the password after first login
-- Temporary hash for 'admin123' - CHANGE THIS IN PRODUCTION!
INSERT INTO users (username, email, full_name, password_hash, is_active) 
VALUES ('admin', 'admin@newsportal.com', 'Administrator', '$argon2id$v=19$m=65536,t=3,p=4$WXhsdWRlZGF0YWluYXBwbGljYXRpb24$ZGVmYXVsdGFkbWlucGFzc3dvcmRoYXNoY2hhbmdlaW5wcm9k', TRUE);

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ADMIN';

