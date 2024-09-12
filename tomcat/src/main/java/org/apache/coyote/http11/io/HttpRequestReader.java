package org.apache.coyote.http11.io;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestReader {

    private final BufferedReader bufferedReader;

    public HttpRequestReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public String readLine() throws IOException {
        return bufferedReader.readLine();
    }

    public String read(int length) throws IOException {
        char[] buffer = new char[length];
        int readCount = bufferedReader.read(buffer, 0, length);

        if (readCount == -1) {
            return null;
        }
        return new String(buffer);
    }

    public boolean isReadable() throws IOException {
        return bufferedReader.ready();
    }
}
