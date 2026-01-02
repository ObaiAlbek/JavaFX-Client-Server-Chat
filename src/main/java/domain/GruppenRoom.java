package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Repräsentiert einen Gruppenchatraum mit mehreren Teilnehmern und Administratoren.
 * Bietet Funktionen zur Verwaltung von Teilnehmern, Nachrichten und Admin-Rechten.
 */
public class GruppenRoom {
    private static int nextGroupId = 1000;        // Zähler für automatische Gruppen-ID Vergabe
    private final int groupId;                    // Eindeutige ID der Gruppe
    private final User creator;                   // Ersteller der Gruppe
    private List<User> admins;                    // Liste der Administratoren
    private List<User> participants;              // Liste aller Teilnehmer
    private String description;                   // Beschreibung der Gruppe
    private String name;                          // Name der Gruppe
    private List<Message> messages;               // Liste aller Gruppen-Nachrichten
    private final LocalDateTime createdAt;        // Erstellungszeitpunkt der Gruppe
    
    /**
     * Erstellt eine neue Gruppe mit dem angegebenen Ersteller, Namen und Beschreibung.
     * 
     * @param creator     Der Ersteller der Gruppe (darf nicht null sein)
     * @param name        Der Name der Gruppe (darf nicht null sein)
     * @param description Die Beschreibung der Gruppe (kann null sein)
     */
    public GruppenRoom(User creator, String name, String description) {
        Objects.requireNonNull(creator, "Creator darf nicht null sein");
        Objects.requireNonNull(name, "Name darf nicht null sein");
        
        this.groupId = nextGroupId++;
        this.creator = creator;
        this.name = name;
        this.description = description != null ? description : "";
        this.createdAt = LocalDateTime.now();
        
        this.admins = new ArrayList<>();
        this.participants = new ArrayList<>();
        this.messages = new ArrayList<>();
        
        this.admins.add(creator);        // Ersteller wird automatisch Admin
        this.participants.add(creator);  // Ersteller wird automatisch Teilnehmer
        
        // Gruppe beim Creator registrieren
        creator.getUserGruppenRoom().addGruppenRoom(this);
    }
    
    /**
     * Fügt einen neuen Teilnehmer zur Gruppe hinzu.
     * 
     * @param user Der hinzuzufügende Benutzer (darf nicht null sein)
     * @return true wenn der Benutzer erfolgreich hinzugefügt wurde
     * @throws IllegalArgumentException wenn der Benutzer bereits in der Gruppe ist
     * @throws IllegalStateException wenn die Gruppe nicht beim Benutzer registriert werden konnte
     */
    public boolean addParticipant(User user) {
        Objects.requireNonNull(user, "User darf nicht null sein");
        
        if (participants.contains(user)) {
            throw new IllegalArgumentException("User ist bereits Teil der Gruppe: " + user.getUsername());
        }
        
        // Gruppe beim User registrieren
        boolean added = user.getUserGruppenRoom().addGruppenRoom(this);
        if (!added) {
            throw new IllegalStateException("Gruppe konnte nicht beim User registriert werden");
        }
        
        return participants.add(user);
    }
    
    /**
     * Entfernt einen Teilnehmer aus der Gruppe.
     * 
     * @param remover       Der Benutzer, der die Entfernung durchführt (darf nicht null sein)
     * @param userToRemove  Der zu entfernende Benutzer (darf nicht null sein)
     * @return true wenn der Benutzer erfolgreich entfernt wurde
     * @throws IllegalArgumentException wenn der Entferner keine Berechtigung hat oder 
     *                                  der Creator entfernt werden soll
     */
    public boolean removeParticipant(User remover, User userToRemove) {
        Objects.requireNonNull(remover, "Remover darf nicht null sein");
        Objects.requireNonNull(userToRemove, "UserToRemove darf nicht null sein");
        
        if (!admins.contains(remover) && !remover.equals(userToRemove)) {
            throw new IllegalArgumentException("Nur Admins können andere User entfernen");
        }
        
        if (userToRemove.equals(creator)) {
            throw new IllegalArgumentException("Der Creator kann nicht entfernt werden");
        }
        
        // Gruppe beim User deregistrieren
        userToRemove.getUserGruppenRoom().removeGruppenRoom(this);
        
        // Admin-Rechte entfernen falls nötig
        if (admins.contains(userToRemove)) {
            admins.remove(userToRemove);
        }
        
        return participants.remove(userToRemove);
    }
    
    /**
     * Befördert einen Teilnehmer zum Administrator.
     * 
     * @param promoter      Der befördernde Administrator (darf nicht null sein)
     * @param userToPromote Der zu befördernde Benutzer (darf nicht null sein)
     * @return true wenn der Benutzer erfolgreich zum Admin befördert wurde
     * @throws IllegalArgumentException wenn der Promoter kein Admin ist, der Benutzer
     *                                  nicht in der Gruppe ist oder bereits Admin ist
     */
    public boolean addAdmin(User promoter, User userToPromote) {
        Objects.requireNonNull(promoter, "Promoter darf nicht null sein");
        Objects.requireNonNull(userToPromote, "UserToPromote darf nicht null sein");
        
        if (!admins.contains(promoter)) {
            throw new IllegalArgumentException("Nur Admins können andere zu Admins befördern");
        }
        
        if (!participants.contains(userToPromote)) {
            throw new IllegalArgumentException("User ist nicht Teil der Gruppe");
        }
        
        if (admins.contains(userToPromote)) {
            throw new IllegalArgumentException("User ist bereits Admin");
        }
        
        return admins.add(userToPromote);
    }
    
    /**
     * Entzieht einem Benutzer die Administrator-Rechte.
     * 
     * @param demoter      Der Administrator, der die Rechte entzieht (darf nicht null sein)
     * @param userToDemote Der Benutzer, dem die Rechte entzogen werden (darf nicht null sein)
     * @return true wenn die Admin-Rechte erfolgreich entzogen wurden
     * @throws IllegalArgumentException wenn der Demoter kein Admin ist, der Creator 
     *                                  betroffen ist oder der Benutzer kein Admin ist
     */
    public boolean removeAdmin(User demoter, User userToDemote) {
        Objects.requireNonNull(demoter, "Demoter darf nicht null sein");
        Objects.requireNonNull(userToDemote, "UserToDemote darf nicht null sein");
        
        if (!admins.contains(demoter)) {
            throw new IllegalArgumentException("Nur Admins können Admin-Rechte entziehen");
        }
        
        if (userToDemote.equals(creator)) {
            throw new IllegalArgumentException("Der Creator kann nicht seiner Admin-Rechte entzogen werden");
        }
        
        if (!admins.contains(userToDemote)) {
            throw new IllegalArgumentException("User ist kein Admin");
        }
        
        return admins.remove(userToDemote);
    }
    
    /**
     * Fügt eine Nachricht zur Gruppenkonversation hinzu.
     * 
     * @param sender  Der Absender der Nachricht (darf nicht null sein)
     * @param content Der Inhalt der Nachricht (darf nicht null sein)
     * @throws IllegalArgumentException wenn der Absender nicht Gruppenmitglied ist
     */
    public void addMessage(User sender, String content) {
        Objects.requireNonNull(sender, "Sender darf nicht null sein");
        Objects.requireNonNull(content, "Content darf nicht null sein");
        
        if (!participants.contains(sender)) {
            throw new IllegalArgumentException("Nur Gruppenmitglieder können Nachrichten senden");
        }
        
        Message message = new Message(sender, content);
        messages.add(message);
    }
    
    /**
     * Gibt alle Nachrichten der Gruppe als String-Liste zurück.
     * 
     * @return Liste der Nachrichten-Strings oder leere Liste bei keinen Nachrichten
     */
    public List<String> showMessages() {
        if (messages.isEmpty()) {
            return new ArrayList<>();
        }
        
        return messages.stream()
                .map(Message::toString)
                .collect(Collectors.toList());
    }
    
    /**
     * Überprüft ob ein Benutzer Administrator der Gruppe ist.
     * 
     * @param user Der zu überprüfende Benutzer
     * @return true wenn der Benutzer Admin ist, sonst false
     */
    public boolean isAdmin(User user) {
        return admins.contains(user);
    }
    
    /**
     * Überprüft ob ein Benutzer Teilnehmer der Gruppe ist.
     * 
     * @param user Der zu überprüfende Benutzer
     * @return true wenn der Benutzer Teilnehmer ist, sonst false
     */
    public boolean isParticipant(User user) {
        return participants.contains(user);
    }
    
    // Getter-Methoden für den Zugriff auf die privaten Felder
    
    /**
     * Gibt die eindeutige Gruppen-ID zurück.
     * 
     * @return Die ID der Gruppe
     */
    public int getGroupId() { return groupId; }
    
    /**
     * Gibt den Ersteller der Gruppe zurück.
     * 
     * @return Der Creator der Gruppe
     */
    public User getCreator() { return creator; }
    
    /**
     * Gibt den Namen der Gruppe zurück.
     * 
     * @return Der Gruppenname
     */
    public String getName() { return name; }
    
    /**
     * Gibt die Beschreibung der Gruppe zurück.
     * 
     * @return Die Gruppenbeschreibung
     */
    public String getDescription() { return description; }
    
    /**
     * Gibt den Erstellungszeitpunkt der Gruppe zurück.
     * 
     * @return Der Zeitpunkt der Gruppenerstellung
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    /**
     * Gibt eine Kopie der Admin-Liste zurück.
     * 
     * @return Liste der Administratoren
     */
    public List<User> getAdmins() { return new ArrayList<>(admins); }
    
    /**
     * Gibt eine Kopie der Teilnehmerliste zurück.
     * 
     * @return Liste der Teilnehmer
     */
    public List<User> getParticipants() { return new ArrayList<>(participants); }
    
    /**
     * Gibt eine Kopie der Nachrichtenliste zurück.
     * 
     * @return Liste der Nachrichten
     */
    public List<Message> getMessages() { return new ArrayList<>(messages); }
    
    /**
     * Gibt eine String-Repräsentation der Gruppe zurück.
     * 
     * @return String mit Gruppenname, ID, Teilnehmer- und Nachrichtenanzahl
     */
    @Override
    public String toString() {
        return String.format("GruppenRoom{name='%s', id=%d, participants=%d, messages=%d}",
                name, groupId, participants.size(), messages.size());
    }
}