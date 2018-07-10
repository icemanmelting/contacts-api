INSERT INTO users(login, password, salt) VALUES ('fabio@dias.com', 'be7075073959f02a3a60b76fe1fd621d64804b6e71ead52731e74e0f559714c2', 'salt'); --password easyone

INSERT INTO contacts(id, owner, first_name, last_name) VALUES ('ef194162-e0f8-4792-9eed-ea5a8d8c2933', 'fabio@dias.com', 'Charles', 'Mountain');

INSERT INTO entries(id, contact_id, phone) VALUES ('82031404-94e1-4fde-865d-1f5d89d116b1', 'ef194162-e0f8-4792-9eed-ea5a8d8c2933', '00351915283614');
INSERT INTO entries(id, contact_id, phone) VALUES ('82031404-94e1-4fde-865d-1f5d89d116b2', 'ef194162-e0f8-4792-9eed-ea5a8d8c2933', '00351968233437');
