package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers = new HttpHeaders(new LinkedHashMap<>());

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        requestLine = new RequestLine(bufferedReader.readLine());
        addHeaders(bufferedReader);
    }

    private void addHeaders(final BufferedReader bufferedReader) throws IOException {
        String header = bufferedReader.readLine();
        while (header != null && !header.isBlank()) {
            headers.put(header);
            header = bufferedReader.readLine();
        }
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

    public boolean isQueryParamsEmpty(){
        return requestLine.isQueryParamsEmpty();
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }
}
