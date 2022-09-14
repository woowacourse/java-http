package nextstep.jwp.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {
    private static final String HEADER_DELIMITER = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final int HEADER_SPLIT_SIZE = 2;

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(List<String> rawRequest) {
        validateRawRequest(rawRequest);
        Map<String, String> headers = new HashMap<>();
        rawRequest.subList(1, rawRequest.size())
                .stream()
                .map(header -> header.split(HEADER_DELIMITER))
                .forEach(parsedHeader -> headers.put(parsedHeader[HEADER_KEY_INDEX], parsedHeader[HEADER_VALUE_INDEX]));
        return new RequestHeaders(headers);
    }

    private static void validateRawRequest(List<String> rawRequest) {
        boolean headerCondition = rawRequest.subList(1, rawRequest.size())
                .stream()
                .anyMatch(each -> each.split(HEADER_DELIMITER).length != HEADER_SPLIT_SIZE);
        if (headerCondition) {
            throw new IllegalArgumentException("Request의 두번째 줄부터 마지막 줄 중 헤더의 형식이 아닌 줄이 있습니다.");
        }
    }

    public HttpCookie generateCookie() {
        return HttpCookie.from(headers);
    }
}
