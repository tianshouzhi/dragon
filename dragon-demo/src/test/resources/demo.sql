#========分库dragon_sharding_00==========================
CREATE DATABASE IF NOT EXISTS dragon_sharding_00;
USE dragon_sharding_00;

CREATE TABLE `user_0000` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_0001` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE DATABASE IF NOT EXISTS dragon_sharding_00;
USE dragon_sharding_00;

#========分库dragon_sharding_01==========================
CREATE DATABASE IF NOT EXISTS dragon_sharding_01;
USE dragon_sharding_01;

CREATE TABLE `user_0100` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_0101` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;