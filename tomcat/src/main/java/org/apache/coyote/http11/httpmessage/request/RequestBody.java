package org.apache.coyote.http11.httpmessage.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private static final String BODY_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> body;

    private RequestBody(final Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody fromRequest(final String request) {
        final String[] requestBodies = request.split(BODY_DELIMITER);
        final Map<String, String> body = Arrays.stream(requestBodies)
            .map(headerContent -> headerContent.split(KEY_VALUE_DELIMITER))
            .collect(Collectors.toMap(
                headerContent -> headerContent[KEY_INDEX],
                headerContent -> headerContent[VALUE_INDEX]));
        return new RequestBody(body);
    }

    public String getAccount() {
        return body.get("account");
    }

    public String getPassword() {
        return body.get("password");
    }

    public String getEmail() {
        return body.get("email");
    }

    public Map<String, String> getBody() {
        return body;
    }
}
