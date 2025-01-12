#Ex2Sheet - Spreadsheet Implementation
This project implements a lightweight and functional spreadsheet system in Java. The Ex2Sheet class allows users to manage a two-dimensional grid of cells, supporting formulas, dependency tracking, and file operations. This implementation was designed to practice advanced Java programming concepts, such as dependency resolution, dynamic evaluation, and efficient file handling.
Key Features
1.	Spreadsheet Basics
Dynamic Cell Management: Each cell in the spreadsheet is represented by an SCell, which stores either plain text or a formula.
Flexible Grid Dimensions: The spreadsheet can be initialized with custom dimensions or default sizes from Ex2Utils.
Error Handling: Handles invalid inputs, such as circular dependencies, with error propagation to the affected cells.
2.	Formula Evaluation
Reference Handling: Formulas can reference other cells using spreadsheet-style notation (e.g., =A1 + B2).
Dependency Management: Tracks and computes dependency depth to prevent invalid evaluations.
Mathematical Calculations: Uses the JavaScript engine to dynamically evaluate mathematical expressions.
3.	File Operations
Save to File: Exports non-empty cells to a structured CSV format.
Load from File: Reads and populates the spreadsheet from a saved file.
4.	Error Detection
Circular Dependencies: Identifies cells involved in cyclic references and marks them with a special depth value of -1.
Invalid Formulas: Flags cells with invalid or non-evaluable formulas as "ERROR".
Implemented Methods
Constructors
-Ex2Sheet(int x, int y): Initializes the spreadsheet with x columns and y rows.
-Ex2Sheet(): Creates a spreadsheet with default dimensions (Ex2Utils.WIDTH and Ex2Utils.HEIGHT).
Core Methods
Cell Operations
-String value(int x, int y): Returns the content or evaluated value of the cell at (x, y).
-void set(int x, int y, String s): Sets the value of the cell at (x, y) to s. Automatically re-evaluates the entire spreadsheet.
-Cell get(int x, int y): Retrieves the Cell object at the specified coordinates (x, y).
-Cell get(String cords): Retrieves the Cell object using a string-based coordinate (e.g., "B3").
Evaluation
-void eval(): Evaluates all cells in the spreadsheet, resolving formulas and updating cell values.
-String eval(int x, int y): Evaluates the specific cell at (x, y), substituting references with evaluated values.
Dependency Management
-int[][] depth(): Returns a 2D array indicating the dependency depth of each cell. Circular dependencies are marked as -1.
File I/O
-void save(String fileName): Exports non-empty cells to a CSV file.
-void load(String fileName): Loads and populates the spreadsheet from a saved file.
File Format
The spreadsheet is saved in CSV format with the following structure:
Header: The first line identifies the spreadsheet ("I2CS ArielU: SpreadSheet (Ex2)").
Data: Each subsequent line contains:
Copy code
x-coordinate, y-coordinate, cell value
Example:
yaml
Copy code
I2CS ArielU: SpreadSheet (Ex2)
0,0,10
1,0,=A1 * 2
2,0,=B1 + 5
Limitations
-Column Limits: Supports single-character columns (A to Z).
-Row Limits: Rows are restricted to two-digit indices (1 to 99).
-Circular Dependencies: Detected but not resolved; marked with depth -1.
-Formula Engine: Relies on the JavaScript engine, limiting syntax to JavaScript-compatible expressions.
Learning Outcomes
This project reinforced several programming concepts, including:
-String manipulation and parsing.
-Dynamic evaluation of mathematical expressions.
-Managing and resolving dependencies in complex systems.
-File I/O for structured data storage.
-Error handling in interconnected systems.
