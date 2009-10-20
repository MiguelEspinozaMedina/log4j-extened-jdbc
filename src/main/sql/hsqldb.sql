CREATE TABLE  applog (
id integer primary key,
date datetime,
host varchar(40),
application varchar(50) ,
category varchar(100) ,
thread varchar(50) ,
priority varchar(20) ,
message varchar(8000) ,
throwable varchar(8000) 
)
