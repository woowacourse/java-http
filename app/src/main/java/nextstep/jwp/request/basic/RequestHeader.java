package nextstep.jwp.request.basic;

import java.util.HashMap;
import java.util.Map;

public class RequestHeader {

    private static final String HEADER_REGEX = ":";
    private static final String CONTENT_LENGTH = "Content-Length";

    private Map<String, String> headers;

    public RequestHeader() {
        this.headers = new HashMap<>();
    }

    public void add(String header) {
        String[] splitedHeaders = header.split(HEADER_REGEX);
        headers.put(splitedHeaders[0], splitedHeaders[1].trim());
    }

    public int contentLength() {
        final String contentLength = headers.get(CONTENT_LENGTH);
        if (contentLength != null) {
            return Integer.parseInt(contentLength);
        }
        return 0;
    }
}
