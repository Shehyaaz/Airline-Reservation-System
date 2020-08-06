
DROP DATABASE IF EXISTS Flight_Reservation;
CREATE DATABASE Flight_Reservation;
USE Flight_Reservation;

DROP TABLE IF EXISTs User;
CREATE TABLE User
(
    userID     		INT AUTO_INCREMENT,
    firstName       VARCHAR(45),
    lastName        VARCHAR(45),
    email           VARCHAR(45),
    password        VARCHAR(45),
    age             INT,

    PRIMARY KEY(userID)
);

DROP TABLE IF EXISTS Airline;
CREATE TABLE Airline
(
    airlineID      	INT AUTO_INCREMENT,
    name         	VARCHAR(45),
    code         	VARCHAR(10),
    country         VARCHAR(45),
    
    PRIMARY KEY(airlineID)
);

DROP TABLE IF EXISTS Airport;
CREATE TABLE Airport
(
    airportID      	INT AUTO_INCREMENT,
    name         	VARCHAR(70),
    city         	VARCHAR(45),
    country         VARCHAR(45),
    code         	VARCHAR(10),
    
    PRIMARY KEY(airportID)
);

DROP TABLE IF EXISTS Route;
CREATE TABLE Route
(
	routeID				INT AUTO_INCREMENT,
	airlineID			INT,
    departureAirportID	INT,
    arrivalAirportID	INT,
    
    PRIMARY KEY(routeID),
    FOREIGN KEY(airlineID) 				REFERENCES Airline(airlineID),
    FOREIGN KEY(departureAirportID) 	REFERENCES Airport(airportID),
    FOREIGN KEY(arrivalAirportID) 		REFERENCES Airport(airportID)
);

DROP TABLE IF EXISTS Pilot;
CREATE TABLE Pilot
(
    pilotID         INT AUTO_INCREMENT,
    firstName       VARCHAR(45),
    lastName        VARCHAR(45),
    experience      INT,
    
    PRIMARY KEY(pilotID)

);

DROP TABLE IF EXISTS Flight;
CREATE TABLE Flight
(
    flightID        INT AUTO_INCREMENT,
    departureTime   TIME DEFAULT '00:00:00',
    departureDate   DATE DEFAULT '0000-00-00',
    arrivalTime     TIME DEFAULT '00:00:00',
    arrivalDate     DATE DEFAULT '0000-00-00',
    routeID      	INT,
    updatedAT       TIMESTAMP NOT NULL 
    ON UPDATE       CURRENT_TIMESTAMP,
    
    PRIMARY KEY(flightID),
    FOREIGN KEY(routeID) 	REFERENCES Route(routeID)   
    ON DELETE CASCADE
);

DROP TABLE IF EXISTS Booking;
CREATE TABLE Booking
(
    ticketID        INT AUTO_INCREMENT,
    flightID        INT,
    seatNum         VARCHAR(45),
    class           VARCHAR(45),
    userID     		INT,
    updatedAT       TIMESTAMP NOT NULL 
    ON UPDATE       CURRENT_TIMESTAMP,
    
    PRIMARY KEY(ticketID),
    FOREIGN KEY(flightID) REFERENCES Flight(flightID) 
    ON DELETE CASCADE,
    FOREIGN KEY(userID) REFERENCES User(userID)
    ON DELETE CASCADE
);

DROP TABLE IF EXISTS FlightsArchive1;
CREATE TABLE FlightsArchive1
(
    flightID        INT AUTO_INCREMENT,
    departureTime   TIME DEFAULT '00:00:00',
    departureDate   DATE DEFAULT '0000-00-00',
    arrivalTime     TIME DEFAULT '00:00:00',
    arrivalDate     DATE DEFAULT '0000-00-00',
    routeID      	INT,
    updatedAT       TIMESTAMP,
    
    PRIMARY KEY(flightID)
);

DROP TABLE IF EXISTS BookingArchive;
CREATE TABLE BookingArchive
(
    ticketID        INT AUTO_INCREMENT,
    flightID        INT,
    seatNum         VARCHAR(45),
    class           VARCHAR(45),
    userID     		INT,
    updatedAT       TIMESTAMP,

    PRIMARY KEY(ticketID)
);

DROP TABLE IF EXISTS Flight_Pilot;
CREATE TABLE Flight_Pilot
(
    flightID        INT,
    pilotID         INT,
    
    FOREIGN KEY(flightID) 		REFERENCES Flight(flightID)
    ON DELETE CASCADE,
    FOREIGN KEY(pilotID) 		REFERENCES Pilot(pilotID)
    ON DELETE CASCADE
);

/*================================================================================
Create/Reserve flight to bookingInput: flightID, seat, class, userID)
=================================================================================*/
DROP PROCEDURE IF EXISTS bookFlight;
DELIMITER //
CREATE PROCEDURE bookFlight(
IN Flight_ID INT,
IN SeatNum_sel VARCHAR(45),
IN Class_sel VARCHAR(45),
IN User_ID INT)
BEGIN
	INSERT INTO Booking (flightID, seatNum, class, userID)
    VALUES (FLight_ID, SeatNum_sel, Class_sel, User_ID);
END//
DELIMITER ;


/*================================================================================
Delete/Cancel flight from booking(Input: flightID and User_ID )
=================================================================================*/
DROP PROCEDURE IF EXISTS cancelFlightReservation;
DELIMITER //
CREATE PROCEDURE cancelFlightReservation(
IN Flight_ID INT,
IN User_ID INT)
BEGIN
    DELETE FROM Booking
	WHERE flightID = FLight_ID 
	AND userID = User_ID;
END//
DELIMITER ;

/*================================================================================
-- Archived function to archive flights before a cutoff time
=================================================================================*/
DROP PROCEDURE IF EXISTS ArchiveFlights;
DELIMITER //
CREATE PROCEDURE ArchiveFlights(IN cutoffTime TIMESTAMP)
BEGIN
	START TRANSACTION;
		INSERT INTO FlightsArchive1
		SELECT flightID, departureTime, departureDate, arrivalTime, arrivalDate, routeID, updatedAT 
		FROM Flight WHERE arrivalDate < cutoffTime;
        
		DELETE FROM Flight WHERE flightID > 0 AND arrivalDate < cutoffTime; 
	COMMIT; 
END//
DELIMITER ;

/*====================================================================================
-- Trigger to archive bookings before it is deleted as a byproduct of archiving flight
======================================================================================*/
DROP TRIGGER IF EXISTS ArchiveBooking;
DELIMITER //
CREATE TRIGGER ArchiveBooking
BEFORE DELETE ON Flight
FOR EACH ROW
BEGIN
	INSERT INTO BookingArchive
    (	SELECT booking.ticketID, booking.flightID, booking.seatNUM, booking.class, booking.userID, booking.updatedAT
		FROM booking 
		WHERE booking.flightID = OLD.flightID
	); 
    
    DELETE FROM Booking WHERE flightID IS NULL;
END//
DELIMITER ;


/*====================================================================================
Trigger that checks if a password is bad (under 4 letters), and sets a default password
=====================================================================================*/
DROP TRIGGER IF EXISTS BadPassword;
DELIMITER //
CREATE TRIGGER BadPassword
BEFORE INSERT ON User
FOR EACH ROW
BEGIN
	IF LENGTH(NEW.password) < 4  
    THEN SET NEW.password = "default"; 
    END IF; 
END//
DELIMITER ;
