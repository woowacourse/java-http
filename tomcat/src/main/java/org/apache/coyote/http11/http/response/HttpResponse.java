package org.apache.coyote.http11.http.response;

import http.HttpHeaderKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.http.common.header.HttpHeader;
import org.apache.coyote.http11.http.common.startline.HttpVersion;

public class HttpResponse {

    private final Map<String, Object> sessionAttributes;
    private HttpStatusLine responseLine;
    private HttpHeader header;
    private HttpResponseBody responseBody;

    private HttpResponse() {
        sessionAttributes = new HashMap<>();
    }

    public static HttpResponse createEmptyResponse() {
        return new HttpResponse();
    }

    public void addAttribute(final String key, final Object value) {
        sessionAttributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return this.sessionAttributes.get(key);
    }

    public String getResponseFormat() {
        final List<String> responseLines = getResponseLines();
        return String.join("\r\n", responseLines.toArray(String[]::new));
    }

    private List<String> getResponseLines() {
        final List<String> formatLine = new ArrayList<>();
        formatLine.add(responseLine.getFormat());
        formatLine.addAll(header.getFormat());

        final Optional<byte[]> responseBodyValue = responseBody.getValue();

        if (responseBodyValue.isEmpty()) {
            return formatLine;
        }
        formatLine.add("");
        formatLine.add(new String(responseBodyValue.get(), StandardCharsets.UTF_8));
        return formatLine;
    }

    public void setOk(final String bodyValue) {
        this.responseLine = HttpStatusLine.of(HttpVersion.HTTP_1_1, HttpStatus.OK);
        this.responseBody = HttpResponseBody.withStaticResourceName(bodyValue);
        this.header = HttpHeader.createByResponseBody(this.responseBody, bodyValue);
    }


    public void setFound(final String targetPath) {
        this.responseLine = HttpStatusLine.of(HttpVersion.HTTP_1_1, HttpStatus.FOUND);
        this.responseBody = HttpResponseBody.emptyBody();
        this.header = HttpHeader.createByResponseBody(this.responseBody, null);
        this.header.addHeader(HttpHeaderKey.LOCATION.getValue(), targetPath);
    }

    public void setUnauthorized() {
        this.responseLine = HttpStatusLine.of(HttpVersion.HTTP_1_1, HttpStatus.UNAUTHORIZED);
        this.responseBody = HttpResponseBody.withStaticResourceName("401.html");
        this.header = HttpHeader.createByResponseBody(this.responseBody, "401.html");
    }

    public void setNotFound() {
        this.responseLine = HttpStatusLine.of(HttpVersion.HTTP_1_1, HttpStatus.NOT_FOUND);
        this.responseBody = HttpResponseBody.withStaticResourceName("404.html");
        this.header = HttpHeader.createByResponseBody(this.responseBody, "404.html");
    }

    public void setInternalServerError() {
        this.responseLine = HttpStatusLine.of(HttpVersion.HTTP_1_1, HttpStatus.INTERNAL_SERVER_ERROR);
        this.responseBody = HttpResponseBody.withStaticResourceName("500.html");
        this.header = HttpHeader.createByResponseBody(this.responseBody, "500.html");
    }

    public void setCookie(final String cookieName, final String cookieValue) {
        String cookieHeaderValue = cookieName + "=" + cookieValue;
        header.addHeader(HttpHeaderKey.SET_COOKIE.getValue(), cookieHeaderValue);
    }
}
