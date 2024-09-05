package org.apache.coyote.http11;

import org.apache.coyote.file.FileExtension;
import org.apache.coyote.file.Resource;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceToResponseConverter {

    public static HttpResponse convert(final HttpStatusCode statusCode, final Resource resource) {
        return new HttpResponse(statusCode, createDefaultHeaders(resource), "HTTP/1.1", resource.getBytes());
    }
    public static HttpResponse redirect(final HttpStatusCode statusCode,final Path path) {
        final Headers headers =new Headers();
        headers.put("Location",path.value());
        return new HttpResponse(statusCode, headers, "HTTP/1.1",new byte[]{});
    }


    private static Headers createDefaultHeaders(final Resource resource) {
        final Headers headers = new Headers();
        headers.put("Content-Type: " + getContentType(resource.getExtension()));
        headers.put("Content-Length: " + resource.length() + " ");
        return headers;
    }

    private static String getContentType(final FileExtension extension) {
        if (extension.equals(FileExtension.HTML)) {
            return "text/html;charset=utf-8 ";
        } else if (extension.equals(FileExtension.CSS)) {
            return "text/css; ";
        }
        return "text/plain;charset=utf-8 ";
    }

    private ResourceToResponseConverter() {}
}
