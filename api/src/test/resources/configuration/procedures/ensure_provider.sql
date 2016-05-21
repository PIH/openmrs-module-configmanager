
-- Ensure a provider account with the given identifier exists for the given user

CREATE PROCEDURE ensure_provider(
  _username            VARCHAR(255),
  _provider_identifier VARCHAR(255)
)
BEGIN

  DECLARE _person_id INT;
  DECLARE _provider_id INT;

  SELECT person_id INTO _person_id FROM users WHERE username = _username;
  SELECT provider_id INTO _provider_id FROM provider WHERE person_id = _person_id AND identifier = _provider_identifier;

  IF (_provider_id IS NULL) THEN
    INSERT INTO provider (person_id, identifier, creator, date_created, uuid) VALUES (_person_id, _provider_identifier, 1, now(), uuid());
  END IF;

END;
