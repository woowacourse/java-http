package org.apache.coyote.http11.common.response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.common.request.HttpMethod;
import org.apache.coyote.http11.servlet.Page;
import org.apache.coyote.http11.util.StaticFileLoader;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpHeaders httpHeaders;
    private final MessageBody messageBody;

    private HttpResponse(final StatusLine statusLine, final HttpHeaders httpHeaders, final MessageBody messageBody) {
        this.statusLine = statusLine;
        this.httpHeaders = httpHeaders;
        this.messageBody = messageBody;
    }

    public static HttpResponse create(StatusCode code, HttpHeaders headers) {
        return new HttpResponse(StatusLine.create(code), headers, MessageBody.empty());
    }

    public static HttpResponse create(StatusCode code, HttpHeaders headers, String content) {
        return new HttpResponse(StatusLine.create(code), headers, MessageBody.create(content));
    }

    public static HttpResponse createBadRequest() throws IOException {
        String content = StaticFileLoader.load(Page.BAD_REQUEST.getUri());

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.TEXT_HTML.getDetail());
        headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));

        return HttpResponse.create(StatusCode.BAD_REQUEST, headers, content);
    }

    public static HttpResponse createMethodNotAllowed(List<HttpMethod> methods) {
        String allowedMethod = methods.stream()
                .map(Enum::toString)
                .collect(Collectors.joining(", "));

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.ALLOW, allowedMethod);
        return HttpResponse.create(StatusCode.METHOD_NOT_ALLOWED, headers);
    }

    public byte[] getBytes() {
        String status = statusLine.toString();
        String headers = httpHeaders.toString();
        String body = messageBody.getContent();
        return String.join(System.lineSeparator(), status, headers, "", body)
                .getBytes(StandardCharsets.UTF_8);
    }
}
