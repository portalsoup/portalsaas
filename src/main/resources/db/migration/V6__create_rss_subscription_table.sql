CREATE TABLE rss_subscription (
    id serial PRIMARY KEY,
    created_on DATE NOT NULL,
    active BOOLEAN NOT NULL,
    updated_on DATE NOT NULL,
    feed_url VARCHAR ( 255 ) NOT NULL,
    user_id VARCHAR ( 50 ) NOT NULL,
    nickname VARCHAR ( 50 ) UNIQUE NOT NULL,
    guild_id VARCHAR ( 50 ) NOT NULL
);
