/*
    Create an event with id 0 and with the standard test data, using a centered date.
*/

INSERT INTO {prefix}User (uid, userInfo)
VALUES (1, 'me');

INSERT INTO {prefix}Event (eid, name, description, postedUid, postedDate, eventDateType, eventDate1,
eventDatePrecision, eventDateDiff, eventDateDiffType)
VALUES (1, 'testing', 'i was testing', 1, TIMESTAMP('2017-07-23',  '13:10:11'), 'c',
TIMESTAMP('2017-07-23',  '13:10:11'), 'd', 4, 'h');
