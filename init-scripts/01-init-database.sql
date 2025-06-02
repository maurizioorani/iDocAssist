-- Initialize the docassist database with required extensions
-- This script runs automatically when the PostgreSQL container starts

-- Enable the vector extension for similarity search
CREATE EXTENSION IF NOT EXISTS vector;

-- Enable UUID extension for generating unique identifiers
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable the pg_trgm extension for text similarity searches
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Enable the btree_gin extension for better indexing
CREATE EXTENSION IF NOT EXISTS btree_gin;

-- Create a schema for the application if needed (optional)
-- CREATE SCHEMA IF NOT EXISTS docassist;

-- Grant necessary permissions
GRANT ALL PRIVILEGES ON DATABASE docassist TO postgres;

-- Set timezone
SET timezone = 'UTC';

-- Log successful initialization
DO $$
BEGIN
    RAISE NOTICE 'DocAssist database initialized successfully with PGVector extension';
END $$;
