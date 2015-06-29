# add user

# --- !Ups

use `crud`

DROP TABLE if exists user;

CREATE TABLE `crud`.`user` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `user_name` VARCHAR(100) NOT NULL,
    `role` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

# --- !Downs

use `crud`

DROP TABLE if exists user;
