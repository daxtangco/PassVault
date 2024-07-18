package main.java;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PassVaultUI extends Application {

    private PasswordVault vault = new PasswordVault();
    private User authenticatedUser;
    private ObservableList<PasswordEntry> entries;

    public PassVaultUI() {
    }

    public PassVaultUI(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Password Manager");

        BorderPane borderPane = new BorderPane();

        // Center GridPane
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.TOP_CENTER); // Align grid at the top center

        Label aliasLabel = new Label("Alias:");
        TextField aliasInput = new TextField();
        aliasInput.setPromptText("Alias");

        Label platformLabel = new Label("Platform:");
        TextField platformInput = new TextField();
        platformInput.setPromptText("Platform");

        Label usernameLabel = new Label("Username:");
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Username");

        Label passwordLabel = new Label("Password:");
        TextField passwordInput = new TextField();
        passwordInput.setPromptText("Password");

        Button generateButton = new Button("Generate");
        generateButton.setOnAction(e -> passwordInput.setText(PasswordGenerator.generate(16)));

        HBox passwordBox = new HBox(10);
        passwordBox.getChildren().addAll(passwordInput, generateButton);

        grid.add(aliasLabel, 0, 0);
        grid.add(aliasInput, 1, 0);
        grid.add(platformLabel, 0, 1);
        grid.add(platformInput, 1, 1);
        grid.add(usernameLabel, 0, 2);
        grid.add(usernameInput, 1, 2);
        grid.add(passwordLabel, 0, 3);
        grid.add(passwordBox, 1, 3);

        ListView<PasswordEntry> listView = new ListView<>();
        entries = FXCollections.observableArrayList(vault.getEntriesForUser(authenticatedUser.getUsername()));
        listView.setItems(entries);
        listView.setCellFactory(param -> new ListCell<PasswordEntry>() {
            @Override
            protected void updateItem(PasswordEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty || entry == null) {
                    setText(null);
                } else {
                    setText(entry.getAlias());
                }
            }
        });

        listView.setPrefHeight(200); // Set preferred height for the list view
        VBox.setVgrow(listView, javafx.scene.layout.Priority.ALWAYS);

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            String alias = aliasInput.getText();
            String platform = platformInput.getText();
            String username = usernameInput.getText();
            String password = passwordInput.getText();
            if (alias.isEmpty() || platform.isEmpty() || username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            } else {
                try {
                    String encryptedPassword = EncryptionService.encryptVaultPassword(password, "your-very-secure-secret");
                    PasswordEntry entry = new PasswordEntry(alias, platform, username, encryptedPassword, authenticatedUser.getUsername());
                    vault.addEntry(entry);
                    entries.add(entry);
                    aliasInput.clear();
                    platformInput.clear();
                    usernameInput.clear();
                    passwordInput.clear();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button showButton = new Button("Show");
        showButton.setOnAction(e -> {
            PasswordEntry selectedEntry = listView.getSelectionModel().getSelectedItem();
            if (selectedEntry != null) {
                try {
                    String decryptedPassword = EncryptionService.decryptVaultPassword(selectedEntry.getEncryptedPassword(), "your-very-secure-secret");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Password Details");
                    alert.setHeaderText("Alias: " + selectedEntry.getAlias());
                    alert.setContentText("Platform: " + selectedEntry.getPlatform() + "\nUsername: " + selectedEntry.getUsername() + "\nPassword: " + decryptedPassword);

                    ButtonType copyButtonType = new ButtonType("Copy", ButtonBar.ButtonData.LEFT);
                    alert.getButtonTypes().add(copyButtonType);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == copyButtonType) {
                            Clipboard clipboard = Clipboard.getSystemClipboard();
                            ClipboardContent content = new ClipboardContent();
                            content.putString(decryptedPassword);
                            clipboard.setContent(content);
                            Alert copyAlert = new Alert(Alert.AlertType.INFORMATION);
                            copyAlert.setTitle("Copy Password");
                            copyAlert.setHeaderText(null);
                            copyAlert.setContentText("Password copied to clipboard.");
                            copyAlert.showAndWait();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            PasswordEntry selectedEntry = listView.getSelectionModel().getSelectedItem();
            if (selectedEntry != null) {
                vault.removeEntry(selectedEntry);
                entries.remove(selectedEntry);
            }
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            PasswordEntry selectedEntry = listView.getSelectionModel().getSelectedItem();
            if (selectedEntry != null) {
                try {
                    String decryptedPassword = EncryptionService.decryptVaultPassword(selectedEntry.getEncryptedPassword(), "your-very-secure-secret");
                    aliasInput.setText(selectedEntry.getAlias());
                    platformInput.setText(selectedEntry.getPlatform());
                    usernameInput.setText(selectedEntry.getUsername());
                    passwordInput.setText(decryptedPassword);
                    vault.removeEntry(selectedEntry);
                    entries.remove(selectedEntry);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(e -> {
            LoginUI loginUI = new LoginUI();
            loginUI.start(new Stage());
            primaryStage.close();
        });

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.getChildren().addAll(addButton, showButton, editButton, deleteButton, logoutButton);

        VBox bottomBox = new VBox(10);
        bottomBox.setPadding(new Insets(10, 10, 10, 10)); // Minimal padding
        bottomBox.getChildren().addAll(listView, actionBox);

        VBox mainLayout = new VBox(10); // Reduced spacing
        mainLayout.setPadding(new Insets(10, 10, 10, 10)); // Minimal padding
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.getChildren().addAll(grid, bottomBox);

        borderPane.setCenter(mainLayout);

        Scene scene = new Scene(borderPane, 800, 600); // Adjusted scene size to 800x600
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
