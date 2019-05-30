DROP DATABASE IF EXISTS youtube_project;
CREATE DATABASE youtube_project;
USE youtube_project;

CREATE TABLE User (
  id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(20) NOT NULL UNIQUE,
  email VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(250) NOT NULL
);

CREATE TABLE Playlist (
  id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  private BOOLEAN NOT NULL,
  user_id INTEGER UNSIGNED NOT NULL,
  FOREIGN KEY (user_id)
	REFERENCES User (id)
);

CREATE TABLE Song (
  id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  title varchar(50) NOT NULL,
  reference varchar(250) NOT NULL UNIQUE
);

CREATE TABLE Playlist_Song (
  id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  playlist_id INTEGER UNSIGNED NOT NULL,
  song_id INTEGER UNSIGNED NOT NULL,
  FOREIGN KEY (playlist_id)
	REFERENCES Playlist (id),
  FOREIGN KEY (song_id)
    REFERENCES Song (id)
);

INSERT INTO User (username, email, password_hash) VALUES ('test', 'test@test.com', 'test');
INSERT INTO Song (title, reference) VALUES ('testTitle', 'testReference');
