package airlinereservationfinaldbms;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class FlightResultCont extends BorderPane{
	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	private static Stage mainWindow = null;
	private static String classType = "";
	private static String col = "";
	private static String row = "";
	private static String[] cols = {}; 
	private static ArrayList<Integer> rows = null;
	private static int ticketNumber = 0;
        private static int price = 0;
	
	TableView<Flight> flightTable;
	
	
	public FlightResultCont(Connection connection, PreparedStatement ps, Stage s) {
		conn = connection;
		preparedStatement = ps;
		mainWindow = s;
		
		//top content
		//Menu Label
		Label menuLabel = new Label();
		menuLabel.setText("Select Seat Preference:");
		menuLabel.setFont(new Font(30));
		
		//center content
		BorderPane borderPane = new BorderPane();
	Label ref = new Label("Only for Reference");	
        final ImageView selectedImage = new ImageView();   
        Image image1 = new Image("file:resources/seat.jpg");        
        selectedImage.setImage(image1);
        
        GridPane bottomGridPane = new GridPane();
        bottomGridPane.setAlignment(Pos.BOTTOM_CENTER);
        GridPane centerGridPane = new GridPane();
        centerGridPane.setAlignment(Pos.CENTER);
        centerGridPane.setVgap(20);
        centerGridPane.setHgap(10);

        final String[] classes = new String[] {"First", "Economy"};
        ArrayList<Integer> firstClassRow = new ArrayList<>();
        ArrayList<Integer> econClassRow = new ArrayList<>();
        for(int i = 1; i <= 2; i++){firstClassRow.add(i);}
        for(int i = 11; i <= 30; i++){econClassRow.add(i);} 
        rows = new ArrayList<Integer>();
        
        //bottomGridPane
        Label priceLabel = new Label();
        priceLabel.setText("Price :");
        GridPane.setConstraints(priceLabel, 0, 0);
        
        TextField priceText = new TextField();
        priceText.setEditable(false); //read only field
        GridPane.setConstraints(priceText, 1, 0);
        
        // ChoiceBox 1
        Label classLabel = new Label();
        classLabel.setText("Class Type:");
        GridPane.setConstraints(classLabel, 0, 0);
        
        final ChoiceBox<String> choiceBox1 = new ChoiceBox<>(
                FXCollections.observableArrayList(classes));
        GridPane.setConstraints(choiceBox1, 1, 0);
        choiceBox1.getSelectionModel().selectedIndexProperty()
        .addListener(new ChangeListener<Number>() {
          @SuppressWarnings("rawtypes")
		public void changed(ObservableValue ov, Number value, Number new_value) {
        	  classType = classes[new_value.intValue()];
        	  if(classType.equals("First")){
        		  rows = firstClassRow;
        		  cols = new String[] {"A", "C", "J", "L"};
        	  }else if(classType.equals("Economy")){
        		  rows = econClassRow;
        		  cols = new String[] {"A", "B", "C", "J", "K", "L"};
                          
        	  }
                  //setting price field
                  String sql = "SELECT price FROM flightclass WHERE flightID = ? AND class = ?";
              try {
                  preparedStatement = conn.prepareStatement(sql);
                  preparedStatement.setInt(1,FlightResult.getflightID());
                  preparedStatement.setString(2,classType);
                  ResultSet rs = preparedStatement.executeQuery();
                  if(rs.next()){
                      priceText.setText(String.valueOf(rs.getInt("price")));
                      price = rs.getInt("price");
                  }
              } catch (SQLException ex) {
                  System.out.println(ex);
              }
        	  showChoiceBox2(centerGridPane);
        	  showChoiceBox3(centerGridPane);
        	  
          }
        });//end ChoiceBox 1
        GridPane topGridPane = new GridPane();
        GridPane.setConstraints(ref, 0, 0);
        GridPane.setConstraints(selectedImage, 0,1);
        topGridPane.getChildren().addAll(ref,selectedImage);
        centerGridPane.getChildren().addAll(classLabel, choiceBox1);
        bottomGridPane.getChildren().addAll(priceLabel,priceText);
        borderPane.setTop(topGridPane);
        borderPane.setCenter(centerGridPane);
        borderPane.setBottom(bottomGridPane);
		
		//bottom menu
		//Back buttons
		Button backButton = new Button("BACK");
		backButton.setPrefWidth(100);
		backButton.setOnAction(e -> mainWindow.setScene(BookFlight.flightResultScene));
		
		//Finish Flight buttons
		Button bookButton = new Button("FINISH BOOKING");
		bookButton.setPrefWidth(250);
		bookButton.setOnAction(e -> {
			if(bookFlight()){
				AlertBox.display("Success", "You have successfully reserved a flight with the payment amount!\n"
											+ "Your ticket# is " + ticketNumber);
				mainWindow.setScene(Main.menuScene);
			}else AlertBox.display("Error", "Error booking your flight.");
			
		});
		
		//GridPane for bottom buttons
		GridPane gridPane = new GridPane();
		gridPane.setHgap(20);
		GridPane.setConstraints(backButton, 0, 0);
		GridPane.setConstraints(bookButton, 1, 0);
		gridPane.setAlignment(Pos.CENTER_RIGHT);				
		gridPane.getChildren().addAll(backButton, bookButton);
		//BorderPane
		setTop(menuLabel);
		setAlignment(menuLabel, Pos.TOP_LEFT);
		setMargin(menuLabel, new Insets(20,0,20,20));
		
		setCenter(borderPane);
		setBottom(gridPane);
		setAlignment(gridPane, Pos.CENTER);
		setMargin(gridPane, new Insets(0,20,20,10));		
	}
	/*
	 * Show column label and CheckBox 2
	 * @param centerGridPane - GridPane instance
	 */
	private void showChoiceBox2(GridPane centerGridPane){
		
		// ChoiceBox 2 
        Label colLabel = new Label();
        colLabel.setText("Seat Column:");
        GridPane.setConstraints(colLabel, 0, 1);
        
        final ChoiceBox<String> choiceBox2 = new ChoiceBox<String>(
                FXCollections.observableArrayList(cols));              
        GridPane.setConstraints(choiceBox2, 1, 1);  
        choiceBox2.setPrefWidth(75);
        choiceBox2.getSelectionModel().selectedIndexProperty()
        .addListener(new ChangeListener<Number>() {
          @SuppressWarnings("rawtypes")
		public void changed(ObservableValue ov, Number value, Number new_value) {
        	  col = cols[new_value.intValue()]; 
        	  
          }
        });//end ChoiceBox 2
        centerGridPane.getChildren().addAll(colLabel, choiceBox2);
	}//showChoiceBox2
	
	/*
	 * Show the rowLabel and CheckBox 3
	 * @param centerGridPane - GridPane instance
	 */
	private void showChoiceBox3(GridPane centerGridPane){

     // ChoiceBox 3 
        Label rowLabel = new Label();
        rowLabel.setText("Seat Row:");
        GridPane.setConstraints(rowLabel, 0, 2);
        
      	final ChoiceBox<Integer> choiceBox3 = new ChoiceBox<Integer>(
                FXCollections.observableArrayList(rows));        
        GridPane.setConstraints(choiceBox3, 1, 2);  
        choiceBox3.setPrefWidth(75);
        choiceBox3.getSelectionModel().selectedIndexProperty()
        .addListener(new ChangeListener<Number>() {
          @SuppressWarnings("rawtypes")
		public void changed(ObservableValue ov, Number value, Number new_value) {
        	  row = rows.get(new_value.intValue()).toString(); 
          }
        });//end ChoiceBox 3
        centerGridPane.getChildren().addAll(rowLabel, choiceBox3);
	}//showChoiceBox3
	
	private boolean bookFlight(){
		UserRequests requests = new UserRequests();
		try {
			CallableStatement cs = conn.prepareCall("{CALL bookFlight(?, ?, ? , ?,?)}");
			cs.setInt(1, FlightResult.getflightID());
			cs.setString(2, row.concat(col));
			cs.setString(3, classType);
			cs.setInt(4, Main.getUserID());
                        cs.setInt(5,price);
			cs.executeUpdate();	
			//System.out.println("ONE");
			ResultSet resultSet = requests.getTicketNumber(conn, FlightResult.getflightID(), Main.getUserID());
			if(resultSet.next()){
				ticketNumber = resultSet.getInt("ticketID");
				return true;
			}else return false;
		} catch (SQLException e) {
			System.out.println(e);
			return false;
		}	
	}
	
}//end of class
