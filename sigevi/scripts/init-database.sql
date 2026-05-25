-- Script manual para criar banco PostgreSQL
CREATE DATABASE sigevi;
CREATE USER sigevi WITH ENCRYPTED PASSWORD 'sigevi';
GRANT ALL PRIVILEGES ON DATABASE sigevi TO sigevi;
