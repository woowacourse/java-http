package nextstep.jwp.util;

import java.util.Map;
import java.util.Map.Entry;

public class RequestParseUtil {

    private static final int REQUEST_HEADER_INDEX = 0;
    private static final int REQUEST_HEADER_VALUE_INDEX = 1;

    private RequestParseUtil() {
    }

    public static Entry<String, String> getRequstHeader(final String requestHeader) {
        String[] s = requestHeader.split(" ");
        return Map.entry(getHeader(s), getHeaderValue(s));
    }

    private static String getHeader(final String[] requestHeader) {
        return requestHeader[REQUEST_HEADER_INDEX].substring(0, requestHeader[REQUEST_HEADER_INDEX].length() - 1);
    }

    private static String getHeaderValue(final String[] requestHeader) {
        return requestHeader[REQUEST_HEADER_VALUE_INDEX];
    }
}
