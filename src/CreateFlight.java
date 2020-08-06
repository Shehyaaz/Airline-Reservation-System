package airlinereservationfinaldbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CreateFlight extends BorderPane{
	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	private static Stage mainWindow = null;
	String departDateStr = "";
	String arriveDateStr = "";

	static Scene flightResultScene;
	static Scene viewRouteScene;
	
	public CreateFlight(Connection connection, PreparedStatement ps, Stage s) {
		conn = connection;
		preparedStatement = ps;
		mainWindow = s;
		
		//top content
		//Menu Label
		Label menuLabel = new Label();
		menuLabel.setText("Create a Flight");
		menuLabel.setFont(new Font(30));
		
		//center content
		//GridPane for inputs
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		
        //Flight RouteID Label and Input
        Label routeLabel = new Label("ROUTE ID");
        GridPane.setConstraints(routeLabel, 0, 0);
		
        TextField routeInput = new TextField();
        routeInput.setPrefWidth(150);
        GridPane.setConstraints(routeInput, 0, 1);
        
        //Find RouteID Button
        Button findRouteButton = new Button();
        findRouteButton.setText("Find RouteID");
        findRouteButton.setPrefWidth(150);
        GridPane.setConstraints(findRouteButton, 0, 2);
        findRouteButton.setOnAction(e -> {
        	BorderPane viewRoute = new ViewRoute(conn, preparedStatement, mainWindow, routeInput); 
        	viewRouteScene = new Scene(viewRoute,970,650);
        	mainWindow.setScene(viewRouteScene);
        });
        
        
		//Depart Date Label and Input
        Label departDateLabel = new Label("DEPART DATE");
        GridPane.setConstraints(departDateLabel, 0, 3);
		
        DatePicker departDateInput = new DatePicker();
        GridPane.setConstraints(departDateInput, 0, 4);
        departDateInput.setOnAction(e -> {
        	LocalDate date = departDateInput.getValue();
        	departDateStr = date.toString();
        });
		
		//DepartTime Label and Input
        Label departTimeLabel = new Label("DEPART TIME (24HR) hh:m:ss format");
        GridPane.setConstraints(departTimeLabel, 0, 5);
		
        TextField departTimeInput = new TextField();
        GridPane.setConstraints(departTimeInput, 0, 6);
        
		       
        //Arrive Date Label and Input
        Label arriveDateLabel = new Label("ARRIVE DATE");
        GridPane.setConstraints(arriveDateLabel, 0, 7);
		
        DatePicker arriveDateInput = new DatePicker();
        GridPane.setConstraints(arriveDateInput, 0, 8);
        arriveDateInput.setOnAction(e -> {
        	LocalDate date = arriveDateInput.getValue();
        	arriveDateStr = date.toString();
        });
        
        //ArriveTime Label and Input
        Label arriveTimeLabel = new Label("ARRIVE TIME (24HR) hh:mm:ss format");
        GridPane.setConstraints(arriveTimeLabel, 0, 9);
		
        TextField arriveTimeInput = new TextField();
        GridPane.setConstraints(arriveTimeInput, 0, 10);
        
        Label totalseatsLabel = new Label("Total number of Seats");
        GridPane.setConstraints(totalseatsLabel, 0, 11);
		
        TextField totalseatsInput = new TextField();
        GridPane.setConstraints(totalseatsInput, 0, 12);
        
        Label priceEconomyLabel = new Label("Price(Economy Class)");
        GridPane.setConstraints(priceEconomyLabel, 0, 13);
		
        TextField priceEconomy = new TextField();
        priceEconomy.setPrefWidth(150);
        GridPane.setConstraints(priceEconomy, 0, 14);
        
        Label priceBusinessLabel = new Label("Price(First Class)");
        GridPane.setConstraints(priceBusinessLabel, 0, 15);
		
        TextField priceBusiness = new TextField();
        priceBusiness.setPrefWidth(150);
        GridPane.setConstraints(priceBusiness, 0, 16);
       
        gridPane.getChildren().addAll(routeLabel, routeInput, findRouteButton, 
        		departDateLabel, departDateInput, departTimeLabel, departTimeInput,
        		arriveDateLabel, arriveDateInput, arriveTimeLabel, arriveTimeInput,totalseatsLabel,totalseatsInput,
                        priceEconomyLabel, priceEconomy, priceBusinessLabel, priceBusiness);
        
                
        //bottom menu
        //Search button
        Button createButton = new Button("CREATE FLIGHT");
        createButton.setPrefWidth(250);
        createButton.setOnAction(e -> {
        	String errorMessage = "";
        	if(routeInput.getText().equals("")) errorMessage += "RouteID is required!\n";
        	if(departDateStr.equals("")) errorMessage += "Depart date is Required!\n";
        	if(departTimeInput.getText().equals("")) errorMessage += "Depart time is required!\n";
        	if(arriveDateStr.equals("")) errorMessage += "Arrive date is Required!\n";
        	if(arriveTimeInput.getText().equals("")) errorMessage += "Arrive time is required!\n";
                if(totalseatsInput.getText().equals("")) errorMessage += "Total seats are required!\n";
                if(priceEconomy.getText().equals("")||priceBusiness.getText().equals("")) errorMessage += "Price for a class is required!\n";
        	
                String dpt = departDateInput.getValue()+" "+departTimeInput.getText();
                String arr = arriveDateInput.getValue()+" "+arriveTimeInput.getText();
                    try {
                        Date deptdate= new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(dpt);
                        Date arrdate= new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(arr);
                        if(arrdate.getTime()<= deptdate.getTime())
                            errorMessage += "Arrival Date and time must be greater than that of Departure!\n";
                    } catch (ParseException ex) {
                        System.out.println(dpt +"  "+arr);
                        errorMessage += "Date or Time are invalid!\n";
                    }
                
        	if(!errorMessage.equals("")) AlertBox.display("Error", errorMessage);
        	else if(setFlight(routeInput, departTimeInput, arriveTimeInput,totalseatsInput, priceEconomy,priceBusiness)){
        		AlertBox.display("Success", "The new flight is successfully created!");
        		routeInput.setText("");
        		departDateInput.getEditor().clear();
        		arriveDateInput.getEditor().clear();
        		departTimeInput.setText("");
        		arriveTimeInput.setText("");
                        totalseatsInput.setText("");
                        priceEconomy.setText("");
                        priceBusiness.setText("");
        	}else AlertBox.display("Error", "Error creating a flight with given information");
        	
        });
        
		//Back buttons
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
		GridPane gridPane2 = new GridPane();
		gridPane2.setHgap(20);
		GridPane.setConstraints(backButton, 0, 0);
		GridPane.setConstraints(createButton, 1, 0);
                GridPane.setConstraints(logoutButton, 2, 0);
		gridPane2.setAlignment(Pos.CENTER_RIGHT);				
		gridPane2.getChildren().addAll(backButton, createButton,logoutButton);
		
		//border pane
		setTop(menuLabel);
		setAlignment(menuLabel, Pos.CENTER);
		setMargin(menuLabel, new Insets(10,0,0,0));
		
		setCenter(gridPane);
		
		setBottom(gridPane2);
		setAlignment(gridPane2, Pos.CENTER);
		setMargin(gridPane2, new Insets(0,20,20,10));
	}//CreateFlight Constructor
	
	
	private boolean setFlight(TextField routeInput, TextField departTime, TextField arriveTime, TextField totalseatsInput,TextField priceEconomy,TextField priceBusiness) {
		
		UserRequests requests = new UserRequests();
		try{
			String startTime = departTime.getText().trim();
			String endTime = arriveTime.getText().trim();
			int route = Integer.parseInt(routeInput.getText().trim());
                        int totalseats = Integer.parseInt(totalseatsInput.getText().trim());
                        int priceEco = Integer.parseInt(priceEconomy.getText().trim());
                        int priceBus = Integer.parseInt(priceBusiness.getText().trim());
			int result = requests.createFlight(conn, departDateStr, startTime, arriveDateStr, endTime, route,totalseats,priceEco, priceBus);
			if(result == 1){
				return true;
			}else {
				return false;
			}
		}catch (Exception e){
                    System.out.println(e);
                };
		
		return false;
	}
	
}//end of class
