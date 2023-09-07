package org.apache.coyote.http11.resource;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FileHandler {
    public String readFromResourcePath(String path) throws IOException {
        final var responseBody = new StringBuilder();

        final URL indexPageURL = this.getClass().getClassLoader().getResource(path);
        final File indexFile;
        try {
            indexFile = new File(indexPageURL.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try (
                final FileInputStream fileInputStream = new FileInputStream(indexFile);
                final BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))
        ) {
            while (bufferedReader.ready()) {
                responseBody
                        .append(bufferedReader.readLine())
                        .append(System.lineSeparator());
            }
        }

        return responseBody.toString();
    }
}
