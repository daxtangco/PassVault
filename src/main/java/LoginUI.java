package main.java;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginUI extends Application {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button registerButton;
    private Button resetPasswordButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // Initialize UI components
        usernameField = new TextField();
        passwordField = new PasswordField();
        loginButton = new Button("Login");
        registerButton = new Button("Register");
        resetPasswordButton = new Button("Reset Password");

        // Set up the layout
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10, 10, 10, 10)); // Set equal padding on all sides
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add components to the grid
        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(new Label("Password:"), 0, 1);
        gridPane.add(passwordField, 1, 1);

        // Add register and back buttons
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginButton, registerButton, resetPasswordButton);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setMaxWidth(Double.MAX_VALUE);
        resetPasswordButton.setMaxWidth(Double.MAX_VALUE);

        gridPane.add(buttonBox, 1, 3);

        // Set the action for the login button
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username and password cannot be empty.");
            } else {
                User user = UserDatabase.getUserByUsername(username);
                if (user == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password.");
                } else {
                    try {
                        if (EncryptionService.verifyPassword(password, user.getEncryptedPassword())) {
                            PassVaultUI passVaultUI = new PassVaultUI(user);
                            passVaultUI.start(new Stage());
                            primaryStage.close();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while processing your request.");
                    }
                }
            }
        });

        // Set the action for the register button
        registerButton.setOnAction(e -> {
            RegisterUI registerUI = new RegisterUI();
            registerUI.start(new Stage());
            primaryStage.close();
        });

        // Set the action for the reset password button
        resetPasswordButton.setOnAction(e -> {
            ResetPasswordUI resetPasswordUI = new ResetPasswordUI();
            resetPasswordUI.start(new Stage());
            primaryStage.close();
        });

        // Set up the scene
        Scene scene = new Scene(gridPane, 400, 250); // Increased width for more padding
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
