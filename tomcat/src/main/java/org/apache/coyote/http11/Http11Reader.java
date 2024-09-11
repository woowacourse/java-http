package org.apache.coyote.http11;

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
        List<String> result = new ArrayList<>();
        String newLine;
        while ((newLine = readLine()) != null && !newLine.isEmpty()) {
            result.add(newLine);
        }
        return result;
    }
}
