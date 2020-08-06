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

public class Menu extends BorderPane{
	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	private static Stage mainWindow = null;
	static Scene bookFlightScene;
	static Scene cancelFlightScene;
	
	public Menu(Connection connection, PreparedStatement ps, Stage s) {
		conn = connection;
		preparedStatement = ps;
		mainWindow = s;
		
		//Menu Label
		Label menuLabel = new Label();
		menuLabel.setText("MENU");
		menuLabel.setFont(new Font(30));
		
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setVgap(20);
		
		//Book flights button
		Button book = new Button("Book Flights");
		GridPane.setConstraints(book, 0, 0);
		book.setPrefWidth(300);
		book.setOnAction(e -> {
			BorderPane bookFlight = new BookFlight(conn, preparedStatement, mainWindow);
			bookFlightScene = new Scene(bookFlight,970,650);
			mainWindow.setScene(bookFlightScene);
		});
			
		//Cancel flights button
		Button cancel = new Button("View Flight Reservations");
		GridPane.setConstraints(cancel, 0, 1);
		cancel.setPrefWidth(300);
		cancel.setOnAction(e -> {
			BorderPane cancelFlight = new CancelFlight(conn, preparedStatement, mainWindow);
			cancelFlightScene = new Scene(cancelFlight,970,650);
			mainWindow.setScene(cancelFlightScene);
		});
                
                //logout button
                Button logoutButton = new Button("LOGOUT");
		logoutButton.setPrefWidth(300);
		logoutButton.setOnAction(e -> {
                    AlertBox.display("Success", "You have logged out successfully");
                    mainWindow.setScene(Main.loginScene);    
                 });
		
		GridPane.setConstraints(logoutButton, 0, 2);		
		
		gridPane.getChildren().addAll(book, cancel,logoutButton);
		
		//BorderPane
		setTop(menuLabel);
		setCenter(gridPane);
		setAlignment(menuLabel, Pos.CENTER);
		setMargin(menuLabel, new Insets(10,0,0,0));
		
	}//Menu Constructor
}//end of class
