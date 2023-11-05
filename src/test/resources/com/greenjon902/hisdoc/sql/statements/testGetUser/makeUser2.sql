/*
    Creates a user and 3 events,
    the user has id 2
    and is linked to event 1 and 2.
    Event 1 has tags 1 & 2, event 2 has tag 1, and event 3 has tag 2.
    Tag 1 has name "Tag1" and color 123, Tag 2 has name "Tag2" and color 321.

    The user has created 2 events.
*/

INSERT INTO {prefix}User (uid, userType, userData)
VALUES (2, 'misc', 'User2');


INSERT INTO {prefix}Event (eid, name, description, postedUid, postedDate, eventDateType, eventDate1,
eventDatePrecision, eventDateDiff, eventDateDiffType)
VALUES (1, 'testing1', 'i was testing', null, null, 'c',
TIMESTAMP('2017-07-24',  '13:10:11'), 'd', 4, 'h');

INSERT INTO {prefix}Event (eid, name, description, postedUid, postedDate, eventDateType, eventDate1,
eventDate2)
VALUES (2, 'testing2', 'i was testing again', 2, TIMESTAMP('2023/10/08',  '13:26:00'), 'b',
TIMESTAMP('2017-07-23',  '13:10:11'), DATE('2017-08-23'));

INSERT INTO {prefix}Event (eid, name, description, postedUid, postedDate, eventDateType, eventDate1,
eventDate2)
VALUES (3, 'testing3', 'more testing', 2, TIMESTAMP('2023/10/08',  '13:26:00'), 'b',
TIMESTAMP('2017-07-25',  '13:10:11'), DATE('2017-08-23'));

INSERT INTO {prefix}EventUserRelation (eid, uid)
VALUES (1, 2), (2, 2);


INSERT INTO {prefix}Tag (tid, name, description, color)
VALUES (1, 'Tag1', '', 123), (2, 'Tag2', '', 321);

INSERT INTO {prefix}EventTagRelation (eid, tid)
VALUES (1, 2), (1, 1), (2, 1), (3, 2);