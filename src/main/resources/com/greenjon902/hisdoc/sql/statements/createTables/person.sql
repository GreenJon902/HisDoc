CREATE TABLE IF NOT EXISTS {prefix}Person (
  pid      INTEGER      NOT NULL AUTO_INCREMENT,
  personType ENUM("MC", "MISC") NOT NULL,  -- See the personInfo record in java for info
  personData VARCHAR(36),
  PRIMARY KEY (pid)
);