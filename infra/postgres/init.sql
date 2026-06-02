-- This script runs ONCE, the first time the Postgres container initializes.
-- It creates a separate database per microservice (remember: one service = one DB).
-- The default "learn" database (from POSTGRES_DB) is created automatically.

CREATE DATABASE orders;
CREATE DATABASE inventory;
CREATE DATABASE payments;
