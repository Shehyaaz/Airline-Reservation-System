package airlinereservationfinaldbms;
public class Booking {
	private int userID;
	private String firstName;
	private String lastName;
	private int flightID;
	private int ticketID;
	private int price;
        private String fclass;
	public Booking(int userID, String first, String last, int flightID, int ticketID, String fclass, int price) {
		this.userID = userID;
		this.firstName = first;
		this.lastName = last;
		this.flightID = flightID;
		this.ticketID = ticketID;
                this.fclass= fclass;
                this.price=price;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getFlightID() {
		return flightID;
	}

	public void setFlightID(int flightID) {
		this.flightID = flightID;
	}

	public int getTicketID() {
		return ticketID;
	}

	public void setTicketID(int ticketID) {
		this.ticketID = ticketID;
	}
	public String getFclass() {
		return fclass;
	}

	public void setFclass(String fclass) {
		this.fclass = fclass;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}//end of class
