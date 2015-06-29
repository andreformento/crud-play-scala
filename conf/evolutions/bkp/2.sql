# add country

# --- !Ups

use `crud`

DROP TABLE if exists country;

CREATE TABLE `crud`.`country` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `population` int ,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

# --- !Downs

use `crud`

DROP TABLE if exists country;