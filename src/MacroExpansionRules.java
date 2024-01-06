import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacroExpansionRules {

    static class MacroExpansionRule {
        Pattern pattern;

        String[] replacer;

        MacroExpansionRule(String pattern, String[] replacer) {
            this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            this.replacer = replacer;
        }

        public String[] replace(Matcher matcher) {
            String[] replaced = new String[replacer.length];
            for (int i = 0; i < replacer.length; i++) {
                replaced[i] = replaceGroups(matcher, replacer[i]);
            }
            return replaced;
        }

        private static String replaceGroups(Matcher matcher, String input) {
            // Check for presence of ${RegExp.$3}, ${RegExp.$2}, ${RegExp.$1} before replacing
            if (input.contains("${RegExp.$3}")) {
                input = input.replace("${RegExp.$3}", getGroup(matcher, 3));
            }
            if (input.contains("${RegExp.$2}")) {
                input = input.replace("${RegExp.$2}", getGroup(matcher, 2));
            }
            if (input.contains("${RegExp.$1}")) {
                input = input.replace("${RegExp.$1}", getGroup(matcher, 1));
            }
            return input;
        }

        private static String getGroup(Matcher matcher, int groupIndex) {
            try {
                return matcher.group(groupIndex);
            } catch (IndexOutOfBoundsException e) {
                // Handle exception if the group index is out of bounds
                return "指令错误！";
            }
        }
    }

    public static Map<String, MacroExpansionRule> expansionRules = new HashMap<>();

    static {
        expansionRules.put("push", new MacroExpansionRule("^push\\s+(\\$\\w{1,2})$", new String[]{"addi $sp, $sp, -4", "sw ${RegExp.$1}, 0($sp)"}));
        expansionRules.put("pop", new MacroExpansionRule("^pop\\s+(\\$\\w{1,2})$", new String[]{"lw ${RegExp.$1}, 0($sp)", "addi $sp, $sp, 4"}));
        expansionRules.put("jg", new MacroExpansionRule("^jg\\s+(\\$\\w{1,2}),\\s+(\\$\\w{1,2}),\\s+(\\w+)$", new String[]{"addi $sp, $sp, -4", "sw $1, 0($sp)", "slt $1, ${RegExp.$2}, ${RegExp.$1}", "bne $1, $0, ${RegExp.$3}", "lw $1, 0($sp)", "addi $sp, $sp, 4"}));
        expansionRules.put("jge", new MacroExpansionRule("^jge\\s+(\\$\\w{1,2}),\\s+(\\$\\w{1,2}),\\s+(\\w+)$", new String[]{"addi $sp, $sp, -4", "sw $1, 0($sp)", "slt $1, ${RegExp.$1}, ${RegExp.$2}", "beq $1, $0, ${RegExp.$3}", "lw $1, 0($sp)", "addi $sp, $sp, 4"}));
        expansionRules.put("jl", new MacroExpansionRule("^jl\\s+(\\$\\w{1,2}),\\s+(\\$\\w{1,2}),\\s+(\\w+)$", new String[]{"addi $sp, $sp, -4", "sw $1, 0($sp)", "slt $1, ${RegExp.$1}, ${RegExp.$2}", "bne $1, $0, ${RegExp.$3}", "lw $1, 0($sp)", "addi $sp, $sp, 4"}));
        expansionRules.put("jle", new MacroExpansionRule("^jle\\s+(\\$\\w{1,2}),\\s+(\\$\\w{1,2}),\\s+(\\w+)$", new String[]{"addi $sp, $sp, -4", "sw $1, 0($sp)", "slt $1, ${RegExp.$2}, ${RegExp.$1}", "beq $1, $0, ${RegExp.$3}", "lw $1, 0($sp)", "addi $sp, $sp, 4"}));
        expansionRules.put("move", new MacroExpansionRule("^move\\s+(\\$\\w{1,2}),\\s+(\\$\\w{1,2})$", new String[]{"or ${RegExp.$1}, ${RegExp.$2}, $zero"}));
    }

}