CREATE SEQUENCE server_id_seq;

CREATE TABLE discordserver (
	serverid BIGINT NOT NULL DEFAULT nextval('server_id_seq'),
	languageid INT NULL,
	CONSTRAINT discordserver_pkey PRIMARY KEY (serverid)
);

CREATE TABLE discordserversettings (
	serverid BIGINT NOT NULL UNIQUE REFERENCES discordserver ON DELETE CASCADE ON UPDATE CASCADE,
	prefix TEXT NOT NULL DEFAULT '..',
	volume INT NOT NULL DEFAULT 100,
	CONSTRAINT discordserversettings_pkey PRIMARY KEY (serverid),
	FOREIGN KEY(serverid) REFERENCES discordserver(serverid)
);

CREATE SEQUENCE playlist_id_seq;

CREATE TABLE playlist (
	serverid BIGINT NOT NULL REFERENCES discordserver ON DELETE CASCADE ON UPDATE CASCADE,
	id BIGINT NOT NULL DEFAULT nextval('playlist_id_seq'),
	"name" VARCHAR(128) NOT NULL,
	url TEXT NOT NULL,
	CONSTRAINT playlist_serverid_with_name_pk PRIMARY KEY (serverid, name),
	FOREIGN KEY(serverid) REFERENCES discordserver(serverid)
);