SELECT {prefix}Event.eid, {prefix}Event.name, {prefix}Event.eventDateType, {prefix}Event.eventDate1,
                                            {prefix}Event.eventDatePrecision, {prefix}Event.eventDateDiff, {prefix}Event.eventDateDiffType,
                                            {prefix}Event.eventDate2 FROM {prefix}Event
RIGHT JOIN (
   SELECT {prefix}EventTagRelation.eid FROM {prefix}EventTagRelation
   WHERE {prefix}EventTagRelation.tid={tid}
) OurEUR ON OurEUR.eid={prefix}Event.eid
ORDER BY {prefix}Event.eventDate1 DESC
LIMIT 10;
-- TODO: Test this limit
-- TODO: Order by posted date, or event date

SELECT {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.description, {prefix}Tag.color
FROM {prefix}Tag
WHERE {prefix}Tag.tid = {tid};