import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static boolean isLink() {
        return link;
    }

    private static boolean link;
    public static final Map<String, Object> globalThis = new HashMap<>();

    public static void main(String[] args) throws IOException {
        link = Arrays.asList(args).contains("-l");
        if (args.length != 3 && args.length != 2) {
            stdoutPrint("Usage: java Main <in_file> <out_dir> [-l]\n");
        } else {
            String inFile = args[0];
            String outDir = args[1];
            globalThis.put("_minisys", Map.of("_userAppOffset", link ? 1280 : 0));

                if (!link) {
                    String asmCode = Files.readString(Paths.get(inFile));
                    AsmProgram asmResult = Assembler.assemble(asmCode);
                    String dataCoe = Converter.dataSegToCoe(asmResult.getDataSeg(),64);
                    String textCoe = Converter.textSegToCoe(asmResult.getTextSeg(),64);
                    writeToFile(outDir, "dmem32.coe", dataCoe);
                    writeToFile(outDir, "prgmip32.coe", textCoe);
                    writeToFile(outDir, "serial.txt", Converter.coeToTxt(textCoe, dataCoe));
                    stdoutPrint("[minisys-asm] Assembling done.");
                }
                else {
                    // 进行链接
                    String biosContent = Files.readString(Paths.get("E:\\Git_Project\\miniasm\\dist\\snippet\\minisys-bios.asm"));
                    String userAppASM = Files.readString(Paths.get(inFile)).replace("\r\n", "\n").trim();
                    String intEntryContent = Files.readString(Paths.get("E:\\Git_Project\\miniasm\\dist\\snippet\\minisys-interrupt-entry.asm"));
                    String intHandlerContent = Files.readString(Paths.get("E:\\Git_Project\\miniasm\\dist\\snippet\\minisys-interrupt-handler.asm"));

                    String[] asm = (userAppASM + "\n")
                            .replace("\r\n", "\n")
                            .replaceAll("#(.*)\n", "\n")
                            .split("\n");

                    int dataSegStartLine = -1;
                    int textSegStartLine = -1;
                    for (int i = 0; i < asm.length; i++) {
                        if (asm[i].matches("\\.data")) {
                            dataSegStartLine = i;
                        } else if (asm[i].matches("\\.text")) {
                            textSegStartLine = i;
                        }
                    }

                    if (dataSegStartLine == -1) {
                        throw new RuntimeException("未找到数据段开始");
                    }
                    if (textSegStartLine == -1) {
                        throw new RuntimeException("未找到代码段开始");
                    }
                    if (dataSegStartLine >= textSegStartLine) {
                        throw new RuntimeException("数据段不能位于代码段之后");
                    }

                    String allProgram = String.join("\n", Arrays.copyOfRange(asm, dataSegStartLine, textSegStartLine)) +
                            "\n" +
                            ".text\n" +
                            Linker.linkAll(biosContent, String.join("\n", Arrays.copyOfRange(asm, textSegStartLine + 1, asm.length)), intEntryContent, intHandlerContent);
                    AsmProgram all = Assembler.assemble(allProgram);
                    String textCoe = Converter.textSegToCoe(all.getTextSeg(),64);
                    String dataCoe = Converter.dataSegToCoe(all.getDataSeg(),64);

                    writeToFile(outDir, "prgmip32.coe", textCoe);
                    writeToFile(outDir, "dmem32.coe", dataCoe);
                    writeToFile(outDir, "serial.txt", Converter.coeToTxt(textCoe, dataCoe));
                    writeToFile(outDir, "linked.asm", allProgram);
                    System.out.println("[minisys-asm] Assembling done with linking. jOffset = 1280 B.");
                }
        }
    }

    private static void stdoutPrint(String content) {
        System.out.print(content);
    }


    private static void writeToFile(String dir, String fileName, String content) throws IOException {
        File file = new File(dir, fileName);
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

}
