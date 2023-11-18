/*
    Create an event with id 1 and with the standard test data, using a between date.
    Requires make persons to be ran beforehand.
*/

INSERT INTO {prefix}Event (eid, name, description, postedUid, postedDate, eventDateType, eventDate1,
eventDate2)
VALUES (2, 'testing', 'i was testing', 1, TIMESTAMP('2023/10/08',  '13:26:00'), 'b',
TIMESTAMP('2017-07-23',  '13:10:11'), DATE('2017-08-23'));
