SET FOREIGN_KEY_CHECKS=0;

INSERT INTO {prefix}ChangeLog (cid, eid, description, authorPid, date)
VALUES
(3, 6, 'er gf', 1, TIMESTAMP('2017-07-23',  '13:10:11')),
(4, 5, 'are gre', 1, TIMESTAMP('2017-07-23',  '13:10:11'));

SET FOREIGN_KEY_CHECKS=1;
