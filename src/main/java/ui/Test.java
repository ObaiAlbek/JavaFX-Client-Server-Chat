package ui;

import fassade.ChatService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Test extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Zentralen Service erstellen (Simuliert Server & Datenbank)
        // WICHTIG: Es gibt nur EINE Instanz, die beide Fenster nutzen!
        ChatService sharedService = new ChatService();

        // 2. Test-User vorab registrieren (damit wir uns nicht erst registrieren müssen)
        try {
            sharedService.createUser("Obai");
            sharedService.createUser("Omar");
            
            // Optional: Die beiden schon als Kontakte verknüpfen
            sharedService.addContact("Omar", "Obai");
            sharedService.addContact("Obai", "Omar");
        } catch (Exception e) {
            System.err.println("Info: " + e.getMessage());
        }

        // 3. Erstes Fenster für "Obai" öffnen
        openChatWindow(sharedService, "Obai", 50, 100);

        // 4. Zweites Fenster für "Omar" öffnen (versetzt positioniert)
        openChatWindow(sharedService, "Omar", 1000, 100);
    }

    /**
     * Hilfsmethode, um ein Chat-Fenster zu erzeugen
     */
    private void openChatWindow(ChatService service, String username, double x, double y) {
        Stage stage = new Stage();
        stage.setTitle("MyLocalChat - " + username);

        MainView mainView = new MainView(username, service);
        
        // HIER ÄNDERN: Statt 450, 650 -> nimm 900, 700
        Scene scene = new Scene(mainView, 900, 700); 
        
        stage.setScene(scene);
        
        // Position setzen
        stage.setX(x);
        stage.setY(y);
        
        stage.show();
    }
    public static void main(String[] args) {
        // Startet die JavaFX Runtime
        launch(args);
    }
}