CREATE TABLE IF NOT EXISTS {prefix}EventEventRelation (
  eid1  INTEGER NOT NULL  REFERENCES {prefix}Event(eid),
  eid2  INTEGER NOT NULL  REFERENCES {prefix}Event(eid),
  PRIMARY KEY(eid1, eid2),
  CONSTRAINT CheckEventRelation CHECK (eid1 != eid2)
);