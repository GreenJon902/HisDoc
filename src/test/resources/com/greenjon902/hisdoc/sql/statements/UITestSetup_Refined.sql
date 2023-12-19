INSERT INTO {prefix}Person (pid, personType, personData) VALUES
(1, 'mc', 'a6f2f5da-5773-4432-b7b4-8ec0b34a104a'),
(2, 'mc', '86f5d3d8-0d4b-4230-9852-77a40baf39bd'),
(3, 'mc', '0dbffb6c-6165-40e4-b0f6-0fab4dcd5511');  -- Omega should not be linked to

INSERT INTO {prefix}Tag (tid, name, description, color) VALUES
(1, 'Exploration', 'An event related to the discovery of new territories and landmarks.', 14219360),
(2, 'Building', 'Activities centered around construction and architecture.', 8290620),
(3, 'Mcmmo', 'A plugin i dont like but others do so fair enough ig?', 000000);  -- Mcmmo should not be linked to

INSERT INTO {prefix}Event (eid, name, eventDateType, eventDate1, eventDatePrecision, eventDateDiff, eventDateDiffType, postedDate, description, postedPid) VALUES
(1, 'Monkey', 'c', TIMESTAMP('2023-06-28', '18:23:52'), 'h', 20, 'h', TIMESTAMP('2023-10-04', '18:23:52'), 'Jon and aj made monkey ;)', 1),
(2, 'NoMonkey', 'c', TIMESTAMP('2023-06-28', '18:23:52'), 'h', 20, 'h', TIMESTAMP('2023-10-02', '18:23:52'), 'monkey not yet :(', 1),
(3, 'MonkeyMake', 'c', TIMESTAMP('2023-06-28', '18:23:52'), 'h', 20, 'h', TIMESTAMP('2023-10-03', '18:23:52'), 'half of monkey, kinda manky i can see its brain', 1),
(4, 'Null PostedPid', 'c', TIMESTAMP('2023-06-28', '18:23:52'), 'h', 20, 'h', TIMESTAMP('2023-10-03', '18:23:52'), 'Look at title', NULL),
(5, 'Null PostedDate', 'c', TIMESTAMP('2023-06-28', '18:23:52'), 'h', 20, 'h', NULL, 'Look at title', 1),
(6, 'Null PostedPid & PostedDate', 'c', TIMESTAMP('2023-06-28', '18:23:52'), 'h', 20, 'h', NULL, 'Look at title', NULL);

INSERT INTO {prefix}EventPersonRelation (eid, pid) VALUES (1, 1), (1, 2);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (2, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (3, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (4, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (5, 1);
INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES (6, 1);