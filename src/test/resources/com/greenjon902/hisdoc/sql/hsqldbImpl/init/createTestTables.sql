CREATE TABLE IF NOT EXISTS Tags (
  tid           INTEGER      NOT NULL PRIMARY KEY IDENTITY,
  name          VARCHAR(255) NOT NULL,
  desc          VARCHAR(64k) NOT NULL,
  color         INTEGER      NOT NULL  -- _RGB, we have 8 unused bits
);

CREATE TABLE IF NOT EXISTS Users (
  uid      INTEGER      NOT NULL PRIMARY KEY IDENTITY,
  userInfo VARCHAR(64k) NOT NULL
);

CREATE TABLE IF NOT EXISTS Dates (
  did       INTEGER   NOT NULL PRIMARY KEY IDENTITY,
  type      CHARACTER NOT NULL CHECK(type IN ('c', 'b')), -- 'c': centered around date1, with precision and a difference, 'b': between two dates
  date1     TIMESTAMP NOT NULL,  -- If type is 'c' then it is centered around this date as a timestamp with precision, if type is 'b' then this is rounded to a date

  precision CHAR,  -- The precision of date1, e.g. do we know its on this day, or this specific hour / minute
  diff      INTEGER,  -- The difference it can be, e.g. within 2 days (see below)
  diffType  CHARACTER,  -- The type of difference: Days, Hours, or Minutes
  date2     DATE,  -- The second date for when type is 'b'

  CONSTRAINT CheckDate_c_precision CHECK ((type = 'c') = (precision IS NOT NULL AND precision IN ('d', 'h', 'h'))),
  CONSTRAINT CheckDate_c_diff      CHECK ((type = 'c') = (diff IS NOT NULL)),
  CONSTRAINT CheckDate_c_diffType  CHECK ((type = 'c') = (diffType IS NOT NULL AND precision IN ('d', 'h', 'h'))),
  CONSTRAINT CheckDate_c_date2     CHECK ((type = 'c') = (date2 IS NULL)),

  CONSTRAINT CheckDate_b_precision CHECK ((type = 'b') = (precision IS NULL)),
  CONSTRAINT CheckDate_b_diff      CHECK ((type = 'b') = (diff IS NULL)),
  CONSTRAINT CheckDate_b_diffType  CHECK ((type = 'b') = (diffType IS NULL)),
  CONSTRAINT CheckDate_b_date2     CHECK ((type = 'b') = (date2 IS NOT NULL)),
);
