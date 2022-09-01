package org.apache.coyote.http11.request;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.http11.response.MimeType;

public class ResourceLocator {

    private final String prefix;

    public ResourceLocator(String prefix) {
        this.prefix = prefix;
    }

    public Resource findResource(final String path) {

        if ("/".equals(path)) {
            return new Resource(MimeType.of("html"), "Hello world!");
        }

        try {
            URL url = getClass().getResource(prefix + path);
            Path filePath = Path.of(Objects.requireNonNull(url).getPath());
            String extension = path.substring(path.lastIndexOf(".") + 1);
            return new Resource(MimeType.of(extension), Files.readString(filePath));
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다. path = " + prefix + path);
        }
    }
}
