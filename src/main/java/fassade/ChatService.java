package fassade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import domain.*;

/**
 * Zentrale Service-Klasse für die Chat-Anwendungslogik.
 * Verwaltet Benutzer, Chaträume, Gruppen und Nachrichten.
 * Implementiert das Observer-Pattern zur UI-Aktualisierung.
 */
public class ChatService {
    private Map<Integer, ChatRoom> chatrooms;        // Speichert alle Chaträume nach ID
    private Map<Integer, GruppenRoom> gruppenRooms;  // Speichert alle Gruppenräume nach ID
    private Map<String, User> users;                 // Speichert Benutzer nach Benutzername
    private Map<Integer, User> usersById;            // Speichert Benutzer nach User-ID
    
    // --- OBSERVER PATTERN: Liste der Zuhörer (z.B. Chat-Fenster) ---
    private List<Runnable> updateListeners = new ArrayList<>();

    /**
     * Konstruktor für den ChatService.
     * Initialisiert alle benötigten Datenstrukturen.
     */
    public ChatService() {
        chatrooms = new HashMap<>();
        gruppenRooms = new HashMap<>();
        users = new HashMap<>();
        usersById = new HashMap<>();
    }
    
    // --- NEUE METHODEN FÜR OBSERVER ---

    /**
     * Fügt einen Listener hinzu, der bei Änderungen (neue Nachricht etc.) benachrichtigt wird.
     * @param listener Die Methode, die aufgerufen werden soll.
     */
    public void addUpdateListener(Runnable listener) {
        updateListeners.add(listener);
    }

    /**
     * Benachrichtigt alle registrierten Listener über eine Änderung.
     */
    private void notifyUpdate() {
        for (Runnable listener : updateListeners) {
            listener.run();
        }
    }

    // --- BESTEHENDE LOGIK (mit notifyUpdate Ergänzungen) ---

    /**
     * Erstellt einen neuen Benutzer.
     */
    public boolean createUser(String userName) {
        if (users.containsKey(userName)) 
            throw new IllegalArgumentException("User existiert bereits: " + userName);
        
        User tempUser = new User(userName);
        users.put(userName, tempUser);
        usersById.put(tempUser.getUserId(), tempUser);
        
        notifyUpdate(); // UI aktualisieren
        return true;
    }
    
    private User getUser(String userName) {
        return users.get(userName);
    }
    
    private User getUser(int userId) {
        return usersById.get(userId);
    }
    
    /**
     * Erstellt einen neuen Chatraum.
     */
    public int createChatRoom(String user1Name, String user2Name) {
        User user1 = getUser(user1Name);
        User user2 = getUser(user2Name);
        
        if (user1 == null || user2 == null) 
            throw new IllegalArgumentException("User existiert nicht");
        
        // Prüft ob bereits ein Chatraum existiert
        for (ChatRoom room : chatrooms.values()) {
            if ((room.getUser1().equals(user1) && room.getUser2().equals(user2)) ||
                (room.getUser1().equals(user2) && room.getUser2().equals(user1))) {
                return room.getRoomId();
            }
        }
        
        // Erstellt neuen Chatraum
        ChatRoom tempChatRoom = new ChatRoom(user1, user2);
        chatrooms.put(tempChatRoom.getRoomId(), tempChatRoom);
        user1.getUserChatRoom().addChat(tempChatRoom);
        user2.getUserChatRoom().addChat(tempChatRoom);
        
        notifyUpdate(); // UI aktualisieren (neuer Chat in der Liste)
        return tempChatRoom.getRoomId();
    }
    
    /**
     * Sendet eine Nachricht.
     */
    public void sendMessage(int roomId, String senderName, String content) {
        ChatRoom room = chatrooms.get(roomId);
        if (room == null) 
            throw new IllegalArgumentException("Chatroom existiert nicht: " + roomId);
        
        User sender = getUser(senderName);
        if (sender == null) {
            throw new IllegalArgumentException("Sender existiert nicht: " + senderName);
        }
        
        if (!room.getUser1().equals(sender) && !room.getUser2().equals(sender)) {
            throw new IllegalArgumentException("Sender ist nicht Teil des Chatrooms");
        }
        
        Message message = new Message(sender, content);
        room.addMessage(message);
        
        notifyUpdate(); // WICHTIG: Alle Fenster benachrichtigen!
    }
    
  
   public List<Message> showMessage(int roomId) {
       ChatRoom room = chatrooms.get(roomId);
       if (room == null) 
           throw new IllegalArgumentException("Chatroom existiert nicht: " + roomId);
       
       // Wir geben direkt die Liste zurück (oder eine Kopie)
       return new ArrayList<>(room.getMessages());
   }
    
    /**
     * Fügt einen Kontakt hinzu.
     */
    public boolean addContact(String contactUserName, String currentUserName) {
        if (!users.containsKey(contactUserName)) 
            throw new IllegalArgumentException("User existiert nicht: " + contactUserName);
        
        if (!users.containsKey(currentUserName)) 
            throw new IllegalArgumentException("User existiert nicht: " + currentUserName);
        
        User contactUser = users.get(contactUserName);
        User currentUser = users.get(currentUserName);
        
        if (contactUser.equals(currentUser)) {
            throw new IllegalArgumentException("Kann sich nicht selbst als Kontakt hinzufügen");
        }
        
        boolean success = currentUser.getUserContacts().addContact(contactUser);
        if (success) notifyUpdate(); // UI aktualisieren (Kontaktliste)
        
        return success;
    }
    
    public List<User> getUserContacts(String userName) {
        if (!users.containsKey(userName)) 
            throw new IllegalArgumentException("User existiert nicht: " + userName);
        
        User user = users.get(userName);
        return user.getUserContacts().getContacts(); // Zugriff auf die User-Liste
    }
    
    public List<Integer> getUserChatRoomIds(String userName) {
        if (!users.containsKey(userName)) 
            throw new IllegalArgumentException("User existiert nicht: " + userName);
        
        User user = users.get(userName);
        return user.getUserChatRoom().getChatRoomIds();
    }
    
    public String getChatRoomInfo(int roomId) {
        ChatRoom room = chatrooms.get(roomId);
        if (room == null) 
            throw new IllegalArgumentException("Chatroom existiert nicht: " + roomId);
        
        return room.toString();
    }
    
    // --- GRUPPEN LOGIK ---

    public int createGruppenRoom(String creatorName, String groupName, String description) {
        User creator = getUser(creatorName);
        if (creator == null) {
            throw new IllegalArgumentException("Creator existiert nicht: " + creatorName);
        }
        
        GruppenRoom gruppenRoom = new GruppenRoom(creator, groupName, description);
        gruppenRooms.put(gruppenRoom.getGroupId(), gruppenRoom);
        
        notifyUpdate(); // UI aktualisieren
        return gruppenRoom.getGroupId();
    }
    
    public boolean addParticipantToGroup(int groupId, String adderName, String userToAddName) {
        GruppenRoom group = gruppenRooms.get(groupId);
        User adder = getUser(adderName);
        User userToAdd = getUser(userToAddName);
        
        if (group == null) throw new IllegalArgumentException("Gruppe existiert nicht: " + groupId);
        if (adder == null) throw new IllegalArgumentException("Adder existiert nicht: " + adderName);
        if (userToAdd == null) throw new IllegalArgumentException("User existiert nicht: " + userToAddName);
        
        if (!group.isAdmin(adder)) {
            throw new IllegalArgumentException("Nur Admins können Teilnehmer hinzufügen");
        }
        
        boolean success = group.addParticipant(userToAdd);
        if (success) notifyUpdate();
        
        return success;
    }
    
    public void sendGroupMessage(int groupId, String senderName, String content) {
        GruppenRoom group = gruppenRooms.get(groupId);
        User sender = getUser(senderName);
        
        if (group == null) throw new IllegalArgumentException("Gruppe existiert nicht: " + groupId);
        if (sender == null) throw new IllegalArgumentException("Sender existiert nicht: " + senderName);
        
        group.addMessage(sender, content);
        notifyUpdate(); // WICHTIG: Alle Fenster benachrichtigen!
    }
    
    public List<String> getGroupMessages(int groupId) {
        GruppenRoom group = gruppenRooms.get(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Gruppe existiert nicht: " + groupId);
        }
        
        return group.showMessages();
    }
    
    public List<String> getUserGruppenRooms(String userName) {
        User user = getUser(userName);
        if (user == null) {
            throw new IllegalArgumentException("User existiert nicht: " + userName);
        }
        
        return user.getUserGruppenRoom().getGruppenRoomNames();
    }
    
    public String getGruppenRoomInfo(int groupId) {
        GruppenRoom room = gruppenRooms.get(groupId);
        if (room == null) 
            throw new IllegalArgumentException("Gruppenraum existiert nicht: " + groupId);
        
        return room.toString();
    }
    
    /**
     * Gibt alle Chaträume zurück, in denen der User ist (Privat & Gruppe).
     * @param username Der Benutzername
     * @return Liste von Objekten (ChatRoom oder GruppenRoom)
     */
    public List<Object> getAllChatsForUser(String username) {
        User user = getUser(username);
        List<Object> allChats = new ArrayList<>();
        
        // 1. Private Chats
        for (ChatRoom room : user.getUserChatRoom().getAllChatRooms()) {
            allChats.add(room);
        }
        
        // 2. Gruppen Chats
        for (GruppenRoom group : user.getUserGruppenRoom().getGruppenRooms()) {
            allChats.add(group);
        }
        
        // Optional: Hier könnte man nach Datum sortieren (letzte Nachricht)
        return allChats;
    }
    
    /**
     * Aktualisiert das Profil eines Benutzers (Name und Status).
     * Gibt den neuen Namen zurück (falls er geändert wurde).
     */
    public String updateUserProfile(String oldName, String newName, UserInfo newStatus, String newStatusText) {
        User user = users.get(oldName);
        if (user == null) throw new IllegalArgumentException("User nicht gefunden.");

        // 1. Namensänderung
        if (!oldName.equals(newName)) {
            if (users.containsKey(newName)) {
                throw new IllegalArgumentException("Name vergeben!");
            }
            users.remove(oldName);
            user.setUsername(newName);
            users.put(newName, user);
        }

        // 2. Status & Text setzen
        user.setUserInfo(newStatus);
        // Wenn Status CUSTOM ist, speichern wir den Text, sonst leeren String
        if (newStatus == UserInfo.CUSTOM) {
            user.setStatusText(newStatusText);
        } else {
            user.setStatusText("");
        }
        
        notifyUpdate();
        return user.getUsername();
    }
    
    public void updateUserStatus(String username, UserInfo newInfo) {
        User user = users.get(username);
        if (user != null) {
            user.setUserInfo(newInfo);
            notifyUpdate();
        }
    }
}