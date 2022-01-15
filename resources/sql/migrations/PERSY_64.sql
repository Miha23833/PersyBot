CREATE TABLE public.serverplayersettings (
	serverid int8 NOT NULL,
	meetaudiolink text
);

ALTER TABLE public.serverplayersettings ADD CONSTRAINT serverplayersettings_fk FOREIGN KEY (serverid) REFERENCES discordserver(serverid) ON UPDATE CASCADE ON DELETE CASCADE;