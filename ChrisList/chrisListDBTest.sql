drop database if exists ChrisListDBTest;

create database ChrisListDBTest;

use ChrisListDBTest;


create table `user`(
`id` int primary key auto_increment,
`username` varchar(30) not null unique,
`password` varchar(100) not null,
firstName varchar(25) not null,
lastName varchar(50) not null,
email varchar(75) not null,
phoneNumber varchar(15) not null,
`enabled` boolean not null
);

create table `role` (
	id int primary key auto_increment,
    `role` varchar(30)
);

create table `keyword` (
	keywordId INT PRIMARY KEY auto_increment,
	`name` varchar(30) not null,
    isDeleted boolean not null
);

create table `user_role` (
	`user_id` int not null,
    `role_id` int not null,
    primary key(`user_id`, `role_id`),
    foreign key(`user_id`) references `user`(id),
    foreign key(`role_id`) references `role`(id)
);

create table Conditions (
	conditionId int primary key auto_increment,
	conditionType varchar(20) not null
);

create table `listings` (
`ListingId` int not null auto_increment primary key,
`Title` varchar(45) not null,
`City` varchar(30) not null,
`ListDate` date not null,
`content` MEDIUMTEXT not null,
`isDeleted` boolean DEFAULT 0 not null,
conditionId int not null,
userId int not null,
price decimal(10, 2) not null,
foreign key (conditionId) references Conditions(conditionId),
foreign key (userId) references `user`(`id`)
);

create table ListingKeywords(
ListingId int not null,
KeywordId int not null,
primary key (ListingId, KeywordId),
foreign key (ListingId) references Listings(ListingId),
foreign key (KeywordId) references keyword(KeywordId)
);

create table Images(
ListingId int not null, 
ImageId int Primary Key auto_increment,
pathString VARCHAR(100) not null,
foreign key (ListingId) references Listings(ListingId)
);



CREATE VIEW CurrentListings as
SELECT *
FROM `listings`
WHERE isDeleted = '0';

CREATE VIEW CurrentKeyword as
SELECT *
FROM `keyword`
WHERE isDeleted = '0';


insert into `user`(`username`,`password`,`enabled`, firstName, lastName, email, phoneNumber)	VALUES
					("admin", "$2a$10$6/krDDN8P/i/CANT5Kn2pO6FSkczr7QbNpaaz/wnMYUpFoy/.vy9C", 1,'Mike', 'Wazowski', 'user@me.com', '555-5555'),
					("user", "$2a$10$6/krDDN8P/i/CANT5Kn2pO6FSkczr7QbNpaaz/wnMYUpFoy/.vy9C", 1, 'fake', 'user', 'user@me.com', '555-5555');

insert into Conditions (conditionType) values
('New'),
('Like New'),
('Used');

insert into `role` (`role`) VALUES
				('ROLE_ADMIN'),
                ('ROLE_USER');
                
insert into `user_role` (`user_id`, `role_id`) VALUES
						(1,1),
                        (1,2),
                        (2,2);


 -- inserting some users and listings and stuff so I can test them    
 
 -- inserting some users and listings and stuff so I can test them    
insert into `user`(`username`,`password`,`enabled`, firstName, lastName, email, phoneNumber) VALUES
 ('oldSocks24', 'password', 1, 'Phil', 'Philipson', 'phil@phil.com', '(320)-234-2342'),
 ('LampLamp', 'password', 1, 'Dalton', 'Galloway', 'daltong@gmail.com', '(320)-234-23412'),
 ('AnimeIsCool', 'password', 1, 'Patricia', 'Patterson', 'PattyP@yahoo.com', '(320)-234-2340')
 ;
 
 insert into keyword (`name`, isDeleted) VALUES
					('vehicle', '0'),
                    ('furniture', '0'),
                    ('pet','0'),
                    ('junk', '0'),
                    ('clothes', '0'),
                    ('appliance', '0'),
                    ('toy', '0'),
                    ('utensil', '0');
                    
                    
 
insert into listings (`Title`, City, ListDate, content, isDeleted, conditionId, userId, price) VALUES 
('Old Socks', 'Bloomington', '2016-04-06', 'These are my old socks. I do not want them in my house anymore.', 0, 3, 3, 50),
('Old Ladder', 'Richfield', '2016-04-04', 'Old ladder. It is old, but is still a ladder.', 0, 3, 3, 100),
('Cool Lamp', 'Minneapolis', '2016-04-06', 'This is a cool lamp. New. Is in the shape of a Cool Lamp', 0, 1, 4, 1),
('Anime Figure', 'Hopkins', '2016-04-05', "This is a Dragonball Z character. Selling it becuase I don't identify with the character anymore", 0, 2, 5, 1000),
('Bugs Bunny', 'Shoreview', '2020-03-25', 'not the real one', 0, 2, 4, 27.00),
('Old coffee mug', 'St Paul', '2019-02-20', 'A dirty mug with coffee stains', 0, 2, 4, 1),
('Waffle Maker', 'Hopkins', '2016-03-05', "New, but I like Pancakes better", 0, 1, 5, 50),
('Cheetah', 'Minneapolis', '2016-04-13', "New, but violent", 0, 1, 5, 1000),
('My Little Pony Figure', 'Hopkins', '2016-04-10', "Like new, but I outgrew ponies", 0, 2, 5, 1000)
;


				
                    
-- inserting into bridge table so I can test "getListingsByKeyword" method
insert into ListingKeywords(ListingId, KeywordId) values
(1, 5),
(2, 4),
(2, 6),
(3, 6),
(3, 2),
(4, 7),
(5, 7),
(6, 8),
(7, 6),
(8, 3),
(8, 1),
(9, 7)	
;




