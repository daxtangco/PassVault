package main.java;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Initialize the database
        Database.initializeDatabase();

        // Load the login UI
        LoginUI loginUI = new LoginUI();
        loginUI.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
