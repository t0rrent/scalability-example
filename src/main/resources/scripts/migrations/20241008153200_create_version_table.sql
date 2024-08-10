-- // create version table

CREATE TABLE version (
   	onerow_id bool PRIMARY KEY DEFAULT true,
	"value" text NOT NULL,
	CONSTRAINT onerow_uni CHECK (onerow_id)
);

INSERT INTO version ( "value" ) VALUES ( '1' );

-- // @undo

DROP TABLE version;
