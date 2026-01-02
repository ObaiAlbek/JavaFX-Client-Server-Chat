package test;

import static org.junit.Assert.*;
import org.junit.Test;

import domain.*;

public class UserTest {
    
    @Test
    public void testUserCreation() {
        User user = new User("TestUser");
        assertEquals("TestUser", user.getUsername());
        assertTrue(user.isOnline());
        assertEquals(UserInfo.VERFÜGBAR, user.getUserInfo());
    }
    
    @Test
    public void testUserStatusChanges() {
        User user = new User("TestUser");
        user.setOnline(false);
        assertFalse(user.isOnline());
        
        user.setUserInfo(UserInfo.BESCHÄFTIGT);
        assertEquals(UserInfo.BESCHÄFTIGT, user.getUserInfo());
    }
    
    @Test
    public void testUserContacts() {
        User user1 = new User("User1");
        User user2 = new User("User2");
        
        assertTrue(user1.getUserContacts().addContact(user2));
        assertEquals(1, user1.getUserContacts().showAllContacts().size());
    }
    
    @Test
    public void testUserEquals() {
        User user1 = new User("SameUser");
        User user2 = new User("SameUser");
        
        assertNotEquals(user1, user2); // Different objects
        assertEquals(user1, user1); // Same object
    }
}