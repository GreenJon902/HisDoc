/*
    Create an event with id 0 and with the standard test data, using a centered date.
    This has no postedPid and no postedDate.
*/

INSERT INTO {prefix}Event (eid, name, description, postedPid, postedDate, eventDateType, eventDate1,
eventDatePrecision, eventDateDiff, eventDateDiffType)
VALUES (1, 'testing', 'i was testing', null, null, 'c',
TIMESTAMP('2017-07-23',  '13:10:11'), 'd', 4, 'h');
