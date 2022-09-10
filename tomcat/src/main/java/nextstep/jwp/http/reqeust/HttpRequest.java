package nextstep.jwp.http.reqeust;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpHeader;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final HttpRequestLine httpRequestLine;
    private final HttpHeader httpHeaders;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(final BufferedReader bufferReader) throws IOException {
        this.httpRequestLine = HttpRequestLine.from(bufferReader.readLine());
        this.httpHeaders = new HttpHeader(readHttpHeaders(bufferReader));
        this.httpRequestBody = new HttpRequestBody(bufferReader, httpHeaders.getValues(CONTENT_LENGTH));
    }

    private List<String> readHttpHeaders(final BufferedReader bufferReader) throws IOException {
        List<String> headers = new ArrayList<>();

        String headerLine = bufferReader.readLine();
        while (headerLine != null && !headerLine.isBlank()) {
            headers.add(headerLine);
        }
        return headers;
    }

    public String findContentType() {
        String url = getPath();
        return ContentType.findContentType(url);
    }

    public boolean hasPostMethod() {
        return httpRequestLine.hasPostMethod();
    }

    public boolean hasGetMethod() {
        return httpRequestLine.hasGetMethod();
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public String getCookie() {
        return httpHeaders.getCookie();
    }

    public String getBody() {
        return httpRequestBody.getBody();
    }
}
