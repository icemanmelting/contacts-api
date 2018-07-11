-- :name select-all-contacts-by-user :query :many
SELECT c.id, c.first_name, c.last_name, e.phone, c.created_at, c.updated_at FROM contacts AS c
LEFT JOIN entries AS e ON (e.contact_id=c.id)
WHERE c.owner=:owner;

-- :name insert-contact :execute :affected
INSERT INTO contacts(id, owner, first_name, last_name, created_at) VALUES (:id, :owner, :first_name, :last_name, NOW());

-- :name update-contact :execute :affected
UPDATE contacts SET
  --~ (when (contains? params :first_name) " first_name=:first_name,")
  --~ (when (contains? params :last_name) " last_name=:last_name,")
updated_at=NOW()
WHERE id=:id::uuid AND owner=:owner;

-- :name delete-contact :execute :affected
DELETE FROM contacts WHERE id=:id::uuid AND owner=:owner;

-- :name insert-entry :execute :affected
INSERT INTO entries(contact_id, phone, region) VALUES (:c_id::uuid, :phone, :region);

