-- :name select-all-contacts-by-user :query :many
SELECT c.id, c.first_name, c.last_name, e.phone FROM contacts AS c
JOIN entries AS e ON (e.contact_id=c.id)
WHERE c.owner=:owner;
