package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringJoiner;

class HttpReader {

    private final BufferedReader reader;

    HttpReader(InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.reader = new BufferedReader(inputStreamReader);
    }

    String readHttpRequest() {
        String line;
        String contentLength = "";
        final StringJoiner joiner = new StringJoiner("\r\n");
        while (!(line = readLine()).isEmpty()) {
            if (line.startsWith("Content-Length")) {
                contentLength = line.split(":")[1].trim();
            }
            joiner.add(line);
        }

        if (contentLength.isEmpty()) {
            return joiner.toString();
        }

        final String messageBody = readByLength(Integer.parseInt(contentLength));
        joiner.add("").add(messageBody);
        return joiner.toString();
    }

    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid byte read");
        }
    }

    private String readByLength(int length) {
        final char[] buffer = new char[length];
        try {
            reader.read(buffer, 0, length);
            return new String(buffer);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid byte read");
        }
    }
}
