DROP DOMAIN Problem;
DROP INDEX Index_Lists_Name;
DROP INDEX Index_Files_Path;
DROP INDEX Index_Files_SearchName;
DROP INDEX Index_ListsContent_Position;

DROP TABLE TrackInfo;
DROP TABLE ListsContent;
DROP TABLE Files;
DROP TABLE Settings;
DROP TABLE Problems;
DROP TABLE Lists;

CREATE TABLE Lists (
       Index INTEGER NOT NULL
     , Name VARCHAR(64) NOT NULL CONSTRAINT Key_Lists_Name UNIQUE
     , Description LONG VARCHAR
     , Priority SMALLINT NOT NULL WITH DEFAULT 0
     , CONSTRAINT PK_LISTS PRIMARY KEY (Index)
);

CREATE TABLE Problems (
       Index SMALLINT NOT NULL
     , Name VARCHAR(256) NOT NULL CONSTRAINT Key_Problems_Name UNIQUE
     , CONSTRAINT PK_PROBLEMS PRIMARY KEY (Index)
);

CREATE TABLE Settings (
       Name CHAR(256) NOT NULL
     , Value LONG VARCHAR
     , CONSTRAINT PK_SETTINGS PRIMARY KEY (Name)
);

CREATE TABLE Files (
       Index INTEGER NOT NULL
     , Path VARCHAR(4092) NOT NULL CONSTRAINT Key_Files_Path UNIQUE
     , SearchName VARCHAR(1024) NOT NULL
     , Name VARCHAR(1024) NOT NULL
     , Duration DOUBLE NOT NULL WITH DEFAULT 0
     , Size BIGINT NOT NULL WITH DEFAULT 0
     , Problem SMALLINT NOT NULL WITH DEFAULT 0
     , CONSTRAINT PK_FILES PRIMARY KEY (Index)
     , CONSTRAINT FK_Files_Problem FOREIGN KEY (Problem)
                  REFERENCES Problems (Index) ON DELETE RESTRICT ON UPDATE RESTRICT
);
CREATE INDEX Index_Files_SearchName ON Files (SearchName ASC);

CREATE TABLE ListsContent (
       List INTEGER NOT NULL
     , Index INTEGER NOT NULL
     , Position INTEGER NOT NULL
     , CONSTRAINT PK_LISTSCONTENT PRIMARY KEY (List, Index)
     , CONSTRAINT "Key_ListsContent_List+Position" UNIQUE (List, Position)
     , CONSTRAINT FK_ListsContent_Files FOREIGN KEY (Index)
                  REFERENCES Files (Index)
     , CONSTRAINT FK_ListsContent_Lists FOREIGN KEY (List)
                  REFERENCES Lists (Index)
);

CREATE TABLE TrackInfo (
       File INTEGER NOT NULL
     , Info LONG VARCHAR NOT NULL
     , CONSTRAINT PK_TRACKINFO PRIMARY KEY (File)
     , CONSTRAINT FK_TrackInfo_File FOREIGN KEY (File)
                  REFERENCES Files (Index)
);

INSERT INTO Problems (Index, Name) VALUES
	(0, 'kein Problem'),
	(1, 'Datei nicht gefunden'),
	(2, 'Datei kann nicht geöffnet werden'),
	(3, 'Datei kann nicht abgespielt werden'),
	(-1, 'Sonstige Probleme');
