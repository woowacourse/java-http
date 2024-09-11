package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InputReader {

    private final BufferedReader reader;

    public InputReader(InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public String readRequestLine() throws IOException {
        String line = reader.readLine();

        if (line == null) {
            throw new IllegalArgumentException("잘못된 값입니다.");
        }
        return line;
    }

    public List<String> readHeaders() throws IOException {
        List<String> lines = new ArrayList<>();

        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            lines.add(line);
            line = reader.readLine();
        }

        return lines;
    }

    public String readBody(int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);

        return new String(buffer);
    }
}
