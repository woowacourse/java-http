package nextstep.jwp.http.reqeust;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpHeader;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final HttpRequestLine httpRequestLine;
    private final HttpHeader httpHeaders;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(final BufferedReader bufferReader) throws IOException {
        this.httpRequestLine = HttpRequestLine.from(bufferReader.readLine());
        this.httpHeaders = new HttpHeader(bufferReader);
        this.httpRequestBody = new HttpRequestBody(bufferReader, httpHeaders.getValues(CONTENT_LENGTH));
    }

    public String findContentType() {
        String url = getPath();
        return ContentType.findContentType(url);
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public String getMethod() {
        return httpRequestLine.getMethod();
    }

    public Map<String, String> getRequestBodies() {
        return httpRequestBody.getValues();
    }

    public String getCookie() {
        return httpHeaders.getCookie();
    }
}
