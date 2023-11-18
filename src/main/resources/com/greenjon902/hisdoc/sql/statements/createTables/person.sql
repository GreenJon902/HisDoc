CREATE TABLE IF NOT EXISTS {prefix}Person (
  uid      INTEGER      NOT NULL AUTO_INCREMENT,
  personType ENUM("MC", "MISC"),  -- See the personInfo record in java for info
  personData VARCHAR(36),
  PRIMARY KEY (uid)
);