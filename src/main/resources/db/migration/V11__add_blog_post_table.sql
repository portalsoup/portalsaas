CREATE TABLE if NOT EXISTS blog_post (
    id serial PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body text NOT NULL,
    created_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_route FOREIGN KEY(route_id) REFERENCES route(id)
);