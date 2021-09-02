package nextstep.jwp.webserver.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {

    private static final String HEADER_REGEX = ":";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers = new HashMap<>();

    public RequestHeader(BufferedReader br) throws IOException {
        parseHeader(br);
    }

    private void parseHeader(BufferedReader br) throws IOException {
        String line;
        while (!(line = br.readLine()).equals("")) {
            add(line);
        }
    }

    public void add(String header) {
        String[] splitHeaders = header.split(HEADER_REGEX);
        headers.put(splitHeaders[0], splitHeaders[1].trim());
    }

    public int contentLength() {
        final String contentLength = headers.get(CONTENT_LENGTH);
        if (contentLength != null) {
            return Integer.parseInt(contentLength);
        }
        return 0;
    }

    public String get(String key) {
        return headers.get(key);
    }
}
