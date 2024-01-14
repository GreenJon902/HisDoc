CREATE TABLE IF NOT EXISTS {prefix}EventPersonRelation (
  eid   INTEGER NOT NULL  REFERENCES {prefix}Event(eid),
  pid   INTEGER NOT NULL  REFERENCES {prefix}Person(pid),
  PRIMARY KEY(eid, pid)
);