connect 'jdbc:derby:/home/mingjun/test/wwwDB'

create table visits(num int, ip varchar(40));


 insert into visits values (1,'192.168.1.1');
 insert into visits values(2,'192.168.1.2');
 
 select * from wwwDB;