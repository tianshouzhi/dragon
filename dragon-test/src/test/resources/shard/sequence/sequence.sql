#========分库dragon_sharding_0==========================
CREATE DATABASE IF NOT EXISTS dragon_sharding_0;
USE dragon_sharding_0;

CREATE TABLE `user_0` (
  `id` int(11) NOT NULL,
  `dsName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_1` (
  `id` int(11) NOT NULL,
  `dsName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#========分库dragon_sharding_1==========================
CREATE DATABASE IF NOT EXISTS dragon_sharding_1;
USE dragon_sharding_1;

CREATE TABLE `user_2` (
  `id` int(11) NOT NULL,
  `dsName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_3` (
  `id` int(11) NOT NULL,
  `dsName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;