//public class CellEntry {
// Add your documentation below:

    public class CellEntry  implements Index2D {

        @Override
        public boolean isValid() {
            return false; // to do
        }

        @Override
        public int getX() {return Ex2Utils.ERR;}

        @Override
        public int getY() {return Ex2Utils.ERR;}
    }
    /**
     *
     * @return the cell index representation in form of a spreadsheet (e.g., "B3").
     */
//    public String toString() {
//    }

