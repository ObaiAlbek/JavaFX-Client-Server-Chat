package test;

import static org.junit.Assert.*;
import org.junit.Test;
import fassade.ChatService;
import domain.*;
import java.util.List;

public class IntegrationTest {
    
    @Test
    public void testCompleteChatWorkflow() {
        ChatService service = new ChatService();
        
        // Create users
        service.createUser("Alice");
        service.createUser("Bob");
        
        // Create chat room
        int roomId = service.createChatRoom("Alice", "Bob");
        
        // Exchange messages
        service.sendMessage(roomId, "Alice", "Hi Bob!");
        service.sendMessage(roomId, "Bob", "Hello Alice!");
        service.sendMessage(roomId, "Alice", "How are you?");
        
        // Verify messages
        List<Message> messages = service.showMessage(roomId);
        assertEquals(3, messages.size());
//        assertTrue(messages.get(0).contains("Hi Bob"));
//        assertTrue(messages.get(1).contains("Hello Alice"));
//        assertTrue(messages.get(2).contains("How are you"));
//        
        // Test contacts
        service.addContact("Bob", "Alice");
        List<User> contacts = service.getUserContacts("Alice");
        assertEquals(1, contacts.size());
     //   assertTrue(contacts.get(0).contains("Bob"));
    }
    
    @Test
    public void testGroupChatWorkflow() {
        ChatService service = new ChatService();
        
        service.createUser("Admin");
        service.createUser("User1");
        service.createUser("User2");
        
        // Create group
        int groupId = service.createGruppenRoom("Admin", "DevTeam", "Development Team");
        
        // Add participants
        service.addParticipantToGroup(groupId, "Admin", "User1");
        service.addParticipantToGroup(groupId, "Admin", "User2");
        
        // Send group messages
        service.sendGroupMessage(groupId, "Admin", "Welcome everyone!");
        service.sendGroupMessage(groupId, "User1", "Thanks for adding me!");
        
        // Verify group messages
        List<String> messages = service.getGroupMessages(groupId);
        assertEquals(2, messages.size());
        assertTrue(messages.get(0).contains("Welcome"));
        assertTrue(messages.get(1).contains("Thanks"));
    }
}