SELECT {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.color, COUNT({prefix}Tag.tid) AS count
FROM {prefix}Tag
RIGHT JOIN (
    SELECT {prefix}EventTagRelation.tid FROM {prefix}EventTagRelation
    RIGHT JOIN (
        SELECT {prefix}EventUserRelation.eid FROM {prefix}EventUserRelation
        WHERE {prefix}EventUserRelation.uid={uid}
    ) OurEUR ON OurEUR.eid={prefix}EventTagRelation.eid
) OurETR ON OurETR.tid={prefix}Tag.tid
GROUP BY {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.color;

SELECT COUNT(*) as count FROM {prefix}Event
WHERE {prefix}Event.postedUid = {uid};

SELECT COUNT(*) as count FROM {prefix}EventUserRelation
WHERE {prefix}EventUserRelation.uid = {uid};

SELECT {prefix}Event.eid, {prefix}Event.name FROM {prefix}Event
RIGHT JOIN (
   SELECT {prefix}EventUserRelation.eid FROM {prefix}EventUserRelation
   WHERE {prefix}EventUserRelation.uid={uid}
) OurEUR ON OurEUR.eid={prefix}Event.eid
ORDER BY eventDate1 DESC
LIMIT 10;
-- TODO: Test this limit
-- TODO: Order by posted date, or event date

SELECT {prefix}User.uid, {prefix}User.userInfo
FROM {prefix}User
WHERE {prefix}User.uid = {uid};