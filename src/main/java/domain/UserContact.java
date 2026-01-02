package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Verwaltet die Kontaktliste eines Benutzers.
 * Bietet Funktionen zum Hinzufügen, Entfernen und Verwalten von Kontakten.
 */
public class UserContact {
    private List<User> contacts; // Liste aller Kontakte des Benutzers
    
    /**
     * Konstruktor für UserContact.
     * Initialisiert eine leere Kontaktliste.
     */
    public UserContact() {
        this.contacts = new ArrayList<>();
    }
    
    /**
     * Fügt einen neuen Kontakt zur Kontaktliste hinzu.
     * 
     * @param user Der hinzuzufügende Benutzer (darf nicht null sein)
     * @return true wenn der Kontakt erfolgreich hinzugefügt wurde
     * @throws NullPointerException wenn der Benutzer null ist
     * @throws IllegalArgumentException wenn der Benutzer bereits in der Kontaktliste existiert
     */
    public boolean addContact(User user) {
    	if (user == null)
    		throw new NullPointerException("User darf nicht null sein");
    	        
        if (contacts.contains(user))
        	throw new IllegalArgumentException("User existiert bereits in der Kontakt Liste");
       
        return contacts.add(user);
    }
    
    /**
     * Entfernt einen Kontakt aus der Kontaktliste.
     * 
     * @param user Der zu entfernende Benutzer (darf nicht null sein)
     * @return true wenn der Kontakt erfolgreich entfernt wurde, false wenn nicht vorhanden
     * @throws NullPointerException wenn der Benutzer null ist
     */
    public boolean removeContact(User user) {
    	if (user == null)
    		throw new NullPointerException("User darf nicht null sein");
        return contacts.remove(user);
    }
    
    /**
     * Überprüft ob ein Benutzer in der Kontaktliste vorhanden ist.
     * 
     * @param user Der zu überprüfende Benutzer (darf nicht null sein)
     * @return true wenn der Benutzer in der Kontaktliste ist, sonst false
     * @throws NullPointerException wenn der Benutzer null ist
     */
    public boolean hasContact(User user) {
    	if (user == null)
    		throw new NullPointerException("User darf nicht null sein");
        return contacts.contains(user);
    }
    
    /**
     * Zeigt alle Kontakte als String-Liste an.
     * 
     * @return Liste von Kontakt-Strings oder leere Liste bei keinen Kontakten
     */
    public List<String> showAllContacts() {
        if (contacts == null || contacts.isEmpty()) {
            return new ArrayList<>();
        }
        
        return contacts.stream()
                .map(User::getUsername) // <--- Wir wollen nur den Namen!
                .collect(Collectors.toList());
    }
    
    /**
     * Gibt eine Kopie der Kontaktliste zurück.
     * 
     * @return Liste aller Kontakte (Kopie zur Wahrung der Encapsulation)
     */
    public List<User> getContacts() {
        return new ArrayList<>(contacts); 
    }
    
    /**
     * Gibt die Anzahl der Kontakte zurück.
     * 
     * @return Anzahl der Kontakte in der Liste
     */
    public int getContactCount() {
        return contacts.size();
    }
}