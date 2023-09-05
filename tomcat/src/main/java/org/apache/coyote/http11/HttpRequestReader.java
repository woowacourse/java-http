package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringJoiner;

public class HttpRequestReader {

    private static final String END_OF_PARAGRAPH = "";

    public static String readLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    public static String readHeader(BufferedReader reader) throws IOException {
        String line;
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        while ((line = reader.readLine()) != null & !END_OF_PARAGRAPH.equals(line)) {
            joiner.add(line);
        }
        return joiner.toString();
    }

    public static String readBody(BufferedReader reader, int length) throws IOException {
        char[] buffer = new char[length];
        reader.read(buffer, 0, length);
        return new String(buffer);
    }
}
