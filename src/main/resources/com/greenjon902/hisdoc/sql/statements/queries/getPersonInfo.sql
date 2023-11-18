SELECT {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.color, COUNT({prefix}Tag.tid) AS count
FROM {prefix}Tag
RIGHT JOIN (
    SELECT {prefix}EventTagRelation.tid FROM {prefix}EventTagRelation
    RIGHT JOIN (
        SELECT {prefix}EventPersonRelation.eid FROM {prefix}EventPersonRelation
        WHERE {prefix}EventPersonRelation.uid={uid}
    ) OurEUR ON OurEUR.eid={prefix}EventTagRelation.eid
) OurETR ON OurETR.tid={prefix}Tag.tid
WHERE {prefix}Tag.tid IS NOT NULL
GROUP BY {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.color;

SELECT COUNT(*) as count FROM {prefix}Event
WHERE {prefix}Event.postedUid = {uid};

SELECT COUNT(*) as count FROM {prefix}EventPersonRelation
WHERE {prefix}EventPersonRelation.uid = {uid};

SELECT {prefix}Event.eid, {prefix}Event.name, {prefix}Event.eventDateType, {prefix}Event.eventDate1,
                                            {prefix}Event.eventDatePrecision, {prefix}Event.eventDateDiff, {prefix}Event.eventDateDiffType,
                                            {prefix}Event.eventDate2, {prefix}Event.description FROM {prefix}Event
RIGHT JOIN (
   SELECT {prefix}EventPersonRelation.eid FROM {prefix}EventPersonRelation
   WHERE {prefix}EventPersonRelation.uid={uid}
) OurEUR ON OurEUR.eid={prefix}Event.eid
ORDER BY eventDate1 DESC
LIMIT 10;

SELECT {prefix}Event.eid, {prefix}Event.name, {prefix}Event.eventDateType, {prefix}Event.eventDate1,
                                            {prefix}Event.eventDatePrecision, {prefix}Event.eventDateDiff, {prefix}Event.eventDateDiffType,
                                            {prefix}Event.eventDate2, {prefix}Event.description FROM {prefix}Event
WHERE {prefix}Event.postedUid = {uid}
ORDER BY eventDate1 DESC
LIMIT 10;


SELECT {prefix}Person.uid, {prefix}Person.personType, {prefix}Person.personData
FROM {prefix}Person
WHERE {prefix}Person.uid = {uid};