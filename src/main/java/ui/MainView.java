package ui;

import domain.GruppenRoom;
import domain.Message;
import domain.User;
import domain.UserInfo;
import fassade.ChatService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainView extends BorderPane {

    private String currentUser;
    private ChatService chatService;
    
    // UI Komponenten Listen
    private ListView<User> contactList;
    private ListView<Object> groupList; // Zeigt Gruppenräume an
    
    // Chat Area Komponenten
    private VBox messageContainer;
    private ScrollPane messageScrollPane;
    
    // Header Komponenten
    private HBox chatHeaderBox;
    private Label headerNameLabel;
    private Label headerStatusLabel;
    private Circle headerStatusDot;
    
    // Status Variablen
    private int currentChatId = -1;
    private boolean isGroupChat = false; // WICHTIG: Merken, ob wir in einer Gruppe sind
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public MainView(String username, ChatService chatService) {
        this.currentUser = username;
        this.chatService = chatService;

        // 1. CSS Laden
        java.net.URL cssUrl = getClass().getResource("/styles.css");
        if (cssUrl != null) this.getStylesheets().add(cssUrl.toExternalForm());

        // 2. Layout erstellen (MUSS als erstes passieren!)
        initLayout();

        // 3. Listener registrieren
        chatService.addUpdateListener(() -> Platform.runLater(() -> {
            refreshLists();
            if (currentChatId != -1) loadMessages();
        }));

        // 4. Daten initial laden
        refreshLists();
    }

    private void initLayout() {
        // --- Sidebar ---
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(320);

     // Profil Header (Klickbar für Edit)
        HBox myProfileBox = new HBox(10);
        myProfileBox.setAlignment(Pos.CENTER_LEFT);
        myProfileBox.setPadding(new Insets(8));
        myProfileBox.setStyle("-fx-background-radius: 10px; -fx-cursor: hand;");
        
        // Hover-Effekt
        myProfileBox.setOnMouseEntered(e -> myProfileBox.setStyle("-fx-background-color: #e9edef; -fx-background-radius: 10px; -fx-cursor: hand;"));
        myProfileBox.setOnMouseExited(e -> myProfileBox.setStyle("-fx-background-color: transparent; -fx-background-radius: 10px; -fx-cursor: hand;"));
        
        // Klick-Action -> Dialog öffnen
        myProfileBox.setOnMouseClicked(e -> showEditProfileDialog());

        Circle myAvatar = createAvatar(currentUser, 22);
        
        VBox profileText = new VBox(2);
        Label userLabel = new Label(currentUser);
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        // Zeigt "Profil bearbeiten" klein darunter an
        Label editLabel = new Label("Profil bearbeiten ✎");
        editLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #667781;");
        
        profileText.getChildren().addAll(userLabel, editLabel);
        myProfileBox.getChildren().addAll(myAvatar, profileText);

        // --- Tabs ---
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // Tab 1: Kontakte
        Tab contactsTab = new Tab("Kontakte");
        contactList = new ListView<>();
        initContactListFactory(); // CellFactory setzen
        
        VBox contactsBox = new VBox(5);
        contactsBox.setPadding(new Insets(5,0,0,0));
        Button addContactBtn = new Button("Neuer Kontakt");
        addContactBtn.getStyleClass().add("button-primary");
        addContactBtn.setMaxWidth(Double.MAX_VALUE);
        addContactBtn.setOnAction(e -> showAddContactDialog());
        
        contactsBox.getChildren().addAll(addContactBtn, contactList);
        VBox.setVgrow(contactList, Priority.ALWAYS);
        contactsTab.setContent(contactsBox);

        // Tab 2: Gruppen
        Tab groupsTab = new Tab("Gruppen");
        groupList = new ListView<>();
        initGroupListFactory(); // CellFactory setzen

        VBox groupsBox = new VBox(5);
        groupsBox.setPadding(new Insets(5,0,0,0));
        Button createGroupBtn = new Button("Neue Gruppe");
        createGroupBtn.getStyleClass().add("button-primary");
        createGroupBtn.setMaxWidth(Double.MAX_VALUE);
        createGroupBtn.setOnAction(e -> showCreateGroupDialog());
        
        groupsBox.getChildren().addAll(createGroupBtn, groupList);
        VBox.setVgrow(groupList, Priority.ALWAYS);
        groupsTab.setContent(groupsBox);

        tabPane.getTabs().addAll(contactsTab, groupsTab);
        sidebar.getChildren().addAll(myProfileBox, new Separator(), tabPane);
        this.setLeft(sidebar);

        // --- Chat Area (Rechts) ---
        VBox chatArea = new VBox();
        chatArea.getStyleClass().add("chat-background");

        // Header (Unsichtbar am Anfang)
        chatHeaderBox = new HBox(15);
        chatHeaderBox.getStyleClass().add("chat-header");
        chatHeaderBox.setAlignment(Pos.CENTER_LEFT);
        chatHeaderBox.setVisible(false);

        Circle headerAvatar = new Circle(20, Color.LIGHTGRAY); 
        headerNameLabel = new Label("Name");
        headerNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        headerStatusLabel = new Label("");
        headerStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #667781;");
        headerStatusDot = new Circle(4, Color.TRANSPARENT);
        
        HBox statusLine = new HBox(5, headerStatusDot, headerStatusLabel);
        statusLine.setAlignment(Pos.CENTER_LEFT);
        VBox headerInfo = new VBox(2, headerNameLabel, statusLine);
        
        chatHeaderBox.getChildren().addAll(headerAvatar, headerInfo);

        // Nachrichten Bereich
        messageContainer = new VBox(8);
        messageContainer.setPadding(new Insets(20));
        messageScrollPane = new ScrollPane(messageContainer);
        messageScrollPane.setFitToWidth(true);
        messageScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        messageScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        // Auto-Scroll
        messageContainer.heightProperty().addListener((o, old, nev) -> messageScrollPane.setVvalue(1.0));
        VBox.setVgrow(messageScrollPane, Priority.ALWAYS);

        // Input Area
        HBox inputArea = new HBox(10);
        inputArea.getStyleClass().add("input-area");
        inputArea.setAlignment(Pos.CENTER_LEFT);

        TextField messageInput = new TextField();
        messageInput.setPromptText("Schreibe eine Nachricht...");
        messageInput.getStyleClass().add("message-input");
        HBox.setHgrow(messageInput, Priority.ALWAYS);

        Button sendBtn = new Button("➤");
        sendBtn.getStyleClass().add("button-primary");
        sendBtn.setPrefSize(40, 40);
        
        // Senden Logik
        Runnable sendAction = () -> {
            String text = messageInput.getText().trim();
            if (!text.isEmpty() && currentChatId != -1) {
                if (isGroupChat) {
                    chatService.sendGroupMessage(currentChatId, currentUser, text);
                } else {
                    chatService.sendMessage(currentChatId, currentUser, text);
                }
                messageInput.clear();
            }
        };
        sendBtn.setOnAction(e -> sendAction.run());
        messageInput.setOnAction(e -> sendAction.run());

        inputArea.getChildren().addAll(messageInput, sendBtn);
        chatArea.getChildren().addAll(chatHeaderBox, messageScrollPane, inputArea);
        this.setCenter(chatArea);
    }

    // --- Cell Factories (Aussehen der Listen) ---

    private void initContactListFactory() {
        contactList.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setGraphic(null);
                } else {
                    Circle avatar = createAvatar(user.getUsername(), 20);
                    VBox textBox = new VBox(3);
                    Label name = new Label(user.getUsername());
                    name.setStyle("-fx-font-weight: bold;");
                    Label status = new Label(formatUserInfo(user));
                    status.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");
                    textBox.getChildren().addAll(name, status);
                    
                    Circle dot = new Circle(5, user.isOnline() ? Color.LIMEGREEN : Color.GRAY);
                    
                    HBox cell = new HBox(10, avatar, textBox, new Region(), dot);
                    HBox.setHgrow(cell.getChildren().get(2), Priority.ALWAYS);
                    cell.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(cell);
                }
            }
        });
        
        contactList.getSelectionModel().selectedItemProperty().addListener((obs, old, user) -> {
            if (user != null) openPrivateChat(user);
        });
    }

    private void initGroupListFactory() {
        groupList.setCellFactory(param -> new ListCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Wir erwarten hier eigentlich GruppenRoom Objekte
                    // Falls du nur Strings hast (aus alten Versionen), müssen wir das anpassen
                    // Hier gehe ich davon aus, dass wir Objekte in die Liste packen (siehe refreshLists)
                    
                    String name = item.toString(); // Fallback
                    if (item instanceof GruppenRoom) {
                        name = ((GruppenRoom) item).getName();
                    }
                    
                    Circle avatar = createAvatar(name, 20);
                    Label nameLabel = new Label(name);
                    nameLabel.setStyle("-fx-font-weight: bold;");
                    
                    HBox cell = new HBox(10, avatar, nameLabel);
                    cell.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(cell);
                }
            }
        });

        groupList.getSelectionModel().selectedItemProperty().addListener((obs, old, item) -> {
            if (item != null && item instanceof GruppenRoom) {
                openGroupChat((GruppenRoom) item);
            }
        });
    }

    // --- Logik Methoden ---

    private void refreshLists() {
        try {
            // Kontakte laden
            List<User> contacts = chatService.getUserContacts(currentUser);
            contactList.getItems().setAll(contacts);
            
            // Gruppen laden (Hier nutzen wir den Service)
            // HINWEIS: Damit das sauber geht, brauchen wir idealerweise eine Methode
            // im ChatService, die echte GruppenRoom-Objekte zurückgibt.
            // Ich nutze hier einen Trick und hole sie über die 'getAllChatsForUser' Logik
            // oder wir nutzen die Namen und suchen sie.
            // Fürs erste nehmen wir an, wir bekommen Objekte.
            
            List<Object> allChats = chatService.getAllChatsForUser(currentUser);
            groupList.getItems().clear();
            for(Object chat : allChats) {
                if (chat instanceof GruppenRoom) {
                    groupList.getItems().add(chat);
                }
            }
            
        } catch (Exception e) {
            // Fehler ignorieren beim Refresh
        }
    }

    private void openPrivateChat(User partner) {
        try {
            this.isGroupChat = false; 
            this.currentChatId = chatService.createChatRoom(currentUser, partner.getUsername());
            
            // Header Update
            chatHeaderBox.setVisible(true);
            headerNameLabel.setText(partner.getUsername());
            
            // HIER WAR DER FEHLER:
            headerStatusLabel.setText(formatUserInfo(partner)); 
            
            headerStatusDot.setFill(partner.isOnline() ? Color.LIMEGREEN : Color.GRAY);
            chatHeaderBox.getChildren().set(0, createAvatar(partner.getUsername(), 20));

            loadMessages();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private void openGroupChat(GruppenRoom group) {
        try {
            this.isGroupChat = true; // WICHTIG
            this.currentChatId = group.getGroupId();
            
            // Header Update
            chatHeaderBox.setVisible(true);
            headerNameLabel.setText(group.getName());
            headerStatusLabel.setText(group.getParticipants().size() + " Teilnehmer");
            headerStatusDot.setFill(Color.TRANSPARENT);
            chatHeaderBox.getChildren().set(0, createAvatar(group.getName(), 20));
            
            loadMessages();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadMessages() {
        messageContainer.getChildren().clear();
        try {
            List<String> rawMessages;
            // Unterscheidung woher wir Nachrichten laden
            if (isGroupChat) {
                rawMessages = chatService.getGroupMessages(currentChatId);
            } else {
                // Hier müssen wir aufpassen: showMessage gibt bei dir Strings zurück
                // Falls du es auf Message-Objekte geändert hast (wie empfohlen), ist das hier anders.
                // Ich nutze hier die String-Variante als Fallback, da ich deinen aktuellen Service-Stand nicht zu 100% kenne.
                // Falls du Message-Objekte hast, musst du das Casting anpassen.
                 // Annahme: Du hast es auf List<Message> geändert wie im vorletzten Schritt.
                 List<Message> msgs = chatService.showMessage(currentChatId);
                 for (Message m : msgs) {
                     addMessageBubble(m);
                 }
                 return; 
            }
            
            // Fallback für Gruppen (da getGroupMessages oft noch Strings liefert in alter Version)
            // Falls du auch getGroupMessages auf List<Message> umgestellt hast, nutze die Logik oben!
            for (String s : rawMessages) {
                // Simples Anzeigen für Strings
                Label l = new Label(s);
                messageContainer.getChildren().add(l);
            }
            
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addMessageBubble(Message msg) {
        boolean isOwn = msg.getSender().getUsername().equals(currentUser);
        
        Text text = new Text(msg.getContent());
        text.setFill(Color.BLACK);
        text.setFont(Font.font("Segoe UI", 14));
        TextFlow textFlow = new TextFlow(text);
        
        Label timeLabel = new Label(msg.getTimestamp().format(TIME_FORMATTER));
        timeLabel.getStyleClass().add("timestamp");
        timeLabel.setPadding(new Insets(5, 0, 0, 8));
        
        HBox contentBox = new HBox(textFlow, timeLabel);
        contentBox.setAlignment(Pos.BOTTOM_RIGHT); 
        contentBox.getStyleClass().add(isOwn ? "bubble-own" : "bubble-other");
        contentBox.setMaxWidth(400);

        HBox container = new HBox(contentBox);
        container.setAlignment(isOwn ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messageContainer.getChildren().add(container);
    }

    // Dialoge
    private void showAddContactDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Kontakt");
        dialog.setHeaderText("Name eingeben:");
        dialog.showAndWait().ifPresent(name -> {
            try { chatService.addContact(name, currentUser); } 
            catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).show(); }
        });
    }
    
    private void showCreateGroupDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Neue Gruppe");
        dialog.setHeaderText("Gruppenname:");
        dialog.showAndWait().ifPresent(name -> {
            try { 
                chatService.createGruppenRoom(currentUser, name, "Eine neue Gruppe"); 
            } catch (Exception ex) { 
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show(); 
            }
        });
    }

    // Utilities
    private Circle createAvatar(String name, double radius) {
        Circle circle = new Circle(radius);
        int hash = name.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = hash & 0x0000FF;
        circle.setFill(Color.rgb(Math.abs(r % 200), Math.abs(g % 200), Math.abs(b % 200)));
        return circle;
    }
    
    private String formatUserInfo(User user) {
        UserInfo info = user.getUserInfo();
        if (info == null) return "";
        
        // Wenn Custom gewählt wurde und Text da ist -> Zeige Text
        if (info == UserInfo.CUSTOM && user.getStatusText() != null && !user.getStatusText().isEmpty()) {
            return user.getStatusText();
        }
        
        // Sonst: Enum schön formatieren (z.B. IN_DER_SCHULE -> In der Schule)
        String s = info.toString().toLowerCase().replace("_", " ");
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    private void showEditProfileDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Profil");
        
     // Styling
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        // HIER: Breite vergrößern (z.B. auf 400 Pixel)
        dialogPane.setPrefWidth(400); 
        // Optional: Mindesthöhe setzen, falls es zu kurz wirkt
        dialogPane.setMinHeight(450);
        
        // Eigener Header (damit es nicht nach Windows 95 aussieht)
        VBox customHeader = new VBox();
        customHeader.setStyle("-fx-background-color: #00a884; -fx-padding: 20px;");
        customHeader.setAlignment(Pos.CENTER);
        Label headerTitle = new Label("Profil bearbeiten");
        headerTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Großes Avatar im Header
        Circle bigAvatar = createAvatar(currentUser, 45);
        bigAvatar.setStroke(Color.WHITE);
        bigAvatar.setStrokeWidth(3);
        
        customHeader.getChildren().addAll(headerTitle, bigAvatar);
        dialogPane.setHeader(customHeader);

        // --- Formular ---
        VBox content = new VBox(15);
        content.getStyleClass().add("edit-form-container");

        // 1. Name Input
        VBox nameBox = new VBox();
        Label lblName = new Label("DEIN NAME");
        lblName.getStyleClass().add("input-label");
        TextField nameField = new TextField(currentUser);
        nameField.getStyleClass().add("modern-input");
        nameBox.getChildren().addAll(lblName, nameField);

        // 2. Status Auswahl
        VBox statusBox = new VBox();
        Label lblStatus = new Label("STATUS");
        lblStatus.getStyleClass().add("input-label");
        
        ComboBox<UserInfo> statusCombo = new ComboBox<>();
        statusCombo.getItems().setAll(UserInfo.values());
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        statusCombo.getStyleClass().add("modern-combo");
        
        // User aktuellen Status holen (Trick: wir suchen uns selbst in der Kontaktliste oder Service)
        // Einfachheitshalber Default:
        statusCombo.setValue(UserInfo.VERFÜGBAR); 
        
        // 3. Custom Text Input (Versteckt am Anfang)
        TextField customStatusField = new TextField();
        customStatusField.setPromptText("Was machst du gerade?");
        customStatusField.getStyleClass().add("modern-input");
        customStatusField.setVisible(false);
        customStatusField.setManaged(false); // Nimmt keinen Platz weg wenn unsichtbar

        // Logik: Zeige Textfeld nur wenn "CUSTOM" gewählt ist
        statusCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCustom = (newVal == UserInfo.CUSTOM);
            customStatusField.setVisible(isCustom);
            customStatusField.setManaged(isCustom);
            
            // Dialog Größe neu berechnen, da sich Inhalt ändert
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });

        statusBox.getChildren().addAll(lblStatus, statusCombo, customStatusField);
        
        content.getChildren().addAll(nameBox, statusBox);
        dialogPane.setContent(content);

        // Buttons
        ButtonType saveType = new ButtonType("Speichern", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(saveType, cancelType);

        // Ergebnis konvertieren
        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    String newName = nameField.getText().trim();
                    UserInfo newStatus = statusCombo.getValue();
                    String statusText = customStatusField.getText().trim();
                    
                    if (!newName.isEmpty()) {
                        // Update im Service aufrufen (mit 4 Parametern!)
                        this.currentUser = chatService.updateUserProfile(currentUser, newName, newStatus, statusText);
                        return true;
                    }
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            }
            return false;
        });

        dialog.showAndWait().ifPresent(success -> {
            if (success) {
                // UI neu laden um Avatar/Namen im Header zu aktualisieren
                initLayout(); 
            }
        });
    }
}