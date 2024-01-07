import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class test {
    public static void main(String[] args) throws IOException {
        String asm= Files.readString(Paths.get("src","snippet","test.txt"));
        AsmProgram asmResult=Assembler.assemble(asm);
        System.out.print(asmResult.toString());
        String textCoe = Converter.textSegToCoe(asmResult.getTextSeg(),64);
        String dataCoe = Converter.dataSegToCoe(asmResult.getDataSeg(),64);
        Main.writeToFile("E:\\ProgramData\\test","datacoeTest.coe",dataCoe);
        Main.writeToFile("E:\\ProgramData\\test","textcoeTest.coe",textCoe);
    }

}
