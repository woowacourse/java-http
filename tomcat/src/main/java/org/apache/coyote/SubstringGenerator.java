package org.apache.coyote;

import java.util.List;

public class SubstringGenerator {

    public static List<String> splitByFirst(String divisor, String dividend) {
        int firstIndex = dividend.indexOf(divisor);
        return split(dividend, firstIndex);
    }

    public static List<String> splitByLast(String divisor, String dividend) {
        int lastIndex = dividend.lastIndexOf(divisor);
        return split(dividend, lastIndex);
    }

    private static List<String> split(String dividend, int lastIndex) {
        String first = dividend.substring(0, lastIndex);
        String last = dividend.substring(lastIndex + 1);
        return List.of(first, last);
    }
}
