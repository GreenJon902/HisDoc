SELECT {prefix}Event.eid, {prefix}Event.name, {prefix}Event.description,
{prefix}Event.eventDateType, {prefix}Event.eventDate1, {prefix}Event.eventDatePrecision, {prefix}Event.eventDateDiff, {prefix}Event.eventDateDiffType, {prefix}Event.eventDateDate2,
    JSON_ARRAYAGG({prefix}EventTagRelation.name) AS tagNames,
    JSON_ARRAYAGG({prefix}EventTagRelation.color) AS tagColors,
    JSON_ARRAYAGG({prefix}EventUserRelation.userInfo) As userInfos,
    JSON_ARRAYAGG({prefix}ChangeLog.description ORDER BY {prefix}ChangeLog.cid) AS changeDescs,
    JSON_ARRAYAGG({prefix}ChangeLog.userInfo ORDER BY {prefix}ChangeLog.cid) AS changeAuthorInfos,
    JSON_ARRAYAGG({prefix}ChangeLog.date ORDER BY {prefix}ChangeLog.cid) AS changeDates
FROM {prefix}Event
LEFT JOIN (
    SELECT eid, name, color
    FROM {prefix}EventTagRelation
    LEFT JOIN {prefix}Tag ON {prefix}Tag.tid={prefix}EventTagRelation.tid
) {prefix}EventTagRelation ON {prefix}Event.eid={prefix}EventTagRelation.eid
LEFT JOIN (
    SELECT eid, userInfo
    FROM {prefix}EventUserRelation
    LEFT JOIN {prefix}User ON {prefix}User.uid={prefix}EventUserRelation.uid
) {prefix}EventUserRelation ON {prefix}Event.eid={prefix}EventUserRelation.eid
LEFT JOIN (
    SELECT cid, eid, description, authorUid, date, userInfo
    FROM {prefix}ChangeLog
    LEFT JOIN {prefix}User ON {prefix}User.uid={prefix}ChangeLog.authorUid
) {prefix}ChangeLog ON {prefix}Event.eid={prefix}ChangeLog.eid
WHERE {prefix}Event.eid = ?
GROUP BY {prefix}Event.eid;

