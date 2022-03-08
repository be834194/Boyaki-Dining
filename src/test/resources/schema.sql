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
 CONSTRAINT unique_username_passwordhistory 
            foreign key(username) references account(username) on delete cascade
);

create table IF NOT EXISTS diary_record(
 username     varchar(255) not null,
 categoryid   int          not null,
 diaryday     date        not null,
 record1      varchar(255) ,
 record2      varchar(255) ,
 record3      varchar(255) ,
 price        int,
 memo         varchar(255) ,
 createat     datetime     ,
 updateat     datetime     ,
 primary key(username,categoryid,diaryday),
 CONSTRAINT unique_username_diaryrecord 
            foreign key(username) references account(username) on delete cascade
 );

 create table IF NOT EXISTS account_info(
 username varchar(255) not null,
 nickname varchar(255) unique,
 profile  varchar(255) ,
 status   int          ,
 gender   int          ,
 age      int          ,
 primary key(username),
 CONSTRAINT unique_username_accountinfo 
            foreign key(username) references account(username) on delete cascade
 );
 
 create table IF NOT EXISTS post(
 username     varchar(255) not null,
 nickname     varchar(255) unique,
 content      varchar(255) not null,
 postcategory int          not null,
 createat     datetime,
 primary key(username,createat),
 CONSTRAINT unique_username_post foreign key(username) 
                                 references account(username) on delete cascade,
 CONSTRAINT unique_nickname_post foreign key(nickname) 
                                 references account_info(nickname) on delete cascade on update cascade
 );