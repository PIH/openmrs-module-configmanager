
-- Ensure the given user account has the given set of roles.
-- This will remove all existing roles for the user and ensure they only have those in this list (comma-delimited)

CREATE PROCEDURE ensure_user_roles( _username VARCHAR(255), _role_list VARCHAR(255) )
BEGIN

  DECLARE _user_id INT;
  DECLARE _role_name VARCHAR(255);

  SELECT user_id INTO _user_id FROM users WHERE username = _username;
  DELETE FROM user_role WHERE user_id = _user_id;

  -- Iterate over each role in the passed role list, using a comma as a separator
  WHILE (_role_list != '') DO
    SET _role_name = SUBSTRING_INDEX(_role_list, ',', 1);
    INSERT INTO user_role(user_id, role) VALUES(_user_id, TRIM(_role_name));
    SET _role_list = SUBSTRING(_role_list, CHAR_LENGTH(_role_name) + 2);
  END WHILE;

END;
