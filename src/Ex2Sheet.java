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
    private SCell[][] table;
    // Add your code here

    // Constructor to initialize the table with given dimensions
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for(int i=0;i<x;i=i+1) {
            for(int j=0;j<y;j=j+1) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        eval();
    }
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        Cell c = get(x,y);
        if(c!=null) {
            ans = c.toString();}
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        if (cords!=null && cords.length()>= 2) {
            int x= cords.toUpperCase().charAt(0) - 'A';
            int y = Integer.parseInt(cords.substring(1));
            if (isIn(x, y)) {
                ans = get(x,y);
            }
        }
        return ans;
    }

    @Override
    public int width() {
        return table.length;
    }
    @Override
    public int height() {
        return table[0].length;
    }
    @Override
    public void set(int x, int y, String s) {
        Cell c = new SCell(s);
        table[x][y] = (SCell) c;
        eval();
    }

    @Override
    public void eval() {
        int[][] dd = depth();
        for(int i=0;i<width();i++) {
            for(int j=0;j<height();j++) {
                String val= eval(i,j);
                if(val!=null) {
                    table[i][j]=new SCell(val);
                }
            }
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx>=0 && yy>=0;// check location isn't negative
        ans = ans && xx < Ex2Utils.WIDTH && yy < Ex2Utils.HEIGHT;
        return ans;
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        boolean[][] visited = new boolean[width()][height()];

        // אתחול של כל הערכים ב- ans להיות 0 בהתחלה
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                ans[i][j] = 0;
            }
        }

        // סריקה של כל הגיליון
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (ans[i][j] == 0 && table[i][j] != null && table[i][j].toString().startsWith("=")) {
                    int depth = calculateDepth(i, j, visited, ans);
                    if (depth == -1) {
                        // אם מצאנו מעגל תלות, אנחנו מציינים שהעמק הזה הוא -1
                        ans[i][j] = -1;
                    }
                }
            }
        }
        return ans;
    }

    private int calculateDepth(int x, int y, boolean[][] visited, int[][] ans) {
        // אם התא כבר ביקר בו בתהליך החישוב הנוכחי, אז מדובר במעגל תלות
        if (visited[x][y]) {
            return -1; // מצאנו מעגל תלות
        }

        // אם כבר יש תשובה לחישוב העומק של התא, מחזירים אותה
        if (ans[x][y] != 0) {
            return ans[x][y];
        }

        // אם התא לא מכיל נוסחה, התוצאה היא 0
        String value = table[x][y].toString();
        if (!value.startsWith("=")) {
            return 0;
        }

        // הצהרת דגל ביקור
        visited[x][y] = true;

        int maxDepth = 0;
        String formula = value.substring(1); // מתעלמים מהסימן "="

        // עברנו על כל הנוסחה ומחפשים הפניות לתאים אחרים
        for (int i = 0; i < formula.length(); i++) {
            if (Character.isLetter(formula.charAt(i))) {
                int j = i + 1;
                while (j < formula.length() && Character.isDigit(formula.charAt(j))) {
                    j++;
                }

                if (j > i + 1) {
                    String cellRef = formula.substring(i, j);
                    int refX = cellRef.charAt(0) - 'A';
                    int refY = Integer.parseInt(cellRef.substring(1));

                    if (isIn(refX, refY)) {
                        int currentDepth = calculateDepth(refX, refY, visited, ans);
                        if (currentDepth == -1) {
                            // אם יש מעגל תלות, מחזירים -1
                            ans[x][y] = -1;
                            visited[x][y] = false; // מבטלים את הדגל לפני חזרה
                            return -1;
                        }
                        maxDepth = Math.max(maxDepth, currentDepth);
                    }
                }
            }
        }

        // שמירה בתוצאה הסופית
        visited[x][y] = false;
        ans[x][y] = maxDepth + 1; // חישוב עומק התלות הסופי
        return ans[x][y];
    }


    @Override
    public void load(String fileName) throws IOException {
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line= br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    if (isIn(x, y)) {
                        set(x,y,parts[2]);
                    }
                }
            }
        }
        eval();
    }

    @Override
    public void save(String fileName) throws IOException {
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write("I2CS ArielU: SpreadSheet (Ex2)\n");
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    if (table[i][j]!=null && !table [i][j].toString().equals(Ex2Utils.EMPTY_CELL)) {
                        fw.write(i+ "," + j + "," +table[i][j].toString() + "\n");
                    }
                }
            }
        }
    }

    @Override
    public String eval(int x, int y) {
        String ans = null;
        if(get(x,y)!=null) {ans = get(x,y).toString();}
        // Add your code here
        // אם זו נוסחה (מתחילה ב-=)
        if(ans.startsWith("=")) {
            // מוריד את סימן ה-= מתחילת המחרוזת
            String formula = ans.substring(1);
            try {
                // מחליף הפניות לתאים (למשל A1) בערכים שלהם
                for (int i = 0; i < formula.length(); i++) {
                    if (Character.isLetter(formula.charAt(i))) {
                        int j = i + 1;
                        while (j < formula.length() && Character.isDigit(formula.charAt(j))) {
                            j++;
                        }
                        if (j > i + 1) {
                            String cellRef = formula.substring(i, j);
                            Cell referencedCell = get(cellRef);
                            if (referencedCell != null) {
                                String cellValue = eval(cellRef.charAt(0) - 'A', Integer.parseInt(cellRef.substring(1)));
                                formula = formula.replace(cellRef, cellValue);
                            }
                        }
                    }
                }
                ans = evaluateFormula(formula);
            } catch (Exception e){
                ans = "ERROR";
            }
        }
        return ans;
    }
    private String evaluateFormula(String formula) {
        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            Object result = engine.eval(formula);
            return result.toString();
            } catch (Exception e) {
        return "ERROR";
        }
    }
}

