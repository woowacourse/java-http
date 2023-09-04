package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import nextstep.jwp.model.User;

public class RequestBody {
    public static final RequestBody EMPTY_REQUEST_BODY = new RequestBody(new HashMap<>());
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String DELIMITER = "&";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final Map<String, String> bodies;

    private RequestBody(final Map<String, String> bodies) {
        this.bodies = bodies;
    }

    public static RequestBody convert(final BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        final Map<String, String> bodies = Arrays.stream(requestBody.split(DELIMITER))
                .map(keyValue -> keyValue.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        splitKeyValue -> splitKeyValue[KEY_INDEX],
                        splitKeyValue -> splitKeyValue[VALUE_INDEX]
                ));
        return new RequestBody(bodies);
    }

    public User parseToUser() {
        final String account = bodies.get("account");
        final String password = bodies.get("password");
        final String mail = bodies.get("email");
        return new User(account, password, mail);
    }

    public Map<String, String> getBodies() {
        return bodies;
    }
}
