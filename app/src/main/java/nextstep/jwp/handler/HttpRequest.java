package nextstep.jwp.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final HttpUri httpUri;
    private final String httpVersion;
    private final HttpHeader httpHeader;
    private final HttpBody httpBody;

    public HttpRequest(HttpMethod httpMethod, String httpUri, String httpVersion, HttpHeader httpHeader, HttpBody httpBody) {
        this.httpMethod = httpMethod;
        this.httpUri = new HttpUri(httpUri);
        this.httpVersion = httpVersion;
        this.httpHeader = httpHeader;
        this.httpBody = httpBody;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        String[] startLineSplit = startLine.split(" ");
        if (startLineSplit.length < 3) {
            throw new IllegalArgumentException("http start line 형식이 올바르지 않습니다.");
        }

        HttpMethod httpMethod = HttpMethod.from(startLineSplit[0]);
        String httpUri = startLineSplit[1];
        String httpVersion = startLineSplit[2];
        HttpHeader httpHeader = extractHeader(reader);
        HttpBody httpBody = extractBody(reader, httpHeader.getContentLength());

        return new HttpRequest(httpMethod, httpUri, httpVersion, httpHeader, httpBody);
    }

    private static HttpHeader extractHeader(BufferedReader reader) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        while (reader.ready()) {
            String headerLine = reader.readLine();
            if (headerLine.equals("")) {
                break;
            }
            String[] headerLineSplit = headerLine.split(": ");
            headerMap.put(headerLineSplit[0], headerLineSplit[1].strip());
        }
        return new HttpHeader(headerMap);
    }

    private static HttpBody extractBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new HttpBody(new String(buffer));
    }

    public boolean isRequestStaticFile() {
        return httpUri.getUri().contains(".") && httpMethod == HttpMethod.GET;
    }

    public boolean isUriContainsQuery() {
        return httpUri.getTotalUri().contains("?");
    }

    public boolean isGet() {
        return httpMethod == HttpMethod.GET;
    }

    public boolean isPost() {
        return httpMethod == HttpMethod.POST;
    }

    public String getHttpUri() {
        return httpUri.getRequestUri();
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }
}
