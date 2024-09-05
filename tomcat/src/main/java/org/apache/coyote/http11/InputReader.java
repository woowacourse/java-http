package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InputReader {

    private static final String NEWLINE = System.lineSeparator();
    private static final String EMPTY_LINE = "";

    private final List<String> requestLines;

    public InputReader(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestLines = readAllLines(bufferedReader);
    }

    private List<String> readAllLines(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        while (bufferedReader.ready()) {
            lines.add(bufferedReader.readLine());
        }
        return lines;
    }

    public String readRequestLine() {
        return requestLines.getFirst();
    }

    public List<String> readHeaders() {
        int toIndex = requestLines.indexOf(EMPTY_LINE);
        return requestLines.subList(1, toIndex);
    }

    public String readBody() {
        int fromIndex = requestLines.indexOf(EMPTY_LINE) + 1;
        List<String> bodyLines = requestLines.subList(fromIndex, requestLines.size());

        return String.join(NEWLINE, bodyLines);
    }
}
