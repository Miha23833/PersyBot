CREATE SEQUENCE playlist_id_seq;

CREATE TABLE playlist (
	serverid BIGINT NOT NULL REFERENCES discordserver ON DELETE CASCADE ON UPDATE CASCADE,
	id BIGINT NOT NULL DEFAULT nextval('playlist_id_seq'),
	"name" VARCHAR(128) NOT NULL,
	url TEXT NOT NULL,
	CONSTRAINT playlist_serverid_with_name_pk PRIMARY KEY (serverid, name),
	FOREIGN KEY(serverid) REFERENCES discordserver(serverid)
);