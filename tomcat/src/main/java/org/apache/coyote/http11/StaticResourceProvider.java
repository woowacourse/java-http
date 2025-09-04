package org.apache.coyote.http11;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceProvider {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceProvider.class);

    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html",
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

        try {
            final URL url = StaticResourceProvider.class.getClassLoader().getResource(resourcePath);
            final Path filePath = Paths.get(url.toURI());
            return Files.readAllBytes(filePath);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private static String getMimeType(String path) {
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < path.length() - 1) {
            String extension = path.substring(dotIndex + 1);
            return MIME_TYPES.getOrDefault(extension, "text/html");
        }
        return "text/html";
    }
}
