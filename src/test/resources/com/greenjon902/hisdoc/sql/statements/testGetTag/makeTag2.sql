/*
    Creates a tag and 3 events,
    the tag has id 2
    and is linked to event 1, 2, and 4.
    The event's date 1 are ordered starting at 2, 3, 1, 4.
    See inserts for event info.

    We also create a user with id 2, so it can create the events
*/

INSERT INTO {prefix}User (uid, userInfo)
VALUES (2, 'User2');


INSERT INTO {prefix}Event (eid, name, description, postedUid, postedDate, eventDateType, eventDate1,
eventDatePrecision, eventDateDiff, eventDateDiffType)
VALUES (1, 'testing1', 'i was testing', 2, TIMESTAMP('2023/10/08',  '13:26:00'), 'c',
TIMESTAMP('2012-07-24',  '13:10:11'), 'd', 4, 'h');

INSERT INTO {prefix}Event (eid, name, description, postedUid, postedDate, eventDateType, eventDate1,
eventDate2)
VALUES (2, 'testing2', 'i was testing again', 2, TIMESTAMP('2000/10/08',  '13:26:00'), 'b',
TIMESTAMP('2010-07-23',  '13:10:11'), DATE('2017-08-23'));

INSERT INTO {prefix}Event (eid, name, description, postedUid, postedDate, eventDateType, eventDate1,
eventDate2)
VALUES (3, 'testing3', 'more testing', 2, TIMESTAMP('2022/10/08',  '13:26:00'), 'b',
TIMESTAMP('2011-07-25',  '13:10:11'), DATE('2017-08-23'));

INSERT INTO {prefix}Event (eid, name, description, postedUid, postedDate, eventDateType, eventDate1,
eventDatePrecision, eventDateDiff, eventDateDiffType)
VALUES (4, 'testing4', 'im bord of testing', null, null, 'c',
TIMESTAMP('2017-07-24',  '13:10:11'), 'd', 4, 'h');


INSERT INTO {prefix}Tag (tid, name, description, color)
VALUES (2, 'Tag2', "This is another tag with a color that i dont quite know", 123);

INSERT INTO {prefix}EventTagRelation (eid, tid)
VALUES (1, 2), (2, 2), (4, 2);