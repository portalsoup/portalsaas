CREATE TABLE if NOT EXISTS blog_post (
    id serial PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body text NOT NULL,
    created_date TIMESTAMP NOT NULL
);