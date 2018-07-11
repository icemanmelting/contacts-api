-- :name insert-user :execute :affected
INSERT INTO users(login, password, salt) VALUES (:username, :password, 'salt');
