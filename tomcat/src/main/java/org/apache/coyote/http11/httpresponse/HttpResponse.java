package org.apache.coyote.http11.httpresponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.httprequest.HttpRequest;

public class HttpResponse {

    private static final String RESPONSE_LINE_DELIMITER = "\r\n";
    private static final String EXTENSION_DELIMITER = ".";
    private static final String HTML_EXTENSION = ".html";
    private static final String STATIC_PATH = "static";

    private final HttpStatusLine httpStatusLine;
    private final HttpResponseHeader httpResponseHeader;
    private final HttpResponseBody httpResponseBody;

    private HttpResponse(
            HttpStatusLine httpStatusLine,
            HttpResponseHeader httpResponseHeader,
            HttpResponseBody httpResponseBody
    ) {
        this.httpStatusLine = httpStatusLine;
        this.httpResponseHeader = httpResponseHeader;
        this.httpResponseBody = httpResponseBody;
    }

    public static HttpResponseBuilder ok(HttpRequest httpRequest) {
        return new HttpResponseBuilder(new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.OK));
    }

    public static HttpResponseBuilder found(HttpRequest httpRequest) {
        return new HttpResponseBuilder(new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.FOUND));
    }

    public static HttpResponseBuilder unauthorized(HttpRequest httpRequest) {
        return new HttpResponseBuilder(new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.UNAUTHORIZED));
    }

    public byte[] getBytes() {
        String statusLine = httpStatusLine.getString();

        String responseHeader = httpResponseHeader.getString();

        if (httpResponseBody != null) {
            String responseBody = httpResponseBody.getBody();
            String join = String.join(RESPONSE_LINE_DELIMITER,
                    statusLine,
                    responseHeader,
                    responseBody);
            return join.getBytes();
        }
        String join = String.join(RESPONSE_LINE_DELIMITER,
                statusLine,
                responseHeader);
        return join.getBytes();
    }

    public HttpStatusLine getHttpStatusLine() {
        return httpStatusLine;
    }

    public HttpResponseHeader getHttpResponseHeader() {
        return httpResponseHeader;
    }

    public HttpResponseBody getHttpResponseBody() {
        return httpResponseBody;
    }

    public static class HttpResponseBuilder {
        private final HttpStatusLine httpStatusLine;
        private final Map<HttpHeaderName, String> headers;
        private HttpResponseBody httpResponseBody;

        public HttpResponseBuilder(HttpStatusLine httpStatusLine) {
            this.httpStatusLine = httpStatusLine;
            this.headers = new HashMap<>();
        }

        public HttpResponseBuilder location(String location) {
            headers.put(HttpHeaderName.LOCATION, location);
            return this;
        }

        public HttpResponseBuilder contentType(String contentType) {
            headers.put(HttpHeaderName.CONTENT_TYPE, contentType);
            return this;
        }

        public HttpResponseBuilder contentLength(String contentLength) {
            headers.put(HttpHeaderName.CONTENT_LENGTH, contentLength);
            return this;
        }

        public HttpResponseBuilder setCookie(String setCookie) {
            headers.put(HttpHeaderName.SET_COOKIE, setCookie);
            return this;
        }

        public HttpResponseBuilder staticResource(String path) throws IOException, URISyntaxException {
            if (!path.contains(EXTENSION_DELIMITER)) {
                path += HTML_EXTENSION;
            }
            String fileName = STATIC_PATH + path;
            var resourceUrl = getClass().getClassLoader().getResource(fileName);
            if (resourceUrl == null) {
                throw new NotFoundException("존재하지 않는 경로입니다.");
            }
            Path filePath = Path.of(resourceUrl.toURI());
            String responseBody = new String(Files.readAllBytes(filePath));

            contentType(Files.probeContentType(filePath) + ContentType.UTF_8.getCharset());
            contentLength(String.valueOf(responseBody.getBytes().length));
            this.httpResponseBody = new HttpResponseBody(responseBody);

            return this;
        }

        public HttpResponseBuilder responseBody(String responseBody) {
            this.httpResponseBody = new HttpResponseBody(responseBody);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(httpStatusLine, new HttpResponseHeader(headers), httpResponseBody);
        }
    }
}
