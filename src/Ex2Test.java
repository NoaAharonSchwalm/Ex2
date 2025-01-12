import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Ex2Test {

    @Test
    void isNumber() {
        // Valid numbers
        assertTrue(SCell.isNumber("123")); // Integer
        assertTrue(SCell.isNumber("123.45")); // Floating-point
        assertTrue(SCell.isNumber("-123")); // Negative integer
        assertTrue(SCell.isNumber("-123.45")); // Negative floating-point

        // Invalid numbers
        assertFalse(SCell.isNumber("123abc")); // Contains letters
        assertFalse(SCell.isNumber("")); // Empty string
        assertFalse(SCell.isNumber(null)); // Null
        assertFalse(SCell.isNumber(" ")); // Only whitespace
        assertFalse(SCell.isNumber("1.2.3")); // Invalid format
    }

    @Test
    void isForm() {
        // Valid formula
        assertTrue(SCell.isForm("=1+2")); // Simple formula
        assertTrue(SCell.isForm("=A1+3")); // Formula with cell reference
        assertTrue(SCell.isForm("=A1*B2")); // Formula with multiple cell references
        assertTrue(SCell.isForm("=1*(2+3)")); // Nested parentheses

//         Invalid formulas
        assertFalse(SCell.isForm(null)); // Null formula
        assertFalse(SCell.isForm("")); // Empty string
        assertFalse(SCell.isForm("1+2")); // Missing '=' at the start
//        assertFalse(SCell.isForm("=")); // Only '='
        assertFalse(SCell.isForm("=+")); // Operator without operands
        assertFalse(SCell.isForm("=1++2")); // Consecutive operators
        assertFalse(SCell.isForm("=1+(2*3")); // Unmatched parentheses
        assertFalse(SCell.isForm("=1)*2")); // Unmatched parentheses
        assertFalse(SCell.isForm("=A1+A2B3")); // Invalid cell reference
        assertFalse(SCell.isForm("=A1+*B2")); // Invalid operator sequence
        assertFalse(SCell.isForm("=A1B2")); // Missing operator between references
    }

    @Test
    void isText() {
        SCell cell = new SCell("");

        // Valid text inputs
        assertTrue(cell.isText("hello"));         // Simple word
        assertTrue(cell.isText("123abc"));       // Mixed digits and letters
        assertTrue(cell.isText("{data}"));       // Special characters
        assertTrue(cell.isText(""));             // Empty string (considered text)

        // Invalid text inputs (numbers)
//        assertFalse(cell.isText("123"));         // Pure number
//        assertFalse(cell.isText("-456.78"));     // Negative decimal number
//        assertFalse(cell.isText("+0.99"));       // Positive decimal number
//
//        // Invalid text inputs (formulas)
//        assertFalse(cell.isText("=A1"));         // Cell reference
//        assertFalse(cell.isText("=1+2"));        // Simple formula
//        assertFalse(cell.isText("=(1+2)*3"));    // Formula with parentheses
//        assertFalse(cell.isText("=A1+B2"));      // Formula with cell references
//
//        // Edge cases
//        assertTrue(cell.isText("123abc="));      // Ends with '=' but not a valid formula
//        assertTrue(cell.isText("=abc"));         // Invalid formula (no valid structure)
//        assertFalse(cell.isText("=123"));        // Formula with a number
    }

        @Test
        void computeForm() {
            // Basic arithmetic
            assertEquals(5.0, SCell.computeForm("=5"), 0.001);
            assertEquals(3.0, SCell.computeForm("=1+2"), 0.001);
            assertEquals(5.0, SCell.computeForm("=1+2*2"), 0.001);
            assertEquals(3.0, SCell.computeForm("=(1+2)"), 0.001);
            assertEquals(6.0, SCell.computeForm("=(1+2)*2"), 0.001);
            assertEquals(2.5, SCell.computeForm("=5/2"), 0.001);
            assertEquals(-2, SCell.computeForm("=3-5"), 0.001);
            assertEquals(0.5, SCell.computeForm("=0.5"));

            // Expressions with parentheses
            assertEquals(15.0, SCell.computeForm("=(2+3)*3/1"));
            assertEquals(6.0, SCell.computeForm("=(1+2)+(3*2)/2"));
            assertEquals(10.0, SCell.computeForm("=(3+2)*2"));

            // Nested parentheses
            assertEquals(14.0, SCell.computeForm("=((2+3)*2)+4"));
            assertEquals(18.0, SCell.computeForm("=(2*(3+4))+4"));
            assertEquals(4.0, SCell.computeForm("=(2*((3+1)/2))"));

            // Invalid inputs
            assertNull(SCell.computeForm(null));          // Null input
            assertNull(SCell.computeForm(""));            // Empty input
            assertNull(SCell.computeForm("2+3"));         // Missing "="
            assertNull(SCell.computeForm("=(2+)"));       // Incomplete expression
            assertNull(SCell.computeForm("=(2+3"));       // Unmatched parentheses
            assertNull(SCell.computeForm("=2*/3"));       // Invalid operator placement

            // Division by zero
            assertNull(SCell.computeForm("=5/0"));
            assertNull(SCell.computeForm("=(10/(5-5))"));

            // Test removing outer parentheses
            assertNull( SCell.computeForm("(2+3)"));
            assertNull( SCell.computeForm("(2+(3*4))"));
            assertNull( SCell.computeForm("(2+3)*4")); // No removal if not fully enclosed



        }

@Test
void isValidIndex() {
    // Valid indices
    CellEntry cell1 = new CellEntry("b3");
    assertTrue(cell1.isValid(), "b3 should be a valid index");

    CellEntry cell2 = new CellEntry("A10");
    assertTrue(cell2.isValid(), "A10 should be a valid index");

    CellEntry cell3 = new CellEntry("Z99");
    assertTrue(cell3.isValid(), "Z99 should be a valid index");

    // Invalid indices
    CellEntry cell4 = new CellEntry("B");
    assertFalse(cell4.isValid(), "B should be a valid index");

    CellEntry cell5 = new CellEntry("123");
    assertFalse(cell5.isValid(), "123 should be a valid index");

    CellEntry cell6 = new CellEntry("AB10");
    assertFalse(cell6.isValid(), "AB10 should be a valid index");

    CellEntry cell7 = new CellEntry("A100");
    assertFalse(cell7.isValid(), "A100 should be a valid index(only one or two digits allowed)");

    CellEntry cell8 = new CellEntry(" ");
    assertFalse(cell8.isValid(), "Empty cell should be a valid index");

    CellEntry cell9 = new CellEntry("");
    assertFalse(cell9.isValid(), "Empty cell should be a valid index");

}
}