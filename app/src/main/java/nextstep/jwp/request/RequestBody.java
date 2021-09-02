package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestBody {
    private static final String REQUEST_BODY_PARAMS_SPLIT_REGEX = "&";
    private static final String REQUEST_BODY_PARAM_KEY_VALUE_SPLIT_REGEX = "=";
    private static final int KEY_INDEX_OF_SPLIT = 0;
    private static final int VALUE_INDEX_OF_SPLIT = 1;

    private final Map<String, String> body;

    public RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody of(BufferedReader reader, String contentLength) throws IOException {
        String requestBody = parseRequestBody(reader, Integer.parseInt(contentLength));
        return new RequestBody(extractParameters(requestBody));
    }

    private static String parseRequestBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private static Map<String, String> extractParameters(String requestBody) {
        return Stream.of(requestBody.split(REQUEST_BODY_PARAMS_SPLIT_REGEX))
                .map(parameter -> parameter.split(REQUEST_BODY_PARAM_KEY_VALUE_SPLIT_REGEX, 2))
                .collect(Collectors.toMap(parameter -> parameter[KEY_INDEX_OF_SPLIT], parameter -> parameter[VALUE_INDEX_OF_SPLIT]));
    }

    public static RequestBody empty() {
        return new RequestBody(Collections.emptyMap());
    }

    public String getValue(String name) {
        return this.body.get(name);
    }

    public Map<String, String> getBody() {
        return body;
    }
}
