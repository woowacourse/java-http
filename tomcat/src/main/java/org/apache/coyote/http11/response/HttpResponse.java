package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String STATIC_RESOURCE_DIRECTORY = "static";
    private static final String HTML_EXTENSION = ".html";
    private static final String FILE_EXTENSION_DELIMITER = ".";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String LOCATION_HEADER = "Location";
    private static final String CRLF = "\r\n";

    private HttpStatusLine httpStatusLine;
    private HttpResponseHeader httpResponseHeader;
    private String httpResponseBody;

    public HttpResponse() {
        this.httpStatusLine = new HttpStatusLine(HTTP_VERSION, HttpStatus.OK);
        this.httpResponseHeader = new HttpResponseHeader();
        this.httpResponseBody = "";
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatusLine.setHttpStatus(httpStatus);
    }

    public void addHttpResponseHeader(String key, String value) {
        this.httpResponseHeader.add(key, value);
    }

    public void setJSessionId(String jSessionId) {
        this.httpResponseHeader.setJSessionId(jSessionId);
    }

    public void setContentType(ContentType contentType) {
        this.httpResponseHeader.add(CONTENT_TYPE_HEADER, contentType.getContentType());
    }

    public void setContentType(String fileExtension) {
        ContentType contentType = ContentType.findByFileExtension(fileExtension);
        this.httpResponseHeader.add(CONTENT_TYPE_HEADER, contentType.getContentType());
    }

    public void setHttpResponseBody(String resourceName) throws IOException {
        if (resourceName.equals("Hello world!")) {
            this.httpResponseBody = resourceName;
            return;
        }
        this.httpResponseBody = getResponseBody(resourceName);
    }

    private String getResponseBody(String resourceName) throws IOException {
        String adjustedResourceName = adjustResourceExtension(resourceName);
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_DIRECTORY + adjustedResourceName);
        Path path = new File(resource.getPath()).toPath();
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }

    private String adjustResourceExtension(String resourceName) {
        if (!hasFileExtension(resourceName)) {
            resourceName += HTML_EXTENSION;
        }
        return resourceName;
    }

    private boolean hasFileExtension(String resourceName) {
        return resourceName.contains(FILE_EXTENSION_DELIMITER);
    }

    public String toHttpResponse() {
        StringBuilder httpResponse = new StringBuilder();

        String responseStatusLine = String.format(
                "%s %d %s ",
                this.httpStatusLine.getHttpVersion(),
                this.httpStatusLine.getHttpStatusCode(),
                this.httpStatusLine.getHttpStatusMessage()
        );
        httpResponse.append(responseStatusLine).append(CRLF);

        appendHeader(httpResponse, SET_COOKIE_HEADER, this.httpResponseHeader.getSetCookieValue());
        appendHeader(httpResponse, CONTENT_TYPE_HEADER, this.httpResponseHeader.get(CONTENT_TYPE_HEADER));
        appendHeader(httpResponse, CONTENT_LENGTH_HEADER, String.valueOf(this.httpResponseBody.getBytes().length));
        appendHeader(httpResponse, LOCATION_HEADER, this.httpResponseHeader.get(LOCATION_HEADER));

        httpResponse.append(CRLF);
        httpResponse.append(this.httpResponseBody);
        return httpResponse.toString();
    }

    private void appendHeader(StringBuilder httpResponse, String headerName, String headerValue) {
        if (headerValue != null && !headerValue.isEmpty()) {
            httpResponse.append(String.format("%s: %s ", headerName, headerValue)).append(CRLF);
        }
    }
}
