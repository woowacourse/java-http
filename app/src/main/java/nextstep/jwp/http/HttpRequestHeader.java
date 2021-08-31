package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {
    private String method;
    private String resource;
    private final Map<String, String> headers = new HashMap<>();


    public HttpRequestHeader(BufferedReader bufferedReader) throws IOException {
        String firstLineBuffer = bufferedReader.readLine();
        if (firstLineBuffer == null) {
            return;
        }
        String[] firstLine = firstLineBuffer.split(" ");
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
            headers.put(header[0].trim(), header[1].trim());
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
}
