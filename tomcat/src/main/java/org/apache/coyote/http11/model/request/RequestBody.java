package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String BODY_DELIMITER = "&";
    private static final String BODY_CONTENT_VALUE_DELIMITER = "=";
    private static final int BODY_KEY_INDEX = 0;
    private static final int BODY_VALUE_INDEX = 1;

    private final Map<String, String> body;

    private RequestBody(final Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody from(final String input) {
        Map<String, String> body = new HashMap<>();
        if(input.equals("")) {
            return new RequestBody(body);
        }
        return splitBody(input, body);
    }

    private static RequestBody splitBody(final String input, final Map<String, String> body) {
        String[] bodyContents = input.split(BODY_DELIMITER);
        for (String bodyContent : bodyContents) {
            String[] splitBodyContent = bodyContent.split(BODY_CONTENT_VALUE_DELIMITER);
            body.put(splitBodyContent[BODY_KEY_INDEX], splitBodyContent[BODY_VALUE_INDEX]);
        }
        return new RequestBody(body);
    }

    public Map<String, String> getBody() {
        return body;
    }
}
