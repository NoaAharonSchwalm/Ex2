import org.junit.jupiter.api.Test;
//0
import static org.junit.jupiter.api.Assertions.*;

class SCellTest {

    @Test
    void testisNumber() {
        // Valid numbers
        assertTrue(SCell.isNumber("123")); // Integer
        assertTrue(SCell.isNumber("123.45")); // Floating-point
        assertTrue(SCell.isNumber("-123")); // Negative integer
        assertTrue(SCell.isNumber("-123.45")); // Negative floating-point
        assertTrue(SCell.isNumber("123")); // Number with spaces

        // Invalid numbers
        assertFalse(SCell.isNumber("123abc")); // Contains letters
        assertFalse(SCell.isNumber("")); // Empty string
        assertFalse(SCell.isNumber(null)); // Null
        assertFalse(SCell.isNumber(" ")); // Only whitespace
        assertFalse(SCell.isNumber("1.2.3")); // Invalid format

    }

    @Test
    void testisForm() {
        // Valid formula
       assertTrue(SCell.isForm("=1+2")); // Simple formula
        assertTrue(SCell.isForm("=A1+3")); // Formula with cell reference
        assertTrue(SCell.isForm("=(1+2)*3")); // Formula with parentheses
        assertTrue(SCell.isForm("=A1*B2")); // Formula with multiple cell references
        assertTrue(SCell.isForm("=1*(2+3)")); // Nested parentheses

//         Invalid formulas
        assertFalse(SCell.isForm(null)); // Null formula
        assertFalse(SCell.isForm("")); // Empty string
        assertFalse(SCell.isForm("1+2")); // Missing '=' at the start
        assertFalse(SCell.isForm("=")); // Only '='
        assertFalse(SCell.isForm("=+")); // Operator without operands
        assertFalse(SCell.isForm("=1++2")); // Consecutive operators
        assertFalse(SCell.isForm("=1+(2*3")); // Unmatched parentheses
        assertFalse(SCell.isForm("=1)*2")); // Unmatched parentheses
        assertFalse(SCell.isForm("=A1+A2B3")); // Invalid cell reference
        assertFalse(SCell.isForm("=A1+*B2")); // Invalid operator sequence
        assertFalse(SCell.isForm("=A1B2")); // Missing operator between references

    }
    }

//    @Test
//    void testisText() {
//    }
//
//    @Test
//    void testcomputeForm() {
//    }
