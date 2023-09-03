package org.apache.coyote.httpresponse.header;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceReader {

    private static final Logger log = LoggerFactory.getLogger(ResourceReader.class);
    private static final String STATIC_RESOURCE_PREFIX = "static";
    private static final String DEFAULT_VALUE = "Hello world!";

    private ResourceReader() {
    }

    public static String read(final String path) {
        try {
            final URL resource = ClassLoader.getSystemClassLoader().getResource(STATIC_RESOURCE_PREFIX + path);
            final File file = new File(resource.getFile());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException | NullPointerException e) {
            log.error("잘못된 파일을 요청했습니다.");
            return DEFAULT_VALUE;
        }
    }
}
