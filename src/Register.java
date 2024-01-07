public class Register {
    private static final String[] registerNames = {
            "zero", "at",
            "v0", "v1",
            "a0", "a1", "a2", "a3",
            "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
            "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
            "t8", "t9",
            "k0", "k1",
            "gp", "sp", "fp",
            "ra",
    };

    /**
     * Returns the five-bit binary representation corresponding to the register.
     * @param reg Register name (e.g., $1, $sp)
     * @return Binary representation of the register
     * @throws IllegalArgumentException if the register is invalid
     */
    public static String regToBin(String reg) {
        reg = reg.replace("$", "").trim();
        int regNumber;
        if (reg.matches("\\d+")) {
            regNumber = Integer.parseInt(reg);
        } else {
            regNumber = indexOfRegister(reg);
        }
        return Utils.decToBin(regNumber, 5,false);
    }

    private static int indexOfRegister(String reg) {
        for (int i = 0; i < registerNames.length; i++) {
            if (registerNames[i].equals(reg)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid register: " + reg);
    }
}