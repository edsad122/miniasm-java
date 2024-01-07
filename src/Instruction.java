import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Instruction {
    private String symbol; // 指令名称（助记符）
    private String desc; // 指令描述
    private String pseudo; // 指令伪代码
    private Pattern insPattern; // 指令正则模式

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    private String src;//原始数据
    private List<InstructionComponent> components; // 指令组分

    public static Instruction newInstance(Instruction baseOn) {
        return new Instruction(
                baseOn.getSymbol(),
                baseOn.getDesc(),
                baseOn.getPseudo(),
                baseOn.getInsPattern(),
                baseOn.getComponents()
        );
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public Pattern getInsPattern() {
        return insPattern;
    }

    public void setInsPattern(Pattern insPattern) {
        this.insPattern = insPattern;
    }

    public List<InstructionComponent> getComponents() {
        return components;
    }

    public void setComponents(List<InstructionComponent> components) {
        this.components = components;
    }

    public Instruction(String symbol, String desc, String pseudo, Pattern insPattern, List<InstructionComponent> components) {
        this.symbol = symbol;
        this.desc = desc;
        this.pseudo = pseudo;
        this.insPattern = insPattern;
        this.components = new ArrayList<>();
        for(InstructionComponent component:components){
            this.components.add(component.clone());
        }
    }

    public void setComponent(String desc, String val) {
        int index = -1;
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).getDesc().equals(desc)) {
                index = i;
                break;
            }
        }
        if (index != -1) {

            components.get(index).setVal(val);
        } else {
            throw new IllegalArgumentException("未知的指令组分: " + desc);
        }
    }

    public String toBinary() {
        for (InstructionComponent component : components) {
            if (component.getVal().trim().isEmpty()) {
                throw new IllegalStateException("尝试将不完整的指令转为2或16进制。");
            }
        }
        return components.stream().map(InstructionComponent::getVal).reduce(String::concat).orElse("");
    }

    public String toHex() {
        return Utils.binToHex(toBinary(), false);
    }

}

enum InstructionComponentType {
    FIXED,
    REG,
    IMMED,
    C0SEL,
    OFFSET,
    ADDR,
    SHAMT,
    CODE
}

class InstructionComponent implements Cloneable{
    private int lBit;
    private int rBit;
    private String desc;

    public Function<Matcher, String> getToBinary() {
        return toBinary;
    }

    private Function<Matcher,String> toBinary;
    private InstructionComponentType type;
    private String val;

    public InstructionComponent(int lBit, int rBit, String desc, Function<Matcher,String> toBinary, InstructionComponentType type, String val) {
        this.lBit = lBit;
        this.rBit = rBit;
        this.desc = desc;
        this.toBinary = toBinary;
        this.type = type;
        this.val = val;
    }

    public int getLBit() {
        return lBit;
    }

    public int getRBit() {
        return rBit;
    }

    public String getDesc() {
        return desc;
    }


    public InstructionComponentType getType() {
        return type;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }


    public String toBinary(Matcher m) {
        if(toBinary != null && m.matches())
            return toBinary.apply(m);
        else
            return "";
    }

    @Override
    public InstructionComponent clone() {
        try {
            InstructionComponent clone = (InstructionComponent) super.clone();
            clone.val = this.val;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

