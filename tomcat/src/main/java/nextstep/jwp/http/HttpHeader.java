package nextstep.jwp.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeader {

    private static final String HEADER_SEPARATOR = ":";
    private static final int SPLIT_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private Map<String, String> headers;

    public HttpHeader(final List<String> headers) {
        this.headers = new HashMap();

        for (String header : headers) {
            String[] headerLine = header.split(HEADER_SEPARATOR, SPLIT_SIZE);
            this.headers.put(headerLine[KEY_INDEX], headerLine[VALUE_INDEX]);
        }
    }
}
