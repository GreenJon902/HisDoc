SET FOREIGN_KEY_CHECKS=0;

INSERT INTO {prefix}ChangeLog (cid, eid, description, authorPid, date)
VALUES
(1, 2, 'I did something', 1, TIMESTAMP('2017-07-23',  '13:10:11')),
(2, 1, 'I did you', 2, TIMESTAMP('2017-07-23',  '13:10:11'));

SET FOREIGN_KEY_CHECKS=1;
