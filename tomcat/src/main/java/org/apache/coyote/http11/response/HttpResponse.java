package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private HttpResponseStatusLine statusLine;
    private Map<String, String> header;
    private String body;

    private HttpResponse(HttpResponseStatusLine statusLine, Map<String, String> header, String body) {
        this.statusLine = statusLine;
        this.header = header;
        this.body = body;
    }

    public static HttpResponse createHttpResponse(HttpStatus httpStatus) {
        return new HttpResponse(new HttpResponseStatusLine(httpStatus), new HashMap<>(), "");
    }

    public void setContentType(ContentType contentType) {
        header.put("Content-Type", contentType.getValueToHttpHeaderForm());
    }

    public void setResponseBody(String content) {
        header.put("Content-Length", String.valueOf(content.getBytes().length));
        body = content;
    }

    public void setCookie(String cookie) {
        header.put("Set-Cookie", cookie);
    }

    public String getContentType() {
        return header.get("Content-Type");
    }

    public String getHttpResponseHttpOutputForm() {
        String httpFormHeader = header.entrySet()
                .stream()
                .map((singleHeader) -> singleHeader.getKey() + ": " + singleHeader.getValue())
                .collect(Collectors.joining("\n"));

        return String.join("\r\n",
                statusLine.toResponseForm(),
                httpFormHeader,
                "",
                body);
    }

    public void setLocation(String location) {
        header.put("Location", location);
    }

    public String getBody() {
        return body;
    }
}
