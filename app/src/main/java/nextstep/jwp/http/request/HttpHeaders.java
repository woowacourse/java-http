package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String CONTENT_LENGTH = "Content-Length";
    private final Map<String, String> headers;

    private HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders parse(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line = null;

        while(isNotEmpty(line)) {
            line = bufferedReader.readLine();
            String[] slicedLine = line.split(":");
            String key = slicedLine[0];
            String value = slicedLine[1];
            headers.put(key, value);
        }
        return new HttpHeaders(headers);
    }

    private static boolean isNotEmpty(String line) {
        return !"".equals(line);
    }

    public int getContentLength() {
        if (!headers.containsKey(CONTENT_LENGTH)) {
            return 0;
        }
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }

    public boolean hasRequestBody() {
        return headers.containsKey(CONTENT_LENGTH);
    }
}
