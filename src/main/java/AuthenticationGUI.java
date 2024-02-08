import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AuthenticationGUI  extends Application {

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Login Page");

        //Creating grid pane
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25,25,25,25));

        //Adding Labels for username and password
        Label userName= new javafx.scene.control.Label("Username: ");
        gridPane.add(userName,0,1);
        TextField userTextField=new TextField();
        gridPane.add(userTextField,1,1);
        Label password=new Label("Password: ");
        gridPane.add(password,0,2);
        PasswordField passwordField=new PasswordField();
        gridPane.add(passwordField,1,2);

        //Adding a login button
        Button button=new Button("Log In");
        gridPane.add(button,1,4);
        final Label message=new Label();
        gridPane.add(message,1,6);

        //Setting Action on Click
        button.setOnAction(e->{
            String username=userTextField.getText();
            String pass=passwordField.getText();
            //Integration with Authentication.java
            String auth =Authentication.authenticate(username,pass);
            if(auth==null){
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Authentication Failed!!");
                alert.setHeaderText(null);
                alert.setContentText("Authentication Failed, Please Try Again!");
                alert.showAndWait();
            } else {
                primaryStage.hide();
                DisplayPage.display(auth, DisplayPage.SlugURLHandler);
            }
        });
        Scene scene=new Scene(gridPane,300,275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
