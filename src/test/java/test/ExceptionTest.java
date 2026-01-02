package test;

import org.junit.Test;
import org.junit.Before;
import fassade.ChatService;

public class ExceptionTest {
    
    private ChatService service;
    
    @Before
    public void setUp() {
        service = new ChatService();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSendMessageToNonExistentRoom() {
        service.sendMessage(9999, "User1", "Test");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSendMessageNonExistentUser() {
        service.createUser("User1");
        service.createUser("User2");
        int roomId = service.createChatRoom("User1", "User2");
        service.sendMessage(roomId, "NonExistent", "Test");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddNonExistentContact() {
        service.createUser("User1");
        service.addContact("NonExistent", "User1");
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testGetUserContactsNonExistentUser() {
        service.getUserContacts("NonExistent");
    }
}