CREATE TABLE IF NOT EXISTS {prefix}EventTagRelation (
  etrid INTEGER NOT NULL AUTO_INCREMENT,
  eid   INTEGER NOT NULL  REFERENCES {prefix}Event(eid),
  tid   INTEGER NOT NULL  REFERENCES {prefix}Tag(tid),
  UNIQUE (eid, tid),
  PRIMARY KEY(etrid)
);
