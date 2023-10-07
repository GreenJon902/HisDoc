CREATE TABLE IF NOT EXISTS {prefix}EventEventRelation (
  eerid INTEGER NOT NULL AUTO_INCREMENT,
  eid1  INTEGER NOT NULL  REFERENCES {prefix}Event(eid),
  eid2  INTEGER NOT NULL  REFERENCES {prefix}Event(eid),
  UNIQUE (eid1, eid2),
  PRIMARY KEY(eerid),
  CONSTRAINT CheckEventRelation CHECK (eid1 != eid2)
);