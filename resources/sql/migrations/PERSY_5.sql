CREATE SEQUENCE IF NOT EXISTS equalizerpreset_id_seq;

CREATE TABLE public.equalizerpreset (
	id bigint NOT NULL DEFAULT nextval('equalizerpreset_id_seq') PRIMARY KEY,
	"name" varchar(20) NOT NULL UNIQUE,
	"bands" float4[] NOT NULL,
	CHECK (array_ndims("bands") = 1 AND array_length("bands", 1) = 15)
);

ALTER TABLE SERVERPLAYERSETTINGS ADD COLUMN equalizerpresetid BIGINT DEFAULT NULL;
ALTER TABLE SERVERPLAYERSETTINGS ADD CONSTRAINT equalizerpresetid_fk
FOREIGN KEY (equalizerpresetid) REFERENCES EQUALIZERPRESET (ID) ON DELETE SET NULL ON UPDATE CASCADE;

