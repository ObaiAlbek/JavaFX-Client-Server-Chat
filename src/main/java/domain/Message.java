package domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Repräsentiert eine Nachricht im Chat-System.
 * Enthält Informationen über Absender, Inhalt, Zeitstempel und Nachrichtentyp.
 */
public class Message {
    private User sender;                    // Absender der Nachricht
    private String content;                 // Inhalt der Nachricht
    private LocalDateTime timestamp;        // Zeitpunkt der Erstellung
    private MessageType type;               // Typ der Nachricht (TEXT, IMAGE, etc.)
    
    /**
     * Zeitformatierer für die Darstellung des Zeitstempels.
     * Formatiert die Zeit im Format "HH:mm" (Stunden:Minuten).
     */
    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm");
   
    
    /**
     * Erstellt eine neue Textnachricht mit aktuellem Zeitstempel.
     * 
     * @param sender  Der Absender der Nachricht (darf nicht null sein)
     * @param content Der Inhalt der Nachricht (darf nicht null oder leer sein)
     */
    public Message(User sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now(); // Setzt aktuellen Zeitpunkt
        this.type = MessageType.TEXT;         // Standardmäßig Textnachricht
    }
    
    /**
     * Erstellt eine neue Nachricht mit spezifischem Typ und aktuellem Zeitstempel.
     * 
     * @param sender  Der Absender der Nachricht (darf nicht null sein)
     * @param content Der Inhalt der Nachricht (darf nicht null oder leer sein)
     * @param type    Der Typ der Nachricht (TEXT, IMAGE, FILE, etc.)
     */
    public Message(User sender, String content, MessageType type) {
        this(sender, content);  // Ruft den Hauptkonstruktor auf
        this.type = type;       // Setzt den spezifischen Nachrichtentyp
    }
    
    /**
     * Gibt die Nachricht mit Zeitstempel formatiert zurück.
     * Format: "[HH:mm] Absender: Nachricht"
     * 
     * @return Formatierter Nachrichten-String mit Zeitstempel
     */
    public String toTimeString() {
        return String.format("[%s] %s: %s", 
            timestamp.format(TIME_FORMATTER),  // Formatiert Zeit im HH:mm Format
            sender.getUsername(),              // Holt den Benutzernamen des Absenders
            content);                          // Nachrichteninhalt
    }
   
    /**
     * Gibt die String-Repräsentation der Nachricht zurück.
     * Verwendet standardmäßig die Zeitstempel-Formatierung.
     * 
     * @return Formatierte Nachricht als String
     */
    @Override
    public String toString() {
        return toTimeString(); // Verwendet die Zeitstempel-Formatierung
    }
    
    // Getter- und Setter-Methoden
    
    /**
     * Gibt den Absender der Nachricht zurück.
     * 
     * @return User-Objekt des Absenders
     */
    public User getSender() {
        return sender;
    }

    /**
     * Setzt den Absender der Nachricht.
     * 
     * @param sender Der neue Absender (darf nicht null sein)
     */
    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Gibt den Inhalt der Nachricht zurück.
     * 
     * @return Nachrichteninhalt als String
     */
    public String getContent() {
        return content;
    }

    /**
     * Setzt den Inhalt der Nachricht.
     * 
     * @param content Der neue Nachrichteninhalt (darf nicht null sein)
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gibt den Zeitstempel der Nachricht zurück.
     * 
     * @return LocalDateTime des Erstellungszeitpunkts
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Setzt den Zeitstempel der Nachricht.
     * 
     * @param timestamp Der neue Zeitstempel (darf nicht null sein)
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gibt den Typ der Nachricht zurück.
     * 
     * @return MessageType der Nachricht
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Setzt den Typ der Nachricht.
     * 
     * @param type Der neue Nachrichtentyp
     */
    public void setType(MessageType type) {
        this.type = type;
    }
}