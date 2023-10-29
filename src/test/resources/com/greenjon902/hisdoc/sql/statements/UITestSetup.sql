INSERT INTO {prefix}User (uid, userInfo)
VALUES (1, 'Ajballistic'),
(2, 'jackaboi1'),
(3, "GreenJon"),
(4, "Omegadestroy400");

INSERT INTO {prefix}Tag (tid, name, description, color)
VALUES (1, 'Base', 'An event that relates to someones base.', 65280),
(2, 'Payed-Server', 'An event that relates to the server switching to ApexMc and going payed.', 16711680);

INSERT INTO {prefix}Event (eid, name, description, postedUid, postedDate, eventDateType, eventDate1,
eventDatePrecision, eventDateDiff, eventDateDiffType)
VALUES
(1, 'Omega Happens', 'Omega starts a new base at (7777, -7777) causing the server to run out of storage.\nThe server runs out of storage. But world download problems delay the payed server.', null, null, 'c', TIMESTAMP('2022.02.05',  '00:00:00'), 'd', 0, 'h');

INSERT INTO {prefix}EventUserRelation (eid, uid)
VALUES (1, 4);

INSERT INTO {prefix}EventTagRelation (eid, tid)
VALUES (1, 1), (1, 2);
