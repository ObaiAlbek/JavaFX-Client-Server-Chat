package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Der Chat-Server verwaltet die Netzwerkverbindungen und Nachrichtenverteilung.
 * Empfängt Nachrichten von Clients und verteilt sie an alle verbundenen Clients.
 */
public class ChatServer {
    private static List<PrintWriter> clientWriters = new ArrayList<>(); // Liste aller Client-OutputStreams
    
    /**
     * Startet den Chat-Server auf Port 12345.
     * Akzeptiert eingehende Client-Verbindungen und verwaltet sie in separaten Threads.
     */
    public static void startServer() {
        System.out.println("Starte Chat-Server auf Port 12345...");
        
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server läuft und wartet auf Verbindungen...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Wartet auf Client-Verbindung
                System.out.println("Neue Client-Verbindung akzeptiert");
                new Thread(new ClientHandler(clientSocket)).start(); // Startet Client-Handler
            }
        } catch (IOException e) {
            System.out.println("Server Fehler: " + e.getMessage());
        }
    }
    
    /**
     * Behandelt die Kommunikation mit einem einzelnen Client.
     * Verwaltet den Nachrichtenaustausch und die Verbindung.
     */
    private static class ClientHandler implements Runnable {
        private Socket socket;      // Client-Socket
        private PrintWriter out;    // OutputStream zum Client
        private String username;    // Benutzername des Clients
        
        /**
         * Konstruktor für den Client-Handler.
         * 
         * @param socket Die Socket-Verbindung zum Client
         */
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        /**
         * Hauptmethode des Client-Handlers.
         * Verarbeitet eingehende Nachrichten und verwaltet die Verbindung.
         */
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                
                // Erste Nachricht ist der Benutzername
                username = in.readLine();
                
                clientWriters.add(out); // Fügt Client zur Broadcast-Liste hinzu
                broadcast(username + " hat den Chat betreten"); // Begrüßungsnachricht
                
                // Empfängt und verteilt Nachrichten
                String message;
                while ((message = in.readLine()) != null) {
                    broadcast(username + ": " + message); // Verteilt Nachricht an alle
                }
            } catch (IOException e) {
            } finally {
                // Aufräumarbeiten bei Verbindungsabbruch
                clientWriters.remove(out);
                broadcast(username +  "hat den Chat verlassen");
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
    
    /**
     * Sendet eine Nachricht an alle verbundenen Clients.
     * 
     * @param message Die zu sendende Nachricht
     */
    private static void broadcast(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message); // Sendet Nachricht an jeden Client
        }
    }
}