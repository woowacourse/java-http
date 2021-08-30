package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {
    private final String method;
    private final String resource;
    private final Map<String, String> headers = new HashMap<>();


    public HttpRequestHeader(BufferedReader bufferedReader) throws IOException {
        String[] firstLine = bufferedReader.readLine().split(" ");
        method = firstLine[0];
        resource = firstLine[1];

        parseHeaders(bufferedReader);
    }

    private void parseHeaders(BufferedReader bufferedReader) throws IOException {
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line == null || "".equals(line)) {
                return;
            }
            String[] header = line.split(": ");
            headers.put(header[0], header[1]);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getResource() {
        return resource;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "HttpRequestHeader{" +
                "method='" + method + '\'' +
                ", resource='" + resource + '\'' +
                ", headers=" + headers +
                '}';
    }
}
