package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceResponseHandler {

    public ResponseBody buildBodyFrom(String uri) {
        File page = getFile(uri);
        String mimeType = getMimeType(page);
        String body = buildResponseBody(page);

        return ResponseBody.of(new ContentType(mimeType), body);
    }

    private File getFile(final String uri) {
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        if (resource == null) {
            throw new IllegalArgumentException("해당 리소스가 존재하지 않습니다.");
        }
        return new File(resource.getFile());
    }

    private String getMimeType(final File file) {
        final var uri = file.toURI();
        return getContentType(uri);
    }

    private String getContentType(final URI uri) {
        try {
            final URLConnection urlConnection = uri.toURL().openConnection();
            return urlConnection.getContentType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildResponseBody(final File file) {
        try {
            final Path path = file.toPath();
            final byte[] bytes = Files.readAllBytes(path);
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
