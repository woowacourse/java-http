package org.apache.coyote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public record TextResource(String path, String type, String content) {

    public static TextResource fromPath(String path) throws IOException {
        try (
                InputStream inputStream = Objects.requireNonNull(
                        ClassLoader.getSystemClassLoader().getResourceAsStream(path)
                );
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            String extension = Arrays.asList(path.split("\\.")).getLast();
            String content = bufferedReader.lines()
                    .collect(Collectors.joining("\r\n", "", "\r\n"));

            return new TextResource(path, extension, content);
        }
    }
}
