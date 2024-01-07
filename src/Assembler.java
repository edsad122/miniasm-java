import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DataSegVarComp {
    String type; // Variable component type
    public String getType() {
        return type;
    }
    public String getVal() {
        return val;
    }
    String val; // Variable component value
    public DataSegVarComp(String type, String val) {
        this.type=type;
        this.val=val;
    }
}
public class Assembler {
    static List<DataSegVar> vars;
    static List<TextSegLabel> labels = new ArrayList<>();
    static int pc = 0;
    public static final List<Instruction> minisysInstructions = MinisysInstructions.getMinisysInstructions();
    public static final String VarCompTypeRegex = "byte|half|word|ascii|space";
    private static final Pattern VAR_START_PATTERN = Pattern.compile("(.+):\\s+\\.(" + VarCompTypeRegex + ")\\s+(.+)");
    private static final Pattern VAR_CONTD_PATTERN = Pattern.compile("\\.(" + VarCompTypeRegex + ")\\s+(.+)");

    public static int getVarAddr(String name) {
        for (DataSegVar dataSegVar : vars) {
            if (dataSegVar.getName().equals(name)) {
                return dataSegVar.getAddr();
            }
        }

        throw new RuntimeException("未知的变量：" + name);
    }

    // Method to get label address
    public static int getLabelAddr(String label) {
        for (TextSegLabel textSegLabel : labels) {
            if (textSegLabel.getName().equals(label)) {
                return textSegLabel.getAddr();
            }
        }

        throw new RuntimeException("未知的标签：" + label);
    }

    // Method to get PC address
    public static int getPC() {
        // Implementation
        return pc;
    }
    /*
    所提供的TypeScript代码中的“parseDataSeg”函数用于解析汇编语言源代码中的数据段。该函数将汇编语言指令的数组作为参数。
    在“parseDataSeg”中，有一个嵌套函数“parseInitValue”，用于解析变量的初始值。
    此函数接受两个参数：“type”和“init”`type是变量类型，init是变量的初始值。
    “parseInitValue”函数首先声明，如果变量类型不是“ascii”，则初始值不应包含双引号字符。它还断言初始值不应以逗号开头或结尾。
    如果变量类型不是“ascii”，则函数会用逗号分隔初始值，并返回一个修剪后的值数组。
    如果变量类型为“ascii”，则函数将进入更复杂的解析过程。
    它初始化几个变量以跟踪解析状态，包括它当前是否在带引号的字符串（“inQuote”）内，是否应转义下一个字符（“nextEscape”）、结果数组（“res”）、当前字符串的缓冲区（“buf”）和前一个字符的缓冲区。
    然后，函数在初始值中的每个字符上进入一个循环。根据当前字符和解析状态，它会更新状态变量并将字符添加到缓冲区或结果数组中。
    该函数使用“assert”函数来确保初始值的语法正确，如果遇到非法字符或序列，则会引发错误。
    assert”函数在其他地方定义，用于在不满足特定条件时抛出错误。它有两个参数：“确保”和“提示”，前者是要检查的条件，后者是在不满足条件时显示的错误消息。
    `VarCompType`类型是`__VarCompType'对象的键，该键未显示在所提供的代码中。此类型用于“parseInitValue”函数的“type”参数。
     */
    private static List<String> parseInitValue(String type, String init) {
        assert !(!Objects.equals(type, "ascii") && init.contains("\"")) : "字符串型数据只能使用.ascii类型";
        init = init.trim();
        assert init.charAt(0) != ',' && init.charAt(init.length() - 1) != ',' : "数据初始化值头或尾有非法逗号";

        List<DataSegVarComp> result = new ArrayList<>();
        if (!Objects.equals(type, "ascii")) {
            return Stream.of(init.split("\\s*,")).toList();
        } else {
            boolean inQuote = false;
            boolean nextEscape = false;
            List<String> res = new ArrayList<>();
            StringBuilder buf = new StringBuilder();
            char prev = '\0';

            for (int i = 0; i < init.length(); i++) {
                char ch = init.charAt(i);
                if (!inQuote && Character.isWhitespace(ch)) {
                    continue;
                }
                if (ch == '"') {
                    if (nextEscape) {
                        assert inQuote : "有非法字符出现在引号以外";
                        buf.append('"');
                        nextEscape = false;
                    } else {
                        inQuote = !inQuote;
                    }
                } else if (ch == '\\') {
                    assert inQuote : "有非法字符出现在引号以外";
                    if (nextEscape) {
                        buf.append('\\');
                        nextEscape = false;
                    } else {
                        nextEscape = true;
                    }
                } else if (ch == ',') {
                    if (inQuote) {
                        buf.append(','); // 引号内逗号可不escape
                        nextEscape = false;
                    } else {
                        assert prev != ',' : "数据初始化值存在连续的逗号分隔";
                        res.add(buf.toString());
                        buf = new StringBuilder();
                    }
                } else {
                    assert inQuote : "有非法字符出现在引号以外";
                    if (nextEscape) {
                        buf.append(StringProcessor.unraw("\\" + ch));
                    } else {
                        buf.append(ch);
                    }
                    nextEscape = false;
                }
                prev = ch;
            }
            res.add(buf.toString());
            return res;
        }
    }

    // Method to parse data segment
    public static DataSeg parseDataSeg(List<String> asm) {
        String startAddr = asm.get(0).split("\\s+").length != 1 ? asm.get(0).split("\\s+")[1] : "0";
        if (asm.get(0).split("\\s+").length > 2) {
            throw new RuntimeException("数据段首声明非法");
        }
        //初始化
        List<DataSegVarComp> comps = new ArrayList<>();
        vars = new ArrayList<>();
        String name = null;
        int i = 1;
        int addr;
        if(startAddr.startsWith("0x")){
            addr=Integer.parseInt(startAddr.substring(2),16);
        }else{
            addr=Integer.parseInt(startAddr);
        }
        AtomicInteger nextAddr = new AtomicInteger(addr);

        while (i < asm.size()) {
            Matcher varStartMatcher = VAR_START_PATTERN.matcher(asm.get(i));
            Matcher varContdMatcher = VAR_CONTD_PATTERN.matcher(asm.get(i));

            if (varStartMatcher.matches()) {
                // 一个新变量开始
                if (name != null) {
                    vars.add(new DataSegVar(name, comps, addr));
                    comps=new ArrayList<>();
                    name=null;
                    addr = nextAddr.get();
                }

                name = varStartMatcher.group(1);
                String type = varStartMatcher.group(2);
                int size = Utils.sizeof(type);

                // 边界对齐
                if (addr % size > 0) {
                    nextAddr.set(addr = addr + size - (addr % size));
                }
                List<DataSegVarComp> finalComps = comps;
                parseInitValue(type, varStartMatcher.group(3)).forEach(val -> {
                    finalComps.add(new DataSegVarComp(type, val.trim()));
                    nextAddr.addAndGet(size * (Objects.equals(type, "ascii") ? val.length() : 1));
                });
            } else if (varContdMatcher.matches()) {
                // 变量组分继续
                String type = varContdMatcher.group(1);
                int size = Utils.sizeof(type);

                // 边界对齐，自动补.space
                while (nextAddr.get() % size > 0) {
                    comps.add(new DataSegVarComp("space", "00"));
                    nextAddr.getAndIncrement();
                }

                // 推入组分记录
                List<DataSegVarComp> finalComps1 = comps;
                parseInitValue(type, varContdMatcher.group(2)).forEach(val -> {
                    finalComps1.add(new DataSegVarComp(type, val.trim()));
                    nextAddr.addAndGet(size * (Objects.equals(type, "ascii") ? val.length() : 1));
                });
            } else {
                // 报错
                throw new RuntimeException("未知的变量定义形式，在数据段第 " + (i + 1) + " 行");
            }

            // 末尾处理
            if (i == asm.size() - 1) {
                vars.add(new DataSegVar(name, comps, addr));
            }

            i++;
        }

        return new DataSeg(startAddr, vars);
    }

    // Method to expand macros
    public static List<String> expandMacros(List<String> asm, List<Integer> lineno) {
        List<String> expandedAsm = new ArrayList<>(asm);
        String[] macros = MacroExpansionRules.expansionRules.keySet().toArray(new String[0]);
        int bias = 0;

        for (int i = 0; i < asm.size(); i++) {
            String v = asm.get(i);
            String labelPreserve = "";
            Pattern labelPattern = Pattern.compile("^(\\w+:)\\s*([\\w\\s$]+)$");
            Matcher lableMatcher = labelPattern.matcher(v);
            if (lableMatcher.matches()) {
                labelPreserve = lableMatcher.group(1);
                v = lableMatcher.group(2).trim();
            }
            for (String macro : macros) {
                Pattern pattern = MacroExpansionRules.expansionRules.get(macro).pattern;
                Matcher m = pattern.matcher(v);
                if (m.matches()) {
                    String[] replacer = MacroExpansionRules.expansionRules.get(macro).replace(m);
                    replacer[0] = labelPreserve + " " + replacer[0];
                    expandedAsm.remove(i + bias);
                    expandedAsm.addAll(i + bias, Arrays.asList(replacer));

                    lineno.remove(i + bias);
                    lineno.addAll(i + bias, new ArrayList<>(Collections.nCopies(replacer.length, lineno.get(i + bias))));
                    bias += replacer.length - 1;
                    break;
                }
            }
        }

        return expandedAsm;
    }

    // Method to parse text segment
    public static TextSeg parseTextSeg(List<String> asm, List<Integer> lineno) {
        // Expand macros
        List<String> expandedAsm = expandMacros(asm, lineno);

        // Determine the start address of the code segment
        String startAddr = expandedAsm.get(0).split("\\s+").length == 1 ? "0" : expandedAsm.get(0).split("\\s+")[1];
        assert expandedAsm.get(0).split("\\s+").length <= 2 : "Code segment first declaration is illegal.";

        // Correct the start address to 4-byte alignment (32-bit)
        final int sizeofWord = Utils.sizeof("word");
        int startAddrNumber;
        if(startAddr.startsWith("0x")){
            startAddrNumber=Integer.parseInt(startAddr.substring(2),16);
        }else{
            startAddrNumber=Integer.parseInt(startAddr);
        }
        startAddr = String.valueOf(((sizeofWord - (startAddrNumber % sizeofWord)) % sizeofWord) + startAddrNumber);

        // Initialize pc pointer
        pc = Utils.getOffsetAddr(startAddr, 0);
        labels.clear();

        // Remove labels and get actual line numbers for instructions
        int insLineno = 1;
        List<String> instructions = new ArrayList<>();
        for (int i = 0; i < expandedAsm.size(); i++) {
            String v = expandedAsm.get(i);
            if (i == 0) {
                instructions.add(v);
                continue;
            }

            Pattern labelPattern = Pattern.compile("(\\w+):\\s*(.*)");
            Matcher labelMatcher = labelPattern.matcher(v);

            if (labelMatcher.matches()) {
                assert labels.stream().noneMatch(label -> label.getName().equals(labelMatcher.group(1))) : "Duplicate label: " + labelMatcher.group(1) + ", at line " + lineno.get(i) + ".";
                labels.add(new TextSegLabel(labelMatcher.group(1), insLineno, Utils.getOffsetAddr(startAddr, (insLineno - 1) * Utils.sizeof("ins"))));
                if (!labelMatcher.group(2).trim().isEmpty()) {
                    insLineno++;
                }
                instructions.add(labelMatcher.group(2));
            } else {
                insLineno++;
                instructions.add(v);
            }
        }
        // 先提取掉所有的label
        expandedAsm = instructions;
        List<Integer> filteredLineno = new ArrayList<>();
        for (int i = 0; i < lineno.size(); i++) {
            if (!expandedAsm.get(i).trim().isEmpty()) {
                filteredLineno.add(lineno.get(i));
            }
        }
        lineno = filteredLineno;
        expandedAsm = expandedAsm.stream()
                .filter(x -> !x.trim().isEmpty())
                .collect(Collectors.toList());

        // Parse instructions
        List<Instruction> parsedInstructions = new ArrayList<>();
        for (int i = 1; i < expandedAsm.size(); i++) {
            parsedInstructions.add(parseOneLine(expandedAsm.get(i), lineno.get(i)));
        }

        return new TextSeg(startAddr, parsedInstructions, labels);
    }


    // Method to parse one line of assembly code
    public static Instruction parseOneLine(String asm, int lineno) {
        // 处理助记符
        Pattern pattern = Pattern.compile("^\\s*(\\w+)\\s*(.*)");
        Matcher matcher = pattern.matcher(asm);
        if (!matcher.matches()) {
            System.out.print("\"没有找到指令助记符，在代码第 " + lineno);
        }
        String symbol = matcher.group(1);
        // 检验助记符合法性
        int instructionIndex = -1;
        for (int i = 0; i < minisysInstructions.size(); i++) {
            if (minisysInstructions.get(i).getSymbol().equals(symbol)) {
                instructionIndex = i;
                break;
            }
        }

        assert instructionIndex != -1 : String.format("无效的指令助记符或错误的指令用法：%s，在代码第 %d 行。", symbol, lineno);
        // 单行汇编去空格
        asm = Utils.serialString(matcher.group(2));
        // pc移进
        pc += Utils.sizeof("ins");
        // 开始组装Instruction对象
        Instruction res = Instruction.newInstance(minisysInstructions.get(instructionIndex));
        res.setSrc(symbol+" "+asm);
        for (InstructionComponent component : res.getComponents()) {
            if (component.getVal().trim().isEmpty()) {
                res.setComponent(component.getDesc(), component.toBinary(res.getInsPattern().matcher(asm)));
            }
        }

        return res;
    }

    // Method to assemble
    public static AsmProgram assemble(String asm_) {
        String[] asm__ = (asm_ + "\n")
                .replaceAll("\r\n", "\n")
                .replaceAll("#(.*)\\n", "\n")
                .split("\n");

        List<Integer> lineno = new ArrayList<>();
        for (int i = 0; i < asm__.length; i++) {
            lineno.add(i + 1);
        }
        List<Integer> filteredLineno = new ArrayList<>();
        for (int i = 0; i < lineno.size(); i++) {
            if (!asm__[i].trim().isEmpty()) {
                filteredLineno.add(lineno.get(i));
            }
        }
        lineno = filteredLineno;
        List<String> asm = Arrays.stream(asm__)
                .filter(line -> !line.trim().isEmpty())
                .map(line -> line.trim().replaceAll("\\s+", " ").replaceAll(",\\s*", ", ").toLowerCase())
                .toList();

        // Find the starting lines of the data segment and text segment
        int dataSegStartLine = findSegmentStartLine(asm, ".data");
        int textSegStartLine = findSegmentStartLine(asm, ".text");
        assert dataSegStartLine != -1 : "Data segment start not found";
        assert textSegStartLine != -1 : "Text segment start not found";
        assert dataSegStartLine < textSegStartLine : "Data segment cannot be after the text segment";

        // Parse the data segment
        DataSeg dataSeg = parseDataSeg(asm.subList(dataSegStartLine, textSegStartLine));

        // Parse the text segment
        TextSeg textSeg = parseTextSeg(asm.subList(textSegStartLine, asm.size()), lineno.subList(textSegStartLine, lineno.size()));

        return new AsmProgram(dataSeg, textSeg);
    }
    private static int findSegmentStartLine(List<String> asm, String segment) {
        for (int i = 0; i < asm.size(); i++) {
            if (asm.get(i).contains(segment)) {
                return i;
            }
        }
        return -1;
    }

}
class DataSegVar {
    public String getName() {
        return name;
    }
    public List<DataSegVarComp> getComps() {
        return comps;
    }
    public int getAddr() {
        return addr;
    }
    String name; // Variable name
    List<DataSegVarComp> comps; // List of variable components
    int addr; // Variable address
    public DataSegVar(String name, List<DataSegVarComp> comps, int addr) {
        this.name = name;
        this.comps = new ArrayList<>(comps);
        this.addr = addr;
    }
    public String toString(){
        StringBuilder s=new StringBuilder();
        for(DataSegVarComp c:comps){
            s.append("\t").append("type:").append(c.type).append("\tval:").append(c.val).append("\n");
        }
        return s.toString();
    }

    static class AsmProgram {
        DataSeg dataSeg; // Data segment

        public DataSeg getDataSeg() {
            return dataSeg;
        }

        public TextSeg getTextSeg() {
            return textSeg;
        }

        TextSeg textSeg; // Text segment

        public AsmProgram(DataSeg dataSeg, TextSeg textSeg) {
            this.dataSeg = dataSeg;
            this.textSeg = textSeg;
        }
    }
}
class DataSeg {
    public String getStartAddr() {
        return startAddr;
    }
    public List<DataSegVar> getVars() {
        return vars;
    }
    private final String startAddr; // Starting address
    private final List<DataSegVar> vars; // List of variables
    public DataSeg(String startAddr, List<DataSegVar> vars) {
        this.startAddr = startAddr;
        this.vars = new ArrayList<>(vars);
    }
    public void newVar(String name, List<DataSegVarComp> comps, int addr) {
        for (DataSegVar v : vars) {
            if (v.name.equals(name)) {
                throw new RuntimeException("重复的变量名。");
            }
        }

        vars.add(new DataSegVar(name, comps, addr));
    }

    // Method to add a new variable component
    public void newComp(String name, DataSegVarComp comp) {
        // Check if the variable exists
        DataSegVar targetVar = null;
        for (DataSegVar v : vars) {
            if (v.name.equals(name)) {
                targetVar = v;
                break;
            }
        }

        // If the variable is found, add the new component
        if (targetVar != null) {
            targetVar.comps.add(comp);
        } else {
            throw new RuntimeException("找不到该变量。");
        }
    }
    public String toString(){
        StringBuilder s=new StringBuilder();
        s.append("startAddr:").append(startAddr).append("\t,vars:{\n");
        for(DataSegVar v:vars){
            s.append("name:").append(v.name).append("\taddr:").append(v.addr).append("\t{\n");
            s.append(v.toString()).append("}\n");
        }
        return s.append("}").toString();
    }
}

// Define the TextSegLabel class
class TextSegLabel {
    public String getName() {
        return name;
    }



    public int getAddr() {
        return addr;
    }

    String name; // Label name
    int lineno; // Line number
    int addr; // Label address
    public TextSegLabel(String name,int lineno,int addr){
        this.addr=addr;
        this.name=name;
        this.lineno=lineno;
    }
}

// Define the TextSeg class
class TextSeg {
    private final String startAddr; // Starting address
    private final List<Instruction> ins; // List of instructions

    private final List<TextSegLabel> labels; // List of labels

    public TextSeg(String startAddr, List<Instruction> ins, List<TextSegLabel> labels) {
        this.startAddr = startAddr;
        this.ins = new ArrayList<>(ins);
        this.labels = new ArrayList<>(labels);
    }

    // Getters for startAddr, ins, and labels
    public String getStartAddr() {
        return startAddr;
    }

    public List<Instruction> getIns() {
        return ins;
    }

    public String toBinary() {
        StringBuilder binaryStringBuilder = new StringBuilder();

        // Iterate through instructions and append binary representation
        for (Instruction instruction : ins) {
            binaryStringBuilder.append(instruction.toBinary()).append("\n");
        }

        return binaryStringBuilder.toString();
    }
    public String toHex() {
        StringBuilder binaryStringBuilder = new StringBuilder();

        // Iterate through instructions and append binary representation
        for (Instruction instruction : ins) {
            binaryStringBuilder.append(instruction.toHex()).append("——").append(instruction.getSrc()).append("\n");
        }

        return binaryStringBuilder.toString();
    }
    public String toString(){
        StringBuilder s= new StringBuilder();
        s.append("startAddr:").append(startAddr).append(",\tInstruction:{\n");
        for(Instruction i:ins){
            s.append("\t").append(i.getSrc()).append("\t Hex:").append(i.toHex()).append("\n");
        }
        s.append("},\nLabels:{\n");
        for(TextSegLabel l:labels){
            s.append("\t").append(l.getName()).append("\n");
        }
        s.append("}");
        return s.toString();
    }
}

// Main class to represent the AsmProgram
class AsmProgram {
    DataSeg dataSeg; // Data segment

    public DataSeg getDataSeg() {
        return dataSeg;
    }

    public TextSeg getTextSeg() {
        return textSeg;
    }

    TextSeg textSeg; // Text segment

    public AsmProgram(DataSeg dataSeg, TextSeg textSeg) {
        this.dataSeg = dataSeg;
        this.textSeg = textSeg;
    }
    public String toString(){
        return "textSeg:{\n"+
                addTabsToLines(textSeg.toString(),1)+"}\n"+
                "dataSeg:{\n"+
                addTabsToLines(dataSeg.toString(),1)+"}";
    }
    private static String addTabsToLines(String inputString, int numberOfTabs) {
        String[] lines = inputString.split("\n");
        StringBuilder indentedStringBuilder = new StringBuilder();

        for (String line : lines) {
            indentedStringBuilder.append(getTabs(numberOfTabs)).append(line).append("\n");
        }

        return indentedStringBuilder.toString();
    }

    private static String getTabs(int numberOfTabs) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < numberOfTabs; i++) {
            tabs.append("\t");
        }
        return tabs.toString();
    }
}

