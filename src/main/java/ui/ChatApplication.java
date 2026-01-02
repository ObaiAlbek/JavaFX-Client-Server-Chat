package ui;

import fassade.ChatService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApplication extends Application {

    // Zentrale Instanz des Services, die wir durchreichen
    private ChatService chatService;

    @Override
    public void start(Stage primaryStage) {
        this.chatService = new ChatService();
        
        // Starte mit dem Login-Screen
        showLoginView(primaryStage);
    }

    public void showLoginView(Stage stage) {
        LoginView loginView = new LoginView(this, chatService);
        Scene scene = new Scene(loginView, 400, 500);
        // Optional: Hier CSS laden
        // scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        stage.setTitle("Login - MyLocalChat");
        stage.setScene(scene);
        stage.show();
    }

    public void showMainView(String username) {
        Stage mainStage = new Stage();
        MainView mainView = new MainView(username, chatService);
        Scene scene = new Scene(mainView, 1000, 700);
        
        mainStage.setTitle("MyLocalChat - Angemeldet als: " + username);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}