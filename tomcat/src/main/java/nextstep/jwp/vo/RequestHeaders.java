package nextstep.jwp.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {
    private static final String HEADER_DELIMITER = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(List<String> rawRequest) {
        Map<String, String> headers = new HashMap<>();
        rawRequest.subList(1, rawRequest.size())
                .stream()
                .map(header -> header.split(HEADER_DELIMITER))
                .forEach(parsedHeader -> headers.put(parsedHeader[HEADER_KEY_INDEX], parsedHeader[HEADER_VALUE_INDEX]));
        return new RequestHeaders(headers);
    }

    public HttpCookie generateCookie() {
        return HttpCookie.from(headers);
    }
}
