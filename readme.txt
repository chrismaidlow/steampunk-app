MySQL 5.0.5 CREATE statements

User Table:
CREATE TABLE `User` (id int(11) NOT NULL AUTO_INCREMENT, `user` varchar(32) NOT NULL UNIQUE, password varchar(16) NOT NULL, Gameid int(11), PRIMARY KEY (id));

Game Table:
CREATE TABLE Game (id int(11) NOT NULL AUTO_INCREMENT, player1 int(11) NOT NULL UNIQUE, player2 int(11) NOT NULL UNIQUE, state blob, PRIMARY KEY (id));



