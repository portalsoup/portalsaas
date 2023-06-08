CREATE TABLE mtg_sets (
    id serial PRIMARY KEY,
    released_date DATE,
    name VARCHAR ( 255 ) UNIQUE NOT NULL,
    code VARCHAR ( 10 ) UNIQUE NOT NULL,
    set_type VARCHAR ( 25 ) NOT NULL,
    block VARCHAR ( 255 ),
    block_code VARCHAR ( 10 ),
    card_count INT

);