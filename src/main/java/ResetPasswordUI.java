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

public class ResetPasswordUI extends Application {
    private TextField usernameField;
    private TextField resetKeyField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private Button resetButton;
    private Button backButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Reset Password");

        // Initialize UI components
        usernameField = new TextField();
        resetKeyField = new TextField();
        newPasswordField = new PasswordField();
        confirmPasswordField = new PasswordField();
        resetButton = new Button("Reset Password");
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
        gridPane.add(new Label("Reset Key:"), 0, 1);
        gridPane.add(resetKeyField, 1, 1);
        gridPane.add(new Label("New Password:"), 0, 2);
        gridPane.add(newPasswordField, 1, 2);
        gridPane.add(new Label("Confirm Password:"), 0, 3);
        gridPane.add(confirmPasswordField, 1, 3);

        // Add reset and back buttons
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(resetButton, backButton);
        resetButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setMaxWidth(Double.MAX_VALUE);

        gridPane.add(buttonBox, 1, 4);

        // Set the action for the reset button
        resetButton.setOnAction(e -> {
            String username = usernameField.getText();
            String resetKey = resetKeyField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (username.isEmpty() || resetKey.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            } else if (!newPassword.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            } else {
                try {
                    UserDatabase userDatabase = new UserDatabase();
                    User user = userDatabase.getUserByUsername(username);
                    if (user == null) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or reset key.");
                    } else if (EncryptionService.verifyPassword(newPassword, user.getEncryptedPassword())) {
                        showAlert(Alert.AlertType.ERROR, "Error", "New password cannot be the same as the old password.");
                    } else {
                        if (userDatabase.resetPassword(username, resetKey, newPassword)) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Password has been reset. Please log in with your new password.");
                            LoginUI loginUI = new LoginUI();
                            loginUI.start(new Stage());
                            primaryStage.close();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or reset key.");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while processing your request.");
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
