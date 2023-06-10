CREATE TABLE if NOT EXISTS attachment (
    id serial PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    uri VARCHAR(255) NOT NULL
);

