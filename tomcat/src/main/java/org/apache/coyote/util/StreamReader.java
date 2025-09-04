package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

public class StreamReader {

    public static String readFile(InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.lines().collect(Collectors.joining("\n"));
    }

    public static String readRequest(InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.lines().collect(Collectors.joining("\r\n"));
    }
}
