package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.Headers;

public class HttpResponse {

    private final String protocolVersion = "HTTP/1.1";
    private final Headers headers = new Headers();
    private HttpStatus httpStatus = HttpStatus.OK;
    private ContentType contentType = ContentType.HTML;
    private String responseBody = "";

    public HttpResponse() {
    }

    public void setHeader(final String name, final String value) {
        headers.add(name, value);
    }

    public void setStatus(final HttpStatus httpStatus) {
        this.httpStatus = Objects.requireNonNull(httpStatus, "HTTP 상태는 null일 수 없습니다.");
    }

    public void setContentType(final ContentType contentType) {
        this.contentType = Objects.requireNonNull(contentType, "Content-Type은 null일 수 없습니다.");
    }

    public void setBody(final String responseBody) {
        this.responseBody = Objects.requireNonNull(responseBody, "응답 본문은 null일 수 없습니다.");
    }

    public boolean containsHeader(final String name) {
        return headers.contains(name);
    }

    public byte[] getResponse() {
        final List<String> responseLines = new ArrayList<>();
        responseLines.add(protocolVersion + " " + httpStatus.statusCode() + " " + httpStatus.name());
        responseLines.add("Content-Type: " + contentType.value() + ";charset=utf-8");
        responseLines.add("Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length);
        headers.entries().forEach((name, value) -> responseLines.add(name + ": " + value));
        responseLines.add("");
        responseLines.add(responseBody);

        return String.join("\r\n", responseLines).getBytes(StandardCharsets.UTF_8);
    }

}
