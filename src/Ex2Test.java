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
       assertFalse(SCell.isForm("=")); // Only '='
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
        assertTrue(cell.isText("123"));
    }

        @Test
        void computeForm() {
            // Basic arithmetic
            assertEquals(5.0, Ex2Sheet.computeForm("=5"), 0.001);
            assertEquals(3.0, Ex2Sheet.computeForm("=1+2"), 0.001);
            assertEquals(5.0,   Ex2Sheet.computeForm("=1+2*2"), 0.001);
            assertEquals(3.0, Ex2Sheet.computeForm("=(1+2)"), 0.001);
            assertEquals(6.0, Ex2Sheet.computeForm("=(1+2)*2"), 0.001);
            assertEquals(5.5, Ex2Sheet.computeForm("=3+5/2"), 0.001);
            assertEquals(-2, Ex2Sheet.computeForm("=3-5"), 0.001);
            assertEquals(0.5, Ex2Sheet.computeForm("=0.5"));

            // Expressions with parentheses
            assertEquals(15.0, Ex2Sheet.computeForm("=(2+3)*3/1"));
            assertEquals(6.0, Ex2Sheet.computeForm("=(1+2)+(3*2)/2"));
            assertEquals(10.0, Ex2Sheet.computeForm("=(3+2)*2"));

            // Nested parentheses
            assertEquals(14.0, Ex2Sheet.computeForm("=((2+3)*2)+4"));
            assertEquals(18.0, Ex2Sheet.computeForm("=(2*(3+4))+4"));
            assertEquals(4.0, Ex2Sheet.computeForm("=(2*((3+1)/2))"));

            // Invalid inputs
            assertNull(Ex2Sheet.computeForm(null));          // Null input
            assertNull(Ex2Sheet.computeForm(""));            // Empty input
            assertNull(Ex2Sheet.computeForm("2+3"));         // Missing "="
            assertNull(Ex2Sheet.computeForm("=(2+)"));       // Incomplete expression
            assertNull(Ex2Sheet.computeForm("=(2+3"));       // Unmatched parentheses
            assertNull(Ex2Sheet.computeForm("=2*/3"));       // Invalid operator placement

            // Division by zero
            assertNull(Ex2Sheet.computeForm("=5/0"));
            assertNull(Ex2Sheet.computeForm("=(10/(5-5))"));

            // Test removing outer parentheses
            assertNull( Ex2Sheet.computeForm("(2+3)"));
            assertNull( Ex2Sheet.computeForm("(2+(3*4))"));
            assertNull( Ex2Sheet.computeForm("(2+3)*4")); // No removal if not fully enclosed
       }

    @Test
    void testEvalEmptyCell() {
        Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "");
        String result = sheet.eval(0, 0);
        assertEquals(Ex2Utils.EMPTY_CELL, result);
    }

    @Test
    void testEvalTextCell() {
        Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "Hello");
        String result = sheet.eval(0, 0);
        assertEquals("Hello", result);
    }

    @Test
    void testEvalNumberCell() {
        Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "10");
        String result = sheet.eval(0, 0);
        assertEquals("10.0", result);
    }


////depth
    @Test
    void testDepthBasicFormula() {
        Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "=1");  // A1 = 1
        int[][] depths = sheet.depth();
//        assertEquals(1, depths[0][0]);  // נוסחה פשוטה, עומק 1
    }


    @Test
    void testIndependentCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "42");        //  Simple cell with no dependency
        int[][] depth = sheet.depth();
        assertEquals(0, depth[0][0]); // The depth of a cell without a formula is always 0
    }

    @Test
    void testEmptySheet() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5); //  Empty sheet
        int[][] depth = sheet.depth();
        for (int i = 0; i < depth.length; i++) {
            for (int j = 0; j < depth[i].length; j++) {
                assertEquals(0, depth[i][j]); //  All cells are empty -> depth is 0
            }
        }
    }

//    @Test
//    void testDepthWithSimpleDependency() {
//        Ex2Sheet sheet = new Ex2Sheet(3, 3);
//
//        // Set some formulas
//        sheet.set(0, 0, "=A1");  // A1 depends on A1, should be 1
//        sheet.set(1, 1, "=A1");  // B1 depends on A1
//        sheet.eval(); // Make sure to evaluate the formulas
//
//        int[][] depth = sheet.depth(); // Calculate depth
//
//        // Verify that the depth is 1 for A1 (direct formula), and 2 for B1
////        assertEquals(-1, depth[0][0], "Depth of A1 should be 1");
////        assertEquals(-1, depth[1][1], "Depth of B1 should be 2, since it depends on A1");
//    }

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