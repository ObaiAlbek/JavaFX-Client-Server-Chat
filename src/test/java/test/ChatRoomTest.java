package test;

import static org.junit.Assert.*;
import org.junit.Test;

import domain.*;

import java.util.List;

public class ChatRoomTest {
    
    @Test
    public void testChatRoomCreation() {
        User user1 = new User("User1");
        User user2 = new User("User2");
        ChatRoom room = new ChatRoom(user1, user2);
        
        assertEquals(user1, room.getUser1());
        assertEquals(user2, room.getUser2());
        assertTrue(room.getMessages().isEmpty());
    }
    
    @Test
    public void testAddMessageToChatRoom() {
        User user1 = new User("User1");
        User user2 = new User("User2");
        ChatRoom room = new ChatRoom(user1, user2);
        
        Message message = new Message(user1, "Hello");
        room.addMessage(message);
        
        assertEquals(1, room.getMessages().size());
        assertEquals(message, room.getMessages().get(0));
    }
    
    @Test
    public void testShowMessages() {
        User user1 = new User("User1");
        User user2 = new User("User2");
        ChatRoom room = new ChatRoom(user1, user2);
        
        room.addMessage(new Message(user1, "Hi"));
        room.addMessage(new Message(user2, "Hello"));
        
        List<String> messages = room.showMessages();
        assertEquals(2, messages.size());
        assertTrue(messages.get(0).contains("Hi"));
        assertTrue(messages.get(1).contains("Hello"));
    }
}