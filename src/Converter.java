import java.util.stream.Collectors;

public class Converter {
    /**
     * Data segment to COE file
     *
     * @param dataSeg Data segment object
     * @param padTo   Padding to how many KBytes
     * @return COE file content
     */
    public static String dataSegToCoe(DataSeg dataSeg, int padTo) {
        int wordLength = Utils.sizeof("word");
        StringBuilder coe = new StringBuilder("memory_initialization_radix = 16;\nmemory_initialization_vector =\n");
        int lineLimit = (padTo * 1024) / wordLength;
        StringBuilder buf = new StringBuilder();
        int lineno = 0;

        for (DataSegVar v : dataSeg.getVars()) {
            if (v.getAddr() / wordLength - lineno > 0 && !buf.isEmpty()) {
                coe.append(Utils.padLeft(String.valueOf(buf),8,'0')).append(",\n");
                buf.setLength(0);
                lineno++;
            }
            coe.append("00000000,\n".repeat((v.getAddr() / wordLength) - lineno));
            lineno = v.getAddr() / wordLength;
            buf.insert(0, "00".repeat(((v.getAddr() % wordLength) - buf.length() / 2 + wordLength) % wordLength));
            for (DataSegVarComp comp : v.getComps()) {
                switch (comp.getType()) {
                    case "ascii":
                        for (char c : comp.getVal().toCharArray()) {
                            buf.insert(0, Utils.decToHex(c, 8, false));
                            if (buf.length() == 8) {
                                coe.append(buf).append(",\n");
                                buf.setLength(0);
                                lineno++;
                            }
                        }
                        break;
                    case "space":
                        buf.insert(0, "00");
                        break;
                    default:
                        buf.insert(0, Utils.binToHex(Utils.literalToBin(comp.getVal(),
                                Utils.sizeof(comp.getType()) * 8, true), false));
                }
                if (buf.length() == 8) {
                    coe.append(buf).append(",\n");
                    buf.setLength(0);
                    lineno++;
                }
            }
        }
        if (!buf.isEmpty()) {
            coe.append("0".repeat(8 - buf.length())).append(buf.toString()).append(",\n");
            lineno++;
        }
        coe.append("00000000,\n".repeat(lineLimit - lineno));
        return coe.substring(0, coe.length() - 2) + ";\n";
    }

    /**
     * Text segment to COE file
     *
     * @param textSeg Text segment object
     * @param padTo   Padding to how many KBytes
     * @return COE file content
     */
    public static String textSegToCoe(TextSeg textSeg, int padTo) {
        int wordLength = Utils.sizeof("word");
        StringBuilder coe = new StringBuilder("memory_initialization_radix = 16;\nmemory_initialization_vector =\n");
        int lineLimit = (padTo * 1024) / wordLength;
        int startLine = (int) (Long.parseLong(textSeg.getStartAddr()) / wordLength);
        int lineno = 0;

        coe.append("00000000,\n".repeat(startLine));
        for (Instruction ins : textSeg.getIns()) {
            StringBuilder buf = new StringBuilder();
            for (InstructionComponent comp : ins.getComponents()) {
                buf.append(comp.getVal());
            }
            coe.append(Utils.binToHex(buf.toString(), false)).append(",\n");
            lineno++;
        }
        coe.append("00000000,\n".repeat(lineLimit - lineno - startLine));
        return coe.substring(0, coe.length() - 2) + ";\n";
    }

    /**
     * Combine two COE files into an ASCII stream file for UART
     *
     * @param programCoe Program COE file content
     * @param dataCoe    Data COE file content
     * @return Combined ASCII stream file content
     */
    public static String coeToTxt(String programCoe, String dataCoe) {
        String introSignal = "03020000"; // Introductory handshake signal
        String programStream = toStream(programCoe);
        String dataStream = toStream(dataCoe);

        return introSignal + programStream + dataStream;
    }

    private static String toStream(String coe) {
        return coe.replace("\r\n", "\n")
                .lines()
                .filter(line -> !line.trim().isEmpty())
                .skip(2)
                .map(line -> line.replaceAll("[,;]", ""))
                .collect(Collectors.joining());
    }
}