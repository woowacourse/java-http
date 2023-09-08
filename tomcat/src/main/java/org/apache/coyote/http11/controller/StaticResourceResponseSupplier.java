package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceResponseSupplier {

    private static final Map<String, ContentType> extensionContentTypes = Map.of(
        ".html", ContentType.HTML_UTF8,
        ".css", ContentType.CSS,
        ".js", ContentType.JAVASCRIPT
    );

    private StaticResourceResponseSupplier() {
    }

    public static HttpResponse getWithExtensionContentType(
        final String resourcePath
    ) throws IOException {
        if (isExist(resourcePath)) {
            final String body = new String(getResourceByteArray(resourcePath));
            final ContentType contentType = extensionContentTypes.entrySet().stream()
                .filter(type -> resourcePath.endsWith(type.getKey()))
                .findFirst()
                .map(Entry::getValue)
                .orElse(ContentType.TEXT_PLAIN);
            return HttpResponse.ok(contentType, body);
        }
        return HttpResponse.notFound();
    }

    public static HttpResponse getWithSpecificContentType(
        final String resourcePath,
        final ContentType contentType
    ) throws IOException {
        if (isExist(resourcePath)) {
            final String body = new String(getResourceByteArray(resourcePath));
            return HttpResponse.ok(contentType, body);
        }
        return HttpResponse.notFound();
    }

    private static boolean isExist(final String resourcePath) {
        return Objects.nonNull(getResource(resourcePath));
    }

    private static URL getResource(final String targetUrl) {
        return StaticResourceResponseSupplier.class.getClassLoader()
            .getResource("static" + targetUrl);
    }

    private static byte[] getResourceByteArray(final String targetUrl) throws IOException {
        final URL resource = getResource(targetUrl);

        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }
}
