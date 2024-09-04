package org.apache.coyote.http11.view;

import java.io.BufferedReader;
import java.io.IOException;

public class InputView {

    private final BufferedReader bufferedReader;

    public InputView(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public String readLine() throws IOException {
        String line = bufferedReader.readLine();
        validateLineEmpty(line);

        return line;
    }

    private void validateLineEmpty(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line is Null");
        }
    }
}
