package CTS.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTests {

    private ConcertGoer userForTesting;
    private final String TEST_PASSWORD = "securePass123";
    private final String TEST_EMAIL = "test@concertgoer.com";

    // Setup method runs before each test to ensure a clean slate
    @BeforeEach
    void setUp() throws Exception {
        // We use a concrete subclass (ConcertGoer) to instantiate the abstract User logic.
        userForTesting = new ConcertGoer(
            101, 
            "Test User", 
            TEST_EMAIL, 
            TEST_PASSWORD
        );
    }
    
    @AfterEach
    void tearDown() throws Exception {
        // No file cleanup needed here, but good practice to keep the method
    }

    // ===============================================
    //  CORE GETTER & CONSTRUCTOR TESTS
    // ===============================================

    @Test
    void testUserConstructorAndGetters() {
        assertNotNull(userForTesting, "User object should not be null after construction.");
        assertEquals(101, userForTesting.getUserId(), "User ID should be correct.");
        assertEquals("Test User", userForTesting.getName(), "Name should be correct.");
        assertEquals(TEST_EMAIL, userForTesting.getEmail(), "Email should be correct.");
        assertEquals("USER", userForTesting.getRole(), "Role should be 'USER' for ConcertGoer.");
    }
    
    // ===============================================
    //  PASSWORD HASHING & CHECK TESTS
    // ===============================================
    
    @Test
    void testPasswordIsHashedOnCreation() {
        // Check that the stored password hash is NOT the plain password
        // SHA-256 hash string is always 64 characters long. SHA-256 is the hashing method we are using.
        assertEquals(64, userForTesting.getPasswordHash().length(), "Password should be hashed (64 chars for SHA-256).");
        assertNotEquals(TEST_PASSWORD, userForTesting.getPasswordHash(), "Stored value must not be the plain text password.");
    }

       
    
    @Test
    void testCheckPasswordMultipleCases() {
        // Case 1: SUCCESS (Correct password)
        // We expect the result to be 'true'
        assertEquals(
            true, 
            userForTesting.checkPassword(TEST_PASSWORD), 
            "Case 1: Correct password should return true."
        );

        // Case 2: FAILURE (Incorrect password)
        // We expect the result to be 'false'
        assertEquals(
            false, 
            userForTesting.checkPassword("wrongPassword"), 
            "Case 2: Incorrect password should return false."
        );

        // Case 3: FAILURE (Null)
        // We expect the result to be 'false' 
        assertEquals(
            false, 
            userForTesting.checkPassword(null), 
            "Case 3: Null password input should return false."
        );
        
        // Case 4: FAILURE (Empty)
        // We expect the result to be 'false'
        assertEquals(
            false, 
            userForTesting.checkPassword(""), 
            "Case 4: Empty string password input should return false."
        );
    }


    // ===============================================
    //  CSV SERIALIZATION TESTS
    // ===============================================

    @Test
    void testToCsvRow_NoEscapingNeeded() {
        // Expected format: ID,Name,Email,Hash,Role
        String expectedStart = "101,Test User,test@concertgoer.com,";
        String csvRow = userForTesting.toCsvRow();
        
        // Check that the row starts correctly and ends with the role
        assertTrue(csvRow.startsWith(expectedStart), "CSV row format should start correctly.");
        assertTrue(csvRow.endsWith(",USER"), "CSV row should end with the correct role.");
     
        String[] parts = csvRow.split(",");
        assertEquals(5, parts.length, "CSV row should have 5 parts.");
        assertEquals(64, parts[3].length(), "Password hash part should be 64 characters long.");
    }

    @Test
    void testToCsvRow_EscapingNeeded() {
        // Test a user with a name containing commas and backslashes
        User complexUser = new ConcertGoer(102, "O'Malley, The III\\rd", "complex@email.com", "pass");// Hash is tested via checkPassword, so using short text is fine"USER"
        
        String csvRow = complexUser.toCsvRow();
        
        assertTrue(csvRow.contains("O'Malley\\, The III\\\\rd"), "CSV should escape commas and backslashes in the name field.");
    }
}