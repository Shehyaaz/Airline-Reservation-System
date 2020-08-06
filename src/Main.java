package airlinereservationfinaldbms;
import java.sql.*;
//JavaFX
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application
{ 
	//JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/airlinereservation";

	//Database credentials
	static final String USER = "root";
	static final String PASS = "root";
	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	
	static Stage mainWindow;
	private static int userID;
	static Scene loginScene;
	static Scene menuScene;
	static Scene adminScene;
	
	public static void main(String[] args) throws SQLException {
		
		try{	
                        Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			/*if (conn != null) {
				System.out.println("Welcome to the ARS Airline Reservation System");
			}*/
			launch(args);
			
		}catch(SQLException se){se.printStackTrace(); }
		catch(Exception e){ e.printStackTrace(); }
		finally{
			try{ if(preparedStatement!=null) preparedStatement.close(); }
			catch(SQLException se2){ }
			
			try{ if(conn!=null) conn.close(); }
			catch(SQLException se){ se.printStackTrace(); }
		}		
	}//end of main

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainWindow = primaryStage;
                mainWindow.getIcons().add(new Image("file:resources/airliner.png"));
		mainWindow.setTitle("ARS Airline Reservation");
		
		//Log-in and Sign-up Scene
		GridPane loginGrid = new LoginSignup(conn, preparedStatement, this);		
		loginGrid.setAlignment(Pos.CENTER);	
		loginGrid.setStyle("-fx-background-color : lightblue");
		loginScene = new Scene(loginGrid, 970,650);	
                
		BorderPane menuGrid = new Menu(conn, preparedStatement, mainWindow);
                menuGrid.setStyle("-fx-background-color : lightblue");
		menuScene = new Scene(menuGrid, 970,650);
		
		BorderPane adminGrid = new Admin(conn, preparedStatement, mainWindow);
                adminGrid.setStyle("-fx-background-color : lightblue");
		adminScene = new Scene(adminGrid, 970,650);

		mainWindow.setScene(loginScene);
		mainWindow.show();
		
	}//start


	public void setID(int num){
		userID = num;		
	}
	
	public static int getUserID(){
		return userID;
	}
	

}//Main