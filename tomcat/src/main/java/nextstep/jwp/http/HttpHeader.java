package nextstep.jwp.http;

import java.util.List;
import java.util.Map;

public class HttpHeader {

    private Map<String, String> headers;

    public HttpHeader(final List<String> headers) {
        for (String header : headers) {
            String[] headerLine = header.split(":", 2);
            this.headers.put(headerLine[0], headerLine[1]);
        }
    }
}
