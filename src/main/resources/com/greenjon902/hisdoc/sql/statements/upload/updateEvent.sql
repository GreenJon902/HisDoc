UPDATE {prefix}Event
SET
    name = ?,
    description = ?,
    details = ?,
    eventDateType = ?,
    eventDate1 = ?,
    eventDateTimeOffset = ?,
    eventDateUnits = ?,
    eventDateDiff = ?,
    eventDate2 = ?
WHERE eid = ?;

INSERT INTO {prefix}ChangeLog (eid, description, authorPid, date) VALUES
(?, ?, ?, UNIX_TIMESTAMP());
