
public class Utils {
    /**
     * 将label或字面量转换为二进制
     *
     * @param label    label名称或字面量数字
     * @param len      转换后的长度
     * @param isOffset 转换而成的是否为相对当前地址的偏移量
     * @param signExt  转换后位数不足时是否进行符号扩展，默认采用零扩展
     */
    public static String labelToBin(String label, int len, boolean isOffset, boolean signExt) {
        try {
            if (!isOffset) {
                int userAppOffset=Main.isLink()? 1280:0;
                return decToBin(
                        Integer.parseInt(literalToBin(label, len, signExt), 2) + userAppOffset,
                        len,
                        signExt
                );
            } else {
                return literalToBin(label, len, signExt);
            }
        } catch (Exception e) {
            return literalToBin(String.valueOf(Assembler.getLabelAddr(label) - (isOffset ? Assembler.getPC() : 0)), len, isOffset);
        }
    }

    /**
     * 将变量名或字面量转换为二进制
     *
     * @param name    变量名称或字面量数字
     * @param len     转换后的长度
     * @param signExt 转换后位数不足时是否进行符号扩展，默认采用零扩展
     */
    public static String varToAddrBin(String name, int len, boolean signExt) {
        try {
            return literalToBin(name, len, signExt);
        } catch (Exception ignored) {
            return literalToBin(String.valueOf(Assembler.getVarAddr(name)), len,false);
        }
    }

    /**
     * 把字面量数字转换为二进制
     *
     * @param literal 要转换的字面量数字
     * @param len     转换后的最少位数
     * @param signExt 转换后位数不足时是否进行符号扩展，默认采用零扩展
     * @example 10
     * @example 0xabcd
     */
    public static String literalToBin(String literal, int len, boolean signExt) {
        if (literal.startsWith("0x")) {
            String num = hexToBin(literal);
            return padLeft(num, len, signExt && Long.parseLong(literal.substring(2), 16) < 0 ? '1' : '0');
        } else {
            return decToBin(Integer.parseInt(literal), len, signExt);
        }
    }
    private static String padLeft(String str, int len, char padChar) {
        StringBuilder result = new StringBuilder(str);
        while (result.length() < len) {
            result.insert(0, padChar);
        }
        return result.toString();
    }

    /**
     * 将十进制数转为二进制，用 pad 补齐到 len 位，支持负数
     */
    public static String decToBin(int dec, int len, boolean signExt) {
        StringBuilder num = new StringBuilder();

        if (dec < 0) {
            // Compute two's complement
            String binary = Integer.toBinaryString(-dec - 1);
            for (char c : binary.toCharArray()) {
                num.append((char) (c ^ 1));
            }
        } else {
            num.append(Integer.toBinaryString(dec));
        }

        return padLeft(num.toString(), len, signExt && dec < 0 ? '1' : '0');
    }


    /**
     * 将4n位二进制转为n位十六进制
     */
    public static String binToHex(String bin, boolean zeroX) {
        if (bin.length() % 4 != 0) {
            throw new Error("二进制位数不为4的倍数。");
        }

        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < bin.length(); i += 4) {
            hex.append("0123456789abcdef".charAt(Integer.parseInt(bin.substring(i, i + 4), 2)));
        }
        return (zeroX ? "0x" : "") + hex;
    }

    /**
     * 将十进制数转为十六进制，十进制数会先被转换为4n位二进制
     */
    public static String decToHex(int dec, int len, boolean zeroX) {
        return binToHex(decToBin(dec, len, false), zeroX);
    }


    /**
     * 将十六进制每位转换为4位二进制，参数带不带0x头都可以
     */
    public static String hexToBin(String hex) {

        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }

        String[] table = new String[16];
        for (int i = 0; i < 16; i++) {
            table[i] = decToBin(i, 4, false);
        }

        StringBuilder result = new StringBuilder();
        for (char c : hex.toCharArray()) {
            int index = "0123456789abcdef".indexOf(c);
            result.append(table[index]);
        }

        return result.toString();
    }

    /**
     * 去除一串字符串中的全部空格
     */
    public static String serialString(String bin) {
        return bin.replaceAll("\\s+", "");
    }


    public static int sizeof(String type) {
        int size = switch (type) {
            case "byte", "space", "ascii" -> 1;
            case "half" -> 2;
            case "word", "ins" -> 4;
            default -> -1;
        };
        if (size == -1) {
            throw new IllegalArgumentException("错误的变量类型：" + type);
        }
        return size;
    }
    /**
     * 算偏移后的地址
     *
     * @param baseAddr   基地址，十六进制或十进制
     * @param offsetBit  偏移位数
     */
    public static int getOffsetAddr(String baseAddr, int offsetBit) {
        int base = baseAddr.startsWith("0x") ? hexToDec(baseAddr) : Integer.parseInt(baseAddr);
        return base + offsetBit;
    }

    private static int hexToDec(String hex) {
        return Integer.parseInt(hex, 16);
    }
}
