package org.apache.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.exception.TempException;

public class StringUtil {

    private StringUtil() {
    }

    public static List<String> splitAsList(String str, String delimiter) {
        return Arrays.asList(str.split(delimiter));
    }

    public static String readOneLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new TempException();
        }
    }

}
