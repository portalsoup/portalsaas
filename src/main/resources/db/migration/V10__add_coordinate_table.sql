CREATE TABLE if NOT EXISTS coordinate (
    id serial PRIMARY KEY,
    lat FLOAT NOT NULL,
    lng FLOAT NOT NULL,
    altitude FLOAT,
    route_id int,
    created_date TIMESTAMP NOT NULL,
    heart_rate bigint,
    CONSTRAINT fk_route FOREIGN KEY(route_id) REFERENCES route(id)
);