package org.apache.coyote.http11;

import org.apache.coyote.file.FileExtension;
import org.apache.coyote.file.Resource;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.ResponseHeader;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceToResponseConverter {

    public static HttpResponse convert(final HttpStatusCode statusCode, final Resource resource) {
        return new HttpResponse(statusCode, createDefaultHeaders(resource), "HTTP/1.1", resource.getBytes());
    }

    public static HttpResponse redirect(final HttpStatusCode statusCode, final Path path) {
        final Headers headers = new Headers();
        headers.put(ResponseHeader.LOCATION, path.value());
        return new HttpResponse(statusCode, headers, "HTTP/1.1", new byte[]{});
    }


    private static Headers createDefaultHeaders(final Resource resource) {
        final Headers headers = new Headers();
        headers.put(ResponseHeader.CONTENT_TYPE, getContentType(resource.getExtension()));
        headers.put(ResponseHeader.CONTENT_LENGTH, String.valueOf(resource.length()));
        return headers;
    }

    private static String getContentType(final FileExtension extension) {
        return switch (extension) {
            case CSS -> "text/css;charset=utf-8 ";
            case HTML -> "text/html;charset=utf-8 ";
            case JAVASCRIPT -> "text/javascript;charset=utf-8 ";
            case UNKNOWN -> "text/plain;charset=utf-8 ";
        };
    }

    private ResourceToResponseConverter() {}
}
