SELECT {prefix}Event.eid, {prefix}Event.name, {prefix}Event.eventDateType, {prefix}Event.eventDate1,
                                            {prefix}Event.eventDateUnits, {prefix}Event.eventDateDiff,
                                            {prefix}Event.eventDate2, {prefix}Event.description FROM {prefix}Event
RIGHT JOIN (
   SELECT {prefix}EventTagRelation.eid FROM {prefix}EventTagRelation
   WHERE {prefix}EventTagRelation.tid={tid}
) OurEUR ON OurEUR.eid={prefix}Event.eid
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
DESC
LIMIT 10;

SELECT {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.description, {prefix}Tag.color, {prefix}Tag.description
FROM {prefix}Tag
WHERE {prefix}Tag.tid = {tid};