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

public class ViewBooking extends BorderPane{
	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	private static Stage mainWindow = null;
	private static int ticketID = 0;
	static Scene flightResultContScene;
	
	TableView<Booking> bookingTable;
	
	@SuppressWarnings("unchecked")
	public ViewBooking(Connection connection, PreparedStatement ps, Stage s) {
		conn = connection;
		preparedStatement = ps;
		mainWindow = s;
		
		//top content
		//Airline Text Field
		Label flightNumberLabel = new Label();
		flightNumberLabel.setFont(new Font(18));
		flightNumberLabel.setText("Flight Number");
		
		
		TextField flightInput = new TextField();
		flightInput.setPromptText("Flight#");
		flightInput.setPrefWidth(200);

		//Submit Button
		Button submit = new Button("SUBMIT");
		submit.setPrefWidth(100);
		submit.setOnAction(e -> {
			if(!getBooking(flightInput)){
				AlertBox.display("Error", "No flights are found\nPlease check again");
			}
		});
		
		//GridPane for top elements
		GridPane gridPaneTop = new GridPane();
		gridPaneTop.setHgap(30);
		GridPane.setConstraints(flightNumberLabel, 0, 0);
		GridPane.setConstraints(flightInput, 0, 1);		
		GridPane.setConstraints(submit, 2, 1);
		gridPaneTop.setAlignment(Pos.CENTER_LEFT);				
		gridPaneTop.getChildren().addAll(flightNumberLabel, flightInput, submit);		
		
		//center content
		//Menu Label
		Label menuLabel = new Label();
		menuLabel.setText("Booking List");
		menuLabel.setFont(new Font(25));
		menuLabel.setPadding(new Insets(0,0,10,0));

		//Declaring columns
        TableColumn<Booking, Integer> ticketNum = new TableColumn<>("Ticket Number");
        ticketNum.setMinWidth(120);
        ticketNum.setCellValueFactory(new PropertyValueFactory<>("ticketID"));

        TableColumn<Booking, Integer> flightNum = new TableColumn<>("Flight");
        flightNum.setMinWidth(100);
        flightNum.setCellValueFactory(new PropertyValueFactory<>("flightID"));
        
        
        TableColumn<Booking, Integer> userNum = new TableColumn<>("User ID");
        userNum.setMinWidth(100);
        userNum.setCellValueFactory(new PropertyValueFactory<>("userID"));
		
        TableColumn<Booking, String> first = new TableColumn<>("First Name");
        first.setMinWidth(150);
        first.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Booking, String> last = new TableColumn<>("Last Name");
        last.setMinWidth(150);
        last.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        
        TableColumn<Booking, String> fclass = new TableColumn<>("Class");
        fclass.setMinWidth(150);
        fclass.setCellValueFactory(new PropertyValueFactory<>("fclass"));
        
        TableColumn<Booking, Integer> price = new TableColumn<>("Price");
        price.setMinWidth(150);
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
              
        //Declaring TableView
        bookingTable = new TableView<>();
        bookingTable.setPlaceholder(new Label("No booking found"));
        bookingTable.getColumns().addAll(ticketNum, flightNum, userNum, first, last,fclass,price);
        bookingTable.getSelectionModel().selectedItemProperty().addListener(
        		new ChangeListener<Booking>() {
                                        @Override
					public void changed(ObservableValue<? extends Booking> observable, Booking oldValue, Booking newValue) {
						ticketID = bookingTable.getSelectionModel().getSelectedItem().getTicketID();
					}
        			
        			}        		
        );
        VBox vBox = new VBox();
        vBox.getChildren().addAll(menuLabel, bookingTable);
        

        //bottom menu
        //Back Button
		Button backButton = new Button("BACK");
		backButton.setPrefWidth(100);
		backButton.setOnAction(e -> mainWindow.setScene(Main.adminScene));
		
                
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
                GridPane.setConstraints(logoutButton, 1, 0);
		gridPane.setAlignment(Pos.CENTER_RIGHT);
				
		gridPane.getChildren().addAll(backButton,logoutButton);
		
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
	}//ViewBooking Constructor
	
    /*
     * @return true if valid Booking number is found, false otherwise
     */
    private boolean getBooking(TextField flightInput){
    	if(!flightInput.getText().trim().equals("")){
    		try{
    			int flightNumber = Integer.parseInt(flightInput.getText().trim());
        		bookingTable.setItems(getBookingList(flightNumber));
        		return true;
    		}catch(Exception e){
    			return false;
    		}
    	}
    	return false;
    }//getFlights

	//Get all of the bookings based on flight Number
    public ObservableList<Booking> getBookingList(int flightNumber){
        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        UserRequests requests = new UserRequests();
        ResultSet bookingRS = null;        
        try{		
        	//System.out.println("ONE");
        	//System.out.println(flightNumber);
        	bookingRS = requests.getBookingOrderedByUserID(conn, flightNumber);
			while(bookingRS.next()){
				//System.out.println("TWO");
				int userID = bookingRS.getInt("userID");
				String firstName = bookingRS.getString("firstName");
				String lastName = bookingRS.getString("lastName");
				int flightID = bookingRS.getInt("flightID");
				int ticketid = bookingRS.getInt("ticketID");
                                String fclass = bookingRS.getString("class");
                                int price = bookingRS.getInt("price");
				bookings.add(new Booking(userID, firstName,lastName, flightID, ticketid,fclass,price));
			}
			
		}catch (SQLException e){
                    System.out.println(e+"here");
                };
		return bookings;        
    }//ObservableList
    
    public static int getTicketID(){
		return ticketID;
	}//getflightID
	
}//end of class
