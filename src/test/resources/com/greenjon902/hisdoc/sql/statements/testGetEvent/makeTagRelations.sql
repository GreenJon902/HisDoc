SET FOREIGN_KEY_CHECKS=0;

INSERT INTO {prefix}EventTagRelation (eid, tid)
VALUES (1, 2), (2, 1);

SET FOREIGN_KEY_CHECKS=1;