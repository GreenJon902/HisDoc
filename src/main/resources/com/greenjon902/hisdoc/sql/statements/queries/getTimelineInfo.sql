SELECT {prefix}Event.eid, {prefix}Event.name, {prefix}Event.eventDateType, {prefix}Event.eventDate1,
{prefix}Event.eventDatePrecision, {prefix}Event.eventDateDiff, {prefix}Event.eventDateDiffType,
{prefix}Event.eventDate2, {prefix}Event.description FROM {prefix}Event;

SELECT {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.color
FROM {prefix}Tag;

SELECT {prefix}User.uid, {prefix}User.userType, {prefix}User.userData
FROM {prefix}User;

SELECT {prefix}EventTagRelation.eid, {prefix}EventTagRelation.tid FROM {prefix}EventTagRelation;
SELECT {prefix}EventUserRelation.eid, {prefix}EventUserRelation.uid FROM {prefix}EventUserRelation;