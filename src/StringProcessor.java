public class StringProcessor {

    public static String unraw(String raw) {
        boolean allowOctals=true;
        StringBuilder result = new StringBuilder();
        boolean escape = false;
        boolean inOctal = false;
        int octalValue = 0;

        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);

            if (escape) {
                switch (ch) {
                    case '\'':
                        result.append('\'');
                        break;
                    case '\"':
                        result.append('\"');
                        break;
                    case '\\':
                        result.append('\\');
                        break;
                    case 'b':
                        result.append('\b');
                        break;
                    case 'f':
                        result.append('\f');
                        break;
                    case 'n':
                        result.append('\n');
                        break;
                    case 'r':
                        result.append('\r');
                        break;
                    case 't':
                        result.append('\t');
                        break;
                    case 'u':
                        if (i + 4 < raw.length()) {
                            String hexCode = raw.substring(i + 1, i + 5);
                            try {
                                int unicodeValue = Integer.parseInt(hexCode, 16);
                                result.append((char) unicodeValue);
                                i += 4;  // Move index to the end of the Unicode escape sequence
                            } catch (NumberFormatException e) {
                                // Handle parsing error if needed
                                e.printStackTrace();
                            }
                        } else {
                            // Handle incomplete Unicode escape sequence if needed
                            result.append(ch);
                        }
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                        inOctal = true;
                        octalValue = ch - '0';
                        break;
                    default:
                        result.append(ch);
                        break;
                }
                escape = false;
            } else if (inOctal && Character.isDigit(ch) && octalValue < 8) {
                octalValue = octalValue * 8 + (ch - '0');
                if (octalValue < 256) {
                    result.append((char) octalValue);
                    inOctal = false;
                } else {
                    // Handle invalid octal value if needed
                    result.append(ch);
                    inOctal = false;
                }
            } else if (ch == '\\') {
                escape = true;
                inOctal = false;
            } else {
                result.append(ch);
                inOctal = false;
            }
        }

        return result.toString();
    }

}