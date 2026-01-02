package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Verwaltet die Chaträume eines Benutzers.
 * Enthält Funktionen zum Hinzufügen, Entfernen und Abfragen von Chaträumen.
 */
public class UserChatRoom {
    private List<ChatRoom> chatRooms; // Liste aller Chaträume des Benutzers
    
    /**
     * Konstruktor für UserChatRoom.
     * Initialisiert eine leere Liste von Chaträumen.
     */
    public UserChatRoom() {
        this.chatRooms = new ArrayList<>();
    }
    
    
    /**
     * Fügt einen Chatraum zur Liste des Benutzers hinzu.
     * 
     * @param chatRoom Der hinzuzufügende Chatraum (darf nicht null sein)
     * @return true wenn der Chatraum erfolgreich hinzugefügt wurde
     * @throws IllegalArgumentException wenn der Chatraum bereits vorhanden ist
     */
    public boolean addChat(ChatRoom chatRoom) {
        Objects.requireNonNull(chatRoom, "ChatRoom darf nicht null sein");
        
        if (chatRooms.contains(chatRoom)) {
            throw new IllegalArgumentException("ChatRoom bereits vorhanden: " + chatRoom.getRoomId());
        }
        
        return chatRooms.add(chatRoom);
    }
    
    /**
     * Entfernt einen Chatraum aus der Liste des Benutzers.
     * 
     * @param chatRoom Der zu entfernende Chatraum (darf nicht null sein)
     * @return true wenn der Chatraum erfolgreich entfernt wurde
     * @throws IllegalArgumentException wenn der Chatraum nicht gefunden wurde
     */
    public boolean removeChat(ChatRoom chatRoom) {
        Objects.requireNonNull(chatRoom, "ChatRoom darf nicht null sein");
        
        if (!chatRooms.contains(chatRoom)) {
            throw new IllegalArgumentException("ChatRoom nicht gefunden: " + chatRoom.getRoomId());
        }
        
        return chatRooms.remove(chatRoom);
    }
    
    /**
     * Gibt eine Kopie aller Chaträume des Benutzers zurück.
     * 
     * @return Liste aller Chaträume
     */
    public List<ChatRoom> getAllChatRooms() {
        return new ArrayList<>(chatRooms); // Rückgabe einer Kopie für Encapsulation
    }
    
    /**
     * Gibt eine Liste aller Chatraum-IDs des Benutzers zurück.
     * 
     * @return Liste von Chatraum-IDs
     */
    public List<Integer> getChatRoomIds() {
        List<Integer> ids = new ArrayList<>();
        for (ChatRoom room : chatRooms) {
            ids.add(room.getRoomId());
        }
        return ids;
    }
    
    /**
     * Überprüft ob ein bestimmter Chatraum vorhanden ist.
     * 
     * @param chatRoom Der zu überprüfende Chatraum
     * @return true wenn der Chatraum vorhanden ist, sonst false
     */
    public boolean contains(ChatRoom chatRoom) {
        return chatRooms.contains(chatRoom);
    }
    
    /**
     * Gibt die Anzahl der Chaträume des Benutzers zurück.
     * 
     * @return Anzahl der Chaträume
     */
    public int getChatRoomCount() {
        return chatRooms.size();
    }
    
    /**
     * Überprüft ob der Benutzer keine Chaträume hat.
     * 
     * @return true wenn keine Chaträume vorhanden sind, sonst false
     */
    public boolean isEmpty() {
        return chatRooms.isEmpty();
    }
    
    /**
     * Entfernt alle Chaträume des Benutzers.
     */
    public void clearChatRooms() {
        chatRooms.clear();
    }
    
    /**
     * Gibt Informationen aller Chaträume als String-Liste zurück.
     * 
     * @return Liste von Chatraum-Informationen
     */
    public List<String> getChatRoomInfos() {
        List<String> infos = new ArrayList<>();
        for (ChatRoom room : chatRooms) {
            infos.add(room.toString());
        }
        return infos;
    }
}