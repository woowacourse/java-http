package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private String body;

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        requestLine = new RequestLine(bufferedReader.readLine());
        headers = getHeaders(bufferedReader);
        bodySet(bufferedReader);
    }

    private void bodySet(final BufferedReader bufferedReader) throws IOException {
        if (requestLine.isPost()) {
            body = getBody(bufferedReader);
        }
    }

    private String getBody(final BufferedReader bufferedReader) throws IOException {
        int contentLength = Integer.parseInt(headers.getHeaderDataByKey("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        String requestBody = new String(buffer);
        LOGGER.debug("request body : {} ", requestBody);
        return requestBody;
    }

    private HttpHeaders getHeaders(final BufferedReader bufferedReader) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        String header = bufferedReader.readLine();
        while (header != null && !header.isBlank()) {
            httpHeaders.put(header);
            header = bufferedReader.readLine();
        }
        return httpHeaders;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public String getQueryParam(final String key) {
        return requestLine.getQueryParam(key);
    }

    public boolean isQueryParamsEmpty() {
        return requestLine.isQueryParamsEmpty();
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }

    public String getBody() {
        return body;
    }
}
