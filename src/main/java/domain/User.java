package domain;

import java.util.List;

/**
 * Repräsentiert einen Benutzer im Chat-System.
 * Enthält Benutzerinformationen, Kontakte, Chaträume und Gruppenmitgliedschaften.
 * Jeder Benutzer hat eine eindeutige ID und kann multiple Chat-Beziehungen verwalten.
 */
public class User {

	private static int nextUserId = 1000;    // Zähler für automatische User-ID Vergabe
	private int userId;                      // Eindeutige ID des Benutzers
	private String username;           // Benutzername (final, da unveränderlich)
	private boolean isOnline;                // Online-Status des Benutzers
	private UserInfo userInfo;               // Statusinformation des Benutzers
	private UserContact userContacts;       // Kontaktliste des Benutzers
	private UserChatRoom chatRoom;           // Verwaltung der Chaträume des Benutzers
	private UserGruppenRoom gruppenRoom;     // Verwaltung der Gruppenmitgliedschaften
	private String statusText = ""; // NEU: Speichert den Custom-Text
	
	/**
	 * Erstellt einen neuen Benutzer mit dem angegebenen Benutzernamen.
	 * Der Benutzer wird automatisch als online und "verfügbar" eingestuft.
	 * 
	 * @param username Der Benutzername für den neuen Benutzer (darf nicht null oder leer sein)
	 */
	public User(String username) {
		this.userId = nextUserId++;           // Vergibt automatische eindeutige ID
		this.username = username;
		this.isOnline = true;                 // Standardmäßig online
		this.userInfo = UserInfo.VERFÜGBAR;   // Standard-Status "verfügbar"
		this.userContacts = new UserContact();      // Initialisiert leere Kontaktliste
		this.chatRoom = new UserChatRoom();          // Initialisiert Chatraum-Verwaltung
		this.gruppenRoom = new UserGruppenRoom();    // Initialisiert Gruppen-Verwaltung
	}

	/**
	 * Gibt die eindeutige User-ID zurück.
	 * 
	 * @return Die ID des Benutzers
	 */
	public int getUserId() {
		return userId;
	}

	
	public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
    
    /**
	 * Überprüft den Online-Status des Benutzers.
	 * 
	 * @return true wenn der Benutzer online ist, false wenn offline
	 */
	public boolean isOnline() {
		return isOnline;
	}
	
	/**
	 * Setzt den Online-Status des Benutzers.
	 * 
	 * @param isOnline true für online, false für offline
	 */
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	/**
	 * Gibt den Benutzernamen zurück.
	 * 
	 * @return Der Benutzername
	 */
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gibt den aktuellen Statusinformationen des Benutzers zurück.
	 * 
	 * @return UserInfo-Objekt mit den Statusinformationen
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}

	/**
	 * Setzt die Statusinformationen des Benutzers.
	 * 
	 * @param userInfo Die neuen Statusinformationen
	 */
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	/**
	 * Gibt die Kontaktliste des Benutzers zurück.
	 * 
	 * @return UserContact-Objekt mit allen Kontakten
	 */
	public UserContact getUserContacts() {
		return userContacts;
	}
	
	/**
	 * Gibt die Chatraum-Verwaltung des Benutzers zurück.
	 * 
	 * @return UserChatRoom-Objekt mit allen Chaträumen
	 */
	public UserChatRoom getUserChatRoom() {
		return chatRoom;
	}
	
	/**
	 * Gibt die Gruppen-Verwaltung des Benutzers zurück.
	 * 
	 * @return UserGruppenRoom-Objekt mit allen Gruppenmitgliedschaften
	 */
	public UserGruppenRoom getUserGruppenRoom() {
		return gruppenRoom;
	}

	/**
	 * Gibt eine String-Repräsentation des Benutzers zurück.
	 * Enthält User-ID, Benutzername, Online-Status und User-Info.
	 * 
	 * @return String mit den Benutzerinformationen
	 */
	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", isOnline=" + isOnline + ", userInfo=" + userInfo
				+ "]";
	}

}