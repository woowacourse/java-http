package org.apache.coyote.http11.view;

import java.io.BufferedReader;
import java.io.IOException;

public class InputView {

    private final BufferedReader bufferedReader;

    public InputView(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public String readLine() throws IOException {
        return bufferedReader.readLine();
    }

    public boolean isReadable() throws IOException {
        return bufferedReader.ready();
    }
}
