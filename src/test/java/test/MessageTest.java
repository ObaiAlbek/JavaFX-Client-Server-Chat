package test;

import static org.junit.Assert.*;
import org.junit.Test;

import domain.*;

public class MessageTest {
    
    @Test
    public void testMessageCreation() {
        User sender = new User("Sender");
        Message message = new Message(sender, "Test message");
        
        assertEquals(sender, message.getSender());
        assertEquals("Test message", message.getContent());
        assertNotNull(message.getTimestamp());
    }
    
    @Test
    public void testMessageToString() {
        User sender = new User("TestUser");
        Message message = new Message(sender, "Hello World");
        
        String result = message.toString();
        assertTrue(result.contains("TestUser"));
        assertTrue(result.contains("Hello World"));
        assertTrue(result.contains(":"));
    }
    
    @Test
    public void testMessageSetters() {
        User sender1 = new User("Sender1");
        User sender2 = new User("Sender2");
        Message message = new Message(sender1, "Original");
        
        message.setSender(sender2);
        message.setContent("Changed");
        
        assertEquals(sender2, message.getSender());
        assertEquals("Changed", message.getContent());
    }
}