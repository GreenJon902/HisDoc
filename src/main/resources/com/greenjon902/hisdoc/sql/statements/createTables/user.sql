CREATE TABLE IF NOT EXISTS {prefix}User (
  uid      INTEGER      NOT NULL AUTO_INCREMENT,
  userType ENUM("MC", "MISC"),  -- See the userInfo record in java for info
  userData VARCHAR(36),
  PRIMARY KEY (uid)
);