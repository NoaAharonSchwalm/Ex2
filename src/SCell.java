//package assignments.Ex2;
// Add your documentation below:

public class SCell implements Cell {
    private String line;
    private int type;
    private int order;

    public SCell(String s) {
        setData(s);// Initialize the data and determine the type
    }

    @Override
    public int getOrder() {
        // Numbers and text always have order 0
        return (type == Ex2Utils.NUMBER || type == Ex2Utils.TEXT) ? 0 : order;
    }

    //@Override
    @Override
    public String toString() {
        return getData();// Return the raw data as a string
    }

    @Override
    public void setData(String s) {
        if (s == null || s.isEmpty()) {
            this.line = "";
            this.type = Ex2Utils.TEXT; //
            return;
        }
        this.line = s.trim();
        if (isNumber(line)) {
            this.type = Ex2Utils.NUMBER;
        } else if (isForm(line)) {
            this.type = Ex2Utils.FORM;
        } else {
            this.type = Ex2Utils.TEXT;
        }
    }

    @Override
    public String getData() {
        return line;// Return the raw data
    }

    @Override
    public int getType() {
        return type;// Return the type of the cell
    }

    @Override
    public void setType(int t) {
        this.type = t;// Set the type of the cell
    }

    @Override
    public void setOrder(int t) {
        this.order = t; // Set the computational order of the cell
    }


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
                continue;
            } else if (c == '.') {
                if (hasDecimalPoint) {
                    return false;
                }
                hasDecimalPoint = true;
            } else if (Character.isDigit(c)) {
                hasDigits = true;
            } else {
                return false;
            }
        }
        return hasDigits;
    }

    // Method to check if a string is a valid formula
    public static boolean isForm(String text) {
        if (text != null && !text.isEmpty() && text.charAt(0) == '=') {
            String content = text.substring(1);
            if (content.isEmpty()) {
                return false; // A valid formula must start with '='
            } else if (content.length() == 1 && "+-*/".indexOf(content.charAt(0)) != -1) {
                return false;
            } else {
                int balance = 0;
                char prevChar = ' ';
                boolean hasLetterInCurrentSequence = false;
                for (int i = 0; i < content.length(); ++i) {
                    char current = content.charAt(i);
                    if (!Character.isDigit(current) && "+-*/()".indexOf(content.charAt(0)) != -1) {
                        return false;
                    }
                    if (Character.isLetter(current)) {
                        if (hasLetterInCurrentSequence) {
                            return false;
                        }
                        if (i > 0 && "+-*/".indexOf(prevChar) == -1) {
                            return false;
                        }
                        if (i + 1 >= content.length() || !Character.isDigit(content.charAt(i + 1))) {
                            return false;
                        }
                        hasLetterInCurrentSequence = true;
                    } else if ("+-*/".indexOf(current) != -1) {
                        hasLetterInCurrentSequence = false;
                    }
                    if (current == '(') {
                        ++balance;
                    } else if (current == ')') {
                        --balance;
                        if (balance < 0) {
                            return false;
                        }
                    }
                    if ("+-*/".indexOf(current) != -1 && "+-*/".indexOf(prevChar) != -1) {
                        return false;
                    }
                    prevChar = current;
                }
                return balance == 0;
            }
        } else {
            return false;
        }
    }

    // Method to check if a string is text (not a number or formula)
    public boolean isText(String text) {
        return !(isNumber(text) && isForm(text)); // A string is text if it's not a number or formula
    }

    public static Double computeForm(String form) {
        if (form == null || !form.startsWith("=")) {
            return null;
        }

        try {
            // Remove the "=" sign and process the expression
            String expression = form.substring(1).trim();
            return evaluateExpression(expression);
        } catch (Exception e) {
            return null;
        }
    }

    private static Double evaluateExpression(String expression) {
        // Remove outer parentheses for cleaner processing
        expression = removeOuterParentheses(expression);

        // Find the last operator outside parentheses
        int operatorIndex = findLastOperator(expression);

        if (operatorIndex == -1) {
            // If no operator is found, try parsing it as a number
            try {
                return Double.parseDouble(expression);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // Split the expression into left and right parts
        String leftPart = expression.substring(0, operatorIndex).trim();
        String rightPart = expression.substring(operatorIndex + 1).trim();
        char operator = expression.charAt(operatorIndex);

        // Recursively compute the values of the left and right parts
        Double leftValue = evaluateExpression(leftPart);
        Double rightValue = evaluateExpression(rightPart);

        if (leftValue == null || rightValue == null) {
            return null;
        }

        // Perform the operation and return the result
        return performOperation(leftValue, rightValue, operator);
    }

    private static Double performOperation(Double left, Double right, char operator) {
        // Execute the mathematical operation based on the operator
        switch (operator) {
            case '+':
                return left + right;
            case '-':
                return left - right;
            case '*':
                return left * right;
            case '/':
                if (right == 0) return null; // Prevent division by zero
                return left / right;
            default:
                return null;
        }
    }

    private static boolean isCellReference(String text) {
        // Check if the text matches the format of a cell reference (e.g., "A1")
        return text.matches("^[A-Z][0-9]{1,2}$");
    }

//    private static Double evaluateCellReference(String cellRef) {
//        // Convert the cell reference to column and row indices
//        int col = cellRef.charAt(0) - 'A';
//        int row = Integer.parseInt(cellRef.substring(1));
//        return Double.parseDouble("5.0");        // Validate the cell's location in the spreadsheet
////            if (!Sheet.isIn(col, row)) {
////                return null;
////            }
////
////            Cell cell = Sheet.get(col, row);
////            if (cell == null) {
////                return null;
////            }
//
//        // If the cell contains a number, parse and return it
////        if (Ex2Utils.NUMBER==1) {
////            try {
////                return Double.parseDouble(cell.getData());
////            } catch (NumberFormatException e) {
////                return null;
////            }
////        }
////        // If the cell contains a formula, evaluate it recursively
////        else if (Ex2Utils.FORM==1) {
////            // TODO: Add circular dependency check here
////            return computeForm(cell.getData());
////        }
////
////        return null;
//    }

    private static String removeOuterParentheses(String expression) {
        // Remove matching outer parentheses if they fully enclose the expression
        while (expression.startsWith("(") && expression.endsWith(")")) {
            // Check if the outer parentheses are balanced
            int balance = 0;
            boolean isMatching = true;

            for (int i = 0; i < expression.length(); i++) {
                if (expression.charAt(i) == '(') balance++;
                if (expression.charAt(i) == ')') balance--;
                if (balance == 0 && i < expression.length() - 1) {
                    isMatching = false;
                    break;
                }
            }

            if (isMatching) {
                expression = expression.substring(1, expression.length() - 1).trim();
            } else {
                break;
            }
        }
        return expression;
    }

    private static int findLastOperator(String expression) {
        // Identify the last operator outside parentheses, prioritizing + or -
        int balance = 0;
        int addSubIndex = -1;
        int mulDivIndex = -1;

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == '(') balance++;
            else if (ch == ')') balance--;
            else if (balance == 0) { // Consider operators outside parentheses only
                if (ch == '+' || ch == '-') addSubIndex = i;
                else if ((ch == '*' || ch == '/') && addSubIndex == -1) mulDivIndex = i;
            }
        }

        // Return the index of the lowest-priority operator
        return addSubIndex != -1 ? addSubIndex : mulDivIndex;
    }
}





