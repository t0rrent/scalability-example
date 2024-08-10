-- // create simple_crud table

CREATE TABLE simple_crud (
	id bigserial PRIMARY KEY,
	entity_name text NOT NULL,
	entity_description text
);

-- // @undo

DROP TABLE simple_crud;
