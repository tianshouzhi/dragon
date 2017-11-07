create database if not exists dragon;

CREATE TABLE `cluster` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024),
  `db_version` varchar(255) DEFAULT NULL,
  `env` varchar(255) DEFAULT NULL,
  `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `gmt_update` (`gmt_update`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `instance` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `clusterId` bigint NOT NULL,
  `ip` varchar(255) NOT NULL,
  `port` int(11) NOT NULL,
  `status` varchar(255) NOT NULL,
  `is_master` boolean NOT NULL,
  `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `gmt_update` (`gmt_update`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `database` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `clusterId` bigint NOT NULL,
  `dbName` varchar(255) NOT NULL,
  `charset` varchar(255),
  `description` varchar(1024),
  `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `gmt_update` (`gmt_update`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `database_account` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `appName` varchar(255),
  `databaseId` bigint NOT NULL,
  `secretKey` varchar(255) NOT NULL,
  `readUserName` varchar(255) NOT NULL,
  `readPassword` varchar(255) NOT NULL,
  `writeUserName` varchar(255) NOT NULL,
  `writePassword` varchar(255) NOT NULL,
  `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `appId` (`appId`),
  KEY `gmt_update` (`gmt_update`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
