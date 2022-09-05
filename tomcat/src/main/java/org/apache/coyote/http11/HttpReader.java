package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpReader {

    private final List<String> lines;

    public HttpReader(final BufferedReader bufferedReader) throws IOException {
        this.lines = readAllLine(bufferedReader);
    }

    private List<String> readAllLine(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            lines.add(line);
        }
        return lines;
    }

    public String getStartLine() {
        return lines.get(0);
    }

    public List<String> getHeaders() {
        return lines.stream()
                .skip(1)
                .collect(Collectors.toList());
    }
}
