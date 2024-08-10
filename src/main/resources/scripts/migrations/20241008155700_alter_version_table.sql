-- // alter version table

UPDATE version
SET "value" = '2';

-- // @undo

UPDATE version
SET "value" = '1';
