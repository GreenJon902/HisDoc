INSERT INTO {prefix}Event (name, description, details, eventDateType, eventDate1, eventDateTimeOffset, eventDateUnits, eventDateDiff, eventDate2, postedPid, postedDate) VALUES
(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP());
-- We set the postedDate here as we don't need java to do that, and this is the only way java will add events.
-- Therefor if an event is added it will either be through java, or by an administrator  who can set the date as they will.

SELECT eid FROM {prefix}Event WHERE name = ?;