SELECT {prefix}Event.eid, {prefix}Event.name, {prefix}Event.eventDateType, {prefix}Event.eventDate1, {prefix}Event.eventDateTimeOffset,
{prefix}Event.eventDateUnits, {prefix}Event.eventDateDiff,
{prefix}Event.eventDate2, {prefix}Event.description FROM {prefix}Event
ORDER BY
    ({prefix}Event.eventDate1 * -- Multiply as comparing seconds to minutes to hours to days doesn't work
    (CASE
        WHEN {prefix}Event.eventDateType = 'c' THEN
            (CASE
                WHEN {prefix}Event.eventDateUnits = 'm' THEN 60
                WHEN {prefix}Event.eventDateUnits = 'h' THEN 60 * 60
                WHEN {prefix}Event.eventDateUnits = 'd' THEN 24 * 60 * 60
            END)
        WHEN {prefix}Event.eventDateType = 'r' THEN 24 * 60 * 60
    END))
DESC;

SELECT {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.color, {prefix}Tag.description
FROM {prefix}Tag;

SELECT {prefix}Person.pid, {prefix}Person.personType, {prefix}Person.personData
FROM {prefix}Person;

SELECT {prefix}EventTagRelation.eid, {prefix}EventTagRelation.tid FROM {prefix}EventTagRelation;
SELECT {prefix}EventPersonRelation.eid, {prefix}EventPersonRelation.pid FROM {prefix}EventPersonRelation;