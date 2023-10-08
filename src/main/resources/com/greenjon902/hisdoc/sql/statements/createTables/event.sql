CREATE TABLE IF NOT EXISTS {prefix}Event (
    eid           INTEGER      NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    description   LONGTEXT NOT NULL,
    details       LONGTEXT,
    postedUid     INTEGER              REFERENCES {prefix}User(uid),
    postedDate    TIMESTAMP NULL DEFAULT NULL,

    eventDateType      CHARACTER NOT NULL CHECK(eventDateType IN ('c', 'b')), -- 'c': centered around date1, with precision and a difference, 'b': between two dates
    eventDate1     TIMESTAMP NOT NULL,  -- If type is 'c' then it is centered around this date as a timestamp with precision, if type is 'b' then this is rounded to a date

    eventDatePrecision CHAR,  -- The precision of date1, e.g. do we know its on this day, or this specific hour / minute
    eventDateDiff      INTEGER,  -- The difference it can be, e.g. within 2 days (see below)
    eventDateDiffType  CHARACTER,  -- The type of difference: Days, Hours, or Minutes
    eventDate2     DATE,  -- The second date for when type is 'b'

    PRIMARY KEY (eid),
    CONSTRAINT CheckDate_c_precision CHECK ((eventDateType = 'c') = (eventDatePrecision IS NOT NULL AND eventDatePrecision IN ('d', 'h', 'h', 'm'))),
    CONSTRAINT CheckDate_c_diff      CHECK ((eventDateType = 'c') = (eventDateDiff IS NOT NULL)),
    CONSTRAINT CheckDate_c_diffType  CHECK ((eventDateType = 'c') = (eventDateDiffType IS NOT NULL AND eventDatePrecision IN ('d', 'h', 'h', 'm'))),
    CONSTRAINT CheckDate_c_date2     CHECK ((eventDateType = 'c') = (eventDate2 IS NULL)),

    CONSTRAINT CheckDate_b_precision CHECK ((eventDateType = 'b') = (eventDatePrecision IS NULL)),
    CONSTRAINT CheckDate_b_diff      CHECK ((eventDateType = 'b') = (eventDateDiff IS NULL)),
    CONSTRAINT CheckDate_b_diffType  CHECK ((eventDateType = 'b') = (eventDateDiffType IS NULL)),
    CONSTRAINT CheckDate_b_date2     CHECK ((eventDateType = 'b') = (eventDate2 IS NOT NULL))
);