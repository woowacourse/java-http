package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> params = new HashMap<>();
    private final BufferedReader bufferedReader;


    public HttpRequest(InputStream inputStream) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String firstLineBuffer = bufferedReader.readLine();
        if (firstLineBuffer == null) {
            return;
        }
        String[] firstLine = firstLineBuffer.split(" ");
        method = firstLine[0];
        path = firstLine[1];

        parseParams(extractRequestBody());

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

    private void parseParams(String body) {
        String[] queries = body.split("&");
        for (String query : queries) {
            int equalIndex = query.indexOf("=");
            String key = query.substring(0, equalIndex);
            String value = query.substring(equalIndex + 1);
            params.put(key, value);
        }
    }

    private String extractRequestBody() throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
