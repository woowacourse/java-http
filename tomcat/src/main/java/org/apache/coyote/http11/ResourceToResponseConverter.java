package org.apache.coyote.http11;

import org.apache.coyote.file.FileExtension;
import org.apache.coyote.file.Resource;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.ResponseHeader;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.response.Charset;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import util.BiValue;
import util.StringUtil;

import static org.apache.coyote.http11.response.Charset.UTF_8;
import static org.apache.coyote.http11.response.ContentType.*;

public class ResourceToResponseConverter {

    private static final String DELIMITER = ";";

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
            case CSS -> combineContentAndCharset(CSS, UTF_8);
            case HTML -> combineContentAndCharset(HTML, UTF_8);
            case JAVASCRIPT -> combineContentAndCharset(JAVASCRIPT, UTF_8);
            case UNKNOWN -> combineContentAndCharset(PLAIN, UTF_8);
        };
    }

    private static String combineContentAndCharset(final ContentType contentType, final Charset charset) {
        return StringUtil.combineWithDelimiter(new BiValue<>(contentType.getMimeType(), charset.getType()), DELIMITER);
    }

    private ResourceToResponseConverter() {}
}
