CREATE TABLE IF NOT EXISTS {prefix}Event (
    eid           INTEGER      NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    description   LONGTEXT NOT NULL,
    details       LONGTEXT,
    postedPid     INTEGER              REFERENCES {prefix}Person(pid),
    postedDate    BIGINT UNSIGNED NULL,  -- A value is automatically set in the insert statement in upload/addEvent.sql

    eventDateType      ENUM('c', 'r') NOT NULL, -- 'c': centered around date1, with units and a difference, 'r': between eventDate1 and eventDate2 in days
    eventDate1     BIGINT NOT NULL,  -- If type is 'c' then this has the units of eventDateUnits

    eventDateUnits ENUM('d', 'h', 'm'),  -- The value of a number in eventDate1 and eventDateDiff, can be days, hours or minutes
    eventDateDiff      BIGINT UNSIGNED,  -- The amount either side it can be, e.g. within 2 days
    eventDate2     BIGINT UNSIGNED,  -- The second date for when type is 'b'

    PRIMARY KEY (eid),
    UNIQUE (name),
    CONSTRAINT CheckDate_c_units CHECK ((eventDateType = 'c') = (eventDateUnits IS NOT NULL)),
    CONSTRAINT CheckDate_c_diff      CHECK ((eventDateType = 'c') = (eventDateDiff IS NOT NULL)),
    CONSTRAINT CheckDate_c_date2     CHECK ((eventDateType = 'c') = (eventDate2 IS NULL)),

    CONSTRAINT CheckDate_b_units CHECK ((eventDateType = 'r') = (eventDateUnits IS NULL)),
    CONSTRAINT CheckDate_b_diff      CHECK ((eventDateType = 'r') = (eventDateDiff IS NULL)),
    CONSTRAINT CheckDate_b_date2     CHECK ((eventDateType = 'r') = (eventDate2 IS NOT NULL)),

    CONSTRAINT CheckDate_b_date2_after     CHECK (eventDate1 <= eventDate2)
);