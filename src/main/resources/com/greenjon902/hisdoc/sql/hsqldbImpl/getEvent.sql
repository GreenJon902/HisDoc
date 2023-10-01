-- {0} is event id

INSERT INTO Date (type, date1, date2)
VALUES ('b', CURRENT_TIMESTAMP, CURRENT_DATE);

INSERT INTO Event (name, desc, actualDid)
VALUES ('test', 'ing', 0);

INSERT INTO Tag (name, desc, color)
VALUES ('test', 'ing', 0);
INSERT INTO EventTagRelation (eid, tid)
VALUES (0, 0);

INSERT INTO Tag (name, desc, color)
VALUES ('umm', 'mmm', 0);
INSERT INTO EventTagRelation (eid, tid)
VALUES (0, 1);

SELECT Event.eid, Event.name, Event.desc,
    ARRAY_AGG(EventTagRelation.name) AS TagName,
    ARRAY_AGG(EventTagRelation.color) AS TagColor,
    ARRAY_AGG(EventUserRelation.userInfo) As UserInfo,
    ARRAY_AGG(ChangeLog.desc ORDER BY ChangeLog.cid) AS ChangeDesc,
    ARRAY_AGG(ChangeLog.authorUid ORDER BY ChangeLog.cid) AS ChangeAuthor,
    ARRAY_AGG(ChangeLog.date ORDER BY ChangeLog.cid) AS ChangeDate,
    ARRAY_AGG(Date.type) AS DateType,
    ARRAY_AGG(Date.date1) AS Date1,
    ARRAY_AGG(Date.precision) AS DatePrecision,
    ARRAY_AGG(Date.diff) AS DateDiff,
    ARRAY_AGG(Date.diffType) AS DateDiffType,
    ARRAY_AGG(Date.date2) AS Date2
FROM Event
INNER JOIN Date ON Event.actualDid=Date.did
LEFT JOIN (
    SELECT eid, name, color
    FROM EventTagRelation
    LEFT JOIN Tag ON Tag.tid=EventTagRelation.tid
) EventTagRelation ON Event.eid=EventTagRelation.eid
LEFT JOIN (
    SELECT eid, userInfo
    FROM EventUserRelation
    LEFT JOIN User ON User.uid=EventUserRelation.uid
) EventUserRelation ON Event.eid=EventUserRelation.eid
LEFT JOIN ChangeLog ON Event.eid=ChangeLog.eid
WHERE Event.eid = {0}
GROUP BY Event.eid
