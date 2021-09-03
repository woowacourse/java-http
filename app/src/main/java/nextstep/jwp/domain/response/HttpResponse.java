package nextstep.jwp.domain.response;

import nextstep.jwp.domain.HttpCookie;
import nextstep.jwp.domain.HttpSession;
import nextstep.jwp.domain.Resource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String NEW_LINE = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private HttpStatus httpStatus;
    private Map<String, String> headerMap;
    private String body;

    public HttpResponse() {
        this.httpStatus = null;
        this.headerMap = new LinkedHashMap<>();
        this.body = "";
    }

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headerMap, String body) {
        this.httpStatus = httpStatus;
        this.headerMap = headerMap;
        this.body = body;
    }

    public byte[] getBytes() {
        return String.join(NEW_LINE,
                getStatusLine(),
                toHeaderLine(headerMap),
                "",
                body
        ).getBytes();
    }

    private String getStatusLine() {
        return HTTP_VERSION + " " + httpStatus.value() + " " + httpStatus.responsePhrase() + " ";
    }

    private String toHeaderLine(Map<String, String> headerMap) {
        return headerMap.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining(NEW_LINE));
    }

    public HttpResponse respond(String uri) throws IOException {
        return respond(uri, HttpStatus.OK);
    }

    public HttpResponse respond(String uri, HttpStatus httpStatus) throws IOException {
        Resource resourceFile = new Resource(uri);
        String contentType = resourceFile.getContentType();
        setProperty(httpStatus, contentType, resourceFile.getContent());
        return this;
    }

    private void setProperty(HttpStatus httpStatus, String contentType, String body) {
        this.httpStatus = httpStatus;
        this.headerMap.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        this.headerMap.put(CONTENT_TYPE, contentType);
        this.body = body;
    }

    public HttpResponse redirect(String uri) {
        this.httpStatus = HttpStatus.FOUND;
        headerMap.put("Location", uri);
        return this;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getBody() {
        return body;
    }

    public void setCookie(HttpSession httpSession, HttpCookie httpCookie) {
        String cookies = makeCookieToOneLine(httpCookie.getCookieMap());
        headerMap.put("Set-Cookie", cookies + "JSESSIONID=" + httpSession.getId());
    }

    private String makeCookieToOneLine(Map<String, String> cookieMap) {
        if (cookieMap.containsKey("JSESSIONID")) {
            cookieMap.remove("JSESSIONID");
        }
        return cookieMap.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}

