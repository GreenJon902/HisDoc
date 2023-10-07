CREATE TABLE IF NOT EXISTS {prefix}EventUserRelation (
  eurid INTEGER NOT NULL AUTO_INCREMENT,
  eid   INTEGER NOT NULL  REFERENCES {prefix}Event(eid),
  uid   INTEGER NOT NULL  REFERENCES {prefix}User(uid),
  UNIQUE (eid, uid),
  PRIMARY KEY(eurid)
);