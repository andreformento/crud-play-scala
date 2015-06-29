# add user

# --- !Ups

CREATE TABLE `user` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `user_name` VARCHAR(100) NOT NULL,
    `role` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

# --- !Downs

DROP TABLE `user`;

