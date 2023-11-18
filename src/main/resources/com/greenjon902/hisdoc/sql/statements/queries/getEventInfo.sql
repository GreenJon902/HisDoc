SELECT {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.color
FROM {prefix}EventTagRelation
LEFT JOIN {prefix}Tag ON {prefix}Tag.tid={prefix}EventTagRelation.tid
WHERE {prefix}EventTagRelation.eid = {eid};

SELECT {prefix}EventPersonRelation.uid, {prefix}Person.personType, {prefix}Person.personData
FROM {prefix}EventPersonRelation
INNER JOIN {prefix}Person ON {prefix}EventPersonRelation.uid={prefix}Person.uid
WHERE {prefix}EventPersonRelation.eid = {eid};


SELECT {prefix}Event.eid, {prefix}Event.name, {prefix}Event.eventDateType, {prefix}Event.eventDate1,
                                            {prefix}Event.eventDatePrecision, {prefix}Event.eventDateDiff, {prefix}Event.eventDateDiffType,
                                            {prefix}Event.eventDate2, {prefix}Event.description FROM {prefix}Event
RIGHT JOIN (
   (SELECT {prefix}EventEventRelation.eid2 as eid
    FROM {prefix}EventEventRelation
    WHERE {prefix}EventEventRelation.eid1 = {eid})
   UNION
   (SELECT {prefix}EventEventRelation.eid1 as eid
    FROM {prefix}EventEventRelation
    WHERE {prefix}EventEventRelation.eid2 = {eid})
) OurEER ON OurEER.eid={prefix}Event.eid;

SELECT {prefix}ChangeLog.description, {prefix}ChangeLog.authorUid, {prefix}Person.personType, {prefix}Person.personData, {prefix}ChangeLog.date
FROM {prefix}ChangeLog
LEFT JOIN {prefix}Person ON {prefix}Person.uid={prefix}ChangeLog.authorUid
WHERE {prefix}ChangeLog.eid = {eid};

SELECT {prefix}Event.eid, {prefix}Event.name, {prefix}Event.description, {prefix}Event.eventDateType, {prefix}Event.eventDate1,
{prefix}Event.eventDatePrecision, {prefix}Event.eventDateDiff, {prefix}Event.eventDateDiffType,
{prefix}Event.eventDate2, {prefix}Event.postedUid, {prefix}Person.personType, {prefix}Person.personData, {prefix}Event.postedDate, {prefix}Event.details
FROM {prefix}Event
LEFT JOIN {prefix}Person ON {prefix}Event.postedUid = {prefix}Person.uid
WHERE {prefix}Event.eid = {eid};
