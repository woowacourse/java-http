package nextstep.jwp.web.network.request;

import nextstep.jwp.web.exception.InputException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpBody {

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
            return new HttpBody("");
        } catch (IOException exception) {
            throw new InputException("Exception while reading body from http request.");
        }
    }

    public Map<String, String> asMap() {
        final Map<String, String> bodyAsMap = new HashMap<>();
        final String[] params = body.split("&");
        for (String param : params) {
            final String[] keyAndValue = param.split("=", 2);
            bodyAsMap.put(keyAndValue[0], keyAndValue[1]);
        }
        return bodyAsMap;
    }
}
