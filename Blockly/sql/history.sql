CREATE TABLE `history`(
  `userId` INT(11) NOT NULL ,
  `gameId` INT(11) NOT NULL ,
  `history` LONGTEXT,
  FOREIGN KEY (userId) REFERENCES user(id),
  PRIMARY KEY (userId,gameId)
)