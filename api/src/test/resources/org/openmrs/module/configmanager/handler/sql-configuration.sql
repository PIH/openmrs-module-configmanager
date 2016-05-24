
-- Test setting up some metatdata via SQL
-- Unfortuantely due to H2 limitations we cannot easily test stored procedure setup

insert into encounter_type (uuid, name, description, retired, creator, date_created) values ('3cb2f1df-2035-11e6-8979-e82aea237783', 'ET1', 'Description 1', 0, 1, now());
insert into encounter_type (uuid, name, description, retired, creator, date_created) values ('3cb2f1df-2035-11e6-8979-e82aea237784', 'ET2', 'Description 2', 0, 1, now());
