-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : lun. 08 avr. 2024 à 14:54
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `authors`
--

INSERT INTO `authors` (`ID`, `SURNAME`, `NAME`, `STYLE`, `BIOGRAPHY`, `BIRTH_DATE`, `DEATH_DATE`) VALUES
(1, 'Albert', 'Camus', 'absurde', 'Philosophe né à Algers.', 1913, 1960),
(2, 'Charles', 'Baudelaire', 'classicisme', 'Poète né à Paris.', 1821, 1867),
(3, 'Antoine', 'de Saint-Exupéry', 'écrivain de conte', 'Disparu en vol pendant la seconde Guerre Mondiale.', 1900, 1944),
(4, 'Victor', 'Hugo', 'romancier dramaturge', 'Ecrivain et homme politique.', 1802, 1885),
(5, 'Alexandre', 'Dumas', 'romancier d\'aventure', 'Ecrivain du mouvement du romantisme.', 1824, 1895),
(9, 'Boris', 'Vian', 'poète', 'chanteur et musicien de jazz', 1920, 1959),
(11, 'Marcel', 'Proust', 'romanesque', 'Ecrivain français romanesque', 1871, 1922),
(12, 'Gustave', 'Flaubert', 'romancier', 'Né à Rouen', 1821, 1880),
(13, 'Guy', 'De Maupassant', 'naturalisme', 'Ecrivain et journaliste littéraire.', 1850, 1893),
(14, 'Jean', 'De La Fontaine', 'classicisme', 'Poète, fabuliste.', 1621, 1695);

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
  `IMAGE` varchar(255) DEFAULT NULL,
  `availability` int DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `AUTHOR_ID` (`AUTHOR_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `books`
--

INSERT INTO `books` (`ID`, `TITLE`, `AUTHOR_ID`, `GENRE`, `RELEASE_DATE`, `DESCRIPTION`, `NB_PAGES`, `IMAGE`, `availability`) VALUES
(1, 'L\'étranger', 1, 'roman', 1942, 'L\'histoire se déroule à Algers.', 214, 'letranger.jpg', 1),
(2, 'Les fleurs du mal', 2, 'poésie', 1857, 'Recueil de poèmes.', 345, 'lesfleursdumal.jpg', 1),
(3, 'Le petit prince', 3, 'conte', 1943, 'Publiée en 1943 à New York.', 84, 'lepetitprince.jpg', 1),
(4, 'Les Misérables', 4, 'roman', 1862, 'Décrit la vie de pauvres gens dans Paris.', 1201, 'lesmiserables.jpg', 0),
(5, 'Le comte de Monte Cristo', 5, 'roman', 1844, 'Inspiré du récit d\'un fait divers.', 1599, 'lecomtedemontecristo.jpg', 1),
(13, 'L\'écume des jours', 9, 'conte', 1947, 'Raconte des histoires d\'amour', 304, 'lecumedesjours.jpg', 1),
(14, 'A la recherche du temps perdu', 11, 'roman', 1927, 'Réflexion psychologique sur la littérature.', 1795, 'alarecherchedutempsperdu.jpg', 1),
(15, 'Madame Bovary', 12, 'roman réaliste', 1857, 'Histoire de l\'épouse d\'un médecin de province.', 468, 'madamebovary.jpg', 1),
(16, 'Bel-Ami', 13, 'roman réaliste', 1885, 'Retrace l\'ascension sociale de Georges Duroy.', 441, 'belami.jpg', 1),
(17, 'Fables', 14, 'fable', 1694, 'Trois recueils de 243 fables.', 243, 'fables.jpg', 1);

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
  `COMMENT` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`USER_ID`,`BOOK_ID`),
  KEY `USER_ID` (`USER_ID`),
  KEY `BOOK_ID` (`BOOK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `borrow`
--

INSERT INTO `borrow` (`USER_ID`, `BOOK_ID`, `LEASING_DATE`, `RETURN_DATE`, `GRADE`, `COMMENT`) VALUES
(3, 2, '2024-02-15', '0000-00-00', NULL, ''),
(4, 1, '2024-02-01', '2024-02-15', 1, ''),
(5, 4, '2024-04-10', '2024-05-10', NULL, NULL);

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
  ADD CONSTRAINT `books_ibfk_new` FOREIGN KEY (`AUTHOR_ID`) REFERENCES `authors` (`ID`) ON DELETE CASCADE ON UPDATE RESTRICT;

--
-- Contraintes pour la table `borrow`
--
ALTER TABLE `borrow`
  ADD CONSTRAINT `borrow_ibfk_1` FOREIGN KEY (`BOOK_ID`) REFERENCES `books` (`ID`) ON DELETE CASCADE ON UPDATE RESTRICT,
  ADD CONSTRAINT `borrow_ibfk_2` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
