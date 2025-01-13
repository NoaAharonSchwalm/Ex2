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
        if (isNumber(line)) {
            this.type = Ex2Utils.NUMBER; // If the value is a valid number, set type to number
        } else if (isForm(line)) {
            this.type = Ex2Utils.FORM; // If the value is a valid formula, set type to formula
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

    // Main method (empty in this case)
    public static void main(String[] args) {
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
            if (i == 0 && (c == '-' || c == '+')) {
                continue; // Allow for a leading '+' or '-' sign
            } else if (c == '.') {
                if (hasDecimalPoint) {
                    return false; // Only one decimal point is allowed
                }
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
            String content = text.substring(1); // Remove the '=' at the beginning
            if (content.isEmpty()) {
                return false; // A valid formula must not be empty
            } else if (content.length() == 1 && "+-*/".indexOf(content.charAt(0)) != -1) {
                return false; // Invalid formula with just an operator
            } else {
                int balance = 0; // Parenthesis balance check
                char prevChar = ' ';
                boolean hasLetterInCurrentSequence = false;
                for (int i = 0; i < content.length(); ++i) {
                    char current = content.charAt(i);
                    if (!Character.isDigit(current) && "+-*/()".indexOf(content.charAt(0)) != -1) {
                        return false; // Invalid formula
                    }
                    if (Character.isLetter(current)) {
                        if (hasLetterInCurrentSequence) {
                            return false; // Multiple letters in a sequence are invalid
                        }
                        if (i > 0 && "+-*/".indexOf(prevChar) == -1) {
                            return false; // Letter must follow an operator or parenthesis
                        }
                        if (i + 1 >= content.length() || !Character.isDigit(content.charAt(i + 1))) {
                            return false; // Letter must be followed by a digit
                        }
                        hasLetterInCurrentSequence = true;
                    } else if ("+-*/".indexOf(current) != -1) {
                        hasLetterInCurrentSequence = false;
                    }
                    if (current == '(') {
                        ++balance; // Increase balance for opening parentheses
                    } else if (current == ')') {
                        --balance; // Decrease balance for closing parentheses
                        if (balance < 0) {
                            return false; // Invalid parenthesis sequence
                        }
                    }
                    if ("+-*/".indexOf(current) != -1 && "+-*/".indexOf(prevChar) != -1) {
                        return false; // Invalid consecutive operators
                    }
                    prevChar = current;
                }
                return balance == 0; // Return true if parentheses are balanced
            }
        } else {
            return false; // Not a formula if it doesn't start with '='
        }
    }

    // Method to check if a string is text (not a number or formula)
    public boolean isText(String text) {
        return !(isNumber(text) && isForm(text)); // A string is text if it's not a number or formula
    }

    // Method to compute a formula (returns null if invalid)
    public static Double computeForm(String form) {
        if (form == null || !form.startsWith("=")) {
            return null; // If it's not a valid formula, return null
        }

        try {
            // Remove the "=" sign and process the expression
            String expression = form.substring(1).trim();
            return evaluateExpression(expression); // Evaluate the expression
        } catch (Exception e) {
            return null; // Return null if evaluation fails
        }
    }

    // Helper method to evaluate a mathematical expression
    private static Double evaluateExpression(String expression) {
        expression = removeOuterParentheses(expression); // Remove outer parentheses if present
        int operatorIndex = findLastOperator(expression); // Find the last operator outside parentheses

        if (operatorIndex == -1) {
            // If no operator is found, try parsing the expression as a number
            try {
                return Double.parseDouble(expression); // Parse number from the expression
            } catch (NumberFormatException e) {
                return null; // Return null if it's not a valid number
            }
        }

        // Split the expression into left and right parts
        String leftPart = expression.substring(0, operatorIndex).trim();
        String rightPart = expression.substring(operatorIndex + 1).trim();
        char operator = expression.charAt(operatorIndex); // The operator

        // Recursively compute the values of the left and right parts
        Double leftValue = evaluateExpression(leftPart);
        Double rightValue = evaluateExpression(rightPart);

        if (leftValue == null || rightValue == null) {
            return null; // Return null if either part is invalid
        }

        // Perform the operation and return the result
        return performOperation(leftValue, rightValue, operator);
    }

    // Perform the operation based on the operator (+, -, *, /)
    private static Double performOperation(Double left, Double right, char operator) {
        switch (operator) {
            case '+':
                return left + right; // Add
            case '-':
                return left - right; // Subtract
            case '*':
                return left * right; // Multiply
            case '/':
                if (right == 0) return null; // Prevent division by zero
                return left / right; // Divide
            default:
                return null; // Invalid operator
        }
    }

    // Helper method to check if a string is a valid cell reference (e.g., "A1")
    private static boolean isCellReference(String text) {
        return text.matches("^[A-Z][0-9]{1,2}$"); // Match the format "A1", "B12", etc.
    }

    // Method to evaluate a cell reference (currently returns a fixed value for demonstration purposes)
    private static Double evaluateCellReference(String cellRef) {
        int col = cellRef.charAt(0) - 'A'; // Convert column letter to index
        int row = Integer.parseInt(cellRef.substring(1)); // Convert row number to index
        return Double.parseDouble("5.0"); // Placeholder value for the cell reference (could be dynamic)
    }

    // Helper method to remove matching outer parentheses from the expression
    private static String removeOuterParentheses(String expression) {
        while (expression.startsWith("(") && expression.endsWith(")")) {
            int balance = 0; // Parentheses balance counter
            boolean isMatching = true;

            // Check if the parentheses are matching
            for (int i = 0; i < expression.length(); i++) {
                if (expression.charAt(i) == '(') balance++;
                if (expression.charAt(i) == ')') balance--;
                if (balance == 0 && i < expression.length() - 1) {
                    isMatching = false; // If parentheses are not matching, stop removing
                    break;
                }
            }

            if (isMatching) {
                expression = expression.substring(1, expression.length() - 1).trim(); // Remove parentheses
            } else {
                break; // Stop if parentheses are mismatched
            }
        }
        return expression; // Return the expression without outer parentheses
    }

    // Helper method to find the last operator outside parentheses
    private static int findLastOperator(String expression) {
        int balance = 0; // Parentheses balance counter
        int addSubIndex = -1; // Index of the last addition/subtraction operator
        int mulDivIndex = -1; // Index of the last multiplication/division operator

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == '(') balance++; // Increase balance for opening parentheses
            else if (ch == ')') balance--; // Decrease balance for closing parentheses
            else if (balance == 0) { // Consider operators outside parentheses only
                if (ch == '+' || ch == '-') addSubIndex = i; // Store addition/subtraction operator index
                else if ((ch == '*' || ch == '/') && addSubIndex == -1) mulDivIndex = i; // Store multiplication/division operator index
            }
        }

        // Return the index of the lowest-priority operator
        return addSubIndex != -1 ? addSubIndex : mulDivIndex;
    }
}






