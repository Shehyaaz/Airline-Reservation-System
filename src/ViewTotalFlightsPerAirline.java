package airlinereservationfinaldbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class ViewTotalFlightsPerAirline extends BorderPane{
	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	private static Stage mainWindow = null;
	private static String airlineName = "";
	static Scene flightResultContScene;
	
	TableView<FlightsPerAirline> flightTable;
	
	@SuppressWarnings("unchecked")
	public ViewTotalFlightsPerAirline(Connection connection, PreparedStatement ps, Stage s) {
		conn = connection;
		preparedStatement = ps;
		mainWindow = s;
		
		//top content
		//Airline Text Field
		Label airlineLabel = new Label();
		airlineLabel.setFont(new Font(18));
		airlineLabel.setText("Arline Name");
		
		
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
		menuLabel.setText("Flights Per Airline");
		menuLabel.setFont(new Font(25));
		menuLabel.setPadding(new Insets(0,0,10,0));

		//Declaring columns
        TableColumn<FlightsPerAirline, Integer> airlineNum = new TableColumn<>("Airline ID");
        airlineNum.setMinWidth(80);
        airlineNum.setCellValueFactory(new PropertyValueFactory<>("airlineID"));

        TableColumn<FlightsPerAirline, String> name = new TableColumn<>("Airline Name");
        name.setMinWidth(150);
        name.setCellValueFactory(new PropertyValueFactory<>("airlineName"));
        
        
        TableColumn<FlightsPerAirline, Integer> totalFlights = new TableColumn<>("Total Flights");
        totalFlights.setMinWidth(90);
        totalFlights.setCellValueFactory(new PropertyValueFactory<>("totalFlights"));
		
       
        //Declaring TableView
        flightTable = new TableView<>();
        flightTable.setPlaceholder(new Label("No FlightsPerAirline found"));

        flightTable.setItems(getFlightTotalList("%"));
        flightTable.getColumns().addAll(airlineNum, name, totalFlights);
        flightTable.getSelectionModel().selectedItemProperty().addListener(
        		new ChangeListener<FlightsPerAirline>() {

					@Override
					public void changed(ObservableValue<? extends FlightsPerAirline> observable, FlightsPerAirline oldValue, FlightsPerAirline newValue) {
						try{
							airlineName = flightTable.getSelectionModel().getSelectedItem().getAirlineName();
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
		
		//Cancel FlightsPerAirline Button
		Button cancelButton = new Button("CANCEL SELECTED FlightsPerAirline");
		cancelButton.setPrefWidth(250);
		cancelButton.setOnAction(e -> {
			if(airlineName.equals("")) AlertBox.display("Error", "Please select a FlightsPerAirline");
			else{
				//BorderPane flightResultCont = new FlightResultCont(conn, preparedStatement, mainWindow);
				//flightResultContScene = new Scene(flightResultCont,970, 650);
				//mainWindow.setScene(flightResultContScene);
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
                GridPane.setConstraints(logoutButton, 2, 0);
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
	}// Constructor
	
    /*
     * 
     * @return true if valid FlightsPerAirline number is found, false otherwise
     */
    private boolean getFlights(TextField airlineInput){
    		try{
    			String airlinename = airlineInput.getText().trim();
    			//User LIKE String comparison
    			airlinename = airlinename.concat("%");
        		flightTable.setItems(getFlightTotalList(airlinename));
        		return true;
    		}catch(Exception e){
    			return false;
    		}
    }//getFlights

	//Get all of the flights
    public ObservableList<FlightsPerAirline> getFlightTotalList(String airlineName){
        ObservableList<FlightsPerAirline> flights = FXCollections.observableArrayList();
        UserRequests requests = new UserRequests();
        ResultSet flightRS = null;        
        try{		
        	flightRS = requests.getTotalFlightsPerAirline(conn, airlineName);
			while(flightRS.next()){
				
				int airlineID2 = flightRS.getInt("airlineID");
				String airlineName2 = flightRS.getString("name");
				int total = flightRS.getInt("totalFlights");
				flights.add(new FlightsPerAirline(airlineID2, airlineName2, total));
			}
			
		}catch (SQLException e){};
		return flights;        
    }//ObservableList

	public static String getAirlineName() {
		return airlineName;
	}	
}//end of class
