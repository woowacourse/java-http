package org.apache.coyote.http11;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceProvider {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceProvider.class);

    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html;charset=utf-8",
            "css", "text/css",
            "js", "application/javascript",
            "png", "image/png",
            "ico", "image/x-icon"
    );

    public static StaticResource getStaticResource(final String path) {
        return new StaticResource(getMimeType(path), loadResourceContent(path));
    }

    private static byte[] loadResourceContent(final String path) {
        final String resourcePath = "static" + path;
        log.debug("resourcePath: {}", resourcePath);

        try {
            final URL url = StaticResourceProvider.class.getClassLoader().getResource(resourcePath);
            final File file = new File(url.getFile());
            return Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private static String getMimeType(final String path) {
        final int dotIndex = path.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < path.length() - 1) {
            final String extension = path.substring(dotIndex + 1);
            return MIME_TYPES.getOrDefault(extension, "text/html;charset=utf-8");
        }
        return "text/html;charset=utf-8";
    }
}
