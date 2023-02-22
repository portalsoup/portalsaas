CREATE TABLE video_game (
    id serial PRIMARY KEY,
    pricecharting_id INT UNIQUE NOT NULL,
    console_name VARCHAR ( 50 ) NOT NULL,
    product_name VARCHAR ( 250 ) NOT NULL,
    created_on DATE NOT NULL,
    updated_on DATE NOT NULL
);

CREATE TABLE video_game_price (
    id serial PRIMARY KEY,
    video_game_id INT REFERENCES video_game ( pricecharting_id ),
    loose_price VARCHAR ( 50 ),
    created_on DATE NOT NULL
);