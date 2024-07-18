package main.java;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterUI extends Application {
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Button registerButton;
    private Button backButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Register");

        // Initialize UI components
        usernameField = new TextField();
        passwordField = new PasswordField();
        confirmPasswordField = new PasswordField();
        registerButton = new Button("Register");
        backButton = new Button("Back to Login");

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
        gridPane.add(new Label("Confirm Password:"), 0, 2);
        gridPane.add(confirmPasswordField, 1, 2);

        // Add register and back buttons
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(registerButton, backButton);
        registerButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setMaxWidth(Double.MAX_VALUE);

        gridPane.add(buttonBox, 1, 3);

        // Set the action for the register button
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            } else if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            } else {
                UserDatabase userDatabase = new UserDatabase();
                if (userDatabase.usernameExists(username)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Username is already taken.");
                } else {
                    try {
                        String salt = EncryptionService.generateSalt();
                        String hashedPassword = EncryptionService.hashPassword(password, salt);
                        String resetKey = EncryptionService.generateResetKey();
                        String encryptedResetKey = EncryptionService.encrypt(resetKey, "your-very-secure-secret");

                        User newUser = new User(username, hashedPassword, encryptedResetKey);
                        userDatabase.saveUser(newUser);

                        // Show the reset key in an alert
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Registration Successful");
                        alert.setHeaderText("Registration Successful");
                        alert.setContentText("Your reset key is: " + resetKey);

                        ButtonType copyButtonType = new ButtonType("Copy", ButtonBar.ButtonData.LEFT);
                        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        alert.getButtonTypes().setAll(copyButtonType, okButtonType);

                        alert.showAndWait().ifPresent(response -> {
                            if (response == copyButtonType) {
                                Clipboard clipboard = Clipboard.getSystemClipboard();
                                ClipboardContent content = new ClipboardContent();
                                content.putString(resetKey);
                                clipboard.setContent(content);
                                showAlert(Alert.AlertType.INFORMATION, "Copied", "Reset key copied to clipboard.");
                            }
                            // Close the registration window and open the login window
                            LoginUI loginUI = new LoginUI();
                            loginUI.start(new Stage());
                            primaryStage.close();
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while processing your request.");
                    }
                }
            }
        });

        // Set the action for the back button
        backButton.setOnAction(e -> {
            LoginUI loginUI = new LoginUI();
            loginUI.start(new Stage());
            primaryStage.close();
        });

        // Set up the scene
        Scene scene = new Scene(gridPane, 400, 300); // Increased width for more padding
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
