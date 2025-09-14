package org.apache.coyote.http11.httpResponse;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.general.CommonHeaderKeys;
import org.apache.coyote.http11.general.ContentType;
import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.general.HttpProtocolVersion;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpHeaders headers;
    private final byte[] body;

    private HttpResponse(StatusLine statusLine, HttpHeaders headers, byte[] body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(HttpProtocolVersion protocolVersion, HttpStatus status, ContentType contentType, String bodyText) {
        validateFields(protocolVersion, status, contentType, bodyText);
        StatusLine statusLine = new StatusLine(protocolVersion, status);
        HttpHeaders headers = HttpHeaders.empty();
        headers.add(CommonHeaderKeys.CONTENT_TYPE.getKey(), contentType.getValueWithUtf8Charset());
        byte[] body = bodyText.getBytes(StandardCharsets.UTF_8);
        headers.add(CommonHeaderKeys.CONTENT_LENGTH.getKey(), String.valueOf(body.length));
        return new HttpResponse(statusLine, headers, body);
    }

    private static void validateFields(HttpProtocolVersion protocolVersion, HttpStatus status, ContentType contentType, String bodyText) {
        if (protocolVersion == null || status == null || contentType == null || bodyText == null) {
            throw new IllegalArgumentException("응답을 만드는 과정에서 오류가 발생했습니다.");
        }
    }

    public void addHeader(String key, String value) {
        this.headers.add(key, value);
    }

    public String toString() {
        return String.join("\r\n",
            statusLine.getProtocolVersion().getVersion() + " " + statusLine.getStatusCode() + " " + statusLine.getStatusMessage(),
            buildHeaderMessage(),
            new String(body, StandardCharsets.UTF_8));
    }

    private String buildHeaderMessage() {
        return this.headers.buildHeaderMessage();
    }
}
