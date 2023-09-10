package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringJoiner;

public class HttpRequestReader {

    private static final String END_OF_PARAGRAPH = "";

    public String readLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    public String readHeader(BufferedReader reader) throws IOException {
        String line;
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        while ((line = reader.readLine()) != null && !END_OF_PARAGRAPH.equals(line)) {
            joiner.add(line);
        }
        return joiner.toString();
    }

    public String readBody(BufferedReader reader, int length) throws IOException {
        char[] buffer = new char[length];
        reader.read(buffer, 0, length);
        return new String(buffer);
    }
}
