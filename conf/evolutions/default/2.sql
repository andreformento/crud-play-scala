# add manufacturer

# --- !Ups

CREATE TABLE `manufacturer` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `description` VARCHAR(100) NOT NULL,
    `link` VARCHAR(100),
    `expiry_date` date,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

ALTER TABLE `aircraft` ADD `manufacturer_Id` INT;

insert into `manufacturer` (`description`, `expiry_date`) values ('Empty/vazio', CURRENT_DATE()-1);

SET SQL_SAFE_UPDATES = 0;
update `aircraft` set `manufacturer_Id` = (select max(`id`) from `manufacturer`) where `manufacturer_Id` is null;

ALTER TABLE `aircraft` MODIFY `manufacturer_Id` INT NOT NULL;

ALTER TABLE `aircraft` ADD CONSTRAINT `fk_AIRCRAFT_manufact` FOREIGN KEY (`manufacturer_Id`) REFERENCES `manufacturer`(`id`);

# --- !Downs

ALTER TABLE `aircraft` DROP FOREIGN KEY `fk_AIRCRAFT_manufact`;

ALTER TABLE `aircraft` DROP COLUMN `manufacturer_Id`;

DROP TABLE `manufacturer`;


