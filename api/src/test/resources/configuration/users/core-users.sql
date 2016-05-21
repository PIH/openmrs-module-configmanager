
-- Ensure admin user is set up correctly

call ensure_user('admin', 'Super', 'User', 'M', 'e28e3ae84c66bfba6b2c50a406567f3e34fea1d76b17c006931571fe5d940f6c6b81e49cf8ea5e0adfca19fe3beb68d8ad79f7c3812e92b8d502a9afcf2029b2', '1c9d7e94aeeb7a2459ef45ed200b2944582e0e7088d75f9b57a3644861ea766c20a269b3fe2eadaff1bc445ecfbd9bd3c0c550dfd813de48d39423cd3d1a8b10');
call ensure_provider('admin', '');
call ensure_user_roles('admin', 'Privilege Level: Full,System Developer');

-- Ensure daemon user is set up correctly

call ensure_user('daemon', 'Super', 'User', 'M', '', '');

-- Ensure other users/providers needed by Bahmni are set up correctly

call ensure_user('Lab Manager', 'Lab', 'Manager', '', '', '');
call ensure_provider('Lab Manager', 'LABMANAGER');
call ensure_user('Lab System', 'Lab', 'System', 'M', '', '');
call ensure_provider('Lab System', 'LABSYSTEM');

-- Ensure Anonymous role has Get Locations privilege in order to choose a location at login

call ensure_role_privilege('Anonymous', 'Get Locations');
