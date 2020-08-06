package airlinereservationfinaldbms;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Flight {
	private int flightNumber;
	private String departureTime;
	private String departureAirport;
	private String arrivalTime;
	private String arrivalAirport;
	private String airlineName;
	private String departDate;
	private String arriveDate;
	private int totalSeats;
        private int availableSeats;
	public Flight(int num, String dTime, String dAirport, String aTime, String aAirport, String airline, int total, int available) {
		this.flightNumber = num;
		this.departureTime = convertTime(dTime);
		this.departureAirport = dAirport;
		this.arrivalTime = convertTime(aTime);
		this.arrivalAirport = aAirport;
		this.airlineName = airline;
                this.totalSeats=total;
                this.availableSeats=available;
	}
	
	public Flight(int num, String dTime, String departDate, String dAirport, 
						String aTime, String arriveDate, String aAirport, String airline,int total, int available) {
		this.flightNumber = num;
		this.departureTime = convertTime(dTime);
		this.departureAirport = dAirport;
		this.arrivalTime = convertTime(aTime);
		this.arrivalAirport = aAirport;
		this.airlineName = airline;
		this.departDate = departDate;
		this.arriveDate = arriveDate;
                this.totalSeats=total;
                this.availableSeats=available;
	}

	public String getDepartDate() {
		return departDate;
	}

	public void setDepartDate(String departDate) {
		this.departDate = departDate;
	}

	public String getArriveDate() {
		return arriveDate;
	}

	public void setArriveDate(String arriveDate) {
		this.arriveDate = arriveDate;
	}

	public int getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(int flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getDepartureAirport() {
		return departureAirport;
	}

	public void setDepartureAirport(String departureAirport) {
		this.departureAirport = departureAirport;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getArrivalAirport() {
		return arrivalAirport;
	}

	public void setArrivalAirport(String arrivalAirport) {
		this.arrivalAirport = arrivalAirport;
	}

	public String getAirlineName() {
		return airlineName;
	}

	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}
        public void setTotalSeats(int total) {
		this.totalSeats = total;
	}
        public int getTotalSeats() {
		return totalSeats;
	}
        public void setAvailableSeats(int available) {
		this.availableSeats = available;
	}
        public int getAvailableSeats() {
		return availableSeats;
	}
        
	
	public String convertTime(String time) {
		String ampmTime = "";
		DateFormat f1 = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
		Date d;
		try {
			d = f1.parse(time);
			DateFormat f2 = new SimpleDateFormat("h:mma");
			ampmTime = f2.format(d).toUpperCase(); // "12:18AM"
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return ampmTime;		
	}
}//end of class
