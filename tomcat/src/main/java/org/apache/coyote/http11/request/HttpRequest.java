package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Messages#http_requests"></a>
 */
public class HttpRequest {

    private RequestLine requestLine;
    private Map<String, String> headers;
    private String body;

    public HttpRequest(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // 1. request line 추출
        RequestLine requestLine = new RequestLine(bufferedReader.readLine());

        // 2. headers 추출
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(": ");
            if (header.length != 2) {
                throw new IllegalArgumentException("invalid header: " + header);
            }
            headers.put(header[0], header[1]);
        }

        // 3. body 추출
        String body = null;
        if (headers.containsKey("Content-Length")) {
            int length = Integer.parseInt(headers.get("Content-Length"));
            char[] buf = new char[length];
            body = new String(
                    buf,
                    0,
                    bufferedReader.read(buf, 0, length));
        }

        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getRequestTarget();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
