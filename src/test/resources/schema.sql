DROP ALL OBJECTS;

create table IF NOT EXISTS account (
 username varchar(255) ,
 password varchar(255) not null,
 mail     varchar(255) not null,
 role     varchar(255) not null,
 primary key(username)
);

create table IF NOT EXISTS password_history(
 username    varchar(255) ,
 password    varchar(255) ,
 useday      datetime     ,
 primary key(username,useday),
 CONSTRAINT unique_username_passwordhistory foreign key(username) references account(username)
);

create table IF NOT EXISTS diary_record(
 username     varchar(255) not null,
 categoryid   int          not null,
 diaryday     date        not null,
 record1      varchar(255) ,
 record2      varchar(255) ,
 record3      varchar(255) ,
 price        int,
 memo         varchar(255),
 primary key(username,categoryid,diaryday),
 CONSTRAINT unique_username_diaryrecord foreign key(username) references account(username)
 );
