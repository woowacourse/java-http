package nextstep.jwp.web.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nextstep.jwp.web.http.Headers;

public class HttpRequest {

    private StartLine startLine;

    private Headers headers;

    private RequestBody body;

    private HttpRequest() {
    }

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.startLine = new StartLine(parseHttpRequestStartLine(bufferedReader));
        this.headers = new Headers(parseHttpRequestHeaders(bufferedReader));
        this.body = new RequestBody(parseHttpRequestBody(bufferedReader));
    }

    private String parseHttpRequestStartLine(BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine();
    }

    private List<String> parseHttpRequestHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> rawHeaders = new ArrayList<>();
        String content = "";

        while (!"".equals(content = bufferedReader.readLine())) {
            rawHeaders.add(content);
        }

        return rawHeaders;
    }

    private String parseHttpRequestBody(BufferedReader bufferedReader) throws IOException {
        int contentLength = headers.getContentLength();
        if (contentLength == 0) {
            return null;
        }

        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public Headers httpHeaders() {
        return headers;
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getUrl() {
        return startLine.getUrl();
    }

    public String getAttribute(String key) {
        return body.getAttribute(key);
    }

    public Map<String, String> getRequestParams() {
        return startLine.getRequestParams();
    }
}
