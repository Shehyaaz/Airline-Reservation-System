package airlinereservationfinaldbms;
import java.sql.*;

public class UserRequests {
	
	/**
	 * Get the list of passengers boarding a specific flight given the flight number
     * @param conn
     * @param flight_ID
     * @throws java.sql.SQLException
	 */
	public ResultSet getPassengersByFlight(Connection conn, int flight_ID) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;

		sql = "SELECT User.userID as passengerID, firstName, lastName, email, age "
				+ "FROM User, Booking " + 
					"WHERE Booking.flightID = ? AND "+
					"Booking.userID = User.userID "+ 
					"ORDER BY User.userID;";
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1, flight_ID);
		rs = preparedStatement.executeQuery();
		return rs;
	}//getPassengersByFlight

	/*
	 * Get the passengers boarding ECONOMY class in a flight
	 */
	public ResultSet getEconomyPassengers(Connection conn, int flight_ID) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		
		sql = "SELECT User.userID as passengerID, firstName, lastName, email, age "+ 
				"FROM User LEFT OUTER JOIN Booking ON " + 
				"User.userID = Booking.userID " +
				"WHERE Booking.flightID = ? AND "+
				"Booking.class = 'Economy' "+ 
				"ORDER BY firstName;";
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1, flight_ID);
		rs = preparedStatement.executeQuery();
		return rs;
	}//getEconomyPassengers

	/*
	 * Get the passengers boarding FIRST class in a flight
	 */
	public ResultSet getFirstPassengers(Connection conn, int flight_ID) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		
		sql = "SELECT User.userID as passengerID, firstName, lastName, email, age "+ 
				"FROM User LEFT OUTER JOIN Booking ON " + 
				"User.userID = Booking.userID " +
				"WHERE Booking.flightID = ? AND "+
				"Booking.class = 'First' "+ 
				"ORDER BY firstName, lastName, age;";
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1, flight_ID);
		rs = preparedStatement.executeQuery();
		return rs;
	}//getFirstPassengers	
	
	/*
	 * Create a flight with date, time, and route(depart, arrive, and airline)
	 */
	public int createFlight(Connection conn, String departDate, String departTime, 
				String arriveDate, String arriveTime, int routeID, int totalseats,int priceEco, int priceBus) throws SQLException{
		String sql = null;
		int rowAffected = 0;
		PreparedStatement preparedStatement = null;
		int fid=0;
		sql = "INSERT INTO flight (departureTime, departureDate, arrivalTime, arrivalDate, routeID,totalseats,availableseats) "+"VALUES(?, ?, ?, ?, ?,?,?);";
                
	    preparedStatement = conn.prepareStatement(sql);
	    preparedStatement.setString(1, departTime);
	    preparedStatement.setString(2, departDate);
	    preparedStatement.setString(3, arriveTime);
	    preparedStatement.setString(4, arriveDate);
	    preparedStatement.setInt(5, routeID);
            preparedStatement.setInt(6, totalseats);
            preparedStatement.setInt(7, totalseats);
	    rowAffected = preparedStatement.executeUpdate();
            //inserting into flightclass table
            sql = "SELECT MAX(flightID) as flightID FROM flight"; //refers to the recently added row's flightID
            preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next())
                 fid = rs.getInt("flightID");
            if(priceEco!=0){
            sql = "INSERT INTO flightclass VALUES(?,?,?);";     
	    preparedStatement = conn.prepareStatement(sql);
	    preparedStatement.setInt(1, fid);
            preparedStatement.setString(2, "Economy");
	    preparedStatement.setInt(3, priceEco);
	    int rowAffected2 = preparedStatement.executeUpdate();
            }
            if(priceBus!=0){
            sql = "INSERT INTO flightclass VALUES(?,?,?);";     
	    preparedStatement = conn.prepareStatement(sql);
	    preparedStatement.setInt(1, fid);
            preparedStatement.setString(2, "First");
	    preparedStatement.setInt(3, priceBus);
	    int rowAffected2 = preparedStatement.executeUpdate();
            }
	    return rowAffected;		
	}//createFlight
	
	/*
	 * Given the airline, list all the flights
	 */
	public ResultSet getFlightsByAirline(Connection conn, String airlineName) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;

		sql = 	"SELECT flightID as flightNumber, departureDate, departureTime, r.departureAirport, arrivalDate, arrivalTime, r.arrivalAirport, r.airlineName,totalseats,availableseats " +
				"FROM flight, (SELECT routeID, airline.name as airlineName, a1.name as departureAirport, a2.name as arrivalAirport " +
						  "FROM route, airline, airport as a1, airport as a2 " +
						  "WHERE route.departureAirportID = a1.airportID " +
						  "AND route.arrivalAirportID = a2.airportID " +
						  "AND route.airlineID = airline.airlineID) r " +
			    "WHERE flight.routeID = r.routeID " +
			    "AND r.airlineName LIKE ? ORDER BY departureDate;";
		
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, airlineName);
		rs = preparedStatement.executeQuery();
		return rs;
	}//getFlightsByAirline

	/*
	 * Search for existing flights based on given departure city, arrival city, and departure date
	 */
	public ResultSet getFlightsByRouteDate(Connection conn, String origin, 
									String destination, String date) throws SQLException{
		PreparedStatement preparedStatement = null;

		String sql = "SELECT flightID as flightNumber, departureTime, r.departureAirport, arrivalTime, r. arrivalAirport, r.airlineName,totalseats,availableseats "+
				"FROM flight, (SELECT routeID, airline.name as airlineName, "+
							"a1.name as departureAirport, a2.name as arrivalAirport "+
						"FROM route, airline, airport as a1, airport as a2 "+
						"WHERE a1.city = ? "+
						"AND a2.city = ? "+
						"AND route.departureAirportID = a1.airportID "+
						"AND route.arrivalAirportID = a2.airportID "+
						"AND route.airlineID = airline.airlineID) r " +
				"WHERE flight.routeID = r.routeID "+
				"AND flight.departureDate = ? AND flight.availableseats>0;";
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, origin);
		preparedStatement.setString(2, destination);
		preparedStatement.setString(3, date);
		ResultSet rs = preparedStatement.executeQuery();
		return rs;
	}//getFlightsByRouteDate

	/*
	 * Return a list of flights that match the given flightID number
	 */
	public ResultSet getFlightsByFlightID(Connection conn, int flightID) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;

		sql = 	"SELECT flightID as flightNumber, departureTime, r.departureAirport, arrivalTime, r. arrivalAirport, r.airlineName,totalseats,availableseats " +
				"FROM flight, (SELECT routeID, airline.name as airlineName, a1.name as departureAirport, a2.name as arrivalAirport " +
						  "FROM route, airline, airport as a1, airport as a2 " +
						  "WHERE route.departureAirportID = a1.airportID " +
						  "AND route.arrivalAirportID = a2.airportID " +
						  "AND route.airlineID = airline.airlineID) r " +
			    "WHERE flight.routeID = r.routeID " +
			    "AND flight.flightID = ?;";
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, Integer.toString(flightID));
		rs = preparedStatement.executeQuery();
		return rs;
	}//getFlightsByFlightID

	/*
	 * After the user reserves a flight, find the ticket number and show it with given flightID and userID
	 */
	public ResultSet getTicketNumber(Connection conn, int flight_ID, int user_ID) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;

		sql = "SELECT ticketID FROM Booking WHERE flightID = ? AND userID = ?;";
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1, flight_ID);
		preparedStatement.setInt(2, user_ID);
		rs = preparedStatement.executeQuery();
		return rs;
	}//getTicketNumber

	/*
	 * Search for all the flight reservations made by a specific user with given userID
	 */
	public ResultSet getBookingByUser(Connection conn, int user_ID) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;

		sql = "SELECT * FROM Booking WHERE userID = ?";
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1, user_ID);
		rs = preparedStatement.executeQuery();
		return rs;
	}//getBookingByUser
	
	/*
	 * Get the airports based on the city or country. It doesn't require both params
	 */
	public ResultSet getAirportByCityCountry(Connection conn, String city, String country) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;

		sql = "SELECT * FROM Airport "
				+ "WHERE city LIKE ? "
				+ "AND country LIKE ?;";
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, city);
		preparedStatement.setString(2, country);
		rs = preparedStatement.executeQuery();
		return rs;
	}//getAirportByCityCountry
	
	/*
	 * Get the route given departing airport ID and arriving airport ID
	 */
	public ResultSet getRouteByAirline(Connection conn, int departID, int arriveID) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		sql = 	"SELECT route.routeID, airline.name "
				+ "FROM Route, Airline "
				+ "WHERE route.airlineID = airline.airlineID "
				+ "AND route.departureAirportID = ? "
				+ "AND route.arrivalAirportID = ?;";
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setInt(1, departID);
		preparedStatement.setInt(2, arriveID);
		rs = preparedStatement.executeQuery();
		return rs;
	}//getRouteByAirline
	
	/*
	 * Get all the bookings for a flight
	 */
	public ResultSet getBookingOrderedByUserID(Connection conn, int flightID) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;

		sql = "SELECT User.userID, User.firstname, User.lastname, Booking.flightID, Booking.ticketID,Booking.class,Booking.price " +
			"FROM User"+""
                        + " LEFT OUTER JOIN Booking ON User.userID = Booking.userID WHERE Booking.flightID = ? ORDER BY User.userID;";
	    preparedStatement = conn.prepareStatement(sql);
	    preparedStatement.setInt(1, flightID);
	    rs = preparedStatement.executeQuery();
		return rs;
	}//getBookingOrderedByUserID
	

	/*
	 * For each airline, find the total number of flights
	 */
	public ResultSet getTotalFlightsPerAirline(Connection conn, String airlineName) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;

		sql = "SELECT Airline.airlineID, Airline.name, count(flightID) AS totalFlights "
				+ "FROM Airline, Route, Flight "
				+ "WHERE Flight.routeID = Route.routeID  "
				+ "AND Route.airlineID = Airline.airlineID "
				+ "AND airline.name LIKE ? "
				+ "GROUP BY Airline.airlineID;";
		
	    preparedStatement = conn.prepareStatement(sql);
	    preparedStatement.setString(1, airlineName);
	    rs = preparedStatement.executeQuery();
		return rs;
	}//getTotalFlightsPerAirline

	
	public ResultSet getFlightsWithTenPlusPassengers(Connection conn) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		Statement statement = null;

		sql = "SELECT FlightID, PassengerCount " +
				"FROM( SELECT FlightID, COUNT(ticketID) AS PassengerCount  " +
				"FROM Booking " +
				"GROUP BY flightID) AS PassengerCount "+
				"WHERE PassengerCount >= 10";
		statement = conn.createStatement();
		rs = statement.executeQuery(sql);
		return rs;
	}

	public ResultSet getFlightWithMaxAverageAge(Connection conn) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		Statement statement = null;

		sql = 	"SELECT flightID, MAX(avg_age) " +
				"FROM(SELECT Flight.flightID, avg(age) AS avg_age " +
					"FROM Passenger, Booking, Flight " +
					"WHERE Booking.userID = Passenger.userID "+ 
					"AND Booking.flightID = Flight.flightID "+
				"GROUP BY flightID) as AverageAgePerFlight";
		statement = conn.createStatement();
		rs = statement.executeQuery(sql);
		return rs;
	}

	public ResultSet getFlightsByDestination(Connection conn, String destination) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;

		sql = "SELECT flightID, departureDate, departureTime, arrivalTime, A1.city, A2.city " +
			"FROM Flight, Route, Airport A1, Airport A2 "+
			"Where Flight.routeID = Route.routeID AND " +
			"Route.departureAirportID =  A1.airportID AND " +
			"Route.arrivalAirportID =  A2.airportID AND " +
			"A2.city = ?;";

	    preparedStatement = conn.prepareStatement(sql);
	    preparedStatement.setString(1, destination);
	    rs = preparedStatement.executeQuery();

	    return rs;
	}

	public ResultSet flightsByMostPassengers(Connection conn) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		Statement statement = null;

		sql = "select flightID, count(*) as num_tickets "+
			"From Booking " +
			"group by flightID " +
			"order by num_tickets DESC, flightID DESC;";
		statement = conn.createStatement();
		rs = statement.executeQuery(sql);
		/*
		while(rs.next()){
			System.out.println("flightID: " + rs.getInt("flightID") + "Passenger Count: " + rs.getInt("num_tickets"));	
		}
		*/
		return rs;
	}

	 public ResultSet flightsByShortestDuration(Connection conn) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		Statement statement = conn.createStatement();

		sql = "select flightID, timestampdiff(MINUTE, departureTime, arrivalTime) as timeDiff " +
			"from flight " +
			"group by flightID " +
			"order by timeDiff DESC;";
		rs = statement.executeQuery(sql);
		/*
		while(rs.next()){
			System.out.println("FlightID: " + rs.getInt("flightID") + "timeDiff: " + rs.getInt("timeDiff"));
		}
		*/
		return rs;
	}


	public ResultSet getTotalBookingPerPassenger(Connection conn) throws SQLException{
		String sql = null;
		ResultSet rs = null;
		Statement statement = conn.createStatement();

		sql = "SELECT User.userID, User.firstname, User.lastname, count(ticketID) AS totalBookings " +
			"FROM User, Booking " +
			"WHERE User.userID = Booking.userID " +
			"GROUP BY User.userID "+
			"UNION "+
			"SELECT User.userID, User.firstname, User.lastname, 0 " +
			"FROM User "+
			"WHERE userID NOT IN (SELECT userID FROM Booking);";
		rs = statement.executeQuery(sql);
		return rs;
	}


	//Archive Stored Procedure
	public ResultSet archiveFlights(Connection conn, String date) throws SQLException{
		CallableStatement cs = conn.prepareCall("{CALL archiveFlights(?)}");
		String timestamp = date + " 00:00:00";
		cs.setString(1, timestamp);
		ResultSet rs = cs.executeQuery();
		return rs;
	}

}//end of class
