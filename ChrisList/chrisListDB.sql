drop database if exists ChrisListDB;

create database ChrisListDB;

use ChrisListDB;


create table `user`(
userId int primary key auto_increment,
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
    foreign key(`user_id`) references `user`(userId),
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
-- imagePath varchar(250),
foreign key (conditionId) references Conditions(conditionId),
foreign key (userId) references `user`(userId)
);

create table Images(
ListingId int not null, 
ImageId int Primary Key auto_increment,
pathString VARCHAR(100) not null,
foreign key (ListingId) references Listings(ListingId)
);

create table ListingKeywords(
ListingId int not null,
KeywordId int not null,
primary key (ListingId, KeywordId),
foreign key (ListingId) references Listings(ListingId),
foreign key (KeywordId) references keyword(KeywordId)
);

CREATE VIEW CurrentListings as
SELECT *
FROM `listings`
WHERE isDeleted = '0';

CREATE VIEW CurrentKeyword as
SELECT *
FROM `keyword`
WHERE isDeleted = '0';


INSERT INTO `user` VALUES (1,'admin','$2a$10$6/krDDN8P/i/CANT5Kn2pO6FSkczr7QbNpaaz/wnMYUpFoy/.vy9C','Mike','Wazowski','user@me.com','555-5555',1),(2,'user','$2a$10$6/krDDN8P/i/CANT5Kn2pO6FSkczr7QbNpaaz/wnMYUpFoy/.vy9C','fake','user','user@me.com','555-5555',1),(3,'oldSocks24','password','Phil','Philipson','phil@phil.com','(320)-234-2342',1),(4,'LampLamp','password','Dalton','Galloway','daltong@gmail.com','(320)-234-23412',1),(5,'AnimeIsCool','password','Patricia','Patterson','PattyP@yahoo.com','(320)-234-2340',1);

insert into Conditions (conditionType) values
('New'),
('Like New'),
('Used');

insert into `role` (`role`) VALUES
				('ROLE_ADMIN'),
                ('ROLE_USER');
--                 
insert into `user_role` (`user_id`, `role_id`) VALUES
						(1,1),
                        (1,2),
                        (2,2);

INSERT INTO `keyword` VALUES (1,'vehicle',0),(2,'furniture',0),(3,'pet',0),(4,'junk',0),(5,'clothes',0),(6,'appliance',0),(7,'toy',0),(8,'utensil',0);
  
INSERT INTO `listings` VALUES (1,'Old Socks','Bloomington','2016-04-06','These are my old socks. I do not want them in my house anymore.',0,3,3,50.00),(2,'Old Ladder','Richfield','2016-04-04','Old ladder. It is old, but is still a ladder.',0,3,3,100.00),(3,'Cool Lamp','Minneapolis','2016-04-06','This is a cool lamp. New. Is in the shape of a Cool Lamp',0,1,4,1.00),(4,'Anime Figure','Hopkins','2016-04-05','This is a Dragonball Z character. Selling it becuase I don\'t identify with the character anymore',0,2,5,1000.00),(5,'Bugs Bunny','Shoreview','2020-03-25','not the real one',0,2,4,27.00),(6,'Old coffee mug','St Paul','2019-02-20','A dirty mug with coffee stains',0,2,4,1.00),(7,'Waffle Maker','Hopkins','2016-03-05','New, but I like Pancakes better',0,1,5,50.00),(8,'Cheetah','Minneapolis','2016-04-13','New, but violent',0,1,5,1000.00),(9,'My Little Pony Figure','Hopkins','2016-04-10','Like new, but I outgrew ponies',0,2,5,1000.00),(10,'Pomeranian','Dog City','2020-04-17','<p>New dog. I\'m editing.</p>',0,1,2,34.00),(11,'Coffee Pot','Coffee City','2020-04-17','Coffee pot. I\'m giving it up.',1,2,2,10.00),(12,'Phone','Minneapolis','2020-04-17','I don\'t want this. Getting an Iphone instead of a samsung galaxy.',1,1,2,34.00),(13,'Used Cat','Minneapolis','2020-04-17','Used Cat. Still a cat. Meows.',0,3,2,0.01),(14,'Coffee Pot','Here City','2020-04-17','<p>I\'m giving the stuff up.</p>',1,1,2,2.00),(15,'Dress','Minneapolis','2020-04-17','Blue dress. Contrasts with my skin tone.',0,2,2,50.00),(16,'Brown Couch','Furnitureville','2020-04-17','Chaise.',0,1,2,3400.00),(17,'2018 FORD F550 ','Minneapolis','2020-04-17','<span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">2018 FORD F550 CREW CAB LARIAT ROLLBACK WITH WHEEL LIFT.....</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">6.7L POWERSTROKE DIESEL</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">AUTOMATIC TRANS</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">4,996 MILES</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">HEATED &amp; COOLED LEATHER INTERIOR</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">SYNC</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">STEERING WHEEL CONTROLS</span>',0,1,2,96000.00),(18,'Floral Couch','Hasting','2020-04-17','<section id=\"postingbody\" style=\"margin: 0px 0px 1em; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; line-height: inherit; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; vertical-align: baseline; clear: left; position: relative; overflow-wrap: break-word; color: #222222; background-color: #ffffff;\">Beautiful Floral Couch - in great condition! Extremely comfortable. Pick up in Hastings</section>\r\n<ul class=\"notices\" style=\"margin: 0px 0px 2em 10px; padding: 10px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; line-height: inherit; font-family: Arial, sans-serif; vertical-align: baseline; list-style-position: initial; list-style-image: initial; order: 1; color: #222222; background-color: #ffffff;\">\r\n<li style=\"margin: 0px; padding: 0px; border: 0px; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; font-size: 0.8em; line-height: inherit; font-family: inherit; vertical-align: baseline;\">do NOT contact me with unsolicited services or offers</li>\r\n</ul>',0,3,2,75.00),(19,'Used Toaster','Hasting','2020-04-17','<span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">As pictured for $5. Call Barry.</span>',0,3,2,5.00),(20,'Coffee Pot','Circle Pines','2020-04-17','<span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">American made 6 cup and a 9 cup, both complete, $25.00 each.</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">USA made 9 cup Pyrex, complete but does not have a glass stem, it does have an aluminum stem set. The glass basket also comes with it. $15.00.</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">Several US made aluminum percolator coffee pots starting at $8.00.</span>',0,3,2,5.00),(21,'Dance LP\'s/Polka/Waltzes','Minneapolis','2020-04-17','<span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">Great condition Dance LP\'s,fun and entertaining,still have plastic covering to protect the covers.</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">Polka,Waltzes for Dancing,CHA CHA CHA.$10 each.</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">Great Fun Gifts!</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">Cash only.</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">Thanks!</span><br style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\" /><span style=\"color: #222222; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; background-color: #ffffff;\">Jerry</span>',0,3,2,10.00),(22,'Dad\'s Mug','White Bear Lake','2020-04-17','<div id=\"postingbody\" style=\"margin: 0px 0px 1em; padding: 0px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; line-height: inherit; font-family: \'Bitstream Vera Serif\', \'Times New Roman\', serif; vertical-align: baseline; clear: left; position: relative; overflow-wrap: break-word; color: #222222; background-color: #ffffff; text-align: center;\"><strong>Never used 12 oz. mug. Great gift!!!</strong><br /><br /><br />Keywords: Harley, Harley-Davidson, coffee, rare, vintage, deal, bargain, Christmas, gift, man-cave, collectible, nude, xxx, biker, HD, dad, grandpa, uncle, motorcycle, friend, boyfriend, chopper,tea, beverage, cup, garage, camp, football, hockey, kitchen, beer, wine, Valentines, fathers day, late.</div>\r\n<div style=\"text-align: center;\">\r\n<ul class=\"notices\" style=\"margin: 0px 0px 2em 10px; padding: 10px; border: 0px; font-variant-numeric: inherit; font-variant-east-asian: inherit; font-stretch: inherit; line-height: inherit; font-family: Arial, sans-serif; vertical-align: baseline; list-style-position: initial; list-style-image: initial; order: 1; color: #222222; background-color: #ffffff;\">\r\n<li style=\"margin: 0px; padding: 0px; border: 0px; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; font-size: 0.8em; line-height: inherit; font-family: inherit; vertical-align: baseline;\">do NOT contact me with unsolicited services or offers</li>\r\n</ul>\r\n</div>',0,3,2,5.00),(23,'New Kitty','Minneapolis','2020-04-17','<h2 style=\"text-align: left;\"><strong>Striped<br /><br />Orange. Only bites sometimes. Purrs. Like New.&nbsp;<br /><br /></strong></h2>',0,1,2,1.00);
                    
INSERT INTO `listingkeywords` VALUES (17,1),(3,2),(16,2),(8,3),(10,3),(23,3),(2,4),(11,4),(12,4),(14,4),(19,4),(21,4),(1,5),(15,5),(2,6),(3,6),(7,6),(11,6),(12,6),(14,6),(17,6),(19,6),(21,6),(22,6),(4,7),(5,7),(9,7),(23,7),(6,8),(11,8),(22,8);

INSERT INTO `images` VALUES (11,11,'images/pot.jpg'),(11,12,'images/pot2.jpg'),(12,13,'images/phone.jpg'),(12,14,'images/phone2.jpg'),(13,15,'images/cat.jpg'),(13,16,'images/cat2.jpg'),(14,19,'images/pot.jpg'),(14,20,'images/pot2.jpg'),(15,21,'images/dress.jpg'),(16,22,'images/couch.jpg'),(10,23,'images/dog.jpg'),(10,24,'images/Dog2.jpg'),(10,25,'images/dog3.jpg'),(17,26,'images/truck.jpg'),(18,27,'images/couch2.jpg'),(19,28,'images/toaster.jpg'),(20,29,'images/pot.jpg'),(20,30,'images/pot2.jpg'),(21,31,'images/polka.jpg'),(21,32,'images/polka2.jpg'),(22,33,'images/deardad.jpg'),(23,36,'images/cat.jpg'),(23,37,'images/cat3.jpg'),(23,38,'images/cat2.jpg');

SELECT * From CurrentListings;