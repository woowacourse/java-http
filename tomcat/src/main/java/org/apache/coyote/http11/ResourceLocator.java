package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import org.apache.coyote.http11.general.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLocator {

    private static final Logger log = LoggerFactory.getLogger(ResourceLocator.class);

    private final String prefix;

    public ResourceLocator(String prefix) {
        this.prefix = prefix;
    }

    public Resource locate(String path) {
        Objects.requireNonNull(path);
        log.debug("request path = {}", path);
        try {
            URL url = getClass().getResource(prefix + path);
            Path filePath = Path.of(Objects.requireNonNull(url).getPath());
            return new Resource(extractExtension(path).toContentType(), Files.readString(filePath));
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다. path = " + prefix + path);
        }
    }

    private Extension extractExtension(String path) {
        return Extension.of(path.substring(path.lastIndexOf(".") + 1));
    }

    private enum Extension {
        HTML("html", "text/html"),
        CSS("css", "text/css"),
        JS("js", "text/javascript"),
        ICO("ico", "image/x-icon");

        private final String extension;
        private final String contentType;

        Extension(String extension, String contentType) {
            this.extension = extension;
            this.contentType = contentType;
        }

        static Extension of(String extension) {
            return Arrays.stream(values())
                    .filter(it -> it.extension.equals(extension))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 확장자 = " + extension));
        }

        ContentType toContentType() {
            if (this == HTML || this == CSS || this == JS) {
                return ContentType.of(contentType);
            }
            throw new IllegalStateException("지원하지 않는 확장자 = " + this);
        }
    }
}
