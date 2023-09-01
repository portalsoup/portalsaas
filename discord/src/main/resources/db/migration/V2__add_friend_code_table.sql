CREATE TABLE friend_code (
	id serial PRIMARY KEY,
	userid VARCHAR ( 50 ) NOT NULL,
	code VARCHAR ( 50 ) NOT NULL,
	created_on DATE NOT NULL
);