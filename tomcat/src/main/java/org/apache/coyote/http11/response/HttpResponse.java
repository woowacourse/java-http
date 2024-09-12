package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private final String DEFAULT_CONTENT_ENCODING_TYPE = "utf-8";

    private HttpResponseStatusLine statusLine;
    private Map<String, String> header;
    private String body;

    private HttpResponse(HttpResponseStatusLine statusLine, Map<String, String> header, String body) {
        this.statusLine = statusLine;
        this.header = header;
        this.body = body;
    }

    public static HttpResponse createEmptyResponse() {
        return new HttpResponse(new HttpResponseStatusLine(HttpStatus.OK), new HashMap<>(), "");
    }

    public void setContentType(ContentType contentType) {
        header.put("Content-Type",
                contentType.toHttpForm() + ";" + "charset=" + DEFAULT_CONTENT_ENCODING_TYPE);
    }

    public void setResponseBody(String content) {
        header.put("Content-Length", String.valueOf(content.getBytes().length));
        body = content;
    }

    public void setCookie(HttpCookie cookie) {
        header.put("Set-Cookie", cookie.toHttpForm());
    }

    public void sendRedirect(String location) {
        header.put("Location", location);
        setHttpStatus(HttpStatus.FOUND);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        statusLine.setStatus(httpStatus);
    }

    public String toHttpForm() {
        String httpFormHeader = header.entrySet()
                .stream()
                .map((singleHeader) -> singleHeader.getKey() + ": " + singleHeader.getValue())
                .collect(Collectors.joining("\r\n"));

        return String.join("\r\n",
                statusLine.toHttpForm(),
                httpFormHeader,
                "",
                body);
    }

}
