-- TODO: Create documentation of each table and its columns

CREATE TABLE IF NOT EXISTS User (
  uid      INTEGER      NOT NULL AUTO_INCREMENT,
  userInfo VARCHAR(640000) NOT NULL,
  PRIMARY KEY (uid)
);





CREATE TABLE IF NOT EXISTS Event (
  eid           INTEGER      NOT NULL AUTO_INCREMENT,
  name          VARCHAR(255) NOT NULL,
  desc          VARCHAR(640000) NOT NULL,
  details       VARCHAR(640000),
  postedUid     INTEGER                REFERENCES User(uid),
  postedDate    TIMESTAMP,

    postedDateType      CHARACTER NOT NULL CHECK(type IN ('c', 'b')), -- 'c': centered around date1, with precision and a difference, 'b': between two dates
    postedDate1     TIMESTAMP NOT NULL,  -- If type is 'c' then it is centered around this date as a timestamp with precision, if type is 'b' then this is rounded to a date

    postedDateprecision CHAR,  -- The precision of date1, e.g. do we know its on this day, or this specific hour / minute
    postedDatediff      INTEGER,  -- The difference it can be, e.g. within 2 days (see below)
    postedDatediffType  CHARACTER,  -- The type of difference: Days, Hours, or Minutes
    postedDatedate2     DATE,  -- The second date for when type is 'b'

PRIMARY KEY (eid),
    CONSTRAINT CheckDate_c_precision CHECK ((type = 'c') = (postedDateprecision IS NOT NULL AND postedDateprecision IN ('d', 'h', 'h'))),
    CONSTRAINT CheckDate_c_diff      CHECK ((type = 'c') = (postedDatediff IS NOT NULL)),
    CONSTRAINT CheckDate_c_diffType  CHECK ((type = 'c') = (postedDatediffType IS NOT NULL AND postedDateprecision IN ('d', 'h', 'h'))),
    CONSTRAINT CheckDate_c_date2     CHECK ((type = 'c') = (postedDatedate2 IS NULL)),

    CONSTRAINT CheckDate_b_precision CHECK ((type = 'b') = (postedDateprecision IS NULL)),
    CONSTRAINT CheckDate_b_diff      CHECK ((type = 'b') = (postedDatediff IS NULL)),
    CONSTRAINT CheckDate_b_diffType  CHECK ((type = 'b') = (postedDatediffType IS NULL)),
    CONSTRAINT CheckDate_b_date2     CHECK ((type = 'b') = (postedDatedate2 IS NOT NULL))
);

CREATE TABLE IF NOT EXISTS Tag (
  tid           INTEGER      NOT NULL AUTO_INCREMENT,
  name          VARCHAR(255) NOT NULL,
  desc          VARCHAR(640000) NOT NULL,
  color         INTEGER      NOT NULL,  -- _RGB, we have 8 unused bits
   PRIMARY KEY(tid)
);

CREATE TABLE IF NOT EXISTS EventTagRelation (
  etrid INTEGER NOT NULL AUTO_INCREMENT,
  eid   INTEGER NOT NULL  REFERENCES Event(eid),
  tid   INTEGER NOT NULL  REFERENCES Tag(tid),
  UNIQUE (eid, tid),
  PRIMARY KEY(etrid)
);

CREATE TABLE IF NOT EXISTS EventEventRelation (
  eerid INTEGER NOT NULL AUTO_INCREMENT,
  eid1  INTEGER NOT NULL  REFERENCES Event(eid),
  eid2  INTEGER NOT NULL  REFERENCES Event(eid),
  UNIQUE (eid1, eid2),
  PRIMARY KEY(eerid)
  CONSTRAINT CheckEventRelation CHECK (eid1 != eid2)
);

CREATE TABLE IF NOT EXISTS EventUserRelation (
  eurid INTEGER NOT NULL AUTO_INCREMENT,
  eid   INTEGER NOT NULL  REFERENCES Event(eid),
  uid   INTEGER NOT NULL  REFERENCES User(uid),
  UNIQUE (eid, uid),
  PRIMARY KEY(eurid)
);

CREATE TABLE IF NOT EXISTS ChangeLog (
  cid       INTEGER      NOT NULL AUTO_INCREMENT,
  eid       INTEGER      NOT NULL  REFERENCES Event(eid),
  desc      VARCHAR(640000) NOT NULL,
  authorUid INTEGER      NOT NULL  REFERENCES User(uid),
  date      TIMESTAMP    NOT NULL
  PRIMARY KEY(cid)
);