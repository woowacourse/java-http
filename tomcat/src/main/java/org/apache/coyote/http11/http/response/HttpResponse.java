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

    private final HttpStatusLine responseLine;
    private final HttpHeader header;
    private final HttpResponseBody responseBody;
    private final Map<String, Object> sessionAttributes;

    private HttpResponse(final HttpStatusLine responseLine,
                         final HttpHeader header,
                         final HttpResponseBody responseBody) {
        this.responseLine = responseLine;
        this.header = header;
        this.responseBody = responseBody;
        this.sessionAttributes = new HashMap<>();
    }

    public static HttpResponse ok() {
        final HttpStatusLine httpStatusLine = HttpStatusLine.of(HttpVersion.HTTP_1_1, HttpStatus.OK);
        final HttpResponseBody httpResponseBody = HttpResponseBody.emptyBody();
        final HttpHeader httpHeader = HttpHeader.createByResponseBody(httpResponseBody, null);
        return new HttpResponse(httpStatusLine, httpHeader, httpResponseBody);
    }

    public static HttpResponse ok(final String responseBodyValue) {
        final HttpStatusLine httpStatusLine = HttpStatusLine.of(HttpVersion.HTTP_1_1, HttpStatus.OK);
        final HttpResponseBody httpResponseBody = HttpResponseBody.withStaticResourceName(responseBodyValue);
        final HttpHeader httpHeader = HttpHeader.createByResponseBody(httpResponseBody, responseBodyValue);
        return new HttpResponse(httpStatusLine, httpHeader, httpResponseBody);
    }

    public static HttpResponse found(final String targetPath) {
        final HttpStatusLine httpStatusLine = HttpStatusLine.of(HttpVersion.HTTP_1_1, HttpStatus.FOUND);
        final HttpResponseBody httpResponseBody = HttpResponseBody.emptyBody();
        final HttpHeader httpHeader = HttpHeader.createByResponseBody(httpResponseBody, null);
        httpHeader.addHeader(HttpHeaderKey.LOCATION.getValue(), targetPath);
        return new HttpResponse(httpStatusLine, httpHeader, httpResponseBody);
    }

    public static HttpResponse unauthorized() {
        final HttpStatusLine httpStatusLine = HttpStatusLine.of(HttpVersion.HTTP_1_1, HttpStatus.UNAUTHORIZED);
        final HttpResponseBody httpResponseBody = HttpResponseBody.withStaticResourceName("401.html");
        final HttpHeader httpHeader = HttpHeader.createByResponseBody(httpResponseBody, "401.html");
        return new HttpResponse(httpStatusLine, httpHeader, httpResponseBody);
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

    public void addAttribute(final String key, final Object value) {
        sessionAttributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return this.sessionAttributes.get(key);
    }

    public void setCookie(final String cookieName, final String cookieValue) {
        String cookieHeaderValue = cookieName + "=" + cookieValue;
        header.addHeader(HttpHeaderKey.SET_COOKIE.getValue(), cookieHeaderValue);
    }
}
