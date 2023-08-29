CREATE TABLE rss_feed (
    id serial PRIMARY KEY,
    created_on DATE NOT NULL,
    feed_url VARCHAR ( 255 ) UNIQUE NOT NULL,
    name VARCHAR ( 255 )
);

CREATE TABLE rss_subscription (
    id serial PRIMARY KEY,
    created_on DATE NOT NULL,
    discord_user_id INT REFERENCES discord_user ( id ),
    rss_feed_id INT REFERENCES rss_feed ( id ),
    nickname VARCHAR ( 50 ) UNIQUE NOT NULL,
    guild_id VARCHAR ( 50 ) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE rss_subscription_read_entries (
    id serial PRIMARY KEY,
    created_on DATE NOT NULL,
    rss_subscription_id INT REFERENCES rss_subscription ( id ),
    discord_user_id INT REFERENCES discord_user ( id )
);