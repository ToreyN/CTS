package CTS.misc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

class MoneyTests {

    private final String USD = "USD";
    private final String EUR = "EUR";

    // Since Money is a simple value object, setUp and tearDown are not strictly needed
    // @BeforeEach
    // void setUp() throws Exception {}
    // @AfterEach
    // void tearDown() throws Exception {}

    // ===============================================
    //  CONSTRUCTOR AND GETTERS TESTS
    // ===============================================

    @Test
    void testConstructorAndGetters() {
        Money m = new Money(15.75, USD);
        // Use a delta (0.001) for comparing doubles
        assertEquals(15.75, m.getAmount(), 0.001, "Amount should be initialized correctly.");
        assertEquals(USD, m.getCurrency(), "Currency should be initialized correctly.");
    }

    // ===============================================
    //  SERIALIZATION (TO INLINE STRING) TESTS
    // ===============================================

    @Test
    void testToInlineString_Standard() {
        Money m = new Money(50.0, USD);
        assertEquals("50.0:USD", m.toInlineString(), "Standard output format should be correct.");
    }
    
    @Test
    void testToInlineString_Decimal() {
        Money m = new Money(1234.5678, EUR);
        // Tests the output format using Double.toString() default precision
        assertEquals("1234.5678:EUR", m.toInlineString(), "Output should handle decimals correctly.");
    }

    @Test
    void testToInlineString_CleansCurrency() {
        Money m = new Money(10.00, "U,S:D");
        // Currency should be cleaned of ':' and ','
        assertEquals("10.0:USD", m.toInlineString(), "Output should clean currency of separators.");
    }


    // ===============================================
    //  DESERIALIZATION (FROM INLINE STRING) TESTS
    // ===============================================

    @Test
    void testFromInlineString_Standard() {
        Money m = Money.fromInlineString("100.25:JPY");
        assertEquals(100.25, m.getAmount(), 0.001, "Amount should be parsed correctly.");
        assertEquals("JPY", m.getCurrency(), "Currency should be parsed correctly.");
    }
    
    @Test
    void testFromInlineString_MalformedAmount() {
        // Your implementation handles NumberFormatException by setting amount to 0.0
        Money m = Money.fromInlineString("FAIL_AMT:USD");
        assertEquals(0.0, m.getAmount(), 0.001, "Should return 0.0 amount for malformed numeric input.");
        assertEquals("USD", m.getCurrency(), "Currency should still be parsed if available.");
    }
    
    @Test
    void testFromInlineString_OnlyAmount() {
        // Test a string with only the amount (e.g., missing ":CUR")
        Money m = Money.fromInlineString("99.99");
        assertEquals(99.99, m.getAmount(), 0.001, "Should parse amount even without currency.");
        assertEquals("", m.getCurrency(), "Currency should be empty string if missing.");
    }


    // ===============================================
    //  EQUALITY TESTS (Verifies the equals() and hashCode() methods)
    // ===============================================

    @Test
    void testEquality_SameContent() {
        Money m1 = new Money(50.00, USD);
        Money m2 = new Money(50.00, USD);
        
        // This test requires equals() and hashCode() to be correct
        assertEquals(m1, m2, "Two Money objects with the same amount and currency must be equal.");
    }
    
    @Test
    void testEquality_DifferentAmount() {
        Money m1 = new Money(50.00, USD);
        Money m2 = new Money(51.00, USD);
        
        assertNotEquals(m1, m2, "Objects with different amounts should not be equal.");
    }
    
    @Test
    void testEquality_DifferentCurrency() {
        Money m1 = new Money(10.00, USD);
        Money m2 = new Money(10.00, EUR);
        
        assertNotEquals(m1, m2, "Objects with different currencies should not be equal.");
    }
    
    @Test
    void testHashCodeConsistency() {
        Money m1 = new Money(10.50, EUR);
        Money m2 = new Money(10.50, EUR);
        
        // Use a set to verify hashCode() behavior (equal objects must have equal hash codes)
        Set<Money> moneySet = new HashSet<>();
        moneySet.add(m1);
        
        assertTrue(moneySet.contains(m2), "A set containing m1 must contain m2 if they are equal.");
        assertEquals(1, moneySet.size(), "Adding an equal object should not increase set size.");
    }
}