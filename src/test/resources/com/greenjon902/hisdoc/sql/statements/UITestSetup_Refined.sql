INSERT INTO {prefix}Person (pid, personType, personData) VALUES
(1, 'MINECRAFT', 'a6f2f5da-5773-4432-b7b4-8ec0b34a104a'),
(2, 'MINECRAFT', '86f5d3d8-0d4b-4230-9852-77a40baf39bd'),
(3, 'MINECRAFT', '0dbffb6c-6165-40e4-b0f6-0fab4dcd5511');  -- Omega should not be linked to

INSERT INTO {prefix}Tag (tid, name, description, color) VALUES
(1, 'Exploration', 'An event related to the discovery of new territories and landmarks.', 14219360),
(2, 'Building', 'Activities centered around construction and architecture.', 8290620),
(3, 'Mcmmo', 'A plugin i dont like but others do so fair enough ig?', 000000);  -- Mcmmo should not be linked to

INSERT INTO {prefix}Event (eid, name, eventDateType, eventDate1, eventDateUnits, eventDateDiff, postedDate, description, postedPid, eventDateTimeOffset) VALUES
(1, 'Monkey', 'c', 201, 'h', 20, 1, 'Jon and aj made monkey ;)', 1, 345),  -- Time offset is (+05:45)
(2, 'NoMonkey', 'c', 199, 'h', 20, 2, 'monkey not yet :(', 1, -540),  -- Time offset is alaska (-9:00)
(3, 'MonkeyMake', 'c', 200, 'h', 20, 4, 'half of monkey, kinda manky i can see its brain', 1, 0),
(4, 'Null PostedPid', 'c', 100, 'h', 20, 3, 'Look at title', NULL, 0),
(5, 'Null PostedDate', 'c', 100, 'h', 20, NULL, 'Look at title', 1, 0),
(6, 'Null PostedPid & PostedDate', 'c', 100, 'h', 20, NULL, 'Look at title', NULL, 0),
(7, 'Speech mark " in title', 'c', 100, 'h', 20, NULL, 'Look at title', NULL, 0),
(8, 'Speech mark in description', 'c', 100, 'h', 20, NULL, 'Oh no: "', NULL, 0),
(9, 'Single quote \' or ’ in title', 'c', 100, 'h', 20, NULL, 'Look at title', NULL, 0),
(10, 'Single quote in description', 'c', 100, 'h', 20, NULL, 'Oh no: \' or ’', NULL, 0);

INSERT INTO {prefix}Event (eid, name, eventDateType, eventDate1, eventDate2, postedDate, description, postedPid, details, eventDateTimeOffset) VALUES
(11, 'Ranged Date', 'r', 2301, 10138, 9425, 'Ranged', 1, null, 589),
(12, 'details', 'r', 2301, 10138, 9425, 'tihs has details', 1, 'Um, haha, no idomt what to put here :(', 589),
(13, 'Quotes everywhere \'’', 'r', 2301, 10138, 9425, 'Oh no: \' or ’', 1, 'Oh no: \' or ’', 589);


INSERT INTO {prefix}EventEventRelation (eid1, eid2) VALUES (1, 2), (2, 3), (1, 3);

INSERT INTO {prefix}EventPersonRelation (eid, pid) VALUES (1, 1), (1, 2);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (2, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (3, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (4, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (5, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (6, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (7, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (8, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (9, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (10, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (11, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (12, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (13, 1);