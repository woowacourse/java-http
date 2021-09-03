package nextstep.jwp.web.network.request;

import nextstep.jwp.web.exception.InputException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpBody {

    private static final String DEFAULT_HTTP_BODY = "";
    private static final int SPLIT_INTO_TWO = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String body;

    private HttpBody(String body) {
        this.body = body;
    }

    public static HttpBody of(BufferedReader bufferedReader, int contentLength) {
        try {
            if (bufferedReader.ready()) {
                final char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                return new HttpBody(new String(buffer));
            }
            return new HttpBody(DEFAULT_HTTP_BODY);
        } catch (IOException exception) {
            throw new InputException("body in http request");
        }
    }

    public Map<String, String> asMap() {
        final Map<String, String> bodyAsMap = new HashMap<>();
        final String[] params = body.split("&");
        for (String param : params) {
            final String[] keyAndValue = param.split("=", SPLIT_INTO_TWO);
            bodyAsMap.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return bodyAsMap;
    }
}
