package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.exception.ResourceNotFoundException;

public class ResourceResponseBuilder {

    public static ResponseBody build(URL resourceUrl) {
        File page = getFile(resourceUrl);
        String mimeType = getMimeType(page);
        String body = buildResponseBody(page);

        return ResponseBody.of(new ContentType(mimeType), body);
    }

    private static File getFile(final URL url) {
        if (url == null) {
            throw new ResourceNotFoundException();
        }
        return new File(url.getFile());
    }

    private static String getMimeType(final File file) {
        final var uri = file.toURI();
        return getContentType(uri);
    }

    private static String getContentType(final URI uri) {
        try {
            final URLConnection urlConnection = uri.toURL().openConnection();
            return urlConnection.getContentType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildResponseBody(final File file) {
        try {
            final Path path = file.toPath();
            final byte[] bytes = Files.readAllBytes(path);
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
