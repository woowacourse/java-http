package org.apache.coyote.http11.file;

import nextstep.jwp.exception.NotFoundException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ResourceLoader {

    private static final String STATIC_RESOURCE_LOCATION = "static/";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_URI = "/";

    private ResourceLoader() {
    }

    public static String getContent(final String uri) throws IOException {
        if (uri.equals(DEFAULT_URI)) {
            return DEFAULT_RESPONSE_BODY;
        }

        final URL resource = ResourceLoader.class.getClassLoader()
                .getResource(STATIC_RESOURCE_LOCATION + uri);
        validateResourceExists(resource);
        final Path path = new File(resource.getPath()).toPath();
        final byte[] bytes = Files.readAllBytes(path);

        return new String(bytes);
    }

    public static String getContentType(final String uri) throws IOException {
        final URL resource = ResourceLoader.class.getClassLoader()
                .getResource(STATIC_RESOURCE_LOCATION + uri);
        validateResourceExists(resource);
        final Path path = new File(resource.getPath()).toPath();

        return Files.probeContentType(path);
    }

    private static void validateResourceExists(final URL resource) {
        if (Objects.isNull(resource)) {
            throw new NotFoundException("파일을 찾을 수 없습니다.");
        }
    }
}
