CREATE TABLE `record`(
  `userId` INT(11) NOT NULL ,
  `gameId` INT(11) NOT NUll ,
  `date` CHAR(255) NOT NULL ,
  `time` CHAR(255) NOT NULL ,
  `status` BOOLEAN NOT NULL DEFAULT FALSE ,
  FOREIGN KEY (userId) REFERENCES user(id)
)