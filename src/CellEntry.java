//public class CellEntry {
// Add your documentation below:

public class CellEntry  implements Index2D {
    private String index; // The string representation of the index, e.g., "B3"

    // Constructor to initialize the CellEntry with the index string
    public CellEntry(String index) {
        this.index = index.trim(); // Remove leading/trailing spaces for consistency
    }

    // Constructor that initializes a CellEntry using x (column) and y (row) coordinates
    public CellEntry(int xx, int yy) {
        // Verify that the values of xx and yy are within valid ranges
        if (xx < 0 || xx > 25 || yy < 1 || yy > 99) {
            throw new IllegalArgumentException("Invalid coordinates"); // Throw an error if coordinates are out of range
        }

        // Convert the column number (xx) to a letter (A=0, B=1, ..., Z=25)
        char letter = (char) ('A' + xx);
        // Format the index as a string (e.g., "B3" for xx=1, yy=3)
        this.index = letter + Integer.toString(yy);
    }

    // Method to validate the format of the index string (e.g., "B3" or "A10")
    @Override
    public boolean isValid() {
        if (index == null || index.isEmpty()) {
            return false; // Null or empty strings are invalid
        }
        // Use regex to validate the format: one letter followed by one or two digits
        return index.matches("^[A-Za-z][0-9]{1,2}$");
    }

    /**
     * Returns the X coordinate (column number).
     * X is represented as the position of the letter in the alphabet (A=0, B=1, ..., Z=25).
     * If the index is invalid, returns Ex2Utils.ERR.
     *
     * @return the X value as an integer.
     */
    @Override
    public int getX() {
        if (!isValid()) {
            return Ex2Utils.ERR; // Return error constant if the index is invalid
        }

        char letter = Character.toUpperCase(index.charAt(0)); // Get the first character as uppercase
        return letter - 'A'; // Calculate the position in the alphabet (A=0)
    }

    /**
     * Returns the Y coordinate (row number).
     * Y is represented as the number following the letter.
     * If the index is invalid, returns Ex2Utils.ERR.
     *
     * @return the Y value as an integer.
     */
    @Override
    public int getY() {
        if (!isValid()) {
            return Ex2Utils.ERR; // Return error constant if the index is invalid
        }

        String numberPart = index.substring(1); // Extract the part after the first character
        return Integer.parseInt(numberPart); // Convert to integer
    }

    /**
     * Returns the string representation of the cell index in spreadsheet format (e.g., "B3").
     *
     * @return the string representation of the index.
     */
    @Override
    public String toString() {
        return index; // Simply return the index as it is
    }
}

