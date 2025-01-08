//package assignments.Ex2;
// Add your documentation below:

    public class SCell implements Cell {
        private String line;
        private int type;
        // Add your code here

        public SCell(String s) {
            // Add your code here
            setData(s);
        }

        @Override
        public int getOrder() {
            // Add your code here

            return 0;
            // ///////////////////
        }

        //@Override
        @Override
        public String toString() {
            return getData();
        }

        @Override
        public void setData(String s) {
            // Add your code here
            line = s;
            /////////////////////
        }

        @Override
        public String getData() {
            return line;
        }

        @Override
        public int getType() {
            return type;
        }

        @Override
        public void setType(int t) {
            type = t;
        }

        @Override
        public void setOrder(int t) {
            // Add your code here

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
                if (i==0 && (c== '-' || c== '+')) {
                 continue;
                } else if ( c=='.') {
                  if (hasDecimalPoint) {
                      return false;
                  }
                  hasDecimalPoint = true;
                } else if (Character.isDigit(c)){
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
            return !(isNumber(text) &&isForm(text)); // A string is text if it's not a number or formula
        }

        public Double computeForm(String form)
        {
            double ans = 0; //// תמחקי או תשני למה שאת צריכה כשאת מממשת את הפונקציה

            return ans ;
        }
    }





