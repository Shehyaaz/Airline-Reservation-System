use airlinereservation;
select * from user;
select * from flight;
select * from flightclass;
select * from route;
select * from airport;
select * from airline;
select * from booking;
desc flight;
commit;
select distinct routeID from flight;
select distinct airlineID from route where routeID=340;
select name from airline where airlineID=108;

SELECT flightID as flightNumber, departureDate, departureTime, r.departureAirport, arrivalDate, arrivalTime, r.arrivalAirport, r.airlineName,totalseats,availableseats 
				FROM flight, (SELECT routeID, airline.name as airlineName, a1.name as departureAirport, a2.name as arrivalAirport 
						  FROM route, airline, airport as a1, airport as a2 
						  WHERE route.departureAirportID = a1.airportID 
						  AND route.arrivalAirportID = a2.airportID 
						  AND route.airlineID = airline.airlineID) r 
			    WHERE flight.routeID = r.routeID 
			    AND r.airlineName LIKE "Air India Limited" ORDER BY departureDate;
commit;

DROP PROCEDURE IF EXISTS bookFlight;
DELIMITER //
CREATE PROCEDURE bookFlight(
IN Flight_ID INT,
IN SeatNum_sel VARCHAR(45),
IN Class_sel VARCHAR(45),
IN User_ID INT,
IN Price INT)
BEGIN
	INSERT INTO Booking (flightID, seatNum, class, userID,price)
    VALUES (Flight_ID, SeatNum_sel, Class_sel, User_ID,Price);
    UPDATE flight SET availableseats = (availableseats-1) WHERE flightID = Flight_ID;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS cancelFlightReservation;
DELIMITER //
CREATE PROCEDURE cancelFlightReservation(
IN Flight_ID INT,
IN User_ID INT)
BEGIN
    DELETE FROM Booking
	WHERE flightID = FLight_ID 
	AND userID = User_ID;
    UPDATE flight SET availableseats = (availableseats+1) WHERE flightID = Flight_ID;
END//
DELIMITER ;

SELECT User.userID, User.firstname, User.lastname, Booking.flightID, Booking.ticketID,Booking.class,Booking.price
			FROM User ,Booking 
			where User.userID = Booking.userID  
			and Booking.flightID = 6001
			ORDER BY User.userID;
            
/*alter table flightclass drop foreign key flightclass_ibfk_1;
alter table flightclass add foreign key (flightID) references flight(flightID) on DELETE CASCADE on UPDATE cascade;
*/
select * from flightclass;

select routeID from route where departureAirportID=1045 and arrivalAirportID=1026;
/*
select * from bookingarchive;
select * from flightsarchive1;
drop table bookingarchive;
drop table flightsarchive1;
*/
/*
SET @i=0;
UPDATE booking SET ticketID=(@i:=@i+1);
*/
select * from booking;
commit;
use airlinereservation;
DROP TABLE IF EXISTS BookingArchive;
CREATE TABLE BookingArchive
(
    ticketID        INT AUTO_INCREMENT,
    flightID        INT,
    seatNum         VARCHAR(45),
    class           VARCHAR(45),
    userID     		INT,

    PRIMARY KEY(ticketID)
);
DROP TRIGGER IF EXISTS ArchiveBooking;
DELIMITER //
CREATE TRIGGER ArchiveBooking
BEFORE DELETE ON Flight
FOR EACH ROW
BEGIN
	INSERT INTO BookingArchive
    (	SELECT booking.ticketID, booking.flightID, booking.seatNUM, booking.class, booking.userID 
		FROM booking 
		WHERE booking.flightID = OLD.flightID
	); 
    
    DELETE FROM Booking WHERE flightID IS NULL;
END//
DELIMITER ;
