package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import domain.*;
import fassade.ChatService;

import java.util.List;

public class ChatServiceTest {
    
    private ChatService service;
    
    @Before
    public void setUp() {
        service = new ChatService();
    }
    
    @Test
    public void testCreateUser() {
        assertTrue(service.createUser("TestUser"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateDuplicateUser() {
        service.createUser("TestUser");
        service.createUser("TestUser"); // Should throw exception
    }
    
    @Test
    public void testCreateChatRoom() {
        service.createUser("User1");
        service.createUser("User2");
        
        int roomId = service.createChatRoom("User1", "User2");
        assertTrue(roomId > 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateChatRoomNonExistentUsers() {
        service.createChatRoom("NonExistent1", "NonExistent2");
    }
    
    @Test
    public void testSendAndReceiveMessages() {
        service.createUser("User1");
        service.createUser("User2");
        int roomId = service.createChatRoom("User1", "User2");
        
        service.sendMessage(roomId, "User1", "Hello");
        service.sendMessage(roomId, "User2", "Hi there");
        
        List<Message> messages = service.showMessage(roomId);
        assertEquals(2, messages.size());
       // assertTrue(messages.get(0).contains("Hello"));
        //assertTrue(messages.get(1).contains("Hi there"));
    }
    
    @Test
    public void testAddContact() {
        service.createUser("User1");
        service.createUser("User2");
        
        assertTrue(service.addContact("User2", "User1"));
        List<User> contacts = service.getUserContacts("User1");
        assertEquals(1, contacts.size());
      //  assertTrue(contacts.get(0).contains("User2"));
    }
}