package airlinereservationfinaldbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class LoginSignup extends GridPane{
	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	private static Main main = null;
	private boolean isAdmin = false;
	
	public LoginSignup(Connection connection, PreparedStatement ps, Main m) {
		conn = connection;
		preparedStatement = ps;
		main = m;
		
		setPadding(new Insets(50,50,50,50));		
		setVgap(9);
		setHgap(20);
		
		//Email Label - constrains use (child, column, row)
        Label emailLabel = new Label("Email Address :");
        emailLabel.setTooltip(new Tooltip("Use admin here to login as Admin"));
        GridPane.setConstraints(emailLabel, 0, 0);

        //Email Input
        TextField emailInput = new TextField();
        emailInput.setPrefWidth(350.0);
        GridPane.setConstraints(emailInput, 0, 1);

        //Password Label
        Label passLabel = new Label("Password :");
        passLabel.setTooltip(new Tooltip("Use password set for admin to login as Admin"));
        GridPane.setConstraints(passLabel, 0, 2);

        //Password Input
        PasswordField passwordField = new PasswordField();
        GridPane.setConstraints(passwordField, 0, 3);
        
        //Login Button
        Button loginButton = new Button("Log In");
        loginButton.setMaxWidth(150.0);
        GridPane.setConstraints(loginButton, 0, 4);
        GridPane.setMargin(loginButton, new Insets(10,0,0,0));
        GridPane.setHalignment(loginButton, HPos.RIGHT);
        loginButton.setOnAction(e -> {
        	if(validate(emailInput, passwordField)){
        		if(isAdmin){
        			Main.mainWindow.setScene(Main.adminScene);
                                isAdmin=false;
        		}else{
        			Main.mainWindow.setScene(Main.menuScene);
        		}
        		
				emailInput.setText("");
                                passwordField.setText("");
        	}else AlertBox.display("Error", "Incorrect email or password." + 
        									"\nPlease try again!");
        });
               
        // Create an account label
        Label createLabel = new Label("Create an account");
        createLabel.setFont(new Font(27));
        GridPane.setConstraints(createLabel, 0, 5);
        GridPane.setMargin(createLabel, new Insets(25,0,0,0));
        
        //FirstName Input
        TextField firstInput = new TextField();
        firstInput.setPromptText("First name");
        GridPane.setConstraints(firstInput, 0, 6);

        //LastName Input
        TextField lastInput = new TextField();
        lastInput.setPromptText("Last name");
        GridPane.setConstraints(lastInput, 0, 7);
        
        //age Input
        TextField ageInput = new TextField();
        ageInput.setPromptText("Age");
        GridPane.setConstraints(ageInput, 0, 8);
        
        //Email Input
        TextField emailInput2 = new TextField();
        emailInput2.setPromptText("Email address");
        GridPane.setConstraints(emailInput2, 0, 9);
        
        //Password Input
        PasswordField passwordField2 = new PasswordField();
        passwordField2.setPromptText("Create a password");
        GridPane.setConstraints(passwordField2, 0, 10);
        
        //Create Button
        Button createButton = new Button("Sign Up");
        createButton.setMaxWidth(150.0);
        GridPane.setConstraints(createButton, 0, 11);
        GridPane.setMargin(createButton, new Insets(10,0,0,0));
        GridPane.setHalignment(createButton, HPos.RIGHT);
        createButton.setOnAction(e -> {
        	String errorMessage = "";
        	if(firstInput.getText().equals("")) errorMessage += "Firstname is required!\n";
        	if(lastInput.getText().equals("")) errorMessage += "Lastname is required!\n";
        	if(ageInput.getText().equals("")) errorMessage += "Age is required!\n";
        	if(emailInput2.getText().equals("")) errorMessage += "Email is required!\n";
        	if(passwordField2.getText().equals("")) errorMessage += "Password is Required!\n";
        	
        	if(!errorMessage.equals("")) AlertBox.display("Error", errorMessage);
        	else if(createAccount(firstInput, lastInput, ageInput, emailInput2, passwordField2)){ 
        		AlertBox.display("Success", "Your new account has been created!" + "\nPlease continue to log-in!");
        		firstInput.setText("");
        		lastInput.setText("");
        		ageInput.setText("");
        		emailInput2.setText("");
        		passwordField2.setText("");
        	}else AlertBox.display("Error", "Error creating account or Account already exists" + 
        									"\nPlease check and try again!");        	        	
        });
                
        //Add everything to grid
        getChildren().addAll(emailLabel, emailInput, passLabel, passwordField, loginButton,
        					createLabel, firstInput, lastInput, ageInput, emailInput2, 
        					passwordField2, createButton);
	}//Constructor
	
	/*
	 * Validate user email and password with Flight database
	 * @param emailInput
	 * @param passwordField
	 * @return true if such user exists, otherwise false
	 */
	private boolean validate(TextField emailInput, PasswordField passwordField){
		
		String email = emailInput.getText().trim();
		String pass = passwordField.getText().trim();
		try{
			String sql =  "SELECT * from User where email  = ? AND password = SHA1(?)";
			preparedStatement= conn.prepareStatement(sql);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, pass);
			
			ResultSet resultSet = preparedStatement.executeQuery();			
			if(resultSet.next())
			{	int userID = resultSet.getInt("userID");		
				String first = resultSet.getString("firstName"); 
				String last = resultSet.getString("lastName");
				
				main.setID(userID);
				if(email.toLowerCase().equals("admin") && pass.toLowerCase().equals("admin")){
					isAdmin = true;
                                        AlertBox.display("Success","Logged in as Admin!\n");
                                }
                                else
                                    AlertBox.display("Success","Welcome " + first + " " + last + "!\n");
				
				return true;
			}
		}catch (SQLException e){}
		return false;
	}//validate log-in
	
	/*
	 * Create a new account for user
	 * @param firstInput
	 * @param lastInput
	 * @param ageInput
	 * @param emailInput2
	 * @param passwordField2
	 * @return true if created successfully, else false
	 */
	private boolean createAccount(TextField firstInput,TextField lastInput,TextField ageInput,
									TextField emailInput2, PasswordField passwordField2){
		try{
                        String preventDuplicate = "SELECT * FROM user where firstName=? and lastName=? and email = ?"+
                                                    "and password = SHA1(?) and age=?";
                        preparedStatement= conn.prepareStatement(preventDuplicate);
			
			preparedStatement.setString(1,firstInput.getText().trim());
			preparedStatement.setString(2,lastInput.getText().trim());
			preparedStatement.setString(3,emailInput2.getText().trim());
			preparedStatement.setString(4,passwordField2.getText().trim());
			preparedStatement.setInt(5, Integer.parseInt(ageInput.getText().trim()));			
			ResultSet rs = preparedStatement.executeQuery();
                        if(rs.next()) {
                            firstInput.setText("");
                            lastInput.setText("");
                            ageInput.setText("");
                            emailInput2.setText("");
                            passwordField2.setText("");
                            return false;
                        }
                        
			String sql =  "INSERT INTO User(firstName, lastName, email, password, age) " + 
							"VALUES(?, ?, ?, SHA1(?), ?)";
			preparedStatement= conn.prepareStatement(sql);
			
			preparedStatement.setString(1,firstInput.getText().trim());
			preparedStatement.setString(2,lastInput.getText().trim());
			preparedStatement.setString(3,emailInput2.getText().trim());
			preparedStatement.setString(4,passwordField2.getText().trim());
			preparedStatement.setInt(5, Integer.parseInt(ageInput.getText().trim()));			
			preparedStatement.executeUpdate();
		}catch (SQLException e){return false;}
		
		return true;
	}//createAccount

}
