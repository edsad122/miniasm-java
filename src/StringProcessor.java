public class StringProcessor {

    public static String unraw(char raw) {
        StringBuilder result = new StringBuilder();
        switch (raw) {
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
            default:
                result.append(raw);
                break;
        }


        return result.toString();
    }

}