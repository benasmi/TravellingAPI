-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: 2020 m. Bal 25 d. 12:30
-- Server version: 10.3.22-MariaDB-0+deb10u1
-- PHP Version: 7.3.14-1~deb10u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `travel`
--

-- --------------------------------------------------------

--
-- Sukurta duomenų struktūra lentelei `PARKING`
--

CREATE TABLE `PARKING` (
  `latitude` float DEFAULT NULL,
  `longitude` float DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `parkingId` int(11) NOT NULL,
  `fk_placeId` int(11) NOT NULL,
  `fk_apiPlaceId` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Sukurta duomenų kopija lentelei `PARKING`
--

INSERT INTO `PARKING` (`latitude`, `longitude`, `address`, `priority`, `parkingId`, `fk_placeId`, `fk_apiPlaceId`) VALUES
(54.4112, 25.1727, 'Arsenalo g. 5', 0, 1, 1, NULL),
(54.4112, 25.1727, 'Arsenalo g. 6', 1, 2, 1, NULL),
(54.4112, 50.3, 'Arsenalo g. 5', 4, 4, 1, NULL),
(54.4112, 50.4, 'Arsenalo g. 6', 3, 5, 1, NULL),
(54.4112, 50.3, 'Arsenalo g. 5', 4, 6, 1, NULL),
(54.4112, 50.3, 'Arsenalo g. 5', 4, 7, 1, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `PARKING`
--
ALTER TABLE `PARKING`
  ADD PRIMARY KEY (`parkingId`),
  ADD KEY `placeHasParking` (`fk_placeId`),
  ADD KEY `apiPlaceHasParking` (`fk_apiPlaceId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `PARKING`
--
ALTER TABLE `PARKING`
  MODIFY `parkingId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Apribojimai eksportuotom lentelėm
--

--
-- Apribojimai lentelei `PARKING`
--
ALTER TABLE `PARKING`
  ADD CONSTRAINT `apiPlaceHasParking` FOREIGN KEY (`fk_apiPlaceId`) REFERENCES `API_PLACE` (`apiPlaceId`),
  ADD CONSTRAINT `placeHasParking` FOREIGN KEY (`fk_placeId`) REFERENCES `PLACE` (`placeId`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
