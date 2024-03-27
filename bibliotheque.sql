-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : mer. 27 mars 2024 à 09:02
-- Version du serveur : 8.2.0
-- Version de PHP : 8.2.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `bibliotheque`
--

-- --------------------------------------------------------

--
-- Structure de la table `authors`
--

DROP TABLE IF EXISTS `authors`;
CREATE TABLE IF NOT EXISTS `authors` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `SURNAME` varchar(50) NOT NULL,
  `NAME` varchar(50) NOT NULL,
  `STYLE` varchar(50) NOT NULL,
  `BIOGRAPHY` varchar(400) NOT NULL,
  `BIRTH_DATE` int NOT NULL,
  `DEATH_DATE` int NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `authors`
--

INSERT INTO `authors` (`ID`, `SURNAME`, `NAME`, `STYLE`, `BIOGRAPHY`, `BIRTH_DATE`, `DEATH_DATE`) VALUES
(1, 'Albert', 'Camus', 'absurde', 'Philosophe né à Algers.', 1913, 1960),
(2, 'Charles', 'Baudelaire', 'classicisme', 'Poète né à Paris.', 1821, 1867),
(3, 'Antoine', 'de Saint-Exupéry', 'écrivain de conte', 'Disparu en vol pendant la seconde Guerre Mondiale.', 1900, 1944),
(4, 'Victor', 'Hugo', 'romancier dramaturge', 'Ecrivain et homme politique.', 1802, 1885),
(5, 'Alexandre', 'Dumas', 'romancier d\'aventure', 'Ecrivain du mouvement du romantisme.', 1824, 1895);

-- --------------------------------------------------------

--
-- Structure de la table `books`
--

DROP TABLE IF EXISTS `books`;
CREATE TABLE IF NOT EXISTS `books` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `TITLE` varchar(100) NOT NULL,
  `AUTHOR_ID` int NOT NULL,
  `GENRE` varchar(50) NOT NULL,
  `RELEASE_DATE` int NOT NULL,
  `DESCRIPTION` varchar(300) NOT NULL,
  `NB_PAGES` int NOT NULL,
  `IMAGE` varchar(50) NOT NULL,
  `AVAILABILITY` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `AUTHOR_ID` (`AUTHOR_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `books`
--

INSERT INTO `books` (`ID`, `TITLE`, `AUTHOR_ID`, `GENRE`, `RELEASE_DATE`, `DESCRIPTION`, `NB_PAGES`, `IMAGE`, `AVAILABILITY`) VALUES
(1, 'L\'étranger', 1, 'roman', 1942, 'Auteur : Albert Camus', 214, '', 1),
(2, 'Les fleurs du mal', 2, 'poésie', 1857, 'Auteur : Charles Beaudelaire', 345, '', 1),
(3, 'Le petit prince', 3, 'conte', 1943, 'Auteur : Antoine de Saint Exupéry', 84, '', 1),
(4, 'Les Misérables', 4, 'roman', 1862, 'Auteur : Victor Hugo', 1201, '', 1),
(5, 'Le comte de Monte Cristo', 5, 'roman', 1844, 'Auteur : Alexandre Dumas', 1598, '', 1);

-- --------------------------------------------------------

--
-- Structure de la table `borrow`
--

DROP TABLE IF EXISTS `borrow`;
CREATE TABLE IF NOT EXISTS `borrow` (
  `USER_ID` int NOT NULL,
  `BOOK_ID` int NOT NULL,
  `LEASING_DATE` date NOT NULL,
  `RETURN_DATE` date NOT NULL,
  `GRADE` int DEFAULT NULL,
  `COMMENT` varchar(300) NOT NULL,
  PRIMARY KEY (`USER_ID`,`BOOK_ID`),
  KEY `USER_ID` (`USER_ID`),
  KEY `BOOK_ID` (`BOOK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `borrow`
--

INSERT INTO `borrow` (`USER_ID`, `BOOK_ID`, `LEASING_DATE`, `RETURN_DATE`, `GRADE`, `COMMENT`) VALUES
(3, 2, '2024-02-15', '0000-00-00', NULL, ''),
(4, 1, '2024-02-01', '2024-02-15', 1, '');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `SURNAME` varchar(50) NOT NULL,
  `NAME` varchar(50) NOT NULL,
  `EMAIL` varchar(100) NOT NULL,
  `PASSWORD` varchar(200) NOT NULL,
  `ADMIN` tinyint(1) NOT NULL DEFAULT '0',
  `DEBT` float NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`ID`, `SURNAME`, `NAME`, `EMAIL`, `PASSWORD`, `ADMIN`, `DEBT`) VALUES
(3, 'Gabriel', 'Brousse', 'gabriel@gmail.com', 'test', 0, 0),
(4, 'Clément', 'Chastagner', 'clement@gmail.com', 'test', 0, 0),
(5, 'Hubert', 'Chavasse', 'hubert@gmail.com', 'test', 0, 0);

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `books`
--
ALTER TABLE `books`
  ADD CONSTRAINT `books_ibfk_1` FOREIGN KEY (`AUTHOR_ID`) REFERENCES `authors` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Contraintes pour la table `borrow`
--
ALTER TABLE `borrow`
  ADD CONSTRAINT `borrow_ibfk_1` FOREIGN KEY (`BOOK_ID`) REFERENCES `books` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `borrow_ibfk_2` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
