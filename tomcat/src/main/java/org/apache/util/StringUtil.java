package org.apache.util;

import java.util.Arrays;
import java.util.List;

public class StringUtil {

    public static List<String> splitAsList(String str, String delimiter) {
        return Arrays.asList(str.split(delimiter));
    }

}
