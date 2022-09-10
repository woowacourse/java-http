package jakarta.http.reqeust;

import jakarta.http.ContentType;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.http.HttpHeader;

public class HttpRequest {

    private static final String EMPTY_REQUEST_BODY = " ";

    private final HttpRequestLine httpRequestLine;
    private final HttpHeader httpHeader;
    private final String httpRequestBody;

    public HttpRequest(final BufferedReader bufferReader) throws IOException {
        this.httpRequestLine = HttpRequestLine.from(bufferReader.readLine());
        this.httpHeader = new HttpHeader(readHeaders(bufferReader));
        this.httpRequestBody = readBody(bufferReader, httpHeader);
    }

    private List<String> readHeaders(final BufferedReader bufferReader) throws IOException {
        List<String> headers = new ArrayList<>();

        String headerLine = bufferReader.readLine();
        while (headerLine != null && !headerLine.isBlank()) {
            headers.add(headerLine);
            headerLine = bufferReader.readLine();
        }
        return headers;
    }

    private String readBody(final BufferedReader bufferReader, final HttpHeader httpHeader) throws IOException {
        if (!httpHeader.hasContentLength()) {
            return EMPTY_REQUEST_BODY;
        }
        int length = Integer.parseInt(httpHeader.getContentLength());
        char[] buffer = new char[length];
        bufferReader.read(buffer, 0, length);
        return new String(buffer);
    }

    public String findContentType() {
        String path = getPath();
        ContentType type = ContentType.findContentType(path);
        return type.getContentType();
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
        return httpHeader.getCookie();
    }

    public String getBody() {
        return httpRequestBody;
    }
}
