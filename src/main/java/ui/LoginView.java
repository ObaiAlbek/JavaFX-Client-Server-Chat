package ui;

import fassade.ChatService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView extends VBox {

    private ChatApplication app;
    private ChatService chatService;

    public LoginView(ChatApplication app, ChatService chatService) {
        this.app = app;
        this.chatService = chatService;

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(40));
        this.setSpacing(20);
        this.setStyle("-fx-background-color: #f0f2f5;");

        // Titel
        Label titleLabel = new Label("MyLocalChat");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: #008069;"); // WhatsApp-Grün ;)

        // Eingabefeld
        TextField usernameField = new TextField();
        usernameField.setPromptText("Benutzername");
        usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        usernameField.setMaxWidth(300);

        // Buttons
        Button loginBtn = new Button("Einloggen");
        loginBtn.setStyle("-fx-background-color: #008069; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px;");
        loginBtn.setMaxWidth(300);

        Button registerBtn = new Button("Neu registrieren");
        registerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #008069; -fx-border-color: #008069; -fx-border-radius: 5px;");
        registerBtn.setMaxWidth(300);

        // Feedback Label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        // Logik
        loginBtn.setOnAction(e -> {
            String name = usernameField.getText().trim();
            if (name.isEmpty()) {
                errorLabel.setText("Bitte Name eingeben!");
                return;
            }
            // Da wir kein Passwort haben, prüfen wir nur Existenz (indirekt)
            // In einer echten App würde hier getUser() geprüft
            try {
                // Wir simulieren Login, indem wir prüfen, ob User existiert 
                // (Hinweis: Dein ChatService hat keine öffentliche 'userExists'-Methode, 
                // daher fangen wir hier einfach an und nehmen an, er existiert, 
                // oder nutzen createUser und fangen die Exception)
                app.showMainView(name);
                ((Stage) this.getScene().getWindow()).close();
            } catch (Exception ex) {
                errorLabel.setText("Login fehlgeschlagen.");
            }
        });

        registerBtn.setOnAction(e -> {
            String name = usernameField.getText().trim();
            if (name.isEmpty()) return;
            
            try {
                boolean success = chatService.createUser(name);
                if (success) {
                    errorLabel.setStyle("-fx-text-fill: green;");
                    errorLabel.setText("Registriert! Bitte einloggen.");
                }
            } catch (IllegalArgumentException ex) {
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText(ex.getMessage());
            }
        });

        this.getChildren().addAll(titleLabel, usernameField, loginBtn, registerBtn, errorLabel);
    }
}