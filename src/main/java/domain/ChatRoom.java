package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repräsentiert einen Chatraum zwischen zwei Benutzern.
 * Verwaltet Nachrichten und Metadaten der Konversation.
 */
public class ChatRoom {
    private static int nextRoomId = 1000; // Zähler für automatische Raum-ID Vergabe
    private int roomId;                   // Eindeutige ID des Chatraums
    private List<Message> messages;       // Liste aller Nachrichten im Raum
    private final LocalDateTime createdAt; // Erstellungszeitpunkt des Raums
    private User user1;                   // Erster Benutzer im Chat
    private User user2;                   // Zweiter Benutzer im Chat

    /**
     * Erstellt einen neuen Chatraum zwischen zwei Benutzern.
     * 
     * @param user1 Der erste Benutzer des Chats
     * @param user2 Der zweite Benutzer des Chats
     */
    public ChatRoom(User user1, User user2) {
        this.roomId = nextRoomId++;         // Vergibt automatische ID
        this.messages = new ArrayList<>();  // Initialisiert leere Nachrichtenliste
        this.createdAt = LocalDateTime.now(); // Setzt aktuellen Zeitpunkt
        this.user1 = user1;
        this.user2 = user2;
    }
    
    /**
     * Fügt eine Nachricht zum Chatraum hinzu.
     * 
     * @param message Die hinzuzufügende Nachricht
     */
    public void addMessage(Message message) {
        if (messages == null) 
            messages = new ArrayList<>(); // Sicherstellt, dass Liste existiert
        
        messages.add(message); // Nachricht zur Liste hinzufügen
    }
    
    // Getter-Methoden für Zugriff auf private Felder
    
    /**
     * Gibt die eindeutige Raum-ID zurück.
     * 
     * @return Die ID des Chatraums
     */
    public int getRoomId() {
        return roomId;
    }
    
    /**
     * Gibt den ersten Benutzer des Chats zurück.
     * 
     * @return User-Objekt des ersten Benutzers
     */
    public User getUser1() {
        return user1;
    }
    
    /**
     * Gibt den zweiten Benutzer des Chats zurück.
     * 
     * @return User-Objekt des zweiten Benutzers
     */
    public User getUser2() {
        return user2;
    }
    
    /**
     * Gibt die Liste aller Nachrichten im Chatraum zurück.
     * 
     * @return Liste von Message-Objekten
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Gibt den Erstellungszeitpunkt des Chatraums zurück.
     * 
     * @return LocalDateTime des Erstellungszeitpunkts
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Zeigt alle Nachrichten des Chatraums als String-Liste an.
     * 
     * @return Liste von Nachrichten-Strings oder leere Liste bei keinen Nachrichten
     */
    public List<String> showMessages(){
        if (messages == null || messages.isEmpty())
            return new ArrayList<>(); // Rückgabe leere Liste falls keine Nachrichten
        
        // Wandelt jede Nachricht in String um und sammelt in Liste
        return messages.stream()
                .map(Message::toString)
                .collect(Collectors.toList());
    }
}