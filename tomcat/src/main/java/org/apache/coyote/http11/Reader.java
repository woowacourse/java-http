package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class Reader {

    private final BufferedReader reader;

    Reader(InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.reader = new BufferedReader(inputStreamReader);
    }

    String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid byte read");
        }
    }

    String readByLength(int length) {
        final char[] buffer = new char[length];
        try {
            reader.read(buffer, 0, length);
            return new String(buffer);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid byte read");
        }
    }
}
