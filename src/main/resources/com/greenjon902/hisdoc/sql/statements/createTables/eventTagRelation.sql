CREATE TABLE IF NOT EXISTS {prefix}EventTagRelation (
  eid   INTEGER NOT NULL  REFERENCES {prefix}Event(eid),
  tid   INTEGER NOT NULL  REFERENCES {prefix}Tag(tid),
  PRIMARY KEY(eid, tid)
);
