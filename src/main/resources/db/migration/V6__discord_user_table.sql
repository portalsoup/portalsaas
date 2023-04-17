CREATE TABLE discord_user (
    id serial PRIMARY KEY,
    created_on DATE NOT NULL,
    updated_on DATE NOT NULL,
    snowflake VARCHAR ( 50 ) NOT NULL,
    nickname VARCHAR ( 50 ) UNIQUE,
    dm_guild_id VARCHAR ( 50 ) NOT NULL
);
