import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Linker {
    /**
     * 计算asm_中有多少条指令（考虑了宏指令展开）
     */
    public static int countIns(String asm) {
        String[] lines = asm.replaceAll("\r\n", "\n")
                .replaceAll("#(.*)\\n", "\n")
                .split("\n");

        int insCount = 0;
        for (String line : lines) {
            if (line.matches("^(\\w+):\\s*$")) {
                continue;  // 纯label行
            } else {
                insCount += 1;
            }
        }
        return insCount;
    }

    /**
     * 链接所有部分，返回链接后的64KB全内存汇编
     */
    public static String linkAll(String biosASM, String userASM, String intEntryASM, String intHandlerASM) {
        // BIOS 0x00000000 ~ 0x00000499
        int biosASMInsCount = countIns(biosASM);
        assertLength(biosASMInsCount, 320, "BIOS 程序段过长。");
        int biosNopPadding = 320 - biosASMInsCount;

        // User App 0x00000500 ~ 0x00005499
        int userASMInsCount = countIns(userASM);
        assertLength(userASMInsCount, 5120, "用户程序段过长。");
        int userNopPadding = 5120 - userASMInsCount;

        // Empty 0x00005500 ~ 0x0000EFFF
        int middleEmptyNopPadding = 39680 / 4;

        // Interrupt Handler Entry 0x0000F000 ~ 0x0000F499
        int intEntryASMInsCount = countIns(intEntryASM);
        assertLength(intEntryASMInsCount, 320, "中断处理程序入口过长。");
        int intEntryNopPadding = 320 - intEntryASMInsCount;

        // Interrupt Handler 0x0000F500 ~ 0x0000FFFF
        int intHandlerASMInsCount = countIns(intHandlerASM);
        assertLength(intHandlerASMInsCount, 704, "中断处理程序过长。");
        int intHandlerNopPadding = 704 - intHandlerASMInsCount;

        int totalLength =
                biosASMInsCount +
                        biosNopPadding +
                        userASMInsCount +
                        userNopPadding +
                        middleEmptyNopPadding +
                        intEntryASMInsCount +
                        intEntryNopPadding +
                        intHandlerASMInsCount +
                        intHandlerNopPadding; // instructions
        assert totalLength * 4 == 0x0000ffff + 1 : "IMEM 布局总长度不正确：有 " + totalLength * 4 + " Bytes.";

        StringBuilder allProgram = new StringBuilder();
        // BIOS
        allProgram.append("# ====== BIOS START ======\n");
        allProgram.append("# BIOS Length = ").append(biosASMInsCount).append("\n");
        allProgram.append(biosASM).append("\n");
        allProgram.append("# BIOS Padding = ").append(biosNopPadding).append("\n");
        allProgram.append("nop\n".repeat(biosNopPadding));
        allProgram.append("# ====== BIOS END ======\n");

        // User Application
        allProgram.append("# ====== User Application START ======\n");
        allProgram.append("# User Application Length = ").append(userASMInsCount).append("\n");
        allProgram.append(userASM).append("\n");
        allProgram.append("# User Application Padding = ").append(userNopPadding).append("\n");
        allProgram.append("nop\n".repeat(userNopPadding));
        allProgram.append("# ====== User Application END ======\n");
        //middleEmptyNop
        allProgram.append("# ====== Middle Empty START ======\n");
        allProgram.append("# Middle Empty Length = ").append(middleEmptyNopPadding).append("\n");
        allProgram.append("nop\n".repeat(middleEmptyNopPadding));
        allProgram.append("# ====== Middle Empty END ======\n");
        // Interrupt Entry
        allProgram.append("# ====== Interrupt Entry START ======\n");
        allProgram.append("# Interrupt Entry Length = ").append(intEntryASMInsCount).append("\n");
        allProgram.append(intEntryASM).append("\n");
        allProgram.append("# Interrupt Entry Padding = ").append(intEntryNopPadding).append("\n");
        allProgram.append("nop\n".repeat(intEntryNopPadding));
        allProgram.append("# ====== Interrupt Entry END ======\n");

        // Interrupt Handler
        allProgram.append("# ====== Interrupt Handler START ======\n");
        allProgram.append("# Interrupt Entry Length = ").append(intHandlerASMInsCount).append("\n");
        allProgram.append(intHandlerASM).append("\n");
        allProgram.append("# Interrupt Entry Padding = ").append(intHandlerNopPadding).append("\n");
        allProgram.append("nop\n".repeat(intHandlerNopPadding));
        allProgram.append("# ====== Interrupt Handler END ======\n");

        return allProgram.toString();
    }

    private static void assertLength(int value, int maxLength, String errorMessage) {
        if (value > maxLength) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

}
