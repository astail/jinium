CREATE DATABASE IF NOT EXISTS `jinium` DEFAULT CHARSET utf8mb4;

DROP TABLE IF EXISTS `slack_users`;
CREATE TABLE `slack_users` (
  `uid` char(10) NOT NULL,
  UNIQUE KEY `idx_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `jinjer`;
CREATE TABLE `jinjer` (
  `company_id` char(10) NOT NULL,
  `uid` varchar(100) NOT NULL,
  `pass` varchar(100) NOT NULL,
  `slack_user_uid` char(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
