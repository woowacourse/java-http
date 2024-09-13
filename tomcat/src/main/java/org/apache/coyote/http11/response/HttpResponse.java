package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.component.HttpCookie;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.HttpStatus;
import org.apache.coyote.http11.component.HttpVersion;
import org.apache.coyote.http11.file.FileDetails;
import org.apache.coyote.http11.file.FileFinder;

public class HttpResponse {

    private final StatusLine statusLine;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body;

    public HttpResponse(HttpVersion httpVersion) {
        this(new StatusLine(httpVersion), "");
    }

    private HttpResponse(StatusLine statusLine, String body) {
        this.statusLine = statusLine;
        this.body = body;
    }

    public void sendRedirect(String path) {
        statusLine.setHttpStatus(HttpStatus.FOUND);
        addHeader(HttpHeaders.LOCATION, path);
    }

    public void addCookie(HttpCookie httpCookie) {
        addHeader(HttpHeaders.SET_COOKIE, httpCookie.getCookieToMessage());
    }

    public void addHeader(String header, String value) {
        headers.put(header, value);
    }

    public void addStaticResource(String uriPath) {
        FileDetails fileDetails = FileDetails.from(uriPath);
        FileFinder fileFinder = new FileFinder(fileDetails);
        String responseBody = fileFinder.resolve();
        addHeader(HttpHeaders.CONTENT_TYPE, fileDetails.extension().getMediaType());
        addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
        setBody(responseBody);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
