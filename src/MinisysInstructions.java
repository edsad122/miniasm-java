import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinisysInstructions {
    private static final List<Instruction> minisysInstructions = new ArrayList<>();

    public static List<Instruction> getMinisysInstructions() {
        return minisysInstructions;
    }

    static {
        // 新增指令
        newInstruction("add", "按字加法", "(rd)←(rs)+(rt)", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "100000")
        });

        newInstruction("addu", "无符号加", "(rd)←(rs)+(rt)", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "100001")
        });

        newInstruction("sub", "按字减法", "(rd)←(rs)-(rt)", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "100010")
        });

        newInstruction("subu", "无符号减", "(rd)←(rs)-(rt)", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "100011")
        });

        newInstruction("and", "按位与", "(rd)←(rs)&(rt)", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "100100")
        });

        newInstruction("mult", "按字乘法", "(HI,LO)←(rs)*(rt)", paramPattern(2), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 6, "rd+shamt", null, InstructionComponentType.FIXED, "0000000000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "011000")
        });
        newInstruction("multu", "无符号乘", "(HI,LO)←(rs)*(rt)", paramPattern(2), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 6, "rd+shamt", null, InstructionComponentType.FIXED, "0000000000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "011001")
        });

        newInstruction("div", "除法", "(HI)←(rs)%(rt), (LO)←(rs)/(rt)", paramPattern(2), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 6, "rd+shamt", null, InstructionComponentType.FIXED, "0000000000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "011010")
        });

        newInstruction("divu", "无符号除", "(HI)←(rs)%(rt), (LO)←(rs)/(rt)", paramPattern(2), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 6, "rd+shamt", null, InstructionComponentType.FIXED, "0000000000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "011011")
        });

        newInstruction("mfhi", "取HI", "(rd)←(HI)", paramPattern(1), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 16, "rs+rt", null, InstructionComponentType.FIXED, "0000000000"),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "010000")
        });

        newInstruction("mflo", "取LO", "(rd)←(LO)", paramPattern(1), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 16, "rs+rt", null, InstructionComponentType.FIXED, "0000000000"),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "010010")
        });

        newInstruction("mthi", "存HI", "(HI)←(rs)", paramPattern(1), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 6, "rt+rd+shamt", null, InstructionComponentType.FIXED, "000000000000000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "010001")
        });

        newInstruction("mtlo", "存LO", "(LO)←(rs)", paramPattern(1), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 6, "rt+rd+shamt", null, InstructionComponentType.FIXED, "000000000000000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "010011")
        });

        newInstruction("mfc0", "取C0", "(rt)=由(rd)和sel确定的C0寄存器的值", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "010000"),
                new InstructionComponent(25, 21, "rs", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", m -> Utils.literalToBin(m.group(3), 6, false), InstructionComponentType.C0SEL, "")
        });

        newInstruction("mtc0", "存C0", "由(rd)和sel确定的C0寄存器的值=(rt)", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "010000"),
                new InstructionComponent(25, 21, "rs", null, InstructionComponentType.FIXED, "00100"),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", m -> Utils.literalToBin(m.group(3), 6, false), InstructionComponentType.C0SEL, "")
        });

        newInstruction("or", "按位或", "(rd)←(rs)|(rt)", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "100101")
        });

        newInstruction("xor", "按位异或", "(rd)←(rs)^(rt)", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "100110")
        });

        newInstruction("nor", "按位或非", "(rd)←~((rs)|(rt))", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "100111")
        });

        newInstruction("slt", "有符号比较", "if (rs<rt) rd=1 else rd=0", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "101010")
        });
        newInstruction("sltu", "无符号比较", "if (rs<rt) rd=1 else rd=0", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "101011")
        });

        newInstruction("sll", "逻辑左移", "(rd)←(rt)<<shamt", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", m -> Utils.literalToBin(m.group(3), 5, false), InstructionComponentType.SHAMT, ""),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "000000")
        });

        newInstruction("srl", "逻辑右移", "(rd)←(rt)>>_L shamt", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", m -> Utils.literalToBin(m.group(3), 5, false), InstructionComponentType.SHAMT, ""),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "000010")
        });

        newInstruction("sra", "算术右移", "(rd)←(rt)>>_A shamt", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", m -> Utils.literalToBin(m.group(3), 5, false), InstructionComponentType.SHAMT, ""),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "000011")
        });

        newInstruction("sllv", "逻辑左移V", "(rd)←(rt)<<(rs)", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "000100")
        });

        newInstruction("srlv", "逻辑右移V", "(rd)←(rt)>>_L (rs)", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "000110")
        });

        newInstruction("srav", "算术右移V", "(rd)←(rt)>>_L (rs)", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "000111")
        });

        newInstruction("jr", "无条件跳转（寄存器）", "(PC)←(rs)", paramPattern(1), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 6, "rt+rd+shamt", null, InstructionComponentType.FIXED, "000000000000000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "001000")
        });

        newInstruction("jalr", "暂存下条后跳转（寄存器）", "(rd)=(PC)+4,(PC)←(rs)", paramPattern(2), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(15, 11, "rd", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(10, 6, "shamt", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "001001")
        });

        newInstruction("break", "断点异常", "断点异常", paramPattern(1), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 6, "code", m -> Utils.literalToBin(m.group(1), 20, false), InstructionComponentType.CODE, ""),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "001101")
        });
        newInstruction("syscall", "系统调用", "系统调用", Pattern.compile("^([\\w$-]+)?$"), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000000"),
                new InstructionComponent(25, 6, "code", m -> Utils.literalToBin(m.group(1)== null ? "0" : m.group(1), 20, false), InstructionComponentType.CODE, ""),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "001100")
        });

        newInstruction("eret", "从中断或者异常中返回", "从中断或者异常中返回", paramPattern(0), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "010000"),
                new InstructionComponent(25, 6, "rs+rt+rd+shamt", null, InstructionComponentType.FIXED, "10000000000000000000"),
                new InstructionComponent(5, 0, "func", null, InstructionComponentType.FIXED, "011000")
        });

// =================== I型指令 ===================

        newInstruction("addi", "加立即数", "(rt)←(rs)+(sign-extend)immediate", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "001000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 0, "immediate", m -> Utils.literalToBin(m.group(3), 16, true), InstructionComponentType.IMMED, "")
        });

        newInstruction("addiu", "无符号加立即数", "(rt)←(rs)+(sign-extend)immediate", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "001001"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 0, "immediate", m -> Utils.literalToBin(m.group(3), 16, true), InstructionComponentType.IMMED, "")
        });

        newInstruction("andi", "按位与立即数", "(rt)←(rs)&(zero-extend)immediate", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "001100"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 0, "immediate", m -> Utils.literalToBin(m.group(3), 16, false), InstructionComponentType.IMMED, "")
        });

        newInstruction("ori", "按位或立即数", "(rt)←(rs)|(zero-extend)immediate", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "001101"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 0, "immediate", m -> Utils.literalToBin(m.group(3), 16, false), InstructionComponentType.IMMED, "")
        });

        newInstruction("xori", "按位异或立即数", "(rt)←(rs)^(zero-extend)immediate", paramPattern(3), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "001110"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 0, "immediate", m -> Utils.literalToBin(m.group(3), 16, false), InstructionComponentType.IMMED, "")
        });

        newInstruction("lui", "取立即数高16位", "(rt)←immediate<<16 & 0xFFFF0000H", paramPattern(2), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "001111"),
                new InstructionComponent(25, 21, "rs", null, InstructionComponentType.FIXED, "00000"),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 0, "immediate", m -> Utils.literalToBin(m.group(2), 16, true), InstructionComponentType.IMMED, "")
        });

        newInstruction("lb", "取字节", "(rt)←(Sign-Extend)Memory[(rs)+(sign_extend)offset]", Pattern.compile("^([\\w$-]+),([\\w-]+)\\(([\\w$-]+)\\)$"), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "100000"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 0, "offset", m -> Utils.varToAddrBin(m.group(2), 16, true), InstructionComponentType.OFFSET, "")
        });

        newInstruction("lbu", "取无符号字节", "(rt)←(Zero-Extend)Memory[(rs)+(sign_extend)offset]", Pattern.compile("^([\\w$-]+),([\\w-]+)(([\\w$-]+))$"), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "100100"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 0, "offset", m -> Utils.varToAddrBin(m.group(2), 16, true), InstructionComponentType.OFFSET, "")
        });

        newInstruction("lh", "取半字", "(rt)←(Sign-Extend)Memory[(rs)+(sign_extend)offset]", Pattern.compile("^([\\w$-]+),([\\w-]+)\\((([\\w$-]+))\\)$"), new InstructionComponent[]{
                new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "100001"),
                new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                new InstructionComponent(15, 0, "offset", m -> Utils.varToAddrBin(m.group(2), 16, true), InstructionComponentType.OFFSET, "")
        });
        newInstruction(
                "lhu",
                "取无符号半字",
                "(rt)←(Zero-Extend)Memory[(rs)+(sign_extend)offset]",
                Pattern.compile("^([\\w$-]+),([\\w-]+)\\(([\\w$-]+)\\)$"), new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "100101"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(15, 0, "offset", m -> Utils.varToAddrBin(m.group(2), 16, true), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "sb",
                "存字节",
                "Memory[(rs)+(sign_extend)offset]←(rt)7..0",
                Pattern.compile("^([\\w$-]+),([\\w-]+)\\(([\\w$-]+)\\)$"),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "101000"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(15, 0, "offset", m -> Utils.varToAddrBin(m.group(2), 16, true), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "sh",
                "存半字",
                "Memory[(rs)+(sign_extend)offset]←(rt)15..0",
                Pattern.compile("^([\\w$-]+),([\\w-]+)\\(([\\w$-]+)\\)$"),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "101001"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(15, 0, "offset", m -> Utils.varToAddrBin(m.group(2), 16, true), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "lw",
                "取字",
                "(rt)←Memory[(rs)+(sign_extend)offset]",
                Pattern.compile("^([\\w$-]+),([\\w-]+)\\(([\\w$-]+)\\)$"),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "100011"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(15, 0, "offset", m -> Utils.varToAddrBin(m.group(2), 16, true), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "sw",
                "存字",
                "Memory[(rs)+(sign_extend)offset]←(rt)",
                Pattern.compile("^([\\w$-]+),([\\w-]+)\\(([\\w$-]+)\\)$"),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "101011"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(3)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(15, 0, "offset", m -> Utils.varToAddrBin(m.group(2), 16, true), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "beq",
                "相等分支",
                "if ((rt)=(rs)) then (PC)←(PC)+4+((Sign-Extend)offset<<2)",
                paramPattern(3),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000100"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(15, 0, "offset", m -> Utils.labelToBin(m.group(3), 18, true, true).substring(0,16), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "bne",
                "不等分支",
                "if ((rt)≠(rs)) then (PC)←(PC)+4+((Sign-Extend)offset<<2)",
                paramPattern(3),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000101"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(15, 0, "offset", m -> Utils.labelToBin(m.group(3), 18, true, true).substring(0,16), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "bgez",
                "大于等于0分支",
                "if ((rs)≥0) then (PC)←(PC)+4+((Sign-Extend)offset<<2)",
                paramPattern(2),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000001"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", null, InstructionComponentType.FIXED, "00001"),
                        new InstructionComponent(15, 0, "offset", m -> Utils.labelToBin(m.group(2), 18, true, true).substring(0,16), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "bgtz",
                "大于0分支",
                "if ((rs)＞0) then (PC)←(PC)+4+((Sign-Extend)offset<<2)",
                paramPattern(2),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000111"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", null, InstructionComponentType.FIXED, "00000"),
                        new InstructionComponent(15, 0, "offset", m -> Utils.labelToBin(m.group(2), 18, true, true).substring(0,16), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "blez",
                "小于等于0分支",
                "if ((rs)≤0) then (PC)←(PC)+4+((Sign-Extend)offset<<2)",
                paramPattern(2),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000110"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", null, InstructionComponentType.FIXED, "00000"),
                        new InstructionComponent(15, 0, "offset", m -> Utils.labelToBin(m.group(2), 18, true, true).substring(0,16), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "bltz",
                "小于0分支",
                "if ((rs)＜0) then (PC)←(PC)+4+((Sign-Extend) offset<<2)",
                paramPattern(2),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000001"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", null, InstructionComponentType.FIXED, "00000"),
                        new InstructionComponent(15, 0, "offset", m -> Utils.labelToBin(m.group(2), 18, true, true).substring(0,16), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "bgezal",
                "大于等于0分支（Link）",
                "if ((rs)≥0) then ($31)←(PC)+4,(PC)←(PC)+4+((Sign-Extend) offset<<2)",
                paramPattern(2),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000001"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", null, InstructionComponentType.FIXED, "10001"),
                        new InstructionComponent(15, 0, "offset", m -> Utils.labelToBin(m.group(2), 18, true, true).substring(0,16), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "bltzal",
                "小于0分支（Link）",
                "if ((rs)＜0) then ($31)←(PC)+4,(PC)←(PC)+4+((Sign-Extend) offset<<2)",
                paramPattern(2),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000001"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", null, InstructionComponentType.FIXED, "10000"),
                        new InstructionComponent(15, 0, "offset", m -> Utils.labelToBin(m.group(2), 18, true, true).substring(0,16), InstructionComponentType.OFFSET, ""),
                }
        );

        newInstruction(
                "slti",
                "小于立即数时Set",
                "if ((rs)<(Sign-Extend)immediate) then (rt)←1; else (rt)←0",
                paramPattern(3),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "001010"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(15, 0, "immediate", m -> Utils.literalToBin(m.group(3), 16, true), InstructionComponentType.IMMED, ""),
                }
        );

        newInstruction(
                "sltiu",
                "小于立即数时Set（无符号）",
                "if ((rs)<(Zero-Extend)immediate) then (rt)←1; else (rt)←0",
                paramPattern(3),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "001011"),
                        new InstructionComponent(25, 21, "rs", m -> Register.regToBin(m.group(2)), InstructionComponentType.REG, ""),
                        new InstructionComponent(20, 16, "rt", m -> Register.regToBin(m.group(1)), InstructionComponentType.REG, ""),
                        new InstructionComponent(15, 0, "immediate", m -> Utils.literalToBin(m.group(3), 16, false), InstructionComponentType.IMMED, ""),
                }
        );

        newInstruction(
                "j",
                "无条件跳转",
                "(PC)←((Zero-Extend)address<<2)",
                paramPattern(1),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000010"),
                        new InstructionComponent(25, 0, "target", m -> Utils.labelToBin(m.group(1), 28, false, false).substring(0,26), InstructionComponentType.ADDR, ""),
                }
        );

        newInstruction(
                "jal",
                "暂存下条后跳转（立即数）",
                "($31)←(PC)+4; (PC)←((Zero-Extend)address<<2),",
                paramPattern(1),
                new InstructionComponent[]{
                        new InstructionComponent(31, 26, "op", null, InstructionComponentType.FIXED, "000011"),
                        new InstructionComponent(25, 0, "target", m -> Utils.labelToBin(m.group(1), 28, false, false).substring(0,26), InstructionComponentType.ADDR, ""),
                }
        );

        newInstruction(
                "nop",
                "空转指令",
                "do nothing",
                paramPattern(0), new InstructionComponent[]{
                        new InstructionComponent(31, 0, "NOP", null, InstructionComponentType.FIXED, "0".repeat(32))
                }
        );

    }


    private static void newInstruction(
            String symbol,
            String desc,
            String pseudo,
            Pattern insPattern,
            InstructionComponent[] componentsData
    ) {
        List<InstructionComponent> components = new ArrayList<>();
        for (InstructionComponent data : componentsData) {
            int lBit = data.getLBit();
            int rBit = data.getRBit();
            String descStr = (String) data.getDesc();
            Function<Matcher, String> toBinary = (Function<Matcher, String>) data.getToBinary();
            InstructionComponentType type = (InstructionComponentType) data.getType();
            String val = (String) data.getVal();
            components.add(new InstructionComponent(lBit, rBit, descStr, toBinary, type, val));
        }

        minisysInstructions.add(new Instruction(symbol, desc, pseudo, insPattern, components));
    }

    private static Pattern paramPattern(int num) {
        if (num < 1) {
            return Pattern.compile("^$");
        } else {
            StringBuilder patternStr = new StringBuilder("^");
            for (int i = 0; i < num; i++) {
                patternStr.append("([\\w$-]+),");
            }
            patternStr.deleteCharAt(patternStr.length() - 1); // Remove the last comma
            patternStr.append("$");
            return Pattern.compile(patternStr.toString());
        }
    }
}
