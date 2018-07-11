INSERT INTO users(login, password, salt) VALUES ('fabio@dias.com', 'be7075073959f02a3a60b76fe1fd621d64804b6e71ead52731e74e0f559714c2', 'salt'); --password easyone

INSERT INTO contacts(id, owner, first_name, last_name, created_at) VALUES ('ef194162-e0f8-4792-9eed-ea5a8d8c2933', 'fabio@dias.com', 'Charles', 'Mountain', NOW());

INSERT INTO entries(contact_id, phone, region) VALUES ('ef194162-e0f8-4792-9eed-ea5a8d8c2933', '00351915283614', 'PT');
INSERT INTO entries(contact_id, phone, region) VALUES ('ef194162-e0f8-4792-9eed-ea5a8d8c2933', '00351968233437', 'PT');
