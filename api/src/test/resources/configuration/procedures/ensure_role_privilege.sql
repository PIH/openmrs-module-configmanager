
-- Ensure the given role has the given privilege

CREATE PROCEDURE ensure_role_privilege( _role_name VARCHAR(255), _privilege_name VARCHAR(255) )
BEGIN
  DECLARE _num_existing INT;
  SELECT count(*) INTO _num_existing FROM role_privilege WHERE role = _role_name AND privilege = _privilege_name;
  IF (_num_existing = 0) THEN
    INSERT INTO role_privilege(role, privilege) VALUES(TRIM(_role_name), TRIM(_privilege_name));
  END IF;
END;
