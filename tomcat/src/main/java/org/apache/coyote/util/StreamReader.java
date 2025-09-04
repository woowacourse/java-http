package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamReader {

    private final String lineSeparator;

    public StreamReader(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public String readAllLine(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder content = new StringBuilder();
        while (reader.ready()) {
            content.append(reader.readLine());
            content.append(lineSeparator);
        }
        return content.toString();
    }
}
