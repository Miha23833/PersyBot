CREATE TABLE discordserversettings (
	serverid BIGINT NOT NULL UNIQUE REFERENCES discordserver ON DELETE CASCADE ON UPDATE CASCADE,
	prefix TEXT NOT NULL DEFAULT '..',
	volume INT NOT NULL DEFAULT 100,
	CONSTRAINT discordserversettings_pkey PRIMARY KEY (serverid),
	FOREIGN KEY(serverid) REFERENCES discordserver(serverid)
);