package org.apache.coyote.http11.httpresponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.CharSet;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.httprequest.HttpRequest;

public class HttpResponse {

    private static final String RESPONSE_LINE_DELIMITER = "\r\n";
    private static final String EXTENSION_DELIMITER = ".";
    private static final String HTML_EXTENSION = ".html";
    private static final String STATIC_PATH = "static";

    private HttpStatusLine httpStatusLine;
    private final HttpResponseHeader httpResponseHeader;
    private HttpResponseBody httpResponseBody;

    private HttpResponse(
            HttpStatusLine httpStatusLine,
            HttpResponseHeader httpResponseHeader,
            HttpResponseBody httpResponseBody
    ) {
        this.httpStatusLine = httpStatusLine;
        this.httpResponseHeader = httpResponseHeader;
        this.httpResponseBody = httpResponseBody;
    }

    public HttpResponse() {
        this(null, new HttpResponseHeader(), null);
    }

    public void ok(HttpRequest httpRequest) {
        this.httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.OK);
    }

    public void found(HttpRequest httpRequest) {
        this.httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.FOUND);
    }

    public void unauthorized(HttpRequest httpRequest) {
        this.httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.UNAUTHORIZED);
    }

    public void addHeader(HttpHeaderName headerName, String value) {
        httpResponseHeader.addHeader(headerName, value);
    }

    public void location(String path) {
        addHeader(HttpHeaderName.LOCATION, path);
    }

    public void setCookie(String cookie) {
        addHeader(HttpHeaderName.SET_COOKIE, cookie);
    }

    public void contentLength(String contentLength) {
        addHeader(HttpHeaderName.CONTENT_LENGTH, contentLength);
    }

    public void contentType(String contentType) {
        addHeader(HttpHeaderName.CONTENT_TYPE, contentType);
    }

    public void responseBody(String responseBody) {
        this.httpResponseBody = new HttpResponseBody(responseBody);
    }

    public void staticResource(String path) {
        try {
            path = settingExtension(path);
            String fileName = STATIC_PATH + path;
            var resourceUrl = getClass().getClassLoader().getResource(fileName);
            validateResourceUrl(resourceUrl);
            Path filePath = Path.of(resourceUrl.toURI());
            String responseBody = new String(Files.readAllBytes(filePath));

            setHttpHeader(filePath, responseBody);
            this.httpResponseBody = new HttpResponseBody(responseBody);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e.getMessage() + e);
        }
    }

    private String settingExtension(String path) {
        if (!path.contains(EXTENSION_DELIMITER)) {
            path += HTML_EXTENSION;
        }
        return path;
    }

    private void validateResourceUrl(URL resourceUrl) {
        if (resourceUrl == null) {
            throw new NotFoundException("존재하지 않는 경로입니다.");
        }
    }

    private void setHttpHeader(Path filePath, String responseBody) throws IOException {
        contentType(Files.probeContentType(filePath) + CharSet.UTF_8.getCharset());
        contentLength(String.valueOf(responseBody.getBytes().length));
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
}
