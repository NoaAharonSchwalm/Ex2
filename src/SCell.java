//package assignments.Ex2;
// Add your documentation below:

public class SCell implements Cell {
    private String line;  // Holds the content of the cell (text, number, or formula)
    private int type;     // Type of the content (number, formula, or text)
    private int order;    // Computational order for resolving formulas

    // Constructor initializes the cell with the provided string and determines its type
    public SCell(String s) {
        setData(s); // Initialize the data and determine the type
    }

    // Returns the computational order of the cell (0 for numbers and text, otherwise the order value)
    @Override
    public int getOrder() {
        return (type == Ex2Utils.NUMBER || type == Ex2Utils.TEXT) ? 0 : order; // Numbers and text always have order 0
    }

    // Returns the raw data of the cell as a string (used when printing the cell)
    @Override
    public String toString() {
        return getData(); // Return the raw data as a string
    }

    // Sets the data of the cell and determines its type based on the content
    @Override
    public void setData(String s) {
        if (s == null || s.isEmpty()) {
            this.line = ""; // Empty string represents an empty cell
            this.type = Ex2Utils.TEXT; // Default type is text
            return;
        }
        this.line = s.trim(); // Trim any leading/trailing spaces
        if (isForm(line)) {
            this.type = Ex2Utils.FORM; // If the value is a valid number, set type to number
        } else if (isNumber(line)) {
            this.type = Ex2Utils.NUMBER; // If the value is a valid formula, set type to formula
        } else {
            this.type = Ex2Utils.TEXT; // Otherwise, treat it as text
        }
    }

    // Returns the raw data of the cell (text, number, or formula)
    @Override
    public String getData() {
        return line; // Return the raw data
    }

    // Returns the type of the cell (number, formula, or text)
    @Override
    public int getType() {
        return type; // Return the type of the cell
    }

    // Sets the type of the cell
    @Override
    public void setType(int t) {
        this.type = t; // Set the type of the cell
    }

    // Sets the computational order of the cell
    @Override
    public void setOrder(int t) {
        this.order = t; // Set the computational order of the cell
    }
    private void computeType() {
        int t_type = Ex2Utils.ERR;
        if (line.startsWith("=")) {
            if (isForm(line)) {
                t_type = Ex2Utils.FORM;
            } else {
                t_type = Ex2Utils.ERR_FORM_FORMAT;
            }
        }
        if (isNumber(line)) {
            type = Ex2Utils.NUMBER;
        } else if(isText(line)){
            type = Ex2Utils.TEXT;
        } else {
            type = Ex2Utils.ERR;
        }
        setType(t_type);
        }

        // Method to check if a string is a valid number
    public static boolean isNumber(String text) {
        if (text == null || text.isEmpty()) {
            return false; // Null or empty strings are not valid numbers
        }
        boolean hasDecimalPoint = false;
        boolean hasDigits = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (i == 0 && (c == '-' || c == '+')) continue;
            if (c == '.') {
                if (hasDecimalPoint) return false;
                hasDecimalPoint = true;
            } else if (Character.isDigit(c)) {
                hasDigits = true; // The character is a digit
            } else {
                return false; // Invalid character
            }
        }
        return hasDigits; // Must contain at least one digit
    }
    // Method to check if a string is a valid formula (must start with '=')
    public static boolean isForm(String text) {
        if (text != null && !text.isEmpty() && text.charAt(0) == '=') {
                return false; // A valid formula must not be empty
            }
        String content = text.substring(1); // הסר את סימן ה-=
        if (content.isEmpty()) {
            return false;
        }
        int balance = 0;
        char prevChar = ' ';
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            // בדיקת סוגריים
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
                if (balance < 0) return false;
            }
            // בדיקת תווים חוקיים
            else if (!Character.isLetterOrDigit(c) && "+-*/".indexOf(c) == -1) {
                return false;
            }
            // לא מאפשר אופרטורים רצופים
            if ("+-*/".indexOf(c) != -1 && "+-*/".indexOf(prevChar) != -1) {
                return false;
            }
            prevChar = c;
        }

        return balance == 0; // וודא שכל הסוגריים סגורים
    }

    // Method to check if a string is text (not a number or formula)
    public boolean isText(String text) {
        return !(isNumber(text) && isForm(text)); // A string is text if it's not a number or formula
    }
}






