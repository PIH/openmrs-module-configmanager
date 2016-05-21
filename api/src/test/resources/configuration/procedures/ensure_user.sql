-- Ensure a user account exists with the given attributes

CREATE PROCEDURE ensure_user (
  _username    VARCHAR(255),
  _given_name  VARCHAR(255),
  _family_name VARCHAR(255),
  _gender      VARCHAR(255),
  _password    VARCHAR(255),
  _salt        VARCHAR(255)
)
BEGIN

  DECLARE _user_id INT;
  DECLARE _person_id INT;
  DECLARE _person_uuid CHAR(38);
  DECLARE _person_name_id INT;

  SELECT user_id, person_id INTO _user_id, _person_id FROM users WHERE system_id = _username OR username = _username;

  IF (_user_id IS NOT null) THEN
    UPDATE users SET username = _username, system_id = _username, password = _password, salt = _salt WHERE user_id = _user_id;
    UPDATE person SET gender = _gender WHERE person_id = _person_id;

    SELECT person_name_id INTO _person_name_id FROM person_name WHERE person_id = _person_id;

    IF (_person_name_id IS NOT null) THEN
      UPDATE person_name SET given_name = _given_name, family_name = _family_name where person_id = _person_id;
    ELSE
      INSERT INTO person_name (person_id, given_name, family_name, preferred, creator, date_created, uuid) VALUES (_person_id, _given_name, _family_name, 1, 1, now(), uuid());
    END IF;
  ELSE
    SELECT uuid() INTO _person_uuid;
    INSERT INTO person(gender, creator, date_created, uuid) values (_gender, 1, now(), _person_uuid);
    SELECT person_id into _person_id FROM person WHERE uuid = _person_uuid;
    INSERT INTO person_name(person_id, given_name, family_name, preferred, creator, date_created, uuid) VALUES (_person_id, _given_name, _family_name, 1, 1, now(), uuid());
    INSERT INTO users (person_id, username, system_id, creator, date_created, uuid, password, salt) VALUES (_person_id, _username, _username, 1, now(), uuid(), _password, _salt);
  END IF;

END;
