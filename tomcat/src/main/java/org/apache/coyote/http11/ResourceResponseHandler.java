package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.apache.coyote.http11.HttpStatus.NOT_FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;

public class ResourceResponseHandler {

    public HttpResponse handleStaticResponse(String uri) {
        try {
            return handleStaticResponse(OK, uri);
        } catch (Exception e) {
            return handleStaticResponse(NOT_FOUND, "/404.html");
        }
    }

    public HttpResponse handleStaticResponse(HttpStatus status, String uri) {
        File page = getFile(uri);
        String contentType = getMimeType(page);
        String body = buildResponseBody(page);
        return HttpResponse.builder()
                .setHttpStatus(status)
                .setContentType(new ContentType(contentType))
                .setBody(body)
                .build();
    }

    @Nullable
    private File getFile(final String uri) {
        if (uri.equals("/")) {
            return null;
        }
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        return ofNullable(resource.getFile()).map(File::new).orElse(null);
    }

    private String getMimeType(final File file) {
        return ofNullable(file)
                .map(File::toURI)
                .map(this::getContentType)
                .orElse("text/plain");
    }

    private String getContentType(final URI uri) {
        try {
            final URLConnection urlConnection = requireNonNull(uri).toURL().openConnection();
            return urlConnection.getContentType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildResponseBody(final File file) {
        return ofNullable(file)
                .map(this::readString)
                .orElse("Hello world!");
    }

    private String readString(final File file) {
        try {
            final Path path = file.toPath();
            final byte[] bytes = Files.readAllBytes(path);
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }

}
