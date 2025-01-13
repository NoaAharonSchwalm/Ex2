//package assignments.ex2;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
// Add your documentation below:

public class Ex2Sheet implements Sheet {
    private SCell[][] table; // 2D array representing the sheet cells
    // Add your code here

    // Constructor to initialize the table with given dimensions
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y]; // Initialize a 2D array of SCells
        for(int i = 0; i < x; i++) {
            for(int j = 0; j < y; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL); // Set each cell to the empty cell value
            }
        }
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT); // Default constructor that uses predefined width and height
    }

    @Override
    public String value(int x, int y) {
        Cell c = get(x, y); // Get the cell at (x, y)
        return c!=null ? c.toString() : Ex2Utils.EMPTY_CELL;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y]; // Return the cell at the given coordinates
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        if (cords != null && cords.length() >= 2) {
            int x = cords.toUpperCase().charAt(0) - 'A'; // Convert the column letter to an index
            int y = Integer.parseInt(cords.substring(1)); // Extract the row number
            if (isIn(x, y)) {
                ans = get(x, y); // Get the cell if coordinates are valid
            }
        }
        return ans;
    }

    @Override
    public int width() {
        return table.length; // Return the width of the table (number of columns)
    }

    @Override
    public int height() {
        return table[0].length; // Return the height of the table (number of rows)
    }

    @Override
    public void set(int x, int y, String s) {
        Cell c;
        if (s.startsWith("=")){
            c = new SCell(s); // Create a new cell with the given string
    } else {
        c = new SCell(s);
    }
        table[x][y] = (SCell) c; // Set the cell at the specified location
       eval(); // Re-evaluate the sheet after setting the new value
    }

    @Override
    public void eval() {
        int[][] depth = depth(); // Calculate the depth of dependencies
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                String val = eval(i, j); // Evaluate each cell
                if (val != null) {
                    table[i][j] = new SCell(val); // Set the evaluated value in the cell
                }
            }
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && yy >= 0 && xx < Ex2Utils.WIDTH && yy < Ex2Utils.HEIGHT;
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()]; // Initialize a 2D array to hold depth values
        boolean [][] inProgress = new boolean[width()][height()]; // Track cells currently being processed (for circular dependency)

        // Initialize all values in ans to 0
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                ans[i][j] = -1;
            }
        }

        // Scan the entire sheet to calculate dependencies
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (ans[i][j] == -1) {
                    calculateDepth(i,j,ans,inProgress);
                }
            }
        }
        return ans; // Return the depth array
    }

    private int calculateDepth(int x, int y,  int[][] ans, boolean[][] inProgress) {
        // If the depth has already been calculated, return the result
        if (!isIn(x,y)){
            return 0;
        }
        Cell cell = get(x,y);
        if (cell == null || cell.getData()== null) {
            return 0;
        }
        if(ans [x][y] != -1) {
            return ans [x][y];
        }
        if (inProgress [x][y]) {
            ans [x][y] = -2;
            return -2;
        }
        if (!cell.getData().startsWith("=")){
            ans [x][y] = 0;
            return 0;
        }
        inProgress [x][y] = true;
        int maxDepth = 0;
        String formula = cell.getData().substring(1);

        for (int i = 0; i < formula.length(); i++) {
            if (Character.isLetter(formula.charAt(i))) {
                StringBuilder cellRef = new StringBuilder();
                cellRef.append(formula.charAt(i));
                int j = i + 1;
                while (j < formula.length() && Character.isDigit(formula.charAt(j))) {
                    cellRef.append(formula.charAt(j));
                    j++;
                }
                if (cellRef.length() > 1) {
                    int refX = Character.toUpperCase(cellRef.charAt(0)) - 'A';
                    int refY = Integer.parseInt(cellRef.substring(1)) - 1;
                    if (isIn(refX, refY)) {
                        int depth = calculateDepth(refX, refY, ans, inProgress);
                        if (depth == -2) { // אם נמצא מעגל
                            ans[x][y] = -2;
                            inProgress[x][y] = false;
                            return -2;
                        }
                        maxDepth = Math.max(maxDepth, depth);
                    }
                }
                i = j - 1;
            }
        }
        inProgress[x][y] = false;
        ans[x][y] = maxDepth + 1;
        return ans[x][y];
    }


    @Override
    public void load(String fileName) throws IOException {
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL); // Initialize all cells as empty
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(","); // Split the line by commas
                if (parts.length >= 3) {
                    int x = Integer.parseInt(parts[0]); // Get the x-coordinate
                    int y = Integer.parseInt(parts[1]); // Get the y-coordinate
                    if (isIn(x, y)) {
                        set(x, y, parts[2]); // Set the value in the cell
                    }
                }
            }
        }
        eval(); // Re-evaluate the sheet after loading data
    }

    @Override
    public void save(String fileName) throws IOException {
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write("I2CS ArielU: SpreadSheet (Ex2)\n");
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    if (table[i][j] != null && !table[i][j].toString().equals(Ex2Utils.EMPTY_CELL)) {
                        fw.write(i + "," + j + "," + table[i][j].toString() + "\n"); // Write non-empty cells to the file
                    }
                }
            }
        }
    }
    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);

        // Check if the cell is empty
        if (cell == null) {
            return Ex2Utils.EMPTY_CELL;  // Return EMPTY_CELL for empty cell
        }

        // Check for circular reference error
        if (cell.getType() == Ex2Utils.ERR_CYCLE_FORM) {
            return Ex2Utils.ERR_CYCLE;  // Return error for circular reference
        }

        // Check for format error in the formula
        if (cell.getType() == Ex2Utils.ERR_FORM_FORMAT) {
            return Ex2Utils.ERR_FORM;  // Return error for invalid formula format
        }

        // Check if the cell contains text
        if (cell.getType() == Ex2Utils.TEXT) {
            return cell.getData();  // Return the text in the cell
        }

        // Check if the cell contains a number
        if (cell.getType() == Ex2Utils.NUMBER) {
            try {
                double value = Double.parseDouble(cell.getData());  // Try parsing the number
                return String.format("%.1f", value);  // Return the number formatted as a string with 1 decimal place
            } catch (NumberFormatException e) {
                cell.setType(Ex2Utils.ERR_FORM_FORMAT);  // If parsing fails, mark as format error
                return Ex2Utils.ERR_FORM;
            }
        }

        // Check if the cell contains a formula
        if (cell.getType() == Ex2Utils.FORM) {
            try {
                // Maintain a set of visited cells to avoid infinite loops
                Set<Cell> visitedCells = new HashSet<>();
                return computeFormulaValue(x, y, cell, visitedCells);  // Compute the value of the formula
            } catch (StackOverflowError e) {
                cell.setType(Ex2Utils.ERR_CYCLE_FORM);  // If stack overflow occurs (infinite recursion), mark as cycle error
                return Ex2Utils.ERR_CYCLE;
            }
        }

        // If no valid type is found, return EMPTY_CELL
        return Ex2Utils.EMPTY_CELL;
    }

    // Method to compute the value of a formula by evaluating its references and expression
    private String computeFormulaValue(int x, int y, Cell cell, Set<Cell> visitedCells) {
        String formula = cell.getData().substring(1);  // Remove the "=" sign
        StringBuilder evaluatedFormula = new StringBuilder();

        for (int i = 0; i < formula.length(); i++) {
            char ch = formula.charAt(i);
            if (Character.isLetter(ch)) {  // If the character is a letter, it might be a cell reference
                int j = i + 1;
                while (j < formula.length() && Character.isDigit(formula.charAt(j))) {
                    j++;  // Find the end of the cell reference
                }
                if (j > i + 1) {
                    String cellRef = formula.substring(i, j);
                    int refX = cellRef.charAt(0) - 'A';  // Convert the column letter to index
                    int refY = Integer.parseInt(cellRef.substring(1)) - 1;  // Convert the row number to index

                    if (!isIn(refX, refY)) {
                        throw new IllegalArgumentException("Invalid cell reference");  // Check if the cell reference is valid
                    }

                    // Prevent infinite loops by checking if the reference has already been visited
                    Cell refCell = get(refX, refY);
                    if (visitedCells.contains(refCell)) {
                        throw new StackOverflowError("Circular reference detected");  // Throw an error if a circular reference is found
                    }
                    visitedCells.add(refCell);  // Mark this cell as visited

                    String cellValue = eval(refX, refY);  // Recursively evaluate the referenced cell
                    evaluatedFormula.append(cellValue);  // Append the evaluated value to the formula
                    i = j - 1;
                }
            } else {
                evaluatedFormula.append(ch);  // If not a letter, append the character to the formula as is
            }
        }

        try {
            return String.format("%.1f", Double.parseDouble(evaluatedFormula.toString()));  // Parse the final evaluated formula as a number and return it
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid expression");  // Handle invalid expression format
        }
    }

    // Method to compute a formula (returns null if invalid)
    public static Double computeForm(String form) {
        if (form == null || !form.startsWith("=")) {
            return null;  // If it's not a valid formula, return null
        }

        // Check for spaces in the formula, which are invalid
        String content = form.substring(1).trim();
        if (content.contains(" ")) {
            return null;  // Invalid formula if there are spaces
        }

        try {
            // Remove the "=" sign and process the expression
            return evaluateExpression(content);  // Evaluate the expression
        } catch (Exception e) {
            return null;  // Return null if evaluation fails
        }
    }

    // Helper method to evaluate a mathematical expression
    private static Double evaluateExpression(String expression) {
        expression = removeOuterParentheses(expression);  // Remove outer parentheses if present
        int operatorIndex = findLastOperator(expression);  // Find the last operator outside parentheses

        if (operatorIndex == -1) {
            // If no operator is found, try parsing the expression as a number
            try {
                return Double.parseDouble(expression);  // Parse number from the expression
            } catch (NumberFormatException e) {
                return null;  // Return null if it's not a valid number
            }
        }

        // Split the expression into left and right parts
        String leftPart = expression.substring(0, operatorIndex).trim();
        String rightPart = expression.substring(operatorIndex + 1).trim();
        char operator = expression.charAt(operatorIndex);  // The operator

        // Recursively compute the values of the left and right parts
        Double leftValue = evaluateExpression(leftPart);
        Double rightValue = evaluateExpression(rightPart);

        if (leftValue == null || rightValue == null) {
            return null;  // Return null if either part is invalid
        }

        // Perform the operation and return the result
        return performOperation(leftValue, rightValue, operator);
    }

    // Perform the operation based on the operator (+, -, *, /)
    private static Double performOperation(Double left, Double right, char operator) {
        switch (operator) {
            case '+':
                return left + right;  // Add
            case '-':
                return left - right;  // Subtract
            case '*':
                return left * right;  // Multiply
            case '/':
                if (right == 0) return null;  // Prevent division by zero
                return left / right;  // Divide
            default:
                return null;  // Invalid operator
        }
    }

    // Helper method to check if a string is a valid cell reference (e.g., "A1")
    private static boolean isCellReference(String text) {
        return text.matches("^[A-Z][0-9]{1,2}$");  // Match the format "A1", "B12", etc.
    }

    // Method to evaluate a cell reference (currently returns a fixed value for demonstration purposes)
    private static Double evaluateCellReference(String cellRef) {
        int col = cellRef.charAt(0) - 'A';  // Convert column letter to index
        int row = Integer.parseInt(cellRef.substring(1));  // Convert row number to index
        return Double.parseDouble("5.0");  // Placeholder value for the cell reference (could be dynamic)
    }

    // Helper method to remove matching outer parentheses from the expression
    private static String removeOuterParentheses(String expression) {
        while (expression.startsWith("(") && expression.endsWith(")")) {
            int balance = 0;  // Parentheses balance counter
            boolean isMatching = true;

            // Check if the parentheses are matching
            for (int i = 0; i < expression.length(); i++) {
                if (expression.charAt(i) == '(') balance++;
                if (expression.charAt(i) == ')') balance--;
                if (balance == 0 && i < expression.length() - 1) {
                    isMatching = false;  // If parentheses are not matching, stop removing
                    break;
                }
            }

            if (isMatching) {
                expression = expression.substring(1, expression.length() - 1).trim();  // Remove parentheses
            } else {
                break;  // Stop if parentheses are mismatched
            }
        }
        return expression;  // Return the expression without outer parentheses
    }

    // Helper method to find the last operator outside parentheses
    private static int findLastOperator(String expression) {
        int balance = 0;  // Parentheses balance counter
        int addSubIndex = -1;  // Index of the last addition/subtraction operator
        int mulDivIndex = -1;  // Index of the last multiplication/division operator

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == '(') balance++;  // Increase balance for opening parentheses
            else if (ch == ')') balance--;  // Decrease balance for closing parentheses
            else if (balance == 0) {  // Consider operators outside parentheses only
                if (ch == '+' || ch == '-') addSubIndex = i;  // Store addition/subtraction operator index
                else if ((ch == '*' || ch == '/') && addSubIndex == -1) mulDivIndex = i;  // Store multiplication/division operator index
            }
        }

        // Return the index of the lowest-priority operator
        return addSubIndex != -1 ? addSubIndex : mulDivIndex;
    }
}


