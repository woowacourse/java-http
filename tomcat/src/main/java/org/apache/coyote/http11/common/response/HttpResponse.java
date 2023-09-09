package org.apache.coyote.http11.common.response;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.common.request.HttpMethod;
import org.apache.coyote.http11.servlet.Page;

public class HttpResponse {

    private StatusLine statusLine;
    private ResponseHeaders responseHeaders;
    private MessageBody messageBody;

    private HttpResponse(final StatusLine statusLine, final ResponseHeaders responseHeaders,
                         final MessageBody messageBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.messageBody = messageBody;
    }

    public static HttpResponse create() {
        return new HttpResponse(null, ResponseHeaders.create(), MessageBody.empty());
    }

    //    public static HttpResponse create(StatusCode code, HttpHeaders headers) {
//        return new HttpResponse(StatusLine.create(code), headers, MessageBody.empty());
//    }
//
//    public static HttpResponse create(StatusCode code, HttpHeaders headers, String content) {
//        return new HttpResponse(StatusLine.create(code), headers, MessageBody.create(content));
//    }

    public void setStatusCode(StatusCode code) {
        statusLine = StatusLine.create(code);
    }

    public void setContentType(ContentType type) {
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE, type.getDetail());
    }

    public void setContentLength(int length) {
        responseHeaders.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(length));
    }

    public void setLocation(Page page) {
        responseHeaders.addHeader(HttpHeaderName.LOCATION, page.getUri());
    }

    public void setCookie(String key, String value) {
        responseHeaders.addHeader(HttpHeaderName.SET_COOKIE, key + "=" + value);
    }

    public void setAllow(List<HttpMethod> methods) {
        String allowedMethod = methods.stream()
                .map(Enum::toString)
                .collect(Collectors.joining(", "));

        responseHeaders.addHeader(HttpHeaderName.ALLOW, allowedMethod);
    }

    public void setBody(String content) {
        messageBody = MessageBody.create(content);
    }

    public byte[] getBytes() {
        System.out.println(statusLine == null);
        String status = statusLine.toString();
        String headers = responseHeaders.toString();
        String body = messageBody.getContent();
        return String.join(System.lineSeparator(), status, headers, "", body)
                .getBytes(StandardCharsets.UTF_8);
    }

//    public static HttpResponse createMethodNotAllowed(List<HttpMethod> methods) {
//        String allowedMethod = methods.stream()
//                .map(Enum::toString)
//                .collect(Collectors.joining(", "));
//
//        ResponseHeaders headers = new ResponseHeaders();
//        headers.addHeader(HttpHeaderName.ALLOW, allowedMethod);
//        return HttpResponse.create(StatusCode.METHOD_NOT_ALLOWED, headers);
//    }
//
//    public static HttpResponse createBadRequest() throws IOException {
//        String content = StaticFileLoader.load(Page.BAD_REQUEST.getUri());
//
//        ResponseHeaders headers = new ResponseHeaders();
//        headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.TEXT_HTML.getDetail());
//        headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));
//
//        return HttpResponse.create(StatusCode.BAD_REQUEST, headers, content);

//    }
}
