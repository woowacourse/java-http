package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamReader {

    private StreamReader() {
    }

    public static String readAllLine(InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder content = new StringBuilder();
        while (reader.ready()) {
            content.append(reader.readLine());
            content.append('\n');
        }
        return content.toString();
    }
}
