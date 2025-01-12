//package assignments.ex2;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        eval(); // Evaluate the sheet after initialization
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT); // Default constructor that uses predefined width and height
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL; // Default answer if the cell is empty
        Cell c = get(x, y); // Get the cell at (x, y)
        if (c != null) {
            ans = c.toString(); // If cell exists, return its string representation
        }
        return ans;
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
        Cell c = new SCell(s); // Create a new cell with the given string
        table[x][y] = (SCell) c; // Set the cell at the specified location
        eval(); // Re-evaluate the sheet after setting the new value
    }

    @Override
    public void eval() {
        int[][] dd = depth(); // Calculate the depth of dependencies
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
        boolean ans = xx >= 0 && yy >= 0; // Check location isn't negative
        ans = ans && xx < Ex2Utils.WIDTH && yy < Ex2Utils.HEIGHT; // Check if the location is within bounds
        return ans;
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()]; // Initialize a 2D array to hold depth values
        boolean[][] visited = new boolean[width()][height()]; // Track visited cells during depth calculation

        // Initialize all values in ans to 0
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                ans[i][j] = 0;
            }
        }

        // Scan the entire sheet to calculate dependencies
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (ans[i][j] == 0 && table[i][j] != null && table[i][j].toString().startsWith("=")) {
                    int depth = calculateDepth(i, j, visited, ans); // Calculate depth of the cell
                    if (depth == -1) {
                        // If a circular dependency is found, mark the cell with -1
                        ans[i][j] = -1;
                    }
                }
            }
        }
        return ans; // Return the depth array
    }

    private int calculateDepth(int x, int y, boolean[][] visited, int[][] ans) {
        // If the cell is already visited in the current calculation, it's a circular dependency
        if (visited[x][y]) {
            return -1; // Found a circular dependency
        }

        // If the depth has already been calculated, return the result
        if (ans[x][y] != 0) {
            return ans[x][y];
        }

        // If the cell doesn't contain a formula, return depth 0
        String value = table[x][y].toString();
        if (!value.startsWith("=")) {
            return 0;
        }

        // Mark the cell as visited during this calculation
        visited[x][y] = true;

        int maxDepth = 0;
        String formula = value.substring(1); // Remove the "=" from the formula

        // Iterate through the formula and look for cell references (e.g., A1)
        for (int i = 0; i < formula.length(); i++) {
            if (Character.isLetter(formula.charAt(i))) {
                int j = i + 1;
                while (j < formula.length() && Character.isDigit(formula.charAt(j))) {
                    j++;
                }

                if (j > i + 1) {
                    String cellRef = formula.substring(i, j); // Get the cell reference (e.g., A1)
                    int refX = cellRef.charAt(0) - 'A'; // Convert the letter to a column index
                    int refY = Integer.parseInt(cellRef.substring(1)); // Extract the row number

                    if (isIn(refX, refY)) {
                        int currentDepth = calculateDepth(refX, refY, visited, ans); // Calculate depth of referenced cell
                        if (currentDepth == -1) {
                            // If a circular dependency is detected, return -1
                            ans[x][y] = -1;
                            visited[x][y] = false; // Unmark the visited flag before returning
                            return -1;
                        }
                        maxDepth = Math.max(maxDepth, currentDepth); // Track the maximum depth
                    }
                }
            }
        }

        // Save the final depth result
        visited[x][y] = false;
        ans[x][y] = maxDepth + 1; // Add 1 to the depth for this cell
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
        String ans = null;
        if (get(x, y) != null) {
            ans = get(x, y).toString(); // Get the cell value as a string
        }
        // Add your code here
        // If it's a formula (starts with "=")
        if (ans.startsWith("=")) {
            // Remove the "=" sign from the beginning of the formula
            String formula = ans.substring(1);
            try {
                // Replace cell references (e.g., A1) with their values
                for (int i = 0; i < formula.length(); i++) {
                    if (Character.isLetter(formula.charAt(i))) {
                        int j = i + 1;
                        while (j < formula.length() && Character.isDigit(formula.charAt(j))) {
                            j++;
                        }
                        if (j > i + 1) {
                            String cellRef = formula.substring(i, j); // Get the cell reference
                            Cell referencedCell = get(cellRef);
                            if (referencedCell != null) {
                                String cellValue = eval(cellRef.charAt(0) - 'A', Integer.parseInt(cellRef.substring(1)));
                                formula = formula.replace(cellRef, cellValue); // Replace with cell value
                            }
                        }
                    }
                }
                ans = evaluateFormula(formula); // Evaluate the final formula
            } catch (Exception e) {
                ans = "ERROR"; // Return error if evaluation fails
            }
        }
        return ans;
    }

    private String evaluateFormula(String formula) {
        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            Object result = engine.eval(formula); // Evaluate the formula using JavaScript engine
            return result.toString(); // Return the result as a string
        } catch (Exception e) {
            return "ERROR"; // Return error if evaluation fails
        }
    }
}


