package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceReader {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceReader.class);

    public String read(String resourcePath) throws URISyntaxException, IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + resourcePath);

        if (resource == null) {
            log.warn("존재하지 않는 경로 : {}", resourcePath);
            return null;
        }

        final Path path = Paths.get(resource.toURI());

        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
