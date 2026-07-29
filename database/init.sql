-- PostgreSQL Database Initializer
CREATE DATABASE retention_db;
CREATE USER retention_user WITH ENCRYPTED PASSWORD 'retention_password';
GRANT ALL PRIVILEGES ON DATABASE retention_db TO retention_user;
