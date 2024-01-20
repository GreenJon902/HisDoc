-- Remove all relations for an event with the given eid.
-- This is in the upload as it would be used during the update of an event

DELETE FROM {prefix}EventEventRelation WHERE eid1 = ? OR eid2 = ?;
DELETE FROM {prefix}EventPersonRelation WHERE eid = ?;
DELETE FROM {prefix}EventTagRelation WHERE eid = ?;