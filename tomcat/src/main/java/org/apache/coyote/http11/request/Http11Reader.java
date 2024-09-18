package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Http11Reader extends BufferedReader {
    public Http11Reader(InputStream inputStream) {
        super(new InputStreamReader(inputStream));
    }

    public List<String> readLines() throws IOException {
        List<String> result = new ArrayList<>(readLinesWhileBuffered());
        if(ready()) {
            result.add("");
            result.add(readWhileBuffered());
        }
        return result;
    }

    private String readWhileBuffered() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (ready()) {
            sb.append((char) read());
        }
        return sb.toString();
    }

    private List<String> readLinesWhileBuffered() throws IOException {
        String newLine;
        List<String> lines = new ArrayList<>();
        while ((newLine = readLine()) != null && !newLine.isBlank()) {
            lines.add(newLine);
        }
        return lines;
    }
}
