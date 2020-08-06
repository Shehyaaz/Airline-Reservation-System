package airlinereservationfinaldbms;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Admin extends BorderPane{
	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	private static Stage mainWindow = null;
	static Scene bookFlightScene;
	static Scene createFlightScene;
	static Scene viewPassengerScene;
	static Scene viewFlightScene, viewBookingScene,viewTotalFlightsScene;
	
	public Admin(Connection connection, PreparedStatement ps, Stage s) {
		// TODO Auto-generated constructor stub
		conn = connection;
		preparedStatement = ps;
		mainWindow = s;
		
		//Menu Label
		Label menuLabel = new Label();
		menuLabel.setText("ADMIN MENU");
		menuLabel.setFont(new Font(30));
		
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setVgap(20);
		
		//View Passenger Button
		Button getPassengerButton = new Button("View Passengers");
		GridPane.setConstraints(getPassengerButton, 0, 0);
		getPassengerButton.setPrefWidth(300);
		getPassengerButton.setOnAction(e -> {
			BorderPane viewPassenger = new ViewPassenger(conn, preparedStatement, mainWindow);
			viewPassengerScene = new Scene(viewPassenger,970,650);
			mainWindow.setScene(viewPassengerScene);
		});

		//View Flights Button
		Button viewFlightButton = new Button("View Flights");
		GridPane.setConstraints(viewFlightButton, 0, 1);
		viewFlightButton.setPrefWidth(300);
		viewFlightButton.setOnAction(e -> {
			BorderPane viewFlight = new ViewFlight(conn, preparedStatement, mainWindow);
			viewFlightScene = new Scene(viewFlight,970,650);
			mainWindow.setScene(viewFlightScene);
		});
		
		//Create a flight button
		Button createFlightButton = new Button("Create a Flight");
		GridPane.setConstraints(createFlightButton, 0, 2);
		createFlightButton.setPrefWidth(300);
		createFlightButton.setPrefWidth(300);
		createFlightButton.setOnAction(e -> {
			BorderPane createFlight = new CreateFlight(conn, preparedStatement, mainWindow);
			createFlightScene = new Scene(createFlight,970,650);
			mainWindow.setScene(createFlightScene);
		});

		//Create a flight button
		Button viewBookingButton = new Button("View Bookings");
		GridPane.setConstraints(viewBookingButton, 0, 3);
		viewBookingButton.setPrefWidth(300);
		viewBookingButton.setPrefWidth(300);
		viewBookingButton.setOnAction(e -> {
			BorderPane viewBooking = new ViewBooking(conn, preparedStatement, mainWindow);
			viewBookingScene = new Scene(viewBooking,970,650);
			mainWindow.setScene(viewBookingScene);
		});		
		
		//Create a flight button
		Button viewTotalFlightsButton = new Button("View Total Flights Per Airline");
		GridPane.setConstraints(viewTotalFlightsButton, 0, 4);
		viewTotalFlightsButton.setPrefWidth(300);
		viewTotalFlightsButton.setPrefWidth(300);
		viewTotalFlightsButton.setOnAction(e -> {
			BorderPane viewTotal = new ViewTotalFlightsPerAirline(conn, preparedStatement, mainWindow);
			viewTotalFlightsScene = new Scene(viewTotal,970,650);
			mainWindow.setScene(viewTotalFlightsScene);
		});	
                
                Button logoutButton = new Button("LOGOUT");
		logoutButton.setPrefWidth(300);
		logoutButton.setOnAction(e -> {
                    AlertBox.display("Success", "You have logged out successfully");
                    mainWindow.setScene(Main.loginScene);    
                 });
		GridPane.setConstraints(logoutButton, 0, 4);
		gridPane.getChildren().addAll(getPassengerButton, viewFlightButton, createFlightButton,
				viewBookingButton,viewTotalFlightsButton,logoutButton);
		
		//BorderPane
		setTop(menuLabel);
		setCenter(gridPane);
		setAlignment(menuLabel, Pos.CENTER);
		setMargin(menuLabel, new Insets(10,0,0,0));
		
	}//Admin Constructor
}//end of class
