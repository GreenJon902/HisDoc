SELECT {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.color, COUNT({prefix}Tag.tid) AS count
FROM {prefix}Tag
RIGHT JOIN (
    SELECT {prefix}EventTagRelation.tid FROM {prefix}EventTagRelation
    RIGHT JOIN (
        SELECT {prefix}EventPersonRelation.eid FROM {prefix}EventPersonRelation
        WHERE {prefix}EventPersonRelation.pid={pid}
    ) OurEUR ON OurEUR.eid={prefix}EventTagRelation.eid
) OurETR ON OurETR.tid={prefix}Tag.tid
WHERE {prefix}Tag.tid IS NOT NULL
GROUP BY {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.color;

SELECT COUNT(*) AS count FROM {prefix}Event
WHERE {prefix}Event.postedPid = {pid};

SELECT COUNT(*) AS count FROM {prefix}EventPersonRelation
WHERE {prefix}EventPersonRelation.pid = {pid};

SELECT {prefix}Event.eid, {prefix}Event.name, {prefix}Event.eventDateType, {prefix}Event.eventDate1,
                                            {prefix}Event.eventDateUnits, {prefix}Event.eventDateDiff,
                                            {prefix}Event.eventDate2, {prefix}Event.description FROM {prefix}Event
RIGHT JOIN (
   SELECT {prefix}EventPersonRelation.eid FROM {prefix}EventPersonRelation
   WHERE {prefix}EventPersonRelation.pid={pid}
) OurEUR ON OurEUR.eid={prefix}Event.eid
ORDER BY
    ({prefix}Event.eventDate1 *  -- Multiply as comparing seconds to minutes to hours to days doesn't work
    (CASE
        WHEN {prefix}Event.eventDateType = 'c' THEN
            (CASE
                WHEN {prefix}Event.eventDateUnits = 'm' THEN 60
                WHEN {prefix}Event.eventDateUnits = 'h' THEN 60 * 60
                WHEN {prefix}Event.eventDateUnits = 'd' THEN 24 * 60 * 60
            END)
        WHEN {prefix}Event.eventDateType = 'r' THEN 24 * 60 * 60
    END))
DESC
LIMIT 10;

SELECT {prefix}Event.eid, {prefix}Event.name, {prefix}Event.eventDateType, {prefix}Event.eventDate1,
                                            {prefix}Event.eventDateUnits, {prefix}Event.eventDateDiff,
                                            {prefix}Event.eventDate2, {prefix}Event.description FROM {prefix}Event
WHERE {prefix}Event.postedPid = {pid}
ORDER BY {prefix}Event.postedDate DESC  -- Posted date is always in seconds so can just order
LIMIT 10;


SELECT {prefix}Person.pid, {prefix}Person.personType, {prefix}Person.personData
FROM {prefix}Person
WHERE {prefix}Person.pid = {pid};