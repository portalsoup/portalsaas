CREATE TABLE schedule (
    id serial PRIMARY KEY,
    created_on DATE NOT NULL,
    active BOOLEAN NOT NULL,
    updated_on DATE NOT NULL,
    cron VARCHAR ( 25 ) NOT NULL,
    message VARCHAR ( 250 ) NOT NULL
);
