SELECT {prefix}Tag.tid, {prefix}Tag.name, {prefix}Tag.color
FROM {prefix}EventTagRelation
LEFT JOIN {prefix}Tag ON {prefix}Tag.tid={prefix}EventTagRelation.tid
WHERE {prefix}EventTagRelation.eid = {eid};

SELECT {prefix}EventUserRelation.uid, {prefix}User.userInfo
FROM {prefix}EventUserRelation
INNER JOIN {prefix}User ON {prefix}EventUserRelation.uid={prefix}User.uid
WHERE {prefix}EventUserRelation.eid = {eid};

SELECT {prefix}ChangeLog.description, {prefix}ChangeLog.authorUid, {prefix}User.userInfo as authorInfo, {prefix}ChangeLog.date
FROM {prefix}ChangeLog
LEFT JOIN {prefix}User ON {prefix}User.uid={prefix}ChangeLog.authorUid
WHERE {prefix}ChangeLog.eid = {eid};

SELECT {prefix}Event.name, {prefix}Event.description, {prefix}Event.eventDateType, {prefix}Event.eventDate1,
{prefix}Event.eventDatePrecision, {prefix}Event.eventDateDiff, {prefix}Event.eventDateDiffType,
{prefix}Event.eventDate2, {prefix}Event.postedUid, {prefix}User.userInfo AS postedInfo, {prefix}Event.postedDate
FROM {prefix}Event
LEFT JOIN {prefix}User ON {prefix}Event.postedUid = {prefix}User.uid
WHERE {prefix}Event.eid = {eid};
