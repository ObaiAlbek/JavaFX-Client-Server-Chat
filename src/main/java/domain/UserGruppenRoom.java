package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet die Gruppenmitgliedschaften eines Benutzers.
 * Enthält Funktionen zum Hinzufügen, Entfernen und Abfragen von Gruppenräumen.
 */
public class UserGruppenRoom {
    private List<GruppenRoom> gruppenRooms; // Liste aller Gruppenräume des Benutzers
    
    /**
     * Konstruktor für UserGruppenRoom.
     * Initialisiert eine leere Liste von Gruppenräumen.
     */
    public UserGruppenRoom() {
        this.gruppenRooms = new ArrayList<>();
    }
    
    /**
     * Fügt einen Gruppenraum zur Liste des Benutzers hinzu.
     * 
     * @param gruppenRoom Der hinzuzufügende Gruppenraum
     * @return true wenn der Gruppenraum erfolgreich hinzugefügt wurde,
     *         false wenn der Gruppenraum bereits vorhanden ist
     */
    public boolean addGruppenRoom(GruppenRoom gruppenRoom) {
        if (gruppenRooms.contains(gruppenRoom)) 
            return false; // Gruppenraum bereits vorhanden
        
        return gruppenRooms.add(gruppenRoom);
    }
    
    /**
     * Entfernt einen Gruppenraum aus der Liste des Benutzers.
     * 
     * @param gruppenRoom Der zu entfernende Gruppenraum
     * @return true wenn der Gruppenraum erfolgreich entfernt wurde,
     *         false wenn der Gruppenraum nicht vorhanden war
     */
    public boolean removeGruppenRoom(GruppenRoom gruppenRoom) {
        return gruppenRooms.remove(gruppenRoom);
    }
    
    /**
     * Gibt eine Kopie aller Gruppenräume des Benutzers zurück.
     * 
     * @return Liste aller Gruppenräume (Kopie zur Wahrung der Encapsulation)
     */
    public List<GruppenRoom> getGruppenRooms() {
        return new ArrayList<>(gruppenRooms); 
    }
    
    /**
     * Überprüft ob ein bestimmter Gruppenraum vorhanden ist.
     * 
     * @param gruppenRoom Der zu überprüfende Gruppenraum
     * @return true wenn der Gruppenraum vorhanden ist, sonst false
     */
    public boolean contains(GruppenRoom gruppenRoom) {
        return gruppenRooms.contains(gruppenRoom);
    }
    
    /**
     * Gibt die Anzahl der Gruppenräume des Benutzers zurück.
     * 
     * @return Anzahl der Gruppenräume
     */
    public int getSize() {
        return gruppenRooms.size();
    }
    
    /**
     * Überprüft ob der Benutzer keine Gruppenräume hat.
     * 
     * @return true wenn keine Gruppenräume vorhanden sind, sonst false
     */
    public boolean isEmpty() {
        return gruppenRooms.isEmpty();
    }
    
    /**
     * Gibt die Namen aller Gruppenräume des Benutzers zurück.
     * 
     * @return Liste von Gruppennamen
     */
    public List<String> getGruppenRoomNames() {
        List<String> names = new ArrayList<>();
        for (GruppenRoom room : gruppenRooms) 
            names.add(room.getName()); // Holt den Namen jedes Gruppenraums
        
        return names;
    }
}