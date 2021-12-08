CREATE SEQUENCE server_id_seq;

CREATE TABLE discordserver (
	serverid BIGINT NOT NULL DEFAULT nextval('server_id_seq'),
	languageid INT NULL,
	CONSTRAINT discordserver_pkey PRIMARY KEY (serverid)
);