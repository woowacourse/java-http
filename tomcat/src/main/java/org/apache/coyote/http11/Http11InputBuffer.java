package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Http11InputBuffer implements AutoCloseable {

    private final InputStream inputStream;
    private final BufferedReader bufferedReader;

    public Http11InputBuffer(InputStream inputStream) {
        this.inputStream = inputStream;
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] buffer = new byte[length];
        int offset = 0;
        for (int read; offset < length; offset += read) {
            read = inputStream.read(buffer, offset, length - offset);
            if (read == -1) {
                throw new EOFException("읽는 중 오류가 발생했습니다.");
            }
        }
        return buffer;
    }

    public String readLine() throws IOException {
        return bufferedReader.readLine();
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }
}
