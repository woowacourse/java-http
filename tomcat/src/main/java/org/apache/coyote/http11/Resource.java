package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public record Resource(String path, String type, String content) {

    public static Resource fromPath(String path) throws IOException {
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

            return new Resource(path, extension, content);
        }
    }
}
