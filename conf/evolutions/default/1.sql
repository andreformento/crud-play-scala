# add aircraft

# --- !Ups

CREATE TABLE `aircraft` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `description` VARCHAR(100) NOT NULL,
    `initials` VARCHAR(40) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

# --- !Downs

DROP TABLE `aircraft`;

