package test;

import static org.junit.Assert.*;
import org.junit.Test;

import domain.*;

public class GruppenRoomTest {
    
    @Test
    public void testGruppenRoomCreation() {
        User creator = new User("Creator");
        GruppenRoom group = new GruppenRoom(creator, "TestGroup", "Description");
        
        assertEquals("TestGroup", group.getName());
        assertEquals("Description", group.getDescription());
        assertEquals(creator, group.getCreator());
        assertTrue(group.getParticipants().contains(creator));
    }
    
    @Test
    public void testAddParticipant() {
        User creator = new User("Creator");
        User newUser = new User("NewUser");
        GruppenRoom group = new GruppenRoom(creator, "TestGroup", "Description");
        
        assertTrue(group.addParticipant(newUser));
        assertTrue(group.getParticipants().contains(newUser));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateParticipant() {
        User creator = new User("Creator");
        GruppenRoom group = new GruppenRoom(creator, "TestGroup", "Description");
        group.addParticipant(creator); // Should throw exception
    }
    
    @Test
    public void testAddAdmin() {
        User creator = new User("Creator");
        User admin = new User("Admin");
        GruppenRoom group = new GruppenRoom(creator, "TestGroup", "Description");
        group.addParticipant(admin);
        
        assertTrue(group.addAdmin(creator, admin));
        assertTrue(group.getAdmins().contains(admin));
    }
}