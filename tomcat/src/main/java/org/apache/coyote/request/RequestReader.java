package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestReader implements Reader{
    private final List<String> lines;

    private final BufferedReader reader;

    public RequestReader(final List<String> lines, final BufferedReader reader) {
        this.lines = lines;
        this.reader = reader;
    }

    public static Reader from(final BufferedReader bufferedReader) throws IOException {
        return new RequestReader(readAllLines(bufferedReader), bufferedReader);
    }

    private static List<String> readAllLines(final BufferedReader reader) throws IOException {
        final ArrayList<String> lines = new ArrayList<>();
        while (true) {
            final String line = reader.readLine();
            if ("".equals(line)) {
                break;
            }
            lines.add(line);
        }
        return lines;
    }

    @Override
    public String getFirstLine() {
        return lines.get(0);
    }

    @Override
    public List<String> getHeader() {
        lines.remove(0);
        return lines;
    }

    @Override
    public String getBody(final int bodyLength) throws IOException {
        char[] buffer = new char[bodyLength];
        reader.read(buffer, 0, bodyLength);
        return new String(buffer);
    }
}
