package airlinereservationfinaldbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ViewFlight extends BorderPane{
	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	private static Stage mainWindow = null;
	private static int flightID = 0;
	static Scene flightResultContScene;
	
	TableView<Flight> flightTable;
	
	@SuppressWarnings("unchecked")
	public ViewFlight(Connection connection, PreparedStatement ps, Stage s) {
		conn = connection;
		preparedStatement = ps;
		mainWindow = s;
		
		//top content
		//Airline Text Field
		Label airlineLabel = new Label();
		airlineLabel.setFont(new Font(18));
		airlineLabel.setText("Airline Name");
		
		
		TextField airlineInput = new TextField();
		airlineInput.setPromptText("Airline Name");
		airlineInput.setPrefWidth(200);

		//Submit Button
		Button submit = new Button("SUBMIT");
		submit.setPrefWidth(100);
		submit.setOnAction(e -> {
			if(!getFlights(airlineInput)){
				AlertBox.display("Error", "No flights are found\nPlease check again");
			}
		});
		
		//GridPane for top elements
		GridPane gridPaneTop = new GridPane();
		gridPaneTop.setHgap(30);
		GridPane.setConstraints(airlineLabel, 0, 0);
		GridPane.setConstraints(airlineInput, 0, 1);
		
		GridPane.setConstraints(submit, 2, 1);
		
		gridPaneTop.setAlignment(Pos.CENTER_LEFT);					
		gridPaneTop.getChildren().addAll(airlineLabel, airlineInput, submit);		
		
		//center content
		//Menu Label
		Label menuLabel = new Label();
		menuLabel.setText("Flight List");
		menuLabel.setFont(new Font(25));
		menuLabel.setPadding(new Insets(0,0,10,0));

		//Declaring columns
        TableColumn<Flight, Integer> flightNum = new TableColumn<>("Flight#");
        flightNum.setMinWidth(50);
        flightNum.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));

        TableColumn<Flight, String> departureDate = new TableColumn<>("Depart Date");
        departureDate.setMinWidth(50);
        departureDate.setCellValueFactory(new PropertyValueFactory<>("departDate"));
        
        
        TableColumn<Flight, String> departureTime = new TableColumn<>("Depart Time");
        departureTime.setMinWidth(50);
        departureTime.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
		
        TableColumn<Flight, String> departureAirport = new TableColumn<>("Depart Airport");
        departureAirport.setMinWidth(150);
        departureAirport.setCellValueFactory(new PropertyValueFactory<>("departureAirport"));

        TableColumn<Flight, String> arrivalDate = new TableColumn<>("Arrive Date");
        arrivalDate.setMinWidth(50);
        arrivalDate.setCellValueFactory(new PropertyValueFactory<>("arriveDate"));
        
        
        TableColumn<Flight, String> arrivalTime = new TableColumn<>("Arrive Time");
        arrivalTime.setMinWidth(50);
        arrivalTime.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        
        TableColumn<Flight, String> arrivalAirport = new TableColumn<>("Arrive Airport");
        arrivalAirport.setMinWidth(150);
        arrivalAirport.setCellValueFactory(new PropertyValueFactory<>("arrivalAirport"));
        
        TableColumn<Flight, String> airlineName = new TableColumn<>("Airline");
        airlineName.setMinWidth(100);
        airlineName.setCellValueFactory(new PropertyValueFactory<>("airlineName"));
        
        TableColumn<Flight, Integer> totalSeats = new TableColumn<>("Total Seats");
        totalSeats.setMinWidth(30);
        totalSeats.setCellValueFactory(new PropertyValueFactory<>("totalSeats"));
        
        TableColumn<Flight, Integer> availableSeats = new TableColumn<>("Available Seats");
        availableSeats.setMinWidth(30);
        availableSeats.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
        
        //Declaring TableView
        flightTable = new TableView<>();
        flightTable.setPlaceholder(new Label("No flight found"));
        flightTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        //flightTable.setItems(getFlights(rs));
        flightTable.getColumns().addAll(flightNum, departureDate, departureTime, departureAirport,
        								arrivalDate, arrivalTime, arrivalAirport, airlineName,totalSeats,availableSeats);
        flightTable.getSelectionModel().selectedItemProperty().addListener(
        		new ChangeListener<Flight>() {

					@Override
					public void changed(ObservableValue<? extends Flight> observable, Flight oldValue, Flight newValue) {
						try{
                                                    flightID = flightTable.getSelectionModel().getSelectedItem().getFlightNumber();
                                                }catch(Exception e){}
					}
        			
        			
				}        		
        );
        VBox vBox = new VBox();
        vBox.getChildren().addAll(menuLabel, flightTable);
        

        //bottom menu
        //Back Button
		Button backButton = new Button("BACK");
		backButton.setPrefWidth(100);
		backButton.setOnAction(e -> mainWindow.setScene(Main.adminScene));
		
		//Cancel Flight Button
		Button cancelButton = new Button("CANCEL SELECTED FLIGHT");
		cancelButton.setPrefWidth(250);
		cancelButton.setOnAction(e -> {
			if(flightID == 0) AlertBox.display("Error", "Please select a flight");
			else{
                           String sql = "DELETE FROM Flight WHERE flightID = ?";
                            try {
                                preparedStatement=conn.prepareStatement(sql);
                                preparedStatement.setInt(1,flightID);
                                int rowsaffected = preparedStatement.executeUpdate();
                                if(rowsaffected>0)
                                     AlertBox.display("Success", "Flight cancelled successfully");
                                else
                                     AlertBox.display("Error", "An error occurred");
                             }catch (SQLException ex) {
                                System.out.println(ex);
                            }
                            
			}	
		});
                //logout button
                Button logoutButton = new Button("LOGOUT");
		logoutButton.setPrefWidth(100);
		logoutButton.setOnAction(e -> {
                    AlertBox.display("Success", "You have logged out successfully");
                    mainWindow.setScene(Main.loginScene);    
                 });
		
		//GridPane for bottom buttons
		GridPane gridPane = new GridPane();
		gridPane.setHgap(20);
		GridPane.setConstraints(backButton, 0, 0);
		GridPane.setConstraints(cancelButton, 1, 0);
                GridPane.setConstraints(logoutButton,2,0);
		gridPane.setAlignment(Pos.CENTER_RIGHT);
				
		gridPane.getChildren().addAll(backButton, cancelButton,logoutButton);
		
		//BorderPane
		setTop(gridPaneTop);
		setAlignment(gridPaneTop, Pos.TOP_LEFT);
		setMargin(gridPaneTop, new Insets(20,0,20,20));
		
		setCenter(vBox);
		setAlignment(vBox, Pos.CENTER);
		setMargin(vBox, new Insets(0,20,0,20));
		
		setBottom(gridPane);
		setAlignment(gridPane, Pos.CENTER);
		setMargin(gridPane, new Insets(0,20,20,10));		
	}//ViewFlight Constructor
	
    /*
     * 
     * @return true if valid flight number is found, false otherwise
     */
    private boolean getFlights(TextField airlineInput){
    	if(!airlineInput.getText().trim().equals("")){
    		try{
    			String airlinename = airlineInput.getText().trim();
    			//User LIKE String comparison
    			airlinename = airlinename.concat("%");
        		flightTable.setItems(getFlightList(airlinename));
        		return true;
    		}catch(Exception e){
    			return false;
    		}
    	}
    	return false;
    }//getFlights

	//Get all of the flights
    public ObservableList<Flight> getFlightList(String airlineName){
        ObservableList<Flight> flights = FXCollections.observableArrayList();
        UserRequests requests = new UserRequests();
        ResultSet flightRS = null;        
        try{		

        	flightRS = requests.getFlightsByAirline(conn, airlineName);
			while(flightRS.next()){

				int flightNum = flightRS.getInt("flightNumber");
				String departureTime = flightRS.getString("departureTime");
				String departureAirport = flightRS.getString("departureAirport");
				String arrivalTime = flightRS.getString("arrivalTime");
				String arrivalAirport = flightRS.getString("arrivalAirport");
				String airlineName2 = flightRS.getString("airlineName");
				String departDate = flightRS.getString("departureDate");
				String arriveDate = flightRS.getString("arrivalDate");
                                int totalSeats = flightRS.getInt("totalseats");
                                int availableSeats = flightRS.getInt("availableseats");

				flights.add(new Flight(flightNum, departureTime, departDate, departureAirport, 
						arrivalTime, arriveDate, arrivalAirport, airlineName2,totalSeats,availableSeats));
			}
			
		}catch (SQLException e){
                    System.out.println(e);
                };
		return flights;        
    }//ObservableList
    
    public static int getflightID(){
		return flightID;
	}//getflightID
	
}//end of class
