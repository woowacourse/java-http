package org.apache.catalina.utils;

import java.io.BufferedReader;
import java.io.IOException;

public class IOUtils {
    public static String readData(final BufferedReader bufferedReader,
                                  final int length) throws IOException {
        final char[] bodyArr = new char[length];
        bufferedReader.read(bodyArr, 0, length);
        return String.copyValueOf(bodyArr);
    }
}
