INSERT INTO {prefix}Event (name, description, details, eventDateType, eventDate1, eventDatePrecision, eventDateDiff, eventDateDiffType, eventDate2, postedPid) VALUES
(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

SELECT eid FROM {prefix}Event WHERE name = ?;