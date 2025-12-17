-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 16, 2025 at 12:51 PM
-- Server version: 8.3.0
-- PHP Version: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `media_monitoring`
--

-- --------------------------------------------------------

--
-- Table structure for table `advertisements`
--

DROP TABLE IF EXISTS `advertisements`;
CREATE TABLE IF NOT EXISTS `advertisements` (
  `ad_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` text,
  `price` decimal(10,2) DEFAULT NULL,
  `comment_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `linked_comment_id` int DEFAULT NULL,
  `created_by_user_id` int DEFAULT NULL,
  PRIMARY KEY (`ad_id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `advertisements`
--

INSERT INTO `advertisements` (`ad_id`, `user_id`, `title`, `description`, `price`, `comment_id`, `created_at`, `linked_comment_id`, `created_by_user_id`) VALUES
(1, 1, 'Smartphone Sale', 'Brand new smartphone with warranty', 499.99, NULL, '2025-11-05 07:30:03', NULL, 1),
(2, 2, 'Used Laptop', 'Second-hand laptop in good condition', 299.50, NULL, '2025-11-05 07:30:03', NULL, 2),
(5, 3, 'Tablete', 'Third brand', 5000.00, NULL, '2025-11-21 14:18:53', NULL, 3);

-- --------------------------------------------------------

--
-- Table structure for table `articles`
--

DROP TABLE IF EXISTS `articles`;
CREATE TABLE IF NOT EXISTS `articles` (
  `article_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text,
  `category_id` int DEFAULT NULL,
  `author_id` int DEFAULT NULL,
  `price_or_value` decimal(12,2) DEFAULT NULL,
  `status` varchar(50) DEFAULT 'DRAFT',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`article_id`),
  KEY `category_id` (`category_id`),
  KEY `author_id` (`author_id`)
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `articles`
--

INSERT INTO `articles` (`article_id`, `name`, `description`, `category_id`, `author_id`, `price_or_value`, `status`, `created_at`) VALUES
(12, 'ira', 'free', 1, NULL, 1000.00, 'published', '2023-10-10 22:00:00'),
(13, 'ange', 'free', 2, 3, 2000.00, 'accountant', '2025-10-22 16:50:22'),
(14, 'Aimee', 'stud', 3, 4, 3000.00, 'accountant', '2025-10-22 16:51:17'),
(16, 'uwe', 'sec', 4, 5, 4000.00, 'publisher', '2025-11-03 15:49:28');

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `attribute1` varchar(255) DEFAULT NULL,
  `attribute2` varchar(255) DEFAULT NULL,
  `attribute3` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `attribute1`, `attribute2`, `attribute3`, `created_at`) VALUES
(1, 'umulisa', 'designer', 'manufacturer', '2025-11-12 09:00:00'),
(2, 'umuhoza', 'producer', 'manufacture', '2025-10-25 20:21:28'),
(3, 'kaka', 'manager', 'producer', '2025-10-27 07:24:07'),
(4, 'uwera', 'programmer', 'desgner', '2025-10-28 06:47:17'),
(5, 'Gahozo', 'programmer', 'producer', '2025-10-28 09:26:39'),
(6, 'Ganza', 'manager', 'manufacture', '2025-11-03 15:44:40');

-- --------------------------------------------------------

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
CREATE TABLE IF NOT EXISTS `comments` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `article_id` int NOT NULL,
  `user_id` int NOT NULL,
  `content` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`),
  KEY `article_id` (`article_id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `comments`
--

INSERT INTO `comments` (`comment_id`, `article_id`, `user_id`, `content`, `created_at`) VALUES
(1, 1, 2, 'This is a great article about technology!', '2025-10-25 08:35:36'),
(3, 5, 6, 'make subscribe', '2025-03-02 10:12:00'),
(4, 8, 12, 'take decision', '2025-07-07 09:12:00'),
(2, 2, 4, 'make share', '2025-02-02 08:10:30'),
(5, 3, 1, 'make follow', '2025-07-07 08:10:30'),
(8, 7, 8, 'take production', '2025-11-03 17:23:41');

-- --------------------------------------------------------

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
CREATE TABLE IF NOT EXISTS `tags` (
  `tag_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  PRIMARY KEY (`tag_id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tags`
--

INSERT INTO `tags` (`tag_id`, `name`, `description`, `category`, `created_at`) VALUES
(1, 'ygsgdhd', '', '', '2025-11-03'),
(2, 'carine', 'stud', '5', '2025-11-03'),
(3, 'Amina', 'Manager', '2', '2025-11-03'),
(4, 'Lea', 'producer', '3', '2025-11-03'),
(5, 'Florence', 'designer', '1', '2025-11-03'),
(6, 'uwase', 'manufacturer', '6', '2025-11-03');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL DEFAULT '123456',
  `role` varchar(50) NOT NULL DEFAULT 'Viewer',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `email`, `password`, `role`, `created_at`) VALUES
(1, 'adminUser', 'admin@example.com', 'admin123', 'Admin', '2025-11-03 10:38:59'),
(2, 'editorUser', 'editor@example.com', 'editor123', 'Editor', '2025-11-03 10:38:59'),
(3, 'viewer', 'viewer@example.com', '123456', 'Viewer', '2025-11-03 12:04:54'),
(4, 'ira', 'ir@gmail.com', '123456', 'Viewer', '2025-11-05 11:50:50'),
(5, 'carine', 'carine@gmail.com', '123', 'Admin', '2025-11-05 15:24:15'),
(6, 'Umuhoza', 'umuhoza@gmail.com', '123456', 'Viewer', '2025-11-21 13:22:57'),
(7, 'carine', 'carine@gmail.com', '123', 'Admin', '2025-11-25 17:12:38'),
(8, 'carine', 'carine@gmail.com', '123', 'Admin', '2025-12-15 16:47:55'),
(9, 'carine', 'carine@gmail.com', '123', 'Admin', '2025-12-15 16:55:53');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
